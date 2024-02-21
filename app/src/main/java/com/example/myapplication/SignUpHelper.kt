package com.example.myapplication

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.navigation.Navigation
import com.example.myapplication.CreateSessionHelper.showMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.mindrot.jbcrypt.BCrypt

object SignUpHelper {
    fun encryptPassword(password: String): String {
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(password, salt)
    }

    fun verifyPassword(plainPass: String, hashedPass: String): Boolean {
        return BCrypt.checkpw(plainPass, hashedPass)
    }

    fun validateInputFields(
        email: String,
        password: String,
        firstName: String,
        lastName: String
    ): Boolean {
        if (email.isNotEmpty() && password.isNotEmpty() && firstName.isNotEmpty() && lastName.isNotEmpty()) {
            Log.d("TAG", "$email, $password, $firstName, $lastName")
            return true
        }
        return false
    }

    fun clearInputFields(
        email: EditText,
        password: EditText,
        firstName: EditText,
        lastName: EditText
    ) {
        email.text.clear()
        password.text.clear()
        firstName.text.clear()
        lastName.text.clear()
    }


    fun createUser(
        student: Student? = null,
        tutor: Tutor? = null,
        email: String? = null,
        password: String? = null,
        context: Context,
        view: View
    ) {
        val dbAuth = FirebaseAuth.getInstance()
        if (student != null && tutor == null && student.isTutor == false) {
            dbAuth.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeUserInDatabase(student, null, context, view)
                    Log.d("TAG", "User successfully registered on firebase")
                } else {
                    val error = task.exception?.message
                    showMessage(context, "Sign up failed.")
                    Log.e("TAG", "Signup failed: $error", task.exception)
                }
            }
        } else if (tutor != null && student == null && tutor.isTutor == true) {
            dbAuth.createUserWithEmailAndPassword(email!!, password!!).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storeUserInDatabase(null, tutor, context, view)
                    Log.d("TAG", "User successfully registered on firebase")
                } else {
                    val error = task.exception?.message
                    showMessage(context, "Sign up failed.")
                    Log.e("TAG", "Signup failed: $error", task.exception)
                }
            }
        } else {
            Log.d("createUser", "Could not parse if user is student or tutor")
        }
    }

    private fun storeUserInDatabase(
        student: Student? = null,
        tutor: Tutor? = null,
        context: Context,
        view: View
    ) {
        //get the database info to store user
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        if (userID != null) {
            //get the reference(pointer) to the database and the 'users' object within the database
            val usersRef = FirebaseDatabase.getInstance().getReference("users")
            //here get the reference to a new child using the userID of the current user that has been authenticated in the database
            val userRef = usersRef.child(userID)

            if (student != null && tutor == null && student.isTutor == false) {
                userRef.setValue(student).addOnSuccessListener {
                    showMessage(context, "Successfully registered!")
                    //navigate back to the login page
                    Navigation.findNavController(view).navigate(R.id.to_login)
                    Log.d("TAG", "User was added to the realtime database")
                }.addOnFailureListener { databaseError ->
                    val error = databaseError.message
                    showMessage(context, "Failed to store user data: $error")
                    Log.e("TAG", "Failed to store user data in the database: $error", databaseError)
                }
            } else if (tutor != null && student == null && tutor.isTutor == true) {
                userRef.setValue(tutor).addOnSuccessListener {
                    showMessage(context, "Successfully registered!")
                    //navigate back to the login page
                    Navigation.findNavController(view).navigate(R.id.to_login)
                    Log.d("TAG", "User was added to the realtime database")
                }.addOnFailureListener { databaseError ->
                    val error = databaseError.message
                    showMessage(context, "Failed to store user data: $error")
                    Log.e("TAG", "Failed to store user data in the database: $error", databaseError)
                }
            } else {
                Log.d("storeUserInDatabase", "Could not parse if user is student or tutor")
            }
        } else {
            Log.d("TAG", "UserID is null, $userID")
        }
    }
}