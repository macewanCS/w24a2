package com.example.myapplication

import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import com.google.android.material.chip.ChipGroup
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class SignupAdditionalInfoUnitTest {

    private lateinit var fragment: SignupAdditionalInfo

    @Before
    fun setup(){
        val args = Bundle().apply {
            putString("firstName", "John")
            putString("lastName", "Doe")
            putString("email", "john.doe@test.com")
            putString("password", "password")
            putString("encPassword", "encPassword")
        }
        fragment = SignupAdditionalInfo().apply { arguments = args }
        Robolectric.buildActivity(MainActivity::class.java).create().start().resume().visible().get()
        .supportFragmentManager.beginTransaction().add(fragment, null).commit()

    }

    @Test
    fun testFragmentNotNull(){
        assertNotNull(fragment)
    }

    @Test
    fun testGetPhoneNumber(){
        val phoneNumberEditText = fragment.view?.findViewById<EditText>(R.id.additionalInfoPhoneNumberEditText)
        phoneNumberEditText?.setText("1234567890")
        assertEquals("1234567890", fragment.getPhoneNumber())
    }

    @Test
    fun testGetPreferredContactMethod(){
        val phoneButton = fragment.view?.findViewById<Button>(R.id.additionalInfoPhoneButton)
        phoneButton?.performClick()
        assertEquals("Phone", fragment.getPreferredContactMethod())
    }

    @Test
    fun testInitBackButton(){
        val backButton = fragment.view?.findViewById<Button>(R.id.additionalInfoBackButton)
        assertNotNull(backButton)
        backButton?.performClick()
    }

    @Test
    fun testInitChipGroup(){
        val chipGroup = fragment.view?.findViewById<ChipGroup>(R.id.additionalInfoGradesChipGroup)
        assertNotNull(chipGroup)
        assertEquals(13, chipGroup?.childCount)
    }

    @Test
    fun testInitChipGroups(){
        val gradesChipGroup = fragment.view?.findViewById<ChipGroup>(R.id.additionalInfoGradesChipGroup)
        assertNotNull(gradesChipGroup)
        val subjectsChipGroup = fragment.view?.findViewById<ChipGroup>(R.id.additionalInfoSubjectsChipGroup)
        assertNotNull(subjectsChipGroup)
        val preferredTimesChipGroup = fragment.view?.findViewById<ChipGroup>(R.id.additionalInfoPreferredTimesChipGroup)
        assertNotNull(preferredTimesChipGroup)
    }

    @Test
    fun testInitUIComponents(){
        assertNotNull(fragment.view?.findViewById<Button>(R.id.additionalInfoBackButton))
        assertNotNull(fragment.view?.findViewById<ChipGroup>(R.id.additionalInfoGradesChipGroup))
        assertNotNull(fragment.view?.findViewById<ChipGroup>(R.id.additionalInfoSubjectsChipGroup))
        assertNotNull(fragment.view?.findViewById<EditText>(R.id.additionalInfoPhoneNumberEditText))
        assertNotNull(fragment.view?.findViewById<RadioGroup>(R.id.additionalInfoContactMethodGroup))
        assertNotNull(fragment.view?.findViewById<ChipGroup>(R.id.additionalInfoPreferredTimesChipGroup))
        assertNotNull(fragment.view?.findViewById<Button>(R.id.additionalInfoRegisterButton))

    }

    @Test
    fun testInitRegisterButton(){
        val registerButton = fragment.view?.findViewById<Button>(R.id.additionalInfoRegisterButton)
        assertNotNull(registerButton)

    }

    @Test
    fun testCheckInputFields_InvalidPhoneNumber_ReturnsFalse(){
        val phoneNumberEditText = fragment.view?.findViewById<EditText>(R.id.additionalInfoPhoneNumberEditText)
        phoneNumberEditText?.setText("invalid")
        val phoneButton = fragment.view?.findViewById<RadioButton>(R.id.additionalInfoPhoneButton)
        phoneButton?.isChecked = true
        assertEquals(false, fragment.checkInputFields("invalid", "Phone"))

    }

    @Test
    fun testCheckInputFields_ValidInput_ReturnsTrue(){
        val phoneNumberEditText = fragment.view?.findViewById<EditText>(R.id.additionalInfoPhoneNumberEditText)
        phoneNumberEditText?.setText("1234567890")
        val phoneButton = fragment.view?.findViewById<RadioButton>(R.id.additionalInfoPhoneButton)
        phoneButton?.isChecked = true
        assertEquals(true, fragment.checkInputFields("1234567890", "Phone"))

    }
}



