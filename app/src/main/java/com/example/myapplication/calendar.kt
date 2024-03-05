package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class calendar : Fragment() {
    private lateinit var view: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var calendarView: CalendarView
    private val tutoringSessions: MutableList<TutoringSession> = mutableListOf()
    private val tutoringSessionAdapter = TutoringSessionAdapter(tutoringSessions)
    //private lateinit var bookBtn: Button

    //private val clickable = view.findViewById<Button>(R.id.bookSession) //Button to book a session, needs to be initialized

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false)

        TutorCheck(view)
        // init UI components
        initUIComponents()

        // start the calendar functionality
        initCalendarView()

        return view
    }

    private fun TutorCheck(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid

        // check if the user is a student or tutor
        if (userID != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val isTutor = checkIfUserIsTutor(userID)
                    if (isTutor) {
                        // start the bottom navigation bar functionality
                        navBarNavigation(view, findNavController())
                    }

                    else {
                        // start the bottom navigation bar functionality
                        navBarNavigationStudents(view, findNavController())
                    }

                } catch (e: Exception) {
                    CreateSessionHelper.showMessage(
                        requireContext(),
                        "Error reading user data. Please try again."
                    )
                }
            }
        }
    }
            private suspend fun checkIfUserIsTutor(userID: String): Boolean {
                return withContext(Dispatchers.IO) {
                    val userRef = FirebaseDatabase.getInstance().getReference("users").child(userID)
                    val snapshot = userRef.get().await()

                    if (snapshot.exists()) {
                        return@withContext snapshot.child("tutor").getValue(Boolean::class.java) ?: false
                    } else {
                        throw Exception("User data not found.")
                    }
                }
            }

    private fun initUIComponents() {
        recyclerView = view.findViewById(R.id.calendarRecyclerView)
        calendarView = view.findViewById(R.id.calendarView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = tutoringSessionAdapter
    }

    private fun initCalendarView() {
        // set up calendar to listen when a new date is clicked
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            // Fetch tutoring sessions for the selected date from Firebase
            fetchTutoringSessions(year, month + 1, dayOfMonth)
        }
    }

    private fun fetchTutoringSessions(year: Int, month: Int, dayOfMonth: Int) {
        // clear any existing sessions to prevent duplicates
        tutoringSessions.clear()
        tutoringSessionAdapter.notifyDataSetChanged()

        // format date to match format in database
        val dateString = String.format("%04d-%02d-%02d", year, month, dayOfMonth)

        // get a reference to the sessions node
        val rootSessionsRef = FirebaseDatabase.getInstance().reference.child("sessions")
        rootSessionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (sessionSnapshot in snapshot.children) {
                    // Check the date within each session
                    val session = sessionSnapshot.getValue(TutoringSession::class.java)
                    if (session?.date == dateString) {
                        // If the date matches, add the session to the list
                        tutoringSessions.add(session)
                        // Log the added session
                        Log.d("fetchTutoringSessions", "Added session: $session")
                    }
                }
                // Log the final list of sessions
                Log.d("fetchTutoringSessions", "Final sessions list: $tutoringSessions")
                tutoringSessionAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Log the error
                Log.e("fetchTutoringSessions", "Firebase Database Error: $error")
            }
        })
    }
}