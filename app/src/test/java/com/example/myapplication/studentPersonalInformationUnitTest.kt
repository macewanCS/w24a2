package com.example.myapplication

import android.os.Build
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.mockito.Mockito.*
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class studentPersonalInformationUnitTest {

    private lateinit var fragment: studentPersonalInformation

    @Before
    fun setup(){
        fragment = studentPersonalInformation()
        startFragment(fragment)
    }

    private fun startFragment(fragment: Fragment){
        val activityScenario = ActivityScenario.launch(FragmentTestActivity::class.java)
        activityScenario.onActivity { activity ->
            activity.supportFragmentManager.beginTransaction().add(android.R.id.content, fragment, null)
      }
  }

    @Test
    fun testFragmentNotNull(){
        assertNotNull(fragment)
    }

    @Test
    fun testBackButton_Setup(){
        val view = fragment.view!!
        val navController = mock(NavController::class.java)
        Navigation.setViewNavController(view, navController)

        view.findViewById<Button>(R.id.back_btn).performClick()

        verify(navController).navigate(R.id.to_student_profile)
    }

  class FragmentTestActivity : FragmentActivity() {}
}