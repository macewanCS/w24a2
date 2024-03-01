package com.example.myapplication

import android.app.admin.TargetUser
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.Navigation
import com.example.myapplication.databinding.FragmentAccountBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [account.newInstance] factory method to
 * create an instance of this fragment.
 */
class account : Fragment() {
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
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val profile_pic: ImageView = view.findViewById(R.id.profilepic)
        profile_pic.setImageResource(R.drawable.pfp)

        val navbar: BottomNavigationView = view.findViewById(R.id.bottomNavigationView)
        navbar.selectedItemId = R.id.account

        navbar.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.calendar -> {
                    Navigation.findNavController(view).navigate(R.id.to_calendar)
                    true

                }
                R.id.home -> {
                    Navigation.findNavController(view).navigate(R.id.to_homePage)
                    true
                }
                R.id.email -> {
                    //Navigation.findNavController(view).navigate(R.id.to_login)
                    true
                }
                R.id.account -> {
                    //Navigation.findNavController(view).navigate(R.id.to_login)
                    true
                } else -> {
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
         * @return A new instance of fragment account.
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