package com.example.myapplication

import android.app.AlertDialog
import android.graphics.Color.BLUE
import android.graphics.Color.WHITE
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.compose.ui.graphics.Color
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.myapplication.CreateSessionHelper.getNumberOfParticipants
import com.example.myapplication.CreateSessionHelper.getSelectedDate
import com.example.myapplication.CreateSessionHelper.showMessage
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateSession : Fragment() {

    private lateinit var endTimeButton: Button
    private lateinit var startTimeButton: Button
    private lateinit var createSessionButton: Button
    private lateinit var cancelButton: Button
    private lateinit var datePicker: DatePicker
    private lateinit var maxParticipantsEditText: EditText
    private lateinit var view: View
    private lateinit var chipGroup: ChipGroup
    private var selectedSubjects: String = ""
    private var selectedGrades: String = ""
    private var fullName: String = ""
    private var sessionTime: String = ""
    private lateinit var startTime: String
    private lateinit var endTime: String
    private var startTimeHour: Int = 0
    private var startTimeMinute: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_create_session, container, false)
        // set UI components here
        setUIComponents()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCancelButton()
        fetchFullName()
        getSelectedSubjects()
        getSelectedGrades()
        initTimeSelection()
        createSession(view)
    }

    private fun initCancelButton() {
        cancelButton.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_account)
        }
    }
    private fun createSession(view: View) {
        createSessionButton.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val userID = currentUser?.uid

            if (userID != null) {
                if (selectedSubjects.isBlank()) {
                    showMessage(requireContext(), "Please select at least one subject")
                    return@setOnClickListener
                }

                if (selectedGrades.isBlank()) {
                    showMessage(requireContext(), "Please select at least one grade level")
                    return@setOnClickListener
                }

                val date = getSelectedDate(datePicker)
                if (date == null) {
                    showMessage(requireContext(), "Please select a valid date")
                    return@setOnClickListener
                }

                if (sessionTime.isBlank()) {
                    showMessage(requireContext(), "Please select a valid session time")
                    return@setOnClickListener
                }

                val maxParticipants = getNumberOfParticipants(maxParticipantsEditText)
                if (maxParticipants <= 0) {
                    showMessage(requireContext(), "Please enter a valid number of participants")
                    return@setOnClickListener
                }

                lifecycleScope.launch {
                    CreateSessionHelper.checkIsTutorAndCreateNewSession(
                        lifecycleScope,
                        requireContext(),
                        userID,
                        fullName,
                        selectedSubjects,
                        selectedGrades,
                        date,
                        sessionTime,
                        maxParticipants
                    ) {
                        Navigation.findNavController(view).navigate(R.id.to_account)
                    }
                }
            } else {
                showMessage(requireContext(), "invalid userID. Please log in.")
            }
        }
    }
    private fun setUIComponents() {
        // setup UI components here
        fetchFullName()
        startTimeButton = view.findViewById(R.id.selectStartTimeButton) ?: return
        endTimeButton = view.findViewById(R.id.selectEndTimeButton) ?: return
        createSessionButton = view.findViewById(R.id.createSessionButton) ?: return
        cancelButton = view.findViewById(R.id.cancelButton) ?: return
        datePicker = view.findViewById(R.id.datePicker) ?: return
        chipGroup = view.findViewById(R.id.selectedSubjectsChipGroup)
        maxParticipantsEditText = view.findViewById(R.id.participantsCountEditText) ?: return

    }
    private fun fetchFullName() {
        lifecycleScope.launch {
            try {
                val fullName = withContext(Dispatchers.IO) {
                    CreateSessionHelper.fetchUsername(requireContext())
                }

                this@CreateSession.fullName =
                    fullName.toString() // set the global variable for use later
            } catch (e: Exception) {
                Log.e("TAG", "Error fetching username")
            }
        }
    }
    private fun initTimeSelection() {
        endTimeButton.isEnabled = false // disable this button until the start time is set
        // setup listeners here
        startTimeButton.setOnClickListener {
            showTimePickerDialog(true)
        }
        endTimeButton.setOnClickListener {
            showTimePickerDialog(false)
        }
    }
    private fun showTimePickerDialog(isStartTime: Boolean) {
        val dialogView =
            LayoutInflater.from(requireContext()).inflate(R.layout.time_picker_dialog, null)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val title: String = if (isStartTime) {
            "Select start time"
        } else {
            "Select end time"
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // create the dialog view and show it
        val dialog = builder.create()
        dialog.show()

        // get the OK button and initially disable it
        val okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
        if (!isStartTime) {
            okButton.isEnabled = false
            timePicker.setOnTimeChangedListener { _, _, _ ->
                okButton.isEnabled =
                    (timePicker.hour > startTimeHour || (timePicker.hour == startTimeHour && timePicker.minute > startTimeMinute))
            }
        }

        // check for a click on the ok button
        okButton.setOnClickListener {
            // Handle the selected start and end time
            val selectedHour24hr = timePicker.hour
            val selectedMinute = timePicker.minute
            val amPm = if (selectedHour24hr < 12) "AM" else "PM"
            val selectedHour =
                if (selectedHour24hr > 12) selectedHour24hr - 12 else selectedHour24hr

            if (isStartTime) {
                startTime = "$selectedHour:${selectedMinute.toString().padStart(2, '0')}$amPm"
                startTimeHour = selectedHour24hr
                startTimeMinute = selectedMinute
                endTimeButton.isEnabled =
                    true // enable the end time button now that the start time is set
                Log.d(
                    "TimePickerDialog",
                    "Start Time Set: $startTime, Hour: $startTimeHour, Minute: $startTimeMinute"
                )
            } else {
                endTime = "$selectedHour:${selectedMinute.toString().padStart(2, '0')}$amPm"
                // set sessionTime once both of the times are entered
                sessionTime = "$startTime-$endTime"
                Log.d("TimePickerDialog", "End Time Set: $endTime, Session Time: $sessionTime")
            }
            dialog.dismiss()
        }
    }
    private fun getSelectedSubjects() {
        val subjectsArray = arrayOf("English", "Mathematics", "Social Sciences",
            "Sciences", "Health", "Physical Education", "Music", "Crafts")

        val chipGroup = view.findViewById<ChipGroup>(R.id.selectedSubjectsChipGroup)

        for (subject in subjectsArray) {
            val chip = Chip(requireContext())
            chip.setChipBackgroundColorResource(R.color.chip_background)
            chip.setTextColor(WHITE)
            chip.text = subject
            chip.isCheckable = true
            chipGroup.addView(chip)
        }

        selectedSubjects = chipGroup.checkedChipIds
            .map {chipGroup.findViewById<Chip>(it).text.toString()}
            .joinToString {", "}

        chipGroup.setOnCheckedStateChangeListener { group, _ ->
            // update the selectedSubjects strings whenever a chip is selected or deselected
            selectedSubjects = group.checkedChipIds
                .map {group.findViewById<Chip>(it).text.toString()}
                .joinToString(", ")

            // check the current selected subjects
            Log.d("getSelectedSubjects", "Selected Subjects: $selectedSubjects")
        }
    }
    private fun getSelectedGrades() {
        val gradesArray: Array<String> = arrayOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "Graduate")

        val chipGroup = view.findViewById<ChipGroup>(R.id.selectedGradesChipGroup)

        for (grade in gradesArray) {
            val chip = Chip(requireContext())
            chip.setChipBackgroundColorResource(R.color.chip_background)
            chip.setTextColor(WHITE)
            chip.text = grade
            chip.isCheckable = true
            chipGroup.addView(chip)
        }

        selectedGrades = chipGroup.checkedChipIds
            .map {chipGroup.findViewById<Chip>(it).text.toString()}
            .joinToString {", "}

        chipGroup.setOnCheckedStateChangeListener { group, _ ->
            // update the selectedSubjects strings whenever a chip is selected or deselected
            selectedGrades = group.checkedChipIds
                .map {group.findViewById<Chip>(it).text.toString()}
                .joinToString(", ")

            // check the current selected subjects
            Log.d("getSelectedSubjects", "Selected Subjects: $selectedGrades")
        }
    }
}