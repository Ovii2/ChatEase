package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.MediaItem
import com.example.chatease.domain.model.enums.FileDownloadState
import com.example.chatease.domain.model.enums.MediaType
import com.example.chatease.domain.repository.FileRepository
import com.example.chatease.presentation.ui.state.MediaAndDocsUiState
import com.example.chatease.util.MainDispatcherRule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MediaAndDocsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val fileRepository: FileRepository = mock()
    private lateinit var viewModel: MediaAndDocsViewModel

    companion object {
        private const val CONVERSATION_ID = "1"
        private const val MEDIA_ITEM_ID = "1"
        private const val FILE_URL = "test url"
        private const val FILE_NAME = "file_1.pdf"
        private const val MIME_TYPE = "/pdf"
    }

    @Before
    fun setUp() {
        viewModel = MediaAndDocsViewModel(
            fileRepository = fileRepository
        )
    }

    private fun setupMediaItem(type: MediaType) = MediaItem(
        id = MEDIA_ITEM_ID,
        thumbnailUrl = "",
        mediaUrl = "",
        type = type
    )

    @Test
    fun `should load media items`() = runTest {
        val mediaItems = listOf(setupMediaItem(MediaType.FILE))

        whenever(fileRepository.getMediaItems(CONVERSATION_ID))
            .thenReturn(mediaItems)

        viewModel.loadMediaItems(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            MediaAndDocsUiState.Success(
                mediaItems = mediaItems
            ), viewModel.uiState.value
        )
    }

    @Test
    fun `should handle exception when loading media items`() = runTest {
        whenever(fileRepository.getMediaItems(CONVERSATION_ID))
            .thenThrow(RuntimeException())

        viewModel.loadMediaItems(CONVERSATION_ID)

        advanceUntilIdle()
        assertEquals(
            MediaAndDocsUiState.Error("Failed to load media items"),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should download document`() = runTest {
        viewModel.downloadDoc(
            fileUrl = FILE_URL,
            filename = FILE_NAME,
            mimeType = MIME_TYPE
        )

        advanceUntilIdle()
        assertEquals(FileDownloadState.SUCCESS, viewModel.downloadState.value)
    }

    @Test
    fun `should handle exception when downloading document`() = runTest {
        whenever(
            fileRepository.saveFileToDownloads(
                fileUrl = FILE_URL,
                fileName = FILE_NAME,
                mimeType = MIME_TYPE
            )
        ).thenThrow(RuntimeException())

        viewModel.downloadDoc(
            fileUrl = FILE_URL,
            filename = FILE_NAME,
            mimeType = MIME_TYPE
        )

        advanceUntilIdle()
        assertEquals(FileDownloadState.FAILED, viewModel.downloadState.value)
    }

}