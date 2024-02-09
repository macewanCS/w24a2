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
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.Navigation
import com.google.android.play.integrity.internal.m
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import org.mindrot.jbcrypt.BCrypt


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

        //get all the values of the elements on the login page
        val register_button: Button = view.findViewById(R.id.register_btn)
        val firstNameInput: EditText = view.findViewById(R.id.FirstName)
        val lastNameInput: EditText = view.findViewById(R.id.LastName)
        val emailInput: EditText = view.findViewById(R.id.EmailAddress)
        val passwordInput: EditText = view.findViewById(R.id.Password)

        register_button.setOnClickListener{
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            //register the user, both in database and firebase authentication
            registerUser(email, password, firstName, lastName)
            //clear the input fields
            clearInputFields(emailInput, passwordInput, firstNameInput, lastNameInput)
            //navigate back to the login page
            Navigation.findNavController(view).navigate(R.id.to_login)
        }
        return view
    }

    private fun encryptPassword(password: String): String {
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(password, salt)
    }

    private fun verifyPassword(plainPass: String, hashedPass: String): Boolean {
        return BCrypt.checkpw(plainPass, hashedPass)
    }

    private fun registerUser(email: String, password: String, firstName: String, lastName: String) {
        //validate the input fields to ensure they are not blank
        if (validateInputFields(email, password, firstName, lastName)) {
            createUser(email, password, firstName, lastName)
        } else {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            Log.i("TAG", "One or more fields left blank")
        }
    }

    private fun storeUserInDatabase(email: String, password: String, firstName: String, lastName: String) {
        //get the database info to store user
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if (userID != null) {
            //get the reference(pointer) to the database and the 'users' object within the database
            val usersRef = FirebaseDatabase.getInstance("https://tutor-application-410b6-default-rtdb.firebaseio.com/").getReference("users")
            //here get the reference to a new child using the userID of the current user that has been authenticated in the database
            val userRef = usersRef.child(userID)

            //created the hashed password to store
            val hashedPass = encryptPassword(password)

            //create a new user object
            val user = User(firstName, lastName, email, hashedPass)

            userRef.setValue(user).addOnSuccessListener {
                Toast.makeText(requireContext(), "Successfully Registered!", Toast.LENGTH_SHORT).show()
                Log.d("TAG", "User was added to the realtime database")
            }.addOnFailureListener{ databaseError ->
                val error = databaseError.message
                Toast.makeText(requireContext(), "Failed to store user data: $error", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "Failed to store user data in the database: $error", databaseError)
            }
        } else {
            Log.d("TAG", "UserID is null, $userID")
        }
    }

    private fun validateInputFields(email: String, password: String, firstName: String, lastName: String): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()){
            Log.d("TAG", "$email, $password, $firstName, $lastName")
            return true
        }
        return false
    }

    private fun createUser(email: String, password: String, firstName: String, lastName: String){
        val dbauth = FirebaseAuth.getInstance()
        dbauth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {task ->
            if (task.isSuccessful) {
                storeUserInDatabase(email, password, firstName, lastName)
                Log.d("TAG", "User successfully registered on firebase")
            } else {
                val error = task.exception?.message
                Toast.makeText(requireContext(), "Signup Failed: $error", Toast.LENGTH_SHORT).show()
                Log.e("TAG", "Signup failed: $error",task.exception)
            }
        }
    }

    private fun clearInputFields(email: EditText, password: EditText, firstName: EditText, lastName: EditText) {
        email.text.clear()
        password.text.clear()
        firstName.text.clear()
        lastName.text.clear()
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