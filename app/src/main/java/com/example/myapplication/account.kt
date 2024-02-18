package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.myapplication.navBarNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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

        //setup the profile picture
        profilePictureSetup(view)

        //get the fullName of the current user using a callback
        fetchUsername(object: FetchFullNameCallback{
            override fun onFullNameFetched(fullName: String?) {
                //if username is not null update the UI otherwise handle error
                if (fullName != null) {
                    //update UI here
                    updateNameField(view, fullName)
                } else {
                    Log.e("TAG", "Unable to fetch username")
                }
            }
        })

        //start the bottom navigation bar functionality
        navBarNavigation(view, findNavController())

        return view
    }

    private fun profilePictureSetup(view: View){
        val profile_pic: ImageView = view.findViewById(R.id.profilepic)
        profile_pic.setImageResource(R.drawable.pfp)
    }

    interface FetchFullNameCallback {
        fun onFullNameFetched(fullName: String?)
    }

    fun fetchUsername(callback: FetchFullNameCallback): String? {
        val userID = FirebaseAuth.getInstance().currentUser?.uid
        val dbref = userID?.let {
            FirebaseDatabase.getInstance().getReference("users").child(userID)
        }
        var fullName: String? = null

        if (dbref != null) {
            dbref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        //retrieve user data
                        val firstName = snapshot.child("firstName").getValue(String::class.java)
                        val lastName = snapshot.child("lastName").getValue(String::class.java)

                        //combine the first and last name into the full name
                        fullName = String.format("%s %s", firstName, lastName)
                        //update ui with user data

                        callback.onFullNameFetched(fullName)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    //Log.e("ValueEventListener", "Error reading data", databaseError.toException())
                    callback.onFullNameFetched(null)
                }
            })
        } else {
            //invoke the callback with a null value if dbref is null
            callback.onFullNameFetched(null)
        }
        return fullName
    }

    private fun updateNameField(view: View, fullName: String?){
        val nameTextField = view.findViewById<TextView>(R.id.nameField)
        nameTextField.text = "$fullName"
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