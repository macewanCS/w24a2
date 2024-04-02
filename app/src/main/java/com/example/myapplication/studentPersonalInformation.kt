package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [studentPersonalInformation.newInstance] factory method to
 * create an instance of this fragment.
 */
class studentPersonalInformation : Fragment() {
    private lateinit var backButton: ImageButton
    private lateinit var profilePic1: ImageView

    private lateinit var textFullName1: TextView
    private lateinit var editFullName1: EditText

    private lateinit var confirmButton1: Button


    private lateinit var textPhoneNumber1: TextView
    private lateinit var editPhoneNumber1: EditText

    private lateinit var textEmailAddress: TextView
    private lateinit var editEmailAddress: EditText

    private lateinit var textPassword: TextView
    private lateinit var editPassword: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_student_personal_information, container, false)
        backButton = view.findViewById(R.id.account_back_btn)

        profilePic1 = view.findViewById(R.id.profilepic1)

        editFullName1 = view.findViewById(R.id.studentEditFullName)
        textFullName1 = view.findViewById(R.id.studentFullName)

        textPhoneNumber1 = view.findViewById(R.id.studentPhoneNumber)
        editPhoneNumber1 = view.findViewById(R.id.studentEditPhoneNumber)

        confirmButton1 = view.findViewById(R.id.studentPersonalInformationBtn)

        textEmailAddress = view.findViewById(R.id.studentEmail)
        editEmailAddress = view.findViewById(R.id.studentEditEmailAddress)

        textPassword = view.findViewById(R.id.studentPassword)
        editPassword = view.findViewById(R.id.studentEditTextPassword)

        editEmailAddress.hint = "email address"
        editPassword.hint = "*********"

        // Show backButton and functionality
        backButtonSetup1(view)

        //showPersonalInformation()
        updateFullName1()

        cannotEditEmailAddress()
        cannotEditPassword()

        return view
    }
    private fun updateFullName1(){
        confirmButton1.setOnClickListener {

            textFullName1.text = editFullName1.text.toString()
            textPhoneNumber1.text = editPhoneNumber1.text.toString()
        }

    }

    private fun cannotEditEmailAddress(){
        editEmailAddress.setOnClickListener {
            editEmailAddress.isEnabled = false
            val message = "Cannot change Email Address"
            textEmailAddress.setTextColor(Color.RED)
            textEmailAddress.text = message
        }
    }

    private fun cannotEditPassword() {
        editPassword.setOnClickListener {
            editPassword.isEnabled = false
            val message = "Cannot change Password"
            textPassword.setTextColor(Color.RED)
            textPassword.text = message
        }
    }

    private fun backButtonSetup1(view: View) {
        backButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_student_profile)
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment studentPersonalInformation.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            studentPersonalInformation().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}