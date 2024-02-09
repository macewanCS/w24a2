package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.Navigation
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.database
import org.w3c.dom.Text

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
        val login_button: Button = view.findViewById(R.id.login_btn)

        //initialize the login button
        login_button.setOnClickListener{
            val auth = FirebaseAuth.getInstance()

            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                Log.i("TAG", "$username: $password")
                auth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener{task ->
                        if(task.isSuccessful){
                            Toast.makeText(requireContext(), "Sign in successful", Toast.LENGTH_SHORT).show()
                            Navigation.findNavController(view).navigate(R.id.account_login)
                        } else {
                            Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
                        }
                    }

            } else {
                Toast.makeText(requireContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "Username or password left blank")
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