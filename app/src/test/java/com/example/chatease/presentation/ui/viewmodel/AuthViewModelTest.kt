package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.data.local.datastore.auth.AuthPreferencesRepository
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.domain.repository.UserRepository
import com.example.chatease.presentation.ui.state.LoginUiState
import com.example.chatease.util.MainDispatcherRule
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authPreferencesRepository: AuthPreferencesRepository = mock()
    private val userRepository: UserRepository = mock()
    private val auth: FirebaseAuth = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val authTask: Task<AuthResult> = mock()
    private val authResult: AuthResult = mock()

    companion object {
        const val EMAIL = "test@email.com"
        const val PASSWORD = "password"
        const val USER_ID = "1"
        const val LOGIN_FAIL_MESSAGE = "Login failed"
    }

    @Test
    fun `should login successfully`() = runTest {
        whenever(auth.signInWithEmailAndPassword(EMAIL, PASSWORD)).thenReturn(authTask)
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_ID)
        doAnswer { invocation ->
            val listener = invocation.getArgument<OnSuccessListener<AuthResult>>(0)
            listener.onSuccess(authResult)
            authTask
        }.whenever(authTask).addOnSuccessListener(any())

        val viewModel = AuthViewModel(
            auth = auth,
            authPreferencesRepository = authPreferencesRepository,
            userRepository = userRepository
        )

        viewModel.login(
            email = EMAIL,
            password = PASSWORD,
            remember = true
        )

        advanceUntilIdle()
        verify(userRepository).updateUserStatus(USER_ID, UserPresenceStatus.ONLINE)
        verify(authPreferencesRepository).saveEmail(EMAIL, true)
    }

    @Test
    fun `should handle exception when login fails`() = runTest {
        whenever { auth.signInWithEmailAndPassword(EMAIL, PASSWORD) }.thenReturn(authTask)
        whenever(authTask.addOnSuccessListener(any())).thenReturn(authTask)
        doAnswer { invocation ->
            val listener = invocation.getArgument<OnFailureListener>(0)
            listener.onFailure(
                Exception(LOGIN_FAIL_MESSAGE)
            )
            authTask
        }.whenever(authTask).addOnFailureListener(any())

        val viewModel = AuthViewModel(
            auth = auth,
            authPreferencesRepository = authPreferencesRepository,
            userRepository = userRepository
        )

        viewModel.login(
            email = EMAIL,
            password = PASSWORD,
            remember = true
        )

        assertEquals(
            LoginUiState.Error(LOGIN_FAIL_MESSAGE),
            viewModel.uiState.value
        )

        advanceUntilIdle()
        verify(auth).signInWithEmailAndPassword(EMAIL, PASSWORD)
        verify(authPreferencesRepository, times(0)).saveEmail(any(), any())
    }

    @Test
    fun `should log out successfully`() = runTest {
        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_ID)

        val viewModel = AuthViewModel(
            auth = auth,
            authPreferencesRepository = authPreferencesRepository,
            userRepository = userRepository
        )

        viewModel.logout()

        advanceUntilIdle()
        verify(auth).signOut()
        verify(userRepository).updateUserStatus(USER_ID, UserPresenceStatus.OFFLINE)

    }

    @Test
    fun `should reset sate to idle`() = runTest {
        whenever(auth.signInWithEmailAndPassword(EMAIL, PASSWORD)).thenReturn(authTask)
        whenever(authTask.addOnSuccessListener(any())).thenReturn(authTask)
        whenever(authTask.addOnFailureListener(any())).thenReturn(authTask)

        val viewModel = AuthViewModel(
            auth = auth,
            authPreferencesRepository = authPreferencesRepository,
            userRepository = userRepository
        )

        viewModel.login(
            email = EMAIL,
            password = PASSWORD,
            remember = true
        )

        assertEquals(LoginUiState.Loading, viewModel.uiState.value)

        viewModel.resetState()

        assertEquals(LoginUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `should save email when onRememberMeChanged is called`() = runTest {
        val viewModel = AuthViewModel(
            auth = auth,
            authPreferencesRepository = authPreferencesRepository,
            userRepository = userRepository
        )
        viewModel.onRememberMeChanged(EMAIL, true)

        advanceUntilIdle()
        verify(authPreferencesRepository).saveEmail(EMAIL, true)
    }


}