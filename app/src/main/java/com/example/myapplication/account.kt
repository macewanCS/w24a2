package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.myapplication.CreateSessionHelper.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class account : Fragment() {

    private lateinit var profilePic: ImageView
    private lateinit var nameTextField: TextView
    private lateinit var createSessionBtn: Button
    private lateinit var logoutBtn: Button
    private lateinit var personalInfoBtn: Button
    private var fullName: String? = null

    private lateinit var editFullName: EditText
    private lateinit var viewFullName: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        // initialize the pages components
        profilePic = view.findViewById(R.id.profilepic)
        nameTextField = view.findViewById(R.id.nameField)
        createSessionBtn = view.findViewById(R.id.createSession)
        logoutBtn = view.findViewById(R.id.logout)
        personalInfoBtn = view.findViewById(R.id.tutor_personalInformation)

        //editFullName = view.findViewById(R.id.tutorEditFullName)
        //viewFullName = view.findViewById(R.id.tutorFullName)

        // start the bottom navigation bar functionality
        navBarNavigation(view, findNavController())
        // change the name field to the users first and last name
        fetchAndUpdateFullName()
        // setup the profile picture
        profilePictureSetup()
        // functionality for create session button
        initCreateSessionButton(view)
        // functionality for the logout button
        initLogoutButton(view)
        // functionality for the Personal Information button
        personalInformationButtonSetup(view)

        return view
    }
        private fun initLogoutButton(view: View) {
            logoutBtn.setOnClickListener {
                Navigation.findNavController(view).navigate(R.id.to_login)
            }

    }

    private fun personalInformationButtonSetup(view: View) {
        personalInfoBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_tutorPersonalInformation)
        }
    }

    private fun initCreateSessionButton(view: View) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid
        // disable button to ensure students cant create sessions
        createSessionBtn.isEnabled = false
        // check if the user is a student or tutor
        if (userID != null) {
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val isTutor = checkIfUserIsTutor(userID)
                    if (isTutor) {
                        // if the user a tutor enable the create a session button
                        createSessionBtn.isEnabled = true
                    }

                } catch (e: Exception) {
                    showMessage(requireContext(), "Error reading user data. Please try again.")
                }
            }
        } else {
            showMessage(requireContext(), "Invalid UserID. Please log in.")
        }

        createSessionBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_createSession)
        }
    }
    internal suspend fun checkIfUserIsTutor(userID: String): Boolean {
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
    internal fun fetchAndUpdateFullName() {
        lifecycleScope.launch {
            try {
                val fullName = withContext(Dispatchers.IO) {
                    CreateSessionHelper.fetchUsername(requireContext())
                }

                nameTextField.text = "$fullName" // update UI here
                this@account.fullName = fullName.toString() // set the global variable for use later
            } catch (e: Exception) {
                Log.e("TAG", "Error fetching username")
            }
        }
    }
    internal fun profilePictureSetup() {
        profilePic.setImageResource(R.drawable.pfp)
    }


}