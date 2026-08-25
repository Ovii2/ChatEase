package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.domain.model.User
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever


@OptIn(ExperimentalCoroutinesApi::class)
class BlockedUsersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val userRepository: UserRepository = mock()

    private var users = emptyList<User>()

    companion object {
        private const val USER_ID = "1"

    }

    @Before
    fun setup() {
        users = List(10) {
            User(
                uid = it.toString(),
                fullName = "Test Test",
                email = "test@email.com",
                imageUrl = null,
                status = UserPresenceStatus.OFFLINE,
                blockedUserIds = emptyList(),
                membership = Membership.FREE
            )
        }
    }

    @Test
    fun `should load blocked users`() = runTest {
        whenever(userRepository.observeBlockedUsers()).thenReturn(flowOf(users))

        val viewModel = BlockedUsersViewModel(
            userRepository = userRepository
        )

        advanceUntilIdle()
        assertEquals(users, viewModel.blockedUsers.value)
    }

    @Test
    fun `should handle exception when loadBlockedUsers fails`() = runTest {
        whenever(userRepository.observeBlockedUsers()).thenReturn(flow { throw Exception() })

        val viewModel = BlockedUsersViewModel(
            userRepository = userRepository
        )

        assertEquals(emptyList<User>(), viewModel.blockedUsers.value)
    }

    @Test
    fun `should unblock blocked user`() = runTest {
        whenever(userRepository.observeBlockedUsers()).thenReturn(flowOf(emptyList()))

        val viewModel = BlockedUsersViewModel(
            userRepository = userRepository
        )

        viewModel.unblockUser(USER_ID)
        advanceUntilIdle()
        verify(userRepository).unblockUser(USER_ID)
    }

    @Test
    fun `should handle exception when unblockUser is called`() = runTest {
        whenever(userRepository.observeBlockedUsers()).thenReturn(flowOf(users))
        whenever(userRepository.unblockUser(USER_ID)).thenThrow(RuntimeException())

        val viewModel = BlockedUsersViewModel(
            userRepository = userRepository
        )

        viewModel.unblockUser(USER_ID)

        advanceUntilIdle()
        verify(userRepository).unblockUser(USER_ID)
    }

}