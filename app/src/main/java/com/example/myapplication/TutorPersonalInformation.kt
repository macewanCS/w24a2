/**
 * A simple [Fragment] subclass.
 * Use the [TutorPersonalInformation.newInstance] factory method to
 * create an instance of this fragment.
 */
package com.example.myapplication

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TutorPersonalInformation : Fragment() {
    private lateinit var backButton1: ImageButton
    private lateinit var profilePic: ImageView

    //private lateinit var nameTextField: TextView

    private lateinit var textFullName: TextView
    private lateinit var editFullName: EditText

    private lateinit var confirmButton: Button

    private lateinit var textEmailAddress: TextView
    private lateinit var editEmailAddress: EditText

    private lateinit var textPassword: TextView
    private lateinit var editPassword: EditText

    private lateinit var textPhoneNumber: TextView
    private lateinit var editPhoneNumber: EditText


    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tutor_personal_information, container, false)
        backButton1 = view.findViewById(R.id.account_back_btn)
        profilePic = view.findViewById(R.id.profilepic)

        //nameTextField = view.findViewById(R.id.nameField)

        editFullName = view.findViewById(R.id.tutorEditFullName)
        textFullName = view.findViewById(R.id.tutorFullName)

        textEmailAddress = view.findViewById(R.id.tutorEmail)
        editEmailAddress = view.findViewById(R.id.tutorEditEmailAddress)

        textPassword = view.findViewById(R.id.tutorPassword)
        editPassword = view.findViewById(R.id.tutorEditTextPassword)

        textPhoneNumber = view.findViewById(R.id.tutorPhoneNumber)
        editPhoneNumber= view.findViewById(R.id.tutorEditPhoneNumber)

        confirmButton = view.findViewById(R.id.tutorPersonalInformationBtn)
        editEmailAddress.hint = "email address"
        editPassword.hint = "*********"


        // Show backButton and functionality
        backButtonSetup1(view)

        // setup the profile picture
        profilePictureSetup()

        //showPersonalInformation()
        updateFullName()

        cannotEditEmailAddress()

        cannotEditPassword()

        //fetchAndUpdateFullName()

        return view
    }



    private fun updateFullName(){
        confirmButton.setOnClickListener {
            val fullNameStr = editFullName.text.toString()
            textFullName.text = fullNameStr


            textPhoneNumber.text = editPhoneNumber.text.toString()

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

    private fun cannotEditPassword(){
        editPassword.setOnClickListener {
            editPassword.isEnabled = false
            val message = "Cannot change Password"
            textPassword.setTextColor(Color.RED)

            textPassword.text = message
        }
    }

        private fun backButtonSetup1(view: View) {
        backButton1.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_account)
        }
    }

    private fun profilePictureSetup() {
        profilePic.setImageResource(R.drawable.pfp)
    }
    /*

    private fun fetchAndUpdateFullName() {
        lifecycleScope.launch {
            try {
                val fullName = withContext(Dispatchers.IO) {
                    CreateSessionHelper.fetchUsername(requireContext())
                }

                nameTextField.text = "$fullName" // update UI here
                //this@account.fullName = fullName.toString() // set the global variable for use later
            } catch (e: Exception) {
                Log.e("TAG", "Error fetching username")
            }
        }
    }

*/
        companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment edit_personal_info.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            account().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}