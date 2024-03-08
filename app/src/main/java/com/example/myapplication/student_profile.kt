package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [student_profile.newInstance] factory method to
 * create an instance of this fragment.
 */
class student_profile : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var profilePic: ImageView
    private lateinit var nameTextField: TextView
    private lateinit var logoutBtn: Button
    private lateinit var searchUserButton: Button
    private var fullName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_student_profile, container, false)

        // initialize the pages components
        profilePic = view.findViewById(R.id.profilepic)
        nameTextField = view.findViewById(R.id.nameField)
        searchUserButton = view.findViewById(R.id.SearchTutor)
        logoutBtn = view.findViewById(R.id.logout)

        // start the bottom navigation bar functionality
        navBarNavigationStudents(view, findNavController())
        // change the name field to the users first and last name
        fetchAndUpdateFullName()
        // setup the profile picture
        profilePictureSetup()

        // Search User page functionality
        searchUserSetup(view)

        // functionality for the logout button
        initLogoutButton(view)

        return view
    }

    private fun initLogoutButton(view: View) {
        logoutBtn.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_login)
        }
    }
    private fun searchUserSetup(view: View) {
        searchUserButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_search_user)
        }
    }


    private fun fetchAndUpdateFullName() {
        lifecycleScope.launch {
            try {
                val fullName = withContext(Dispatchers.IO) {
                    CreateSessionHelper.fetchUsername(requireContext())
                }

                nameTextField.text = "$fullName" // update UI here
                this@student_profile.fullName =
                    fullName.toString() // set the global variable for use later
            } catch (e: Exception) {
                Log.e("TAG", "Error fetching username")
            }
        }
    }

    private fun profilePictureSetup() {
        profilePic.setImageResource(R.drawable.student_pfp)
    }






    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment student_profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            student_profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
