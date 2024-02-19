package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.myapplication.AccountHelper.checkIsTutorAndCreateNewSession
import com.example.myapplication.AccountHelper.showMessage
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class account : Fragment() {

    private lateinit var profilePic: ImageView
    private lateinit var nameTextField: TextView
    private lateinit var createSessionBtn: Button
    private var fullName: String? = null

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

        // start the bottom navigation bar functionality
        navBarNavigation(view, findNavController())
        // change the name field to the users first and last name
        fetchAndUpdateFullName()
        // setup the profile picture
        profilePictureSetup()
        // start functionality for create session button
        createSession()

        return view
    }

    private fun fetchAndUpdateFullName() {
        lifecycleScope.launch {
            try {
                val fullName = withContext(Dispatchers.IO) {
                    AccountHelper.fetchUsername(requireContext())
                }

                nameTextField.text = "$fullName" // update UI here
                this@account.fullName = fullName.toString() // set the global variable for use later
            } catch (e: Exception) {
                Log.e("TAG", "Error fetching username")
            }
        }
    }
    private fun profilePictureSetup() {
        profilePic.setImageResource(R.drawable.pfp)
    }
    private fun createSession() {
        createSessionBtn.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userID = currentUser?.uid

            if (userID != null) {
                createSessionBtn.isEnabled = false
                lifecycleScope.launch {
                    checkIsTutorAndCreateNewSession(lifecycleScope, requireContext(), userID, fullName) {
                        // callback to re-enable the button
                        createSessionBtn.isEnabled = true}
                }
            } else {
                showMessage(requireContext(), "invalid userID. Please log in.")
            }
        }
    }

}