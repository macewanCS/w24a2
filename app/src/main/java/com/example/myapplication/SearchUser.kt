package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
 * Use the [SearchUser.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchUser : Fragment() {
    private var searchInput: EditText? = null
    private var searchButton: ImageButton? = null
    private lateinit var backButton: ImageButton
    private var recyclerView: RecyclerView? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)

        // initialize layout buttons and design
        searchInput = view.findViewById(R.id.search_username_input)
        searchButton = view.findViewById(R.id.search_user_btn)
        backButton = view.findViewById(R.id.back_btn)
        recyclerView = view.findViewById(R.id.search_user_recycler_view)

        backButtonSetup(view)

        return view
    }

    private fun backButtonSetup(view: View) {
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
         * @return A new instance of fragment student_profile.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            student_profile().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}
