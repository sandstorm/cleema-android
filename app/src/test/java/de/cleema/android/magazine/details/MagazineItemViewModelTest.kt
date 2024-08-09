package de.cleema.android.magazine.details

import androidx.lifecycle.SavedStateHandle
import de.cleema.android.core.models.MagazineItem
import de.cleema.android.helpers.FakeMagazineRepository
import de.cleema.android.helpers.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MagazineItemViewModelTest {

    private lateinit var savedHandle: SavedStateHandle
    private lateinit var repository: FakeMagazineRepository
    private lateinit var sut: MagazineItemViewModel

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun givenRepositoryHasItem(item: de.cleema.android.core.models.MagazineItem) {
        savedHandle["newsId"] = item.id.toString()
        repository.itemChannel.trySend(Result.success(item))
    }

    @Before
    fun setUp() {
        repository = FakeMagazineRepository()
        savedHandle = SavedStateHandle()
        sut = MagazineItemViewModel(repository, savedHandle)
    }

    @Test
    fun `It loads the content`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        assertEquals(MagazineItemUiState.Loading, sut.uiState.value)

        val item = de.cleema.android.core.models.MagazineItem(title = "Item")
        givenRepositoryHasItem(item)

        assertEquals(MagazineItemUiState.Success(item), sut.uiState.value)
        collectJob.cancel()
    }

    @Test
    fun `Mark as read will invoke the repository`() = runTest {
        val collectJob = launch(UnconfinedTestDispatcher()) { sut.uiState.collect() }
        val item = de.cleema.android.core.models.MagazineItem(title = "Item")
        givenRepositoryHasItem(item)

        sut.markAsRead()

        assertEquals(item.id, repository.invokedReadId)
        collectJob.cancel()
    }

}
