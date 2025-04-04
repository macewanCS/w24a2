package com.example.myapplication

import android.content.Context
import android.util.Log
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object CreateSessionHelper {
    private const val usersNode = "users"
    private const val sessionsNode = "sessions"
    private const val toastDuration = Toast.LENGTH_SHORT

    suspend fun fetchUsername(context: Context): Any {
        return try {
            val userID = FirebaseAuth.getInstance().currentUser?.uid
            val dbRef = userID?.let {
                FirebaseDatabase.getInstance().getReference(usersNode).child(userID)
            }

            val snapshot = dbRef?.get()?.await()
            if (snapshot != null && snapshot.exists()) {
                //retrieve users first and last name and combine
                val firstName = snapshot.child("firstName").getValue(String::class.java)
                val lastName = snapshot.child("lastName").getValue(String::class.java)
                //combine the first and last name into the full name
                return String.format("%s %s", firstName, lastName)
            } else {
                showMessage(context, "Error fetching username.")
            }
        } catch (e: Exception) {
            Log.e("fetchUsername", "Error reading data", e)
            showMessage(context, "Error fetching username.")
        }
    }
    fun showMessage(context: Context, message: String) {
        //display the message given
        Toast.makeText(context, message, toastDuration).show()
    }
    private suspend fun isTutor(context: Context): Boolean {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userID = currentUser?.uid

            if (userID != null) {
                val dbRef = FirebaseDatabase.getInstance().getReference(usersNode).child(userID)
                val snapshot = dbRef.get().await()

                val isTutor = snapshot.child("tutor").getValue(Boolean::class.java) ?: false
                isTutor
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e("isTutor", "Error reading data", e)
            showMessage(context, "Error checking tutor status. Please try again.")
            false
        }
    }
    private suspend fun createNewSessionAsync(
        scope: LifecycleCoroutineScope,
        context: Context,
        userID: String,
        fullName: String?,
        subjects: String,
        grades: String,
        date: String,
        time: String,
        maxParticipants: Int
    ) {
        scope.launch {
            try {
                val isExisting = checkForExistingSessionsAsync(context, userID, date, time)
                if (isExisting) {
                    showMessage(
                        context,
                        "Creating session failed. Tutoring session already booked at that time."
                    )
                } else {
                    createNewSession(
                        context,
                        userID,
                        fullName,
                        subjects,
                        grades,
                        date,
                        time,
                        maxParticipants
                    )
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error creating new session", e)
                showMessage(context, "Error creating session")
            }
        }
    }
    private suspend fun checkExistingSession(
        context: Context,
        userID: String,
        date: String,
        time: String
    ): Boolean {
        return suspendCoroutine { continuation ->
            Log.d(
                "checkExistingSession",
                "Checking existing sessions for user $userID, date $date, and time $time"
            )
            val sessionsRef = FirebaseDatabase.getInstance().getReference(usersNode).child(userID)
                .child(sessionsNode)

            sessionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d(
                        "checkExistingSession",
                        "onDataChange: ${snapshot.childrenCount} sessions found"
                    )

                    // using a flag to track if a matching session is found or not
                    var matchingSessionFound = false

                    // check if there are even any sessions
                    if (snapshot.childrenCount.toInt() == 0) {
                        Log.d("checkExistingSession", "No sessions found")
                        continuation.resume(false)
                    }

                    /* because the sessionsIDs in the user nodes are for some reason stored as hashmaps,
                     use the mapNotNull function to create a list of strings of the session ids */
                    val sessionIDs = snapshot.children.mapNotNull { sessionSnapshot ->
                        sessionSnapshot.getValue(String::class.java)
                    }

                    sessionIDs.forEach { sessionID ->
                        // call checkSessionDetails to ensure the details are not the same, if they are return true
                        checkSessionDetails(context, sessionID, date, time) { exists ->
                            Log.d("checkExistingSession", "Session ID: $sessionID, Exists: $exists")
                            if (exists) {
                                matchingSessionFound = true
                            }

                            // check if this is the last iteration and no matching session found, invoke callback
                            if (sessionID == sessionIDs.last()) {
                                if (matchingSessionFound) {
                                    Log.d("checkExistingSession", "Matching session found")
                                } else {
                                    Log.d("checkExistingSession", "No matching sessions found")
                                }
                                continuation.resume(matchingSessionFound)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        "checkExistingSession",
                        "Error checking existing sessions",
                        error.toException()
                    )
                    continuation.resume(false)

                    // inform the user there was an error checking their existing sessions
                    showMessage(context, "Error checking existing sessions. Please try again.")
                }
            })
        }
    }
    private suspend fun checkForExistingSessionsAsync(
        context: Context,
        userID: String,
        date: String,
        time: String
    ): Boolean {
        return try {
            checkExistingSession(context, userID, date, time)
        } catch (e: Exception) {
            Log.e("checkForExistingSessionsAsync", "Error checking existing sessions", e)
            false
        }
    }
    private fun updateUsersSessions(context: Context, userID: String, sessionID: String?) {
        if (sessionID != null) {
            val userRef = FirebaseDatabase.getInstance().getReference(usersNode).child(userID)
            val sessionsRef = userRef.child("sessions")

            //get current session count to determine session key
            sessionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val sessionCount = snapshot.childrenCount.toInt() + 1
                    val sessionKey = "session$sessionCount"

                    //update node with sessionID and key
                    sessionsRef.child(sessionKey).setValue(sessionID)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("updateUsersSessions", "Error getting session count", error.toException())

                    //inform the user there was an error updating their current sessions
                    showMessage(context, "Error updating current sessions. Please try again.")
                }
            })
        }
    }
    private fun createNewSession(
        context: Context,
        userID: String,
        fullName: String?,
        subjects: String,
        grades: String,
        date: String,
        time: String,
        maxParticipants: Int
    ) {
        val sessionsRef = FirebaseDatabase.getInstance().getReference("sessions")
        val newSessionRef = sessionsRef.push()
        val newRegisteredStudents: List<String> = listOf()

        //set values based on page to add a new session
        val sessionDetails = mapOf(
            "tutorName" to fullName,
            "tutorID" to userID,
            "subjects" to subjects,
            "grades" to grades,
            "date" to date,
            "time" to time,
            "maxParticipants" to maxParticipants,
            "sessionID" to newSessionRef.key,
            "registeredStudents" to newRegisteredStudents
        )

        newSessionRef.setValue(sessionDetails)
            .addOnSuccessListener {
                //update the users account with the session ID
                updateUsersSessions(context, userID, newSessionRef.key)
                showMessage(context, "Session created successfully!")
            }.addOnFailureListener {
                Log.e("createSession", "Error creating session", it)
                showMessage(context, "Error creating session")
            }
    }

    private fun isUserTutorAsync(context: Context): Boolean = runBlocking {
        return@runBlocking try {
            withContext(Dispatchers.IO) {
                isTutor(context)
            }
        } catch (e: Exception) {
            Log.e("fetchAndUpdateFullName", "Error checking tutor status", e)
            false
        }
    }
    suspend fun checkIsTutorAndCreateNewSession(
        scope: LifecycleCoroutineScope,
        context: Context,
        userID: String,
        fullName: String?,
        subjects: String,
        grades: String,
        date: String,
        time: String,
        maxParticipants: Int,
        onComplete: () -> Unit
    ) {
        scope.launch {
            try {
                val isTutor = isUserTutorAsync(context)
                if (isTutor) {
                    if (fullName != null) {
                        createNewSessionAsync(
                            scope,
                            context,
                            userID,
                            fullName,
                            subjects,
                            grades,
                            date,
                            time,
                            maxParticipants
                        )
                    } else {
                        showMessage(context, "Unable to fetch profile information.")
                    }
                } else {
                    showMessage(context, "Permission Denied. Only tutors can create sessions.")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Error checking tutor and creating new session", e)
            } finally {
                onComplete.invoke()
            }
        }
    }
    fun checkSessionDetails(
        context: Context,
        sessionID: String,
        date: String,
        time: String,
        callback: (Boolean) -> Unit
    ) {
        val sessionsRef = FirebaseDatabase.getInstance().getReference(sessionsNode).child(sessionID)

        sessionsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(sessionSnapshot: DataSnapshot) {
                val sessionDate = sessionSnapshot.child("date").getValue(String::class.java)
                val sessionTime = sessionSnapshot.child("time").getValue(String::class.java)

                //check if the session details match
                val exists = (sessionDate == date && sessionTime == time)
                callback.invoke(exists)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("checkSessionDetails", "Error checking session details", error.toException())
                callback.invoke(true)

                // inform the user there was an error checking their session details
                showMessage(context, "Error checking session details. Please try again.")
            }
        })
    }
    fun getSelectedDate(datePicker: DatePicker): String? {
        val selectedYear = datePicker.year
        val selectedMonth = datePicker.month + 1 // months start at 0 for some reason
        val selectedDay = datePicker.dayOfMonth
        if (isDateBeforeCurrent(selectedYear, selectedMonth, selectedDay)) {
            return null
        }
        return "$selectedYear-${selectedMonth.toString().padStart(2, '0')}-${
            selectedDay.toString().padStart(2, '0')
        }"
    }
    private fun isDateBeforeCurrent(year: Int, month: Int, day: Int): Boolean {
        val currentDate = getCurrentDate()
        val selectedDate =
            "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
        return selectedDate < currentDate
    }
    private fun getCurrentDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1 // Months are zero-based
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return "$year-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}"
    }
    fun getNumberOfParticipants(maxParticipantsEditText: EditText): Int {
        val maxParticipantsText = maxParticipantsEditText.text.toString().trim()
        // check the value of the participants
        Log.d("getNumberOfParticipants", "Max Participants: ${maxParticipantsText.toIntOrNull()}")
        return maxParticipantsText.toIntOrNull() ?: 0
    }
}