package com.example.myapplication

import android.os.Build
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class student_profileUnitTest {

    private lateinit var fragment: student_profile

    @Before
    fun setup(){
        fragment = student_profile()
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
    fun testViewsNotNull(){
        assertNotNull(fragment.view?.findViewById<ImageView>(R.id.profilepic))
        assertNotNull(fragment.view?.findViewById<TextView>(R.id.nameField))
        assertNotNull(fragment.view?.findViewById<Button>(R.id.SearchTutor))
        assertNotNull(fragment.view?.findViewById<Button>(R.id.logout))
        assertNotNull(fragment.view?.findViewById<Button>(R.id.student_personalInformation))
    }

    @Test
    fun testSearchUser_ButtonNavigation(){
        val navController = NavController(ApplicationProvider.getApplicationContext())
        Navigation.setViewNavController(fragment.requireView(), navController)

        fragment.view?.findViewById<Button>(R.id.SearchTutor)?.performClick()

        assertNotNull(navController.currentDestination?.id)
        assert(navController.currentDestination?.id == R.id.to_search_user)
    }

    @Test
    fun testLogout_ButtonNavigation(){
        val navController = NavController(ApplicationProvider.getApplicationContext())
        Navigation.setViewNavController(fragment.requireView(), navController)

        fragment.view?.findViewById<Button>(R.id.logout)?.performClick()

        assertNotNull(navController.currentDestination?.id)
        assert(navController.currentDestination?.id == R.id.to_login)
    }

    @Test
    fun testPersonalInformation_ButtonNavigation(){
        val navController = NavController(ApplicationProvider.getApplicationContext())
        Navigation.setViewNavController(fragment.requireView(), navController)

        fragment.view?.findViewById<Button>(R.id.student_personalInformation)?.performClick()

        assertNotNull(navController.currentDestination?.id)
        assert(navController.currentDestination?.id == R.id.to_studentPersonalInformation)
    }

    class FragmentTestActivity : FragmentActivity() {}
}