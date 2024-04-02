package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class calendarUnitTest {

    @Mock
    private lateinit var mockView: View

    @Mock
    private lateinit var mockInflater: LayoutInflater

    @Mock
    private lateinit var mockContainer: ViewGroup

    @Mock
    private lateinit var mockSavedInstanceState: Bundle

    @Mock
    private lateinit var mockRecyclerView: RecyclerView

    @Mock
    private lateinit var mockCalendarView: CalendarView

    @Mock
    private lateinit var mockRefreshSessionButton: ImageButton

    @Mock
    private lateinit var mockAuth: FirebaseAuth

    @Mock
    private lateinit var mockUser: FirebaseUser

    @Mock
    private lateinit var mockDatabase: FirebaseDatabase

    @Mock
    private lateinit var mockSnapshot: Task<DataSnapshot>

    @Mock
    private lateinit var mockSessionSnapshot: DataSnapshot

    @Mock
    private lateinit var mockSession: TutoringSession

    private lateinit var fragment: calendar

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
        fragment = calendar()
        `when`(FirebaseAuth.getInstance()).thenReturn(mockAuth)
        `when`(FirebaseDatabase.getInstance()).thenReturn(mockDatabase)
        `when`(mockView.findViewById<RecyclerView>(R.id.calendarRecyclerView)).thenReturn(mockRecyclerView)
        `when`(mockView.findViewById<CalendarView>(R.id.calendarView)).thenReturn(mockCalendarView)
        `when`(mockView.findViewById<ImageButton>(R.id.refreshSessionsButton)).thenReturn(mockRefreshSessionButton)
    }

    @Test
    fun testOnCreateView(){
        `when`(mockInflater.inflate(R.layout.fragment_calendar, mockContainer, false)).thenReturn(mockView)

        val result = fragment.onCreateView(mockInflater, mockContainer, mockSavedInstanceState)

        assertEquals(mockView, result)
    }

    @Test
    fun testTutorCheck(){
        val userID = "testUserID"
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn(userID)
        `when`(mockDatabase.getReference("users").child(userID)).thenReturn(mockDatabase.getReference("users").child(userID))
        `when`(mockDatabase.getReference("users").child(userID).get()).thenReturn(null)

        fragment.TutorCheck(mockView)

        Mockito.verify(mockAuth).currentUser
        Mockito.verify(mockDatabase).getReference("users").child(userID)
    }

    @Test
    fun testCheckIfUserIsTutor(){
        val userID = "testUserID"
        val expectedIsTutor = true
        `when`(mockAuth.currentUser).thenReturn(mockUser)
        `when`(mockUser.uid).thenReturn(userID)
        `when`(mockDatabase.getReference("users").child(userID)).thenReturn(mockDatabase.getReference("users").child(userID))
        `when`(mockDatabase.getReference("users").child(userID).get()).thenReturn(mockSnapshot)
        `when`(mockSnapshot.isSuccessful).thenReturn(true)
        `when`(mockSessionSnapshot.child("tutor").getValue(Boolean::class.java)).thenReturn(true)

        val isTutor = runBlocking {
            fragment.checkIfUserIsTutor(userID)
        }

        assertEquals(expectedIsTutor, isTutor)
    }

    @Test
    fun testInitRefreshSessionButton(){
        fragment.initRefreshSessionButton()

        Mockito.verify(mockRefreshSessionButton).setOnClickListener(Mockito.any())
    }

    @Test
    fun testInitUIComponents(){
        fragment.initUIComponents()

        Mockito.verify(mockRecyclerView).adapter = Mockito.any()
    }

    @Test
    fun testInitCalendarView(){
        fragment.initCalendarView()

        Mockito.verify(mockCalendarView).setOnDateChangeListener(Mockito.any())
    }

    @Test
    fun testFetchTutoringSessions(){
        val year = 2024
        val month = 4
        val dayOfMonth = 1
        val dateString = String.format("%04d-%02d-%02d", year, month, dayOfMonth)
        val sessionsRef = Mockito.mock(DatabaseReference::class.java)
        `when`(mockDatabase.reference.child("sessions")).thenReturn(sessionsRef)
        `when`(sessionsRef.addListenerForSingleValueEvent(Mockito.any())).thenAnswer {
            val listener = it.arguments[0] as ValueEventListener
            listener.onDataChange(mockSessionSnapshot)
            null
        }
        `when`(mockSessionSnapshot.getValue(TutoringSession::class.java)).thenReturn(mockSession)
        `when`(mockSession.date).thenReturn(dateString)

        fragment.fetchTutoringSessions(year, month, dayOfMonth)

        assertEquals(1, fragment.tutoringSessions.size)
        assertEquals(mockSession, fragment.tutoringSessions[0])
    }

}