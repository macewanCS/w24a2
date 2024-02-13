package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.myapplication.R.id.bottomNavigationView
import com.google.android.material.bottomnavigation.BottomNavigationView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [homePage.newInstance] factory method to
 * create an instance of this fragment.
 */
class calendar : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_calendar, container, false)

        val navbar: BottomNavigationView = view.findViewById(bottomNavigationView)
        navbar.selectedItemId = R.id.calendar

        navbar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.calendar -> {
                    //Navigation.findNavController(view).navigate(R.id.from_home_to_calendar)
                    true
                }

                R.id.messages -> {
                    //Navigation.findNavController(view).navigate(R.id.to_login)
                    true
                }

                R.id.home -> {
                    Navigation.findNavController(view).navigate(R.id.calendar_to_homePage)
                    true
                }

                R.id.email -> {
                    //Navigation.findNavController(view).navigate(R.id.to_login)
                    true
                }

                R.id.account -> {
                    Navigation.findNavController(view).navigate(R.id.calendar_to_account2)
                    true
                }

                else -> {
                    false
                }
            }
        }
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment homePage.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            homePage().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}