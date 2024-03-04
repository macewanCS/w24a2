package com.example.myapplication.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.google.firebase.auth.FirebaseAuth

class Logout : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_account, container, false)

        val logoutBtn: Button = view.findViewById(R.id.logout)

        logoutBtn.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            findNavController().navigate(R.id.to_login)
        }
        return view
    }
}