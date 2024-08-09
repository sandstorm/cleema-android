package de.cleema.android.joinedchallenges

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.components.addWeeks
import de.cleema.android.core.components.endOfDay
import de.cleema.android.core.components.startOfDay
import de.cleema.android.core.components.weeksUntil
import de.cleema.android.core.data.ChallengesRepository
import de.cleema.android.core.data.UserRepository
import de.cleema.android.core.models.Challenge
import de.cleema.android.core.models.JoinedChallenge
import de.cleema.android.core.models.UserProgress
import de.cleema.android.core.models.duration
import de.cleema.android.core.models.ordinalFor
import de.cleema.android.di.InstantGenerator
import de.cleema.android.di.UrlOpener
import de.cleema.android.joinedchallenges.JoinedChallengeUiState.*
import de.cleema.android.joinedchallenges.JoinedChallengeUiState.Value.Status
import de.cleema.android.joinedchallenges.JoinedChallengeUiState.Value.Status.*
import de.cleema.android.shared.CurrentUserUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone.Companion.UTC
import kotlinx.datetime.daysUntil
import kotlinx.datetime.toLocalDateTime
import java.util.*
import javax.inject.Inject
import kotlin.math.max
import kotlin.time.Duration.Companion.days

sealed interface JoinedChallengeUiState {
    object Loading : JoinedChallengeUiState
    object NotJoined : JoinedChallengeUiState
    data class Error(val reason: String) : JoinedChallengeUiState
    data class Value(
        val joinedChallenge: JoinedChallenge,
        val status: Status? = null,
        val showsLeaveAlert: Boolean = false,
        val progresses: List<UserProgress> = listOf()
    ) : JoinedChallengeUiState {
        sealed interface Status {
            data class Pending(val dayIndex: Int, val date: LocalDate) : Status
            data class PendingWeekly(val pendingIndex: Int, val currentWeekIndex: Int) : Status
            object Answered : Status
            object Expired : Status
            data class Upcoming(val days: Int) : Status

            val isPending: Boolean
                get() = when (this) {
                    Answered, Expired, is Upcoming -> false
                    is Pending, is PendingWeekly -> true
                }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class JoinedChallengeViewModel @Inject constructor(
    private val repository: ChallengesRepository,
    userRepository: UserRepository,
    savedState: SavedStateHandle,
    @InstantGenerator private val today: () -> Instant,
    private val opener: UrlOpener
) :
    ViewModel() {
    private var showsAlertState = MutableStateFlow(false)
    private val idFlow = savedState.getStateFlow("challengeId", "")
    private val currentUserUseCase = CurrentUserUseCase(userRepository)

    fun answer(answer: JoinedChallenge.Answer) {
        val state = uiState.value as? Value ?: return
        //  if (state.status !is Pending) return
        when (state.status) {
            Answered, is Upcoming, Expired, null -> return
            is Pending -> viewModelScope.launch {
                repository.answer(
                    state.joinedChallenge.challenge.id,
                    state.joinedChallenge.answers.plus(state.status.dayIndex to answer)
                )
            }
            is PendingWeekly -> viewModelScope.launch {
                repository.answer(
                    state.joinedChallenge.challenge.id,
                    state.joinedChallenge.answers.plus(state.status.pendingIndex to answer)
                )
            }
        }

    }

    fun partnerTapped() {
        uiState.value.currentChallenge?.let {
            when (val kind = it.challenge.kind) {
                is Challenge.Kind.Partner -> {
                    viewModelScope.launch {
                        opener.openUrl(kind.partner.url)
                    }
                }

                is Challenge.Kind.Collective -> {
                    viewModelScope.launch {
                        opener.openUrl(kind.partner.url)
                    }
                }

                else -> {
                    // We need the empty else for some kotlin reason
                }
            }
        }
    }

    fun leaveTapped() {
        showsAlertState.value = true
    }

    fun confirmLeave() {
        uiState.value.currentChallenge?.let {
            viewModelScope.launch {
                repository.leaveChallenge(it.challenge.id)
            }
        }
    }

    fun cancelLeaveDialog() {
        showsAlertState.value = false
    }

    val uiState: StateFlow<JoinedChallengeUiState> = idFlow
        .map {
            try {
                UUID.fromString(it)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
        .filterNotNull()
        .flatMapLatest { id ->
            repository.getJoinedChallengeStream(id)
                .combine(showsAlertState) { challenge, showsAlert ->
                    challenge.fold(onSuccess = {
                        Value(
                            it,
                            it.calculateStatus(today()),
                            showsAlert,
                            progresses = it.calculateProgress(currentUserUseCase().id)
                        )
                    }, onFailure = { error ->
                        when (error) {
                            is IllegalArgumentException -> {
                                NotJoined
                            }
                            else -> {
                                Error(reason = error.localizedMessage ?: error.toString())
                            }
                        }
                    })
                }.catch {
                    emit(NotJoined)
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = Loading
        )
}

private fun JoinedChallenge.calculateProgress(userId: UUID): List<UserProgress> {
    val kind = challenge.kind
    return when (kind) {
        is de.cleema.android.core.models.Challenge.Kind.Group -> kind.progresses.filter { it.user.id != userId }
        is de.cleema.android.core.models.Challenge.Kind.Partner -> listOf()
        is de.cleema.android.core.models.Challenge.Kind.Collective -> listOf()
        de.cleema.android.core.models.Challenge.Kind.User -> listOf()
    }
}

val JoinedChallengeUiState.currentChallenge: JoinedChallenge?
    get() = when (this) {
        is Error, Loading, NotJoined -> null
        is Value -> joinedChallenge
    }

private fun JoinedChallenge.calculateStatus(
    today: Instant,
): Status {
    val todayDate = today.toLocalDateTime(UTC).date
    val todayFromStart = challenge.startDate.toLocalDateTime(UTC).date.daysUntil(todayDate)
    if (todayFromStart < 0) return Upcoming(-todayFromStart)
    if (todayFromStart > challenge.duration.dayCount + 2) return Expired
    if (challenge.interval == de.cleema.android.core.models.Challenge.Interval.DAILY) {
        val maxAllowedDate = challenge.endDate.toLocalDateTime(UTC).date
        val index = challenge.ordinalFor(today) ?: return previousPendingOrNull(challenge.endDate) ?: Expired
        return when {
            answers.containsKey(index) -> {
                previousPendingOrNull(today) ?: Answered
            }
            else -> Pending(
                minOf(index, challenge.duration.valueCount),
                minOf(maxAllowedDate, todayDate)
            )
        }
    } else {
        if (challenge.endDate.plus(3.days) <= today.endOfDay) {
            return Expired
        }
        return previousPendingIndexOrNullWhenWeekly(today)?.let { pendingIndex ->
            val numberOfWeeksFromStart = challenge.startDate.weeksUntil(today.startOfDay, UTC) + 1
            PendingWeekly(pendingIndex, numberOfWeeksFromStart)
        } ?: Answered
    }
}

private fun JoinedChallenge.previousPendingOrNull(referenceDate: Instant): Status? =
    challenge.ordinalFor(referenceDate)?.let { index ->
        if (index == 0) return null
        val previous = index.downTo(maxOf(1, index - 3))

        for (day in previous) {
            if (!answers.containsKey(day)) {
                return Pending(
                    minOf(day, challenge.duration.valueCount),
                    referenceDate.minus((index - day).days).toLocalDateTime(UTC).date
                )
            }
        }
        return null
    }

private fun JoinedChallenge.previousPendingIndexOrNullWhenWeekly(referenceDate: Instant): Int? =
    challenge.ordinalFor(referenceDate)?.let { index ->
        if (index == 0) return null
        val previous = index.downTo(maxOf(1, index - 1))

        // max one week back
        val maxAllowedDate = challenge.startDate.addWeeks(max(1, previous.max() - 1)).plus(2.days).endOfDay
        if (referenceDate > maxAllowedDate) {
            return null
        }
        for (weekIndex in previous) {
            if (!answers.containsKey(weekIndex)) {
                return weekIndex
            }
        }
        return null
    }
