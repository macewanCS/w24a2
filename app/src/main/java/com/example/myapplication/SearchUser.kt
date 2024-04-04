package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import androidx.navigation.Navigation

class SearchUser : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var imageList: Array<Int>
    private lateinit var firestore: FirebaseFirestore
    private var searchInput: EditText? = null
    private var searchButton: ImageButton? = null
    private var backButton: ImageButton? = null


    private lateinit var titleList: List<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)

        searchInput = view.findViewById(R.id.search_username_input)
        searchButton = view.findViewById(R.id.search_user_btn)
        backButton = view.findViewById(R.id.back_btn)
        recyclerView = view.findViewById(R.id.search_user_recycler_view)

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance()

        // Initialize imageList and dataList
        imageList = arrayOf(R.drawable.account_icon, R.drawable.account_icon)
        dataList = arrayListOf()

        // Fetch first names and populate RecyclerView
        //titleList = fetchFirstNames()
        titleList = listOf("User1", "John Smith", "Tutor1", "Student1", "Jheaney Perico")

        // Initialize RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.setHasFixedSize(true)


        backButtonSetup(view)
        searchUserSetup(view)


        return view
    }

    private fun searchUserSetup(view: View) {
        for (firstName in titleList) {
            val dataClass = DataClass(firstName)
            dataList.add(dataClass)
        }

        // Set up RecyclerView adapter
        recyclerView.adapter = AdapterClass(dataList)

        searchButton?.setOnClickListener {
            val searchTerm: String = searchInput?.text.toString()
            if (searchTerm.isEmpty()) {
                searchInput?.setError(getString(R.string.invalid_user))

            }
            else {
                val filteredList = titleList.filter { it.contains(searchTerm, ignoreCase = true) }
                val filteredDataList = arrayListOf<DataClass>()

                for (firstName in filteredList) {
                    val dataClass = DataClass(firstName)
                    filteredDataList.add(dataClass)
                }
                // Set up RecyclerView adapter with filtered data
                recyclerView.adapter = AdapterClass(filteredDataList)
            }
        }
    }


    private fun backButtonSetup(view: View) {
        backButton?.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.to_student_profile)
        }
    }
}