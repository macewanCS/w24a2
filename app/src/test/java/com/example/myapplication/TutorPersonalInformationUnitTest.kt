package com.example.myapplication

import android.os.Build
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class TutorPersonalInformationUnitTest {

    private lateinit var fragment: TutorPersonalInformation

    @Before
    fun setup(){
        fragment = TutorPersonalInformation()
        val fragmentManager = ApplicationProvider.getApplicationContext<MainActivity>().supportFragmentManager
        fragmentManager.beginTransaction().add(fragment, null).commitNow()
    }

    @Test
    fun testBackButtonSetup(){
        val view = fragment.view
        val backButton = view?.findViewById<ImageButton>(R.id.account_back_btn)
        backButton?.performClick()

        assertEquals(R.id.to_account, fragment.findNavController().currentDestination?.id)
    }

    @Test
    fun testProfilePictureSetup(){
        val view = fragment.view
        val profilePic = view?.findViewById<ImageView>(R.id.profilepic)
        val expectedDrawableId = R.drawable.pfp
        val actualDrawableId = profilePic?.drawable?.constantState?.let {
            shadowOf(it.newDrawable()).createdFromResId
        }
        assertEquals(expectedDrawableId, actualDrawableId)
    }

    @Test
    fun testUpdateFullName(){
        val view = fragment.view
        val fullName = "John Doe"
        val editFullName = view?.findViewById<EditText>(R.id.tutorEditFullName)
        val textFullName = view?.findViewById<TextView>(R.id.tutorFullName)
        val confirmButton = view?.findViewById<Button>(R.id.tutorPersonalInformationBtn)

        editFullName?.setText(fullName)
        confirmButton?.performClick()

        assertEquals(fullName, textFullName?.text.toString())
    }

    @Test
    fun testUpdateEmailAddress(){
        val view = fragment.view
        val newEmailAddress = "newemail@example.com"
        val editEmailAddress = view?.findViewById<EditText>(R.id.tutorEditEmailAddress)
        val textEmailAddress = view?.findViewById<TextView>(R.id.tutorEmail)
        val confirmButton = view?.findViewById<Button>(R.id.tutorPersonalInformationBtn)

        editEmailAddress?.setText(newEmailAddress)
        confirmButton?.performClick()

        assertEquals(newEmailAddress, textEmailAddress?.text.toString())
    }

    @Test
    fun testUpdatePassword(){
        val view = fragment.view
        val newPassword = "newPassword123"
        val editPassword = view?.findViewById<EditText>(R.id.tutorEditTextPassword)
        val textPassword = view?.findViewById<TextView>(R.id.tutorPassword)
        val confirmButton = view?.findViewById<Button>(R.id.tutorPersonalInformationBtn)

        editPassword?.setText(newPassword)
        confirmButton?.performClick()

        assertEquals("Password has been changed", textPassword?.text.toString())
    }

    @Test
    fun testUpdatePhoneNumber(){
        val view = fragment.view
        val newPhoneNumber = "123-456-7890"
        val editPhoneNumber = view?.findViewById<EditText>(R.id.tutorEditPhoneNumber)
        val textPhoneNumber = view?.findViewById<TextView>(R.id.tutorPhoneNumber)
        val confirmButton = view?.findViewById<Button>(R.id.tutorPersonalInformationBtn)

        editPhoneNumber?.setText(newPhoneNumber)
        confirmButton?.performClick()

        assertEquals(newPhoneNumber, textPhoneNumber?.text.toString())
    }
}