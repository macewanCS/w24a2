package com.example.myapplication

data class Student(val firstName: String, val lastName: String, val email: String, val password: String, val isTutor: Boolean? = null)

data class Tutor(val firstName: String? = null, val lastName: String? = null, val email: String? = null, val password: String? = null, val isTutor: Boolean? = null, val grades: String, val subjects: String, val phoneNumber: String, val preferredContactMethod: String, val preferredTimes: String)
data class TutoringSession(val date: String = "", val maxParticipants: Int = 0, val subjects: String = "", val time: String = "", val tutorID: String = "", val tutorName: String = "")