package com.example.myapplication

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering.Context
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class TutoringSessionAdapterUnitTest {

    private lateinit var adapter: TutoringSessionAdapter
    private lateinit var tutoringSessions: List<TutoringSession>

    @Before
    fun setup(){
        tutoringSessions = listOf(
            TutoringSession("2024-01-01", 1, "Math", "10:00", "1", "Tutor1", "1", "111", listOf()),
            TutoringSession("2024-02-02", 0, "Science", "11:00", "2", "Tutor2", "10", "111", listOf() ))

        adapter = TutoringSessionAdapter(tutoringSessions)
    }

    @Test
    fun testGetItemCount(){
        assertEquals(2, adapter.itemCount)
    }

    @Test
    fun testViewHolderBinding(){
        val parent = LayoutInflater.from(ApplicationProvider.getApplicationContext()).inflate(R.layout.session_item, null) as ViewGroup
        val viewType = 0
        val viewHolder = adapter.onCreateViewHolder(parent, viewType)
        adapter.onBindViewHolder(viewHolder, 0)

        val expectedDetailsString = "Name: Tutor 1\nDate: 2024-01-01\nTime: 10:00\nSubjects: Math\nGrades: 9\nNumber of Students: 1"
        assertEquals(expectedDetailsString, viewHolder.sessionDetails.text.toString())
    }

    @Test
    fun testButtonEnabled(){
        val parent = LayoutInflater.from(ApplicationProvider.getApplicationContext()).inflate(R.layout.session_item, null) as ViewGroup
        val viewType = 0
        val viewHolder = adapter.onCreateViewHolder(parent, viewType)
        adapter.onBindViewHolder(viewHolder, 0)

        val sessionButton = viewHolder.itemView.findViewById<Button>(R.id.bookSession)
        assertTrue(sessionButton.isEnabled)
    }

    @Test
    fun testButtonDisabled(){
        val parent = LayoutInflater.from(ApplicationProvider.getApplicationContext()).inflate(R.layout.session_item, null) as ViewGroup
        val viewType = 0
        val viewHolder = adapter.onCreateViewHolder(parent, viewType)

        val sessionButton = viewHolder.itemView.findViewById<Button>(R.id.bookSession)
        assertFalse(sessionButton.isEnabled)
    }
}