package com.example.myapplication

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.CreateSessionHelper.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class TutoringSessionAdapter(private val tutoringSessions: List<TutoringSession>) : RecyclerView.Adapter<TutoringSessionAdapter.SessionViewHolder>() {
        inner class SessionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val sessionDetails: TextView = itemView.findViewById(R.id.sessionDetails)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SessionViewHolder {
            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.session_item, parent, false)
            return SessionViewHolder(view)
        }

        override fun onBindViewHolder(holder: SessionViewHolder, position: Int) {
            val tutoringSession = tutoringSessions[position]

            // create a string from all the session details from the database
            val detailsString = "Name: ${tutoringSession.tutorName}\nDate: ${tutoringSession.date}\n" + "Time: ${tutoringSession.time}\nSubjects: ${tutoringSession.subjects}\nGrades: ${tutoringSession.grades}\nNumber of Students: ${tutoringSession.maxParticipants}"

            // Set the formatted details string to the TextView
            holder.sessionDetails.text = detailsString

            // set click listener for the "Book session" button
            val sessionButton = holder.itemView.findViewById<Button>(R.id.bookSession)

            // Disable the "Book session" button and update the text if maxParticipants is zero
            if (tutoringSession.maxParticipants == 0) {
                holder.sessionDetails.text = holder.sessionDetails.text.toString().replace("Number of Students: ${tutoringSession.maxParticipants}", "Class Full")
                sessionButton.isEnabled = false
            } else {
                sessionButton.isEnabled = true
            }

            sessionButton.setOnClickListener {
                if (tutoringSession.maxParticipants > 0) {
                    val newMaxStudents = tutoringSession.maxParticipants - 1
                    Log.d("TutoringSessionAdapter", "Updated maxParticipants field")

                    // add the ID of the student to the registered students list
                    val existingRegisteredStudents = tutoringSession.registeredStudents
                    val updatedRegisteredStudents = existingRegisteredStudents.toMutableList().apply {
                        val studentID = FirebaseAuth.getInstance().currentUser?.uid.toString()
                        add(studentID)
                    }

                    // Update the maxParticipants and registered students in the 'sessions' node
                    updateRegisteredStudents(tutoringSession.sessionID, newMaxStudents, updatedRegisteredStudents)
                    showMessage(holder.itemView.context, "Successfully Registered for Class")
                } else {
                    showMessage(holder.itemView.context, "Class is full")
                }
            }
        }

        private fun updateRegisteredStudents(sessionID: String, newValue: Int, updatedRegisteredStudents: List<String>) {
            //update the maxParticipants node in the current session
            val sessionRef = FirebaseDatabase.getInstance().getReference("sessions").child(sessionID.toString())
            sessionRef.child("maxParticipants").setValue(newValue)
            sessionRef.child("registeredStudents").setValue(updatedRegisteredStudents)
        }

        override fun getItemCount(): Int {
            return tutoringSessions.size
        }
    }