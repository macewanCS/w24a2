package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.example.myapplication.CreateSessionHelper.showMessage
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyBoolean
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class homePageUnitTest {

    @Mock
    private lateinit var mockFirebaseAuth: FirebaseAuth

    @Mock
    private lateinit var mockDatabaseReference: DatabaseReference

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Mock
    private lateinit var mockSnapshot: DataSnapshot

    private lateinit var fragment: homePage

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        val activity = Robolectric.buildActivity(FragmentActivity::class.java).get()
        fragment = homePage.newInstance("param1", "param2")

        `when`(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)
        `when`(mockFirebaseAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn("testUserID")
        `when`(FirebaseDatabase.getInstance().getReference("users").child(any())).thenReturn(
            mockDatabaseReference
        )

        activity.supportFragmentManager.beginTransaction().add(fragment, null).commit()
    }

    @Test
    fun testOnCreateView() {
        val inflater = mock(LayoutInflater::class.java)
        val container = mock(ViewGroup::class.java)
        val savedInstanceState = mock(Bundle::class.java)
        val view = mock(View::class.java)

        `when`(
            inflater.inflate(
                anyInt(),
                any(ViewGroup::class.java),
                anyBoolean()
            )
        ).thenReturn(view)

        val result = fragment.onCreateView(inflater, container, savedInstanceState)

        assertEquals(result, view)
        verify(fragment).TutorCheck(view)
    }

    @Test
    fun testTutorCheckIsTutor() {
        val view = mock(View::class.java)
        val task: Task<DataSnapshot> = mock(Task::class.java) as Task<DataSnapshot>
        `when`(mockDatabaseReference.get()).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(mockSnapshot)
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.child("tutor").getValue(Boolean::class.java)).thenReturn(true)

        fragment.TutorCheck(view)

        verify(fragment.findNavController()).navigate(R.id.tutor_nav)
    }

    @SuppressLint("CheckResult")
    @Test
    fun testTutorCheckDataNotFound() {
        val view = mock(View::class.java)
        val task: Task<DataSnapshot> = mock(Task::class.java) as Task<DataSnapshot>
        `when`(mockDatabaseReference.get()).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(false)

        fragment.TutorCheck(view)

        verify(showMessage(any(), eq("Error reading user data. Please try again")))
    }

    @Test
    fun testCheckIfUserIsTutor_IsTutor() {
        val userID = "testUserID"
        val task: Task<DataSnapshot> = mock(Task::class.java) as Task<DataSnapshot>
        `when`(mockDatabaseReference.get()).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(mockSnapshot)
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.child("tutor").getValue(Boolean::class.java)).thenReturn(true)

        runBlocking {
            val result = fragment.checkIfUserIsTutor(userID)
            assertTrue(result)
        }
    }

    @Test
    fun testCheckIfUserIsTutor_IsNotTutor() {
        val userID = "testUserID"
        val task: Task<DataSnapshot> = mock(Task::class.java) as Task<DataSnapshot>
        `when`(mockDatabaseReference.get()).thenReturn(task)
        `when`(task.isSuccessful).thenReturn(true)
        `when`(task.result).thenReturn(mockSnapshot)
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.child("tutor").getValue(Boolean::class.java)).thenReturn(false)

        runBlocking {
            val result = fragment.checkIfUserIsTutor(userID)
            assertFalse(result)
        }
    }
}