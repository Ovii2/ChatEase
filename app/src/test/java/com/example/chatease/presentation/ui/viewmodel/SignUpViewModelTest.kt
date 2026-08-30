package com.example.chatease.presentation.ui.viewmodel

import com.example.chatease.R
import com.example.chatease.data.remote.dto.UserDto
import com.example.chatease.domain.model.enums.Membership
import com.example.chatease.domain.model.enums.UserPresenceStatus
import com.example.chatease.presentation.ui.state.SignUpUiState
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class SignUpViewModelTest {

    private val auth: FirebaseAuth = mock()
    private val firestore: FirebaseFirestore = mock()
    private val firebaseUser: FirebaseUser = mock()
    private val authResult: AuthResult = mock()
    private val authTask: Task<AuthResult> = mock()
    private val saveTask: Task<Void> = mock()
    private val collectionReference: CollectionReference = mock()
    private val documentReference: DocumentReference = mock()

    private val viewModel = SignUpViewModel(
        auth = auth,
        firestore = firestore
    )

    companion object {
        private const val USER_ID = "1"
        private const val FULL_NAME = "John Doe"
        private const val EMAIL = "test@email.com"
        private const val PASSWORD = "password123"
    }

    private fun stubAuthRequest() {
        whenever(
            auth.createUserWithEmailAndPassword(
                EMAIL,
                PASSWORD
            )
        ).thenReturn(authTask)
    }

    private fun stubAuthSuccess() {
        stubAuthRequest()

        whenever(auth.currentUser).thenReturn(firebaseUser)
        whenever(firebaseUser.uid).thenReturn(USER_ID)

        whenever(
            authTask.addOnSuccessListener(any())
        ).thenAnswer { invocation ->
            val listener =
                invocation.getArgument<OnSuccessListener<AuthResult>>(0)

            listener.onSuccess(authResult)
            authTask
        }

        whenever(
            authTask.addOnFailureListener(any())
        ).thenReturn(authTask)
    }

    private fun stubAuthFailure(exception: Exception) {
        stubAuthRequest()

        whenever(
            authTask.addOnSuccessListener(any())
        ).thenReturn(authTask)

        whenever(
            authTask.addOnFailureListener(any())
        ).thenAnswer { invocation ->
            val listener =
                invocation.getArgument<OnFailureListener>(0)

            listener.onFailure(exception)
            authTask
        }
    }

    private fun stubFirestoreSet() {
        whenever(
            firestore.collection(SignUpViewModel.USERS)
        ).thenReturn(collectionReference)

        whenever(
            collectionReference.document(USER_ID)
        ).thenReturn(documentReference)

        whenever(
            documentReference.set(any<UserDto>())
        ).thenReturn(saveTask)
    }

    private fun stubSaveSuccess() {
        stubFirestoreSet()

        whenever(
            saveTask.addOnSuccessListener(any())
        ).thenAnswer { invocation ->
            val listener =
                invocation.getArgument<OnSuccessListener<Void>>(0)

            listener.onSuccess(null)
            saveTask
        }

        whenever(
            saveTask.addOnFailureListener(any())
        ).thenReturn(saveTask)
    }

    private fun stubSaveFailure(exception: Exception) {
        stubFirestoreSet()

        whenever(
            saveTask.addOnSuccessListener(any())
        ).thenReturn(saveTask)

        whenever(
            saveTask.addOnFailureListener(any())
        ).thenAnswer { invocation ->
            val listener =
                invocation.getArgument<OnFailureListener>(0)

            listener.onFailure(exception)
            saveTask
        }
    }

    @Test
    fun `should have idle state initially`() {
        assertEquals(
            SignUpUiState.Idle,
            viewModel.uiState.value
        )
    }

    @Test
    fun `should set loading state when signing up`() {
        stubAuthRequest()

        whenever(
            authTask.addOnSuccessListener(any())
        ).thenReturn(authTask)

        whenever(
            authTask.addOnFailureListener(any())
        ).thenReturn(authTask)

        viewModel.signUp(
            fullName = FULL_NAME,
            email = EMAIL,
            password = PASSWORD
        )

        assertEquals(
            SignUpUiState.Loading,
            viewModel.uiState.value
        )
    }

    @Test
    fun `should trim email before creating account`() {
        whenever(
            auth.createUserWithEmailAndPassword(
                EMAIL,
                PASSWORD
            )
        ).thenReturn(authTask)

        whenever(
            authTask.addOnSuccessListener(any())
        ).thenReturn(authTask)

        whenever(
            authTask.addOnFailureListener(any())
        ).thenReturn(authTask)

        viewModel.signUp(
            fullName = FULL_NAME,
            email = "  $EMAIL  ",
            password = PASSWORD
        )

        verify(auth).createUserWithEmailAndPassword(
            EMAIL,
            PASSWORD
        )
    }

    @Test
    fun `should create user and save profile`() {
        stubAuthSuccess()
        stubSaveSuccess()

        viewModel.signUp(
            fullName = "jOhN doe",
            email = "  $EMAIL  ",
            password = PASSWORD
        )

        val userCaptor = argumentCaptor<UserDto>()

        verify(documentReference).set(
            userCaptor.capture()
        )

        val user = userCaptor.firstValue

        assertEquals(USER_ID, user.uid)
        assertEquals("John Doe", user.fullName)
        assertEquals(EMAIL, user.email)
        assertNull(user.imageUrl)
        assertEquals(
            UserPresenceStatus.OFFLINE.name,
            user.status
        )
        assertEquals(
            Membership.FREE.name,
            user.membership
        )
        assertEquals(
            SignUpUiState.Success,
            viewModel.uiState.value
        )
    }

    @Test
    fun `should not save user when authenticated user is null`() {
        stubAuthRequest()

        whenever(auth.currentUser).thenReturn(null)

        whenever(
            authTask.addOnSuccessListener(any())
        ).thenAnswer { invocation ->
            val listener =
                invocation.getArgument<OnSuccessListener<AuthResult>>(0)

            listener.onSuccess(authResult)
            authTask
        }

        whenever(
            authTask.addOnFailureListener(any())
        ).thenReturn(authTask)

        viewModel.signUp(
            fullName = FULL_NAME,
            email = EMAIL,
            password = PASSWORD
        )

        assertEquals(
            SignUpUiState.Loading,
            viewModel.uiState.value
        )

        verify(
            firestore,
            never()
        ).collection(any())
    }

    @Test
    fun `should return email in use error when account already exists`() {
        val exception: FirebaseAuthUserCollisionException = mock()

        stubAuthFailure(exception)

        viewModel.signUp(
            fullName = FULL_NAME,
            email = EMAIL,
            password = PASSWORD
        )

        assertEquals(
            SignUpUiState.Error(
                messageRes = R.string.email_in_use
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should handle signup failure`() {
        stubAuthFailure(
            RuntimeException("Failed")
        )

        viewModel.signUp(
            fullName = FULL_NAME,
            email = EMAIL,
            password = PASSWORD
        )

        assertEquals(
            SignUpUiState.Error(
                message = "Failed",
                messageRes = R.string.fail_sing_up
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should handle signup failure without message`() {
        stubAuthFailure(
            RuntimeException()
        )

        viewModel.signUp(
            fullName = FULL_NAME,
            email = EMAIL,
            password = PASSWORD
        )

        assertEquals(
            SignUpUiState.Error(
                message = null,
                messageRes = R.string.fail_sing_up
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should handle user save failure`() {
        stubAuthSuccess()
        stubSaveFailure(
            RuntimeException("Failed")
        )

        viewModel.signUp(
            fullName = FULL_NAME,
            email = EMAIL,
            password = PASSWORD
        )

        assertEquals(
            SignUpUiState.Error(
                message = "Failed",
                messageRes = R.string.fail_save_user
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should handle user save failure without message`() {
        stubAuthSuccess()
        stubSaveFailure(
            RuntimeException()
        )

        viewModel.signUp(
            fullName = FULL_NAME,
            email = EMAIL,
            password = PASSWORD
        )

        assertEquals(
            SignUpUiState.Error(
                message = null,
                messageRes = R.string.fail_save_user
            ),
            viewModel.uiState.value
        )
    }

    @Test
    fun `should reset state`() {
        stubAuthFailure(
            RuntimeException("Failed")
        )

        viewModel.signUp(
            fullName = FULL_NAME,
            email = EMAIL,
            password = PASSWORD
        )

        viewModel.resetState()

        assertEquals(
            SignUpUiState.Idle,
            viewModel.uiState.value
        )
    }

}
