package de.cleema.android.magazine.list

import de.cleema.android.core.data.UserValue.Valid
import de.cleema.android.helpers.FAKE_REMOTE
import de.cleema.android.helpers.FakeMagazineRepository
import de.cleema.android.helpers.FakeUserRepository
import de.cleema.android.helpers.MainDispatcherRule
import de.cleema.android.magazine.list.MagazineUiState.Loading
import de.cleema.android.magazine.list.MagazineUiState.Success
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.Clock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.*
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@OptIn(ExperimentalCoroutinesApi::class)
class MagazineViewModelTest {

    private lateinit var userRepository: FakeUserRepository
    private lateinit var repository: FakeMagazineRepository
    private lateinit var sut: MagazineViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Before
    fun setUp() {
        repository = FakeMagazineRepository()
        userRepository = FakeUserRepository()
        sut = MagazineViewModel(repository, userRepository)
    }

    @Test
    fun `Magazine items will sorted by date and filtered by users region`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(Loading, sut.uiState.value)
        userRepository.stubbedUserValue = Valid(FAKE_REMOTE.copy(region = de.cleema.android.core.models.Region.LEIPZIG))

        val now = Clock.System.now()
        val items = listOf(
            de.cleema.android.core.models.MagazineItem(
                title = "yesterdays news",
                date = now.minus(1.days),
                type = de.cleema.android.core.models.MagazineItem.ItemType.NEWS,
                region = de.cleema.android.core.models.Region.PIRNA
            ),
            de.cleema.android.core.models.MagazineItem(
                title = "todays news",
                date = now,
                type = de.cleema.android.core.models.MagazineItem.ItemType.NEWS,
                region = de.cleema.android.core.models.Region.LEIPZIG
            ),
            de.cleema.android.core.models.MagazineItem(
                title = "yesterdays tip",
                date = now.minus(1.days).plus(1.hours),
                type = de.cleema.android.core.models.MagazineItem.ItemType.TIP,
                region = de.cleema.android.core.models.Region.LEIPZIG
            ),
            de.cleema.android.core.models.MagazineItem(
                title = "todays tip",
                date = now.minus(1.hours),
                type = de.cleema.android.core.models.MagazineItem.ItemType.TIP,
                region = de.cleema.android.core.models.Region.DRESDEN
            ),
        )
        repository.itemsChannel.send(Result.success(items))
        assertEquals(de.cleema.android.core.models.Region.LEIPZIG.id, repository.invokedNewsRegion)

        assertEquals(Success(items.sortedByDescending { it.date }), sut.uiState.value)

        collectJob.cancel()
    }
}

