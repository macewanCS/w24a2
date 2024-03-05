package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [login.newInstance] factory method to
 * create an instance of this fragment.
 */
class login : Fragment() {
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

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        val usernameInput: EditText = view.findViewById(R.id.EmailAddress)
        val passwordInput: EditText = view.findViewById(R.id.Password)

        //init the login button
        initLoginBtn(view, usernameInput, passwordInput)

        //init the clickable sign up text
        initSignUpText(view)


        return view
    }

    private fun initSignUpText(view: View) {
        //make register text clickable here
        val register: TextView = view.findViewById(R.id.register)
        register.setOnClickListener{
            Navigation.findNavController(view).navigate(R.id.to_signup)
        }
    }

    private fun initLoginBtn(view: View, usernameInput: EditText, passwordInput: EditText) {
        //get a reference to the login button
        val loginBtn: Button = view.findViewById(R.id.login_btn)

        //initialize the login button
        loginBtn.setOnClickListener{
            val auth = FirebaseAuth.getInstance()

            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()



            if (username.isNotEmpty() && password.isNotEmpty()) {
                Log.i("TAG", "$username: $password")
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            Toast.makeText(requireContext(), "Sign in successful", Toast.LENGTH_SHORT).show()
                            //--------------
                            CoroutineScope(Dispatchers.Main).launch {
                                val currentUser = FirebaseAuth.getInstance().currentUser
                                val userID = currentUser?.uid
                                // disable button to ensure students cant create sessions

                                // check if the user is a student or tutor
                                if (userID != null) {
                                    viewLifecycleOwner.lifecycleScope.launch {
                                        try {
                                            val isTutor = checkIfUserIsTutor(userID)
                                            if (isTutor) {
                                                Navigation.findNavController(view).navigate(R.id.account)
                                            }
                                            else{
                                                Navigation.findNavController(view).navigate(R.id.student_profile)
                                            }

                                        } catch (e: Exception) {
                                            CreateSessionHelper.showMessage(
                                                requireContext(),
                                                "Error reading user data. Please try again."
                                            )
                                        }
                                    }
                                } else {
                                    CreateSessionHelper.showMessage(
                                        requireContext(),
                                        "Invalid UserID. Please log in."
                                    )
                                }
                            }


                            // -------------

                        } else {
                            Toast.makeText(requireContext(), "Invalid email or password", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "Username or password left blank")
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
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment login.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            login().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}