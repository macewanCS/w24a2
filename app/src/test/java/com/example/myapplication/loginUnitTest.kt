package com.example.myapplication

import android.os.Build
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.FirebaseAuth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.lang.reflect.Field

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class loginUnitTest {

    private lateinit var fragment: login

    @Before
    fun setup(){
        fragment = login()
        val view = fragment.onCreateView(LayoutInflater.from(ApplicationProvider.getApplicationContext()),
            null,
            null)
    }

    @Test
    fun testEmptyFields(){
        val view = fragment.requireView()
        val loginBtn = view.findViewById<Button>(R.id.login_btn)

        loginBtn.performClick()
        assertEquals("Please fill in all fields.", fragment.toastMessage)
    }

    @Test
    fun testInvalidCredentials(){
        val view = fragment.requireView()
        val usernameInput = view.findViewById<EditText>(R.id.EmailAddress)
        val passwordInput = view.findViewById<EditText>(R.id.Password)
        val loginBtn = view.findViewById<Button>(R.id.login_btn)

        val mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.signInWithEmailAndPassword("","")).thenThrow(IllegalArgumentException::class.java)

        val mockFirebaseAuth = mock(FirebaseAuth::class.java)
        `when`(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)

        usernameInput.setText("")
        passwordInput.setText("")

        loginBtn.performClick()
        assertEquals("Invalid email or password", fragment.toastMessage)
    }

    @Test
    fun testLoginBtnClick(){
        val view = fragment.requireView()
        val usernameInput = view.findViewById<EditText>(R.id.EmailAddress)
        val passwordInput = view.findViewById<EditText>(R.id.Password)
        val loginBtn = view.findViewById<Button>(R.id.login_btn)

        val mockAuth = mock(FirebaseAuth::class.java)
        `when`(mockAuth.signInWithEmailAndPassword(anyString(), anyString())).thenReturn(null)

        val mockFirebaseAuth = mock(FirebaseAuth::class.java)
        `when`(FirebaseAuth.getInstance()).thenReturn(mockFirebaseAuth)

        usernameInput.setText("test@example.com")
        passwordInput.setText("password")

        loginBtn.performClick()

        verify(mockFirebaseAuth).signInWithEmailAndPassword("test@example.com", "password")

        val field: Field = login::class.java.getDeclaredField("lastLog")
        field.isAccessible = true
        val lastLogValue = field.get(fragment) as String?

        assertEquals("test@example.com: password", lastLogValue)
    }

    @Test
    fun testSignUpTextClick(){
        val view = fragment.requireView()
        val signUpText = view.findViewById<TextView>(R.id.register)

        signUpText.performClick()
        assertEquals(R.id.to_signup, fragment.navigationId)
    }

    private var login.navigationId: Int?
        get() = null
        set(value) = TODO()

    private var login.toastMessage: String?
        get() = null
        set(value) = TODO()

}