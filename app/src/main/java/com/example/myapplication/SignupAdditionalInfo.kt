package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.navigation.Navigation
import com.example.myapplication.CreateSessionHelper.showMessage
import com.example.myapplication.SignUpHelper.createUser
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class SignupAdditionalInfo : Fragment() {

    private lateinit var view: View
    private var firstName: String? = ""
    private var lastName: String? = ""
    private var email: String? = ""
    private var password: String? = ""
    private var encPassword: String? = ""
    private lateinit var backButton: Button
    private lateinit var gradesChipGroup: ChipGroup
    private lateinit var subjectsChipGroup: ChipGroup
    private lateinit var phoneNumberEditText: EditText
    private lateinit var preferredContactMethodGroup: RadioGroup
    private lateinit var preferredTimesChipGroup: ChipGroup
    private lateinit var uploadResumeButton: Button
    private lateinit var registerButton: Button
    private var selectedGrades: String = ""
    private var selectedSubjects: String = ""
    private var selectedPreferredTimes: String = ""
    private var subjectsArray: Array<String> = arrayOf("English", "Mathematics", "Social Sciences", "Sciences", "Health", "Physical Education", "Music", "Crafts")
    private var gradesArray: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "Graduate")
    private var preferredTimesArray: Array<String> = arrayOf("Morning", "Afternoon", "Evening")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get the variables from the first registration page
        firstName = arguments?.getString("firstName")
        lastName = arguments?.getString("lastName")
        email = arguments?.getString("email")
        password = arguments?.getString("password")
        encPassword = arguments?.getString("encPassword")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment'
        view = inflater.inflate(R.layout.fragment_signup_additional_info, container, false)

        // one time setup for UI components
        initUIComponents()

        // initialize the back button
        initBackButton()
        // initialize the chip group displays
        initChipGroups()
        // initialize the register button
        initRegisterButton()
        return view
    }

    private fun initRegisterButton() {
        registerButton.setOnClickListener {
            val phoneNumber = getPhoneNumber()
            val preferredContactMethod = getPreferredContactMethod()
            if (checkInputFields(phoneNumber, preferredContactMethod)) {
                // create a new tutor object
                val tutor = Tutor(firstName, lastName, email, encPassword,true, selectedGrades, selectedSubjects, phoneNumber, preferredContactMethod, selectedPreferredTimes)

                // register the user, both in database and firebase authentication
                createUser(null, tutor, email, password, requireContext(), view)
            } else {
                return@setOnClickListener
            }
        }
    }

    private fun checkInputFields(phoneNumber: String, preferredContactMethod: String): Boolean {
        if (selectedGrades.isBlank()) {
            showMessage(requireContext(), "Please select at least one grade.")
            return false
        }

        if (selectedSubjects.isBlank()) {
            showMessage(requireContext(), "Please select at least one subject.")
            return false
        }

        if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
            showMessage(requireContext(), "Please enter a valid phone number.")
            return false
        }

        if (preferredContactMethod.isBlank()) {
            showMessage(requireContext(), "Please choose a preferred contact method.")
            return false
        }

        if (selectedPreferredTimes.isBlank()) {
            showMessage(requireContext(), "Please select at least one preferred time.")
            return false
        }

        return true
    }

    private fun initChipGroups() {
        gradesChipGroup.setOnCheckedStateChangeListener { group, _ ->
            // update the selectedSubjects strings whenever a chip is selected or deselected
            selectedGrades = group.checkedChipIds
                .map { group.findViewById<Chip>(it).text.toString() }
                .joinToString(", ")

            // check the current selected subjects
            Log.d("getSelectedSubjects", "Selected Subjects: $selectedGrades")
        }

        subjectsChipGroup.setOnCheckedStateChangeListener { group, _ ->
            // update the selectedSubjects strings whenever a chip is selected or deselected
            selectedSubjects = group.checkedChipIds
                .map { group.findViewById<Chip>(it).text.toString() }
                .joinToString(", ")

            // check the current selected subjects
            Log.d("getSelectedSubjects", "Selected Subjects: $selectedSubjects")
        }

        preferredTimesChipGroup.setOnCheckedStateChangeListener { group, _ ->
            // update the selectedSubjects strings whenever a chip is selected or deselected
            selectedPreferredTimes = group.checkedChipIds
                .map { group.findViewById<Chip>(it).text.toString() }
                .joinToString(", ")

            // check the current selected subjects
            Log.d("getSelectedSubjects", "Selected Subjects: $selectedPreferredTimes")
        }
    }

    private fun getPreferredContactMethod(): String {
        val checkedButtonID = preferredContactMethodGroup.checkedRadioButtonId

        val selectedContactMethod: String = when (checkedButtonID) {
            R.id.additionalInfoPhoneButton -> "Phone"
            R.id.additionalInfoEmailButton -> "Email"
            else -> ""
        }
        return selectedContactMethod
    }

    private fun getPhoneNumber(): String {
        val phoneNumber = phoneNumberEditText.text.toString()
        // check the value of the phone number
        Log.d("getPhoneNumber", "Phone Number: $phoneNumber")
        return phoneNumber
    }

    private fun initUIComponents() {
        backButton = view.findViewById(R.id.additionalInfoBackButton) ?: return
        gradesChipGroup = view.findViewById(R.id.additionalInfoGradesChipGroup) ?: return
        subjectsChipGroup = view.findViewById(R.id.additionalInfoSubjectsChipGroup) ?: return
        phoneNumberEditText = view.findViewById(R.id.additionalInfoPhoneNumberEditText)
        preferredContactMethodGroup = view.findViewById(R.id.additionalInfoContactMethodGroup) ?: return
        preferredTimesChipGroup = view.findViewById(R.id.additionalInfoPreferredTimesChipGroup) ?: return
        uploadResumeButton = view.findViewById(R.id.additionalInfoResumeButton) ?: return
        registerButton = view.findViewById(R.id.additionalInfoRegisterButton)

        initChipGroup(gradesChipGroup, gradesArray)
        initChipGroup(subjectsChipGroup, subjectsArray)
        initChipGroup(preferredTimesChipGroup, preferredTimesArray)

    }

    private fun initChipGroup(chipGroup: ChipGroup, items: Array<String>) {
        for (item in items) {
            val chip = Chip(requireContext())
            chip.text = item
            chip.isCheckable = true
            chipGroup.addView(chip)
        }
    }

    private fun initBackButton() {
        backButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_signup)
        }
    }
}