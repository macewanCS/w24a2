package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.myapplication.CreateSessionHelper.showMessage
import com.example.myapplication.SignUpHelper.clearInputFields
import com.example.myapplication.SignUpHelper.createUser
import com.example.myapplication.SignUpHelper.encryptPassword
import com.example.myapplication.SignUpHelper.validateInputFields

class signup : Fragment() {
    private lateinit var view: View
    private lateinit var registerBtn: Button
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var isTutorBtn: CheckBox
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_signup, container, false)

        // initialize all components needed for the fragment
        initUIComponents()

        // init the text to go back to the login screen
        initLoginText()

        // init the register button
        initRegisterButton()

        return view
    }

    private fun initUIComponents() {
        //get all the values of the elements on the login page
        registerBtn = view.findViewById(R.id.register_btn)
        firstNameInput = view.findViewById(R.id.FirstName)
        lastNameInput = view.findViewById(R.id.LastName)
        emailInput = view.findViewById(R.id.EmailAddress)
        passwordInput = view.findViewById(R.id.Password)
        isTutorBtn = view.findViewById(R.id.tutorCheckBtn)
    }

    private fun initRegisterButton() {
        registerBtn.setOnClickListener {
            val firstName = firstNameInput.text.toString()
            val lastName = lastNameInput.text.toString()
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()
            val isTutor = isTutorBtn.isChecked
            if (validateInputFields(email, password, firstName, lastName)) {
                // create the hashed password to store
                val hashedPass = encryptPassword(password)
                if (isTutor) {
                    val bundle = Bundle().apply {
                        putString("firstName", firstName)
                        putString("lastName", firstName)
                        putString("email", email)
                        putString("password", password)
                        putString("encPassword", hashedPass)
                    }
                    // navigate to get additional information here
                    Navigation.findNavController(view).navigate(R.id.to_additional_info, bundle)
                } else {
                    // create a new student object
                    val student = Student(firstName, lastName, email, hashedPass, isTutor)
                    // register the user, both in database and firebase authentication
                    createUser(student, null, email, password, requireContext(), view)
                    // clear the input fields
                    clearInputFields(emailInput, passwordInput, firstNameInput, lastNameInput)
                }
            } else {
                showMessage(requireContext(), "Please fill in all fields.")
                return@setOnClickListener
            }
        }
    }

    private fun initLoginText() {
        // make login text clickable here
        val login = view.findViewById<TextView>(R.id.existingAccountLogin)
        login.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_login)
        }
    }
}