package com.example.myapplication

import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

fun navBarNavigationStudents(view: View, navController: NavController) {
    val navbar: BottomNavigationView = view.findViewById(R.id.bottomNavigationView)

    val currentFragmentID = navController.currentDestination?.id
    Log.d("TAG", "$currentFragmentID")

    //based on the current fragment, detect which button should be highlighted
    when (currentFragmentID) {
        R.id.homePage -> {
            navbar.selectedItemId = R.id.home
        }
        R.id.student_profile -> {
            navbar.selectedItemId = R.id.account
        }
        R.id.calendar -> {
            navbar.selectedItemId = R.id.calendar
        }
    }

    navbar.setOnItemSelectedListener { menuItem ->
        when (menuItem.itemId) {
            R.id.calendar -> {
                if (currentFragmentID != R.id.calendar) {
                    Navigation.findNavController(view).navigate(R.id.to_calendar)
                }
                true
            }
//            R.id.messages -> {
//                if (currentFragmentID != R.id.fragment_messages) {
//                    Navigation.findNavController(view).navigate(R.id.to_messages)
//                }
//                true
//            }
            R.id.home -> {
                if (currentFragmentID != R.id.homePage) {
                    Navigation.findNavController(view).navigate(R.id.to_homePage)
                }
                true
            }
//            R.id.email -> {
//                if (currentFragmentID != R.id.fragment_email) {
//                    //Navigation.findNavController(view).navigate(R.id.to_email)
//                }
//                true
//            }
            R.id.account -> {
                if (currentFragmentID != R.id.student_profile) {
                    Navigation.findNavController(view).navigate(R.id.to_student_profile)
                }
                true
            }
            else -> false
        }
    }
}

