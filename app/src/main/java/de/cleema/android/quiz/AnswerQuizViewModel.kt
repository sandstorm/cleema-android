package de.cleema.android.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.cleema.android.core.data.QuizRepository
import de.cleema.android.quiz.AnswerQuizUiState.Answered
import de.cleema.android.quiz.AnswerQuizUiState.Pending
import de.cleema.android.shared.RegionForCurrentUserUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AnswerQuizViewModel @Inject constructor(
    private val quizRepository: QuizRepository,
    private val regionForCurrentUserUseCase: RegionForCurrentUserUseCase
) : ViewModel() {
    private var selectedRegion = MutableStateFlow<UUID?>(null)

    val uiState: StateFlow<AnswerQuizUiState> =
        selectedRegion
            .onStart { emit(regionForCurrentUserUseCase().id) }
            .filterNotNull()
            .flatMapLatest { uuid ->
                quizRepository
                    .getQuizStream(uuid)
                    .map(Result<de.cleema.android.core.models.QuizState>::toUiState)

            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = AnswerQuizUiState.Loading
            )

    fun answer(choice: de.cleema.android.core.models.Quiz.Choice) {
        val state = uiState.value
        if (state !is Pending) {
            return
        }
        viewModelScope.launch {
            quizRepository.answerQuiz(id = state.quizId, choice)
        }
    }
}

sealed interface AnswerQuizUiState {
    object Loading : AnswerQuizUiState
    data class Pending(
        val question: String,
        val choices: SortedMap<de.cleema.android.core.models.Quiz.Choice, String>,
        val quizId: UUID
    ) :
        AnswerQuizUiState

    data class Answered(
        val question: String,
        val choice: de.cleema.android.core.models.Quiz.Choice,
        val answer: String,
        val explanation: String,
        val isCorrect: Boolean
    ) : AnswerQuizUiState

    data class Error(val reason: String) : AnswerQuizUiState
}

private fun Result<de.cleema.android.core.models.QuizState>.toUiState(): AnswerQuizUiState = fold(
    onSuccess = {
        it.answer?.let { answer ->
            Answered(
                it.quiz.question,
                answer.choice,
                it.quiz.choices[answer.choice] ?: "",
                explanation = it.quiz.explanation,
                isCorrect = it.quiz.correctAnswer == answer.choice
            )
        } ?: Pending(
            question = it.quiz.question,
            choices = it.quiz.choices.toSortedMap(),
            quizId = it.quiz.id
        )
    },
    onFailure = { AnswerQuizUiState.Error(reason = it.localizedMessage ?: "Unknown error") }
)

val AnswerQuizUiState.question: String?
    get() = when (this) {
        is Answered -> question
        AnswerQuizUiState.Loading -> null
        is Pending -> question
        is AnswerQuizUiState.Error -> null
    }
