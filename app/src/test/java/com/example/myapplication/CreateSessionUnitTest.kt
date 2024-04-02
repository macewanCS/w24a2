package com.example.myapplication

import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import org.junit.Assert.assertEquals
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering.Context
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class CreateSessionUnitTest {

    @Mock
    private lateinit var createSession: CreateSession

    @Before
    fun setup(){
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testInitCancelButton(){
        val cancelButton = mock(Button::class.java)
        `when`(cancelButton.setOnClickListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as View.OnClickListener
            listener.onClick(mock(View::class.java))
        }

        createSession.initCancelButton()
        verify(cancelButton).setOnClickListener(any())
    }

    @Test
    fun testCreateSession(){
        val view = mock(View::class.java)
        val createSessionButton = mock(Button::class.java)
        val userID = "userID"

        `when`(createSessionButton.setOnClickListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as View.OnClickListener
            listener.onClick(mock(View::class.java))
        }
        `when`(FirebaseAuth.getInstance().currentUser).thenReturn(mock(FirebaseUser::class.java).apply {
            `when`(uid).thenReturn(userID)
        })

        createSession.createSession(view)

        verify(createSessionButton).setOnClickListener(any())
    }

    @Test
    fun testSetUIComponents(){
        val startTimeButton = mock(Button::class.java)
        val endTimeButton = mock(Button::class.java)
        val createSessionButton = mock(Button::class.java)
        val cancelButton = mock(Button::class.java)
        val datePicker = mock(DatePicker::class.java)
        val chipGroup = mock(ChipGroup::class.java)
        val maxParticipantsEditText = mock(EditText::class.java)

        `when`(createSession.view?.findViewById<Button>(R.id.selectStartTimeButton)).thenReturn(startTimeButton)
        `when`(createSession.view?.findViewById<Button>(R.id.selectEndTimeButton)).thenReturn(endTimeButton)
        `when`(createSession.view?.findViewById<Button>(R.id.createSessionButton)).thenReturn(createSessionButton)
        `when`(createSession.view?.findViewById<Button>(R.id.cancelButton)).thenReturn(cancelButton)
        `when`(createSession.view?.findViewById<DatePicker>(R.id.datePicker)).thenReturn(datePicker)
        `when`(createSession.view?.findViewById<ChipGroup>(R.id.selectedSubjectsChipGroup)).thenReturn(chipGroup)
        `when`(createSession.view?.findViewById<EditText>(R.id.participantsCountEditText)).thenReturn(maxParticipantsEditText)

        createSession.setUIComponents()

        verify(createSession.view)?.findViewById<Button>(R.id.selectStartTimeButton)
        verify(createSession.view)?.findViewById<Button>(R.id.selectEndTimeButton)
        verify(createSession.view)?.findViewById<Button>(R.id.createSessionButton)
        verify(createSession.view)?.findViewById<Button>(R.id.cancelButton)
        verify(createSession.view)?.findViewById<DatePicker>(R.id.datePicker)
        verify(createSession.view)?.findViewById<ChipGroup>(R.id.selectedSubjectsChipGroup)
        verify(createSession.view)?.findViewById<EditText>(R.id.participantsCountEditText)
    }

    @Test
    fun testFetchFullName(){
        val fullName = "John Doe"
        val scope = mock(CoroutineScope::class.java)
        `when`(createSession.viewLifecycleOwner.lifecycleScope).thenReturn(scope as LifecycleCoroutineScope?)

        runBlocking {
            createSession.fetchFullName()
        }

        assertEquals(fullName, createSession.fullName)
    }

    @Test
    fun testInitTimeSelection(){
        val endTimeButton = mock(Button::class.java)
        val startTimeButton = mock(Button::class.java)
        `when`(createSession.view?.findViewById<Button>(R.id.selectStartTimeButton)).thenReturn(startTimeButton)
        `when`(createSession.view?.findViewById<Button>(R.id.selectEndTimeButton)).thenReturn(endTimeButton)
        `when`(startTimeButton.setOnClickListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as View.OnClickListener
            listener.onClick(mock(View::class.java))
        }
        `when`(endTimeButton.setOnClickListener(any())).thenAnswer { invocation ->
            val listener = invocation.arguments[0] as View.OnClickListener
            listener.onClick(mock(View::class.java))
        }

        createSession.initTimeSelection()

        verify(startTimeButton).setOnClickListener(any())
        verify(endTimeButton).setOnClickListener(any())
    }

    @Test
    fun testGetSelectedSubjects(){
        val chipGroup = mock(ChipGroup::class.java)
        val chip1 = mock(Chip::class.java)
        val chip2 = mock(Chip::class.java)
        `when`(createSession.view?.findViewById<ChipGroup>(R.id.selectedSubjectsChipGroup)).thenReturn(chipGroup)
        `when`(chipGroup.checkedChipIds).thenReturn(setOf(chip1.id, chip2.id).toMutableList())
        `when`(chipGroup.findViewById<Chip>(chip1.id)).thenReturn(chip1)
        `when`(chipGroup.findViewById<Chip>(chip2.id)).thenReturn(chip2)
        `when`(chip1.text).thenReturn("English")
        `when`(chip2.text).thenReturn("Mathematics")

        createSession.getSelectedSubjects()

        assertEquals("English, Mathematics", createSession.selectedSubjects)
    }

    @Test
    fun testGetSelectedGrades(){
        val chipGroup = mock(ChipGroup::class.java)
        val chip1 = mock(Chip::class.java)
        val chip2 = mock(Chip::class.java)
        `when`(createSession.view?.findViewById<ChipGroup>(R.id.selectedGradesChipGroup)).thenReturn(chipGroup)
        `when`(chipGroup.checkedChipIds).thenReturn(setOf(chip1.id, chip2.id).toMutableList())
        `when`(chipGroup.findViewById<Chip>(chip1.id)).thenReturn(chip1)
        `when`(chipGroup.findViewById<Chip>(chip2.id)).thenReturn(chip2)

        createSession.getSelectedGrades()

        assertEquals("1, 2", createSession.selectedGrades)
    }
}