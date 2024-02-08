package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [signup.newInstance] factory method to
 * create an instance of this fragment.
 */
class signup : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        val register_button: Button = view.findViewById(R.id.register_btn)

        val firstNameInput: EditText = view.findViewById(R.id.FirstName)
        val lastNameInput: EditText = view.findViewById(R.id.LastName)
        val emailInput: EditText = view.findViewById(R.id.EmailAddress)
        val passwordInput: EditText = view.findViewById(R.id.Password)

        register_button.setOnClickListener{
            var firstName = firstNameInput.text.toString()
            var lastName = lastNameInput.text.toString()
            var email = emailInput.text.toString()
            var password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
                Log.i("TAG", "$email: $password $firstName $lastName")

                val db = FirebaseDatabase.getInstance("https://tutor-application-410b6-default-rtdb.firebaseio.com/")
                val users = db.getReference("users")

                users.updateChildren(mapOf("users" to HashMap<String, Any>())).addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        val user = User(firstName, lastName, email, password)
                        val newUserRef = users.push()

                        newUserRef.setValue(user).addOnSuccessListener {
                            val firebaseAuth = FirebaseAuth.getInstance()
                            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener{task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(requireContext(), "Successfully registered!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(requireContext(), "Signup Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                            firstName = ""
                            lastName = ""
                            email = ""
                            password = ""
                            Navigation.findNavController(view).navigate(R.id.to_login)
                        }.addOnFailureListener{
                            Toast.makeText(requireContext(), "Registration Failed", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("TAG", "Failed to create 'users' path", task.exception)
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "One or more fields left blank")
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment signup.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            signup().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}