package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homePage.newInstance] factory method to
 * create an instance of this fragment.
 */
class homePage : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)
        TutorCheck(view)
        return view
    }


    internal fun TutorCheck(view: View) {
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



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}