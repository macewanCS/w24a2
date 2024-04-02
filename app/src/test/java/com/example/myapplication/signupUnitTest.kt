package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import org.junit.Before
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class signupUnitTest {

    private lateinit var fragment: signup

    @Before
    fun setup(){
        fragment = signup()
        val view = fragment.onCreateView(
            LayoutInflater.from(RuntimeEnvironment.application),
            null,
            null)
        fragment.onViewCreated(view, null)

        fragment.registerBtn = view.findViewById<Button>(R.id.register_btn)
        fragment.firstNameInput = view.findViewById(R.id.FirstName)
        fragment.lastNameInput = view.findViewById(R.id.LastName)
        fragment.emailInput = view.findViewById(R.id.EmailAddress)
        fragment.passwordInput = view.findViewById(R.id.Password)
        fragment.isTutorBtn = view.findViewById(R.id.tutorCheckBtn)
    }

    @Test
    fun testRegisterButtonClicks_ValidInputs(){
        fragment.firstNameInput.setText("John")
        fragment.lastNameInput.setText("Doe")
        fragment.emailInput.setText("john.doe@test.com")
        fragment.passwordInput.setText("password")
        fragment.isTutorBtn.isChecked = false

        fragment.initRegisterButton()

        val expectedBundle = Bundle().apply {
            putString("firstName", "John")
            putString("lastName", "Doe")
            putString("email", "john.doe@test.com")
            putString("password", "password")
            putString("encPassword", "encPassword")
        }

        assertEquals(expectedBundle, fragment.findNavController().currentDestination?.arguments)

    }

    @Test
    fun testRegisterButtonClicks_InvalidInput(){
        fragment.firstNameInput.setText("John")
        fragment.lastNameInput.setText("Doe")
        fragment.emailInput.setText("")
        fragment.passwordInput.setText("")

        fragment.initRegisterButton()

        assertNotEquals("", fragment.firstNameInput.text.toString())
        assertNotEquals("", fragment.lastNameInput.text.toString())
        assertNotEquals("", fragment.emailInput.text.toString())
        assertNotEquals("", fragment.passwordInput.text.toString())
    }

    @Test
    fun testLogin_Click(){
        val loginText = fragment.view.findViewById<TextView>(R.id.existingAccountLogin)
        loginText.performClick()

        assertEquals(R.id.to_login, fragment.findNavController().currentDestination?.id)
    }
}