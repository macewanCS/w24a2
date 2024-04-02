package com.example.myapplication

import android.os.Build
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.util.ReflectionHelpers

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class accountUnitTest {

    private lateinit var fragment: account

    @Before
    fun setup(){
        fragment = account()
        ReflectionHelpers.callInstanceMethod<account>(fragment, "onCreateView")
    }

    @Test
    fun testLogoutButton(){
        val logoutBtn = fragment.view?.findViewById<Button>(R.id.logout)
        assertTrue(logoutBtn != null)
        logoutBtn?.performClick()
        assertEquals("Expected destination was not reached", R.id.to_login, fragment.findNavController().currentDestination?.id)
    }

    @Test
    fun testPersonalInformationButton(){
        val personalInfoBtn = fragment.view?.findViewById<Button>(R.id.tutor_personalInformation)
        assertTrue(personalInfoBtn != null)
        personalInfoBtn?.performClick()
        assertEquals("Expected destination was not reached", R.id.to_tutorPersonalInformation, fragment.findNavController().currentDestination?.id)
    }

    @Test
    fun testCreateSessionButton(){
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userID = currentUser?.uid
        val createSessionBtn = fragment.view?.findViewById<Button>(R.id.createSession)
        assertTrue(createSessionBtn != null)

        runBlocking {
            val isTutor = fragment.checkIfUserIsTutor(userID!!)
            if(isTutor){
                createSessionBtn?.isEnabled = true
            }
            createSessionBtn?.performClick()
            assertEquals("Expected destination was not reached", R.id.to_createSession, fragment.findNavController().currentDestination?.id)
        }
    }

    @Test
    fun testCheckIfUserIsTutor(){
        val userID = "testUserId"
        val isTutor = runBlocking {
            fragment.checkIfUserIsTutor(userID)
        }
        assertTrue(!isTutor)
    }

    @Test
    fun testFetchAndUpdateFullName(){
        val nameTextField = fragment.view?.findViewById<TextView>(R.id.nameField)
        assertTrue(nameTextField != null)

        runBlocking {
            fragment.fetchAndUpdateFullName()
            assertEquals("Expected name was not set", "testFullName", nameTextField?.text)
        }
    }

    @Test
    fun testProfilePictureSetup(){
        val profilePic = fragment.view?.findViewById<ImageView>(R.id.profilepic)
        assertTrue(profilePic != null)

        fragment.profilePictureSetup()
        assertEquals("Expected profile picture resource was not set", R.drawable.pfp, profilePic?.background)
    }
}