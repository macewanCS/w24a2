package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

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
        }

        override fun getItemCount(): Int {
            return tutoringSessions.size
        }
    }