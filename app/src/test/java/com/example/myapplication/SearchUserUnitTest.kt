package com.example.myapplication

import android.os.Build
import android.widget.EditText
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowToast

@RunWith(RobolectricTestRunner::class)
@Config(sdk = intArrayOf(Build.VERSION_CODES.O_MR1))
class SearchUserUnitTest {

    private lateinit var fragment: SearchUser

    @Before
    fun setup(){
        fragment = SearchUser()
        startFragment(fragment)
    }

    private fun startFragment(fragment: Fragment){
        val activityScenario = ActivityScenario.launch(FragmentTestActivity::class.java)
        activityScenario.onActivity { activity ->
            activity.supportFragmentManager.beginTransaction().add(android.R.id.content, fragment, null).commit()
        }
    }

    @Test
    fun testFragmentNotNull(){
        assertNotNull(fragment)
    }

    @Test
    fun testViewInitialized(){
        val view = fragment.view!!
        assertNotNull(view.findViewById<ImageButton>(R.id.back_btn))
        assertNotNull(view.findViewById<ImageButton>(R.id.search_user_btn))
        assertNotNull(view.findViewById<RecyclerView>(R.id.search_user_recycler_view))
        assertNotNull(view.findViewById<EditText>(R.id.search_username_input))
    }

    @Test
    fun testBackButtonNavigation(){
        val view = fragment.view!!
        val navController = mock(NavController::class.java)
        Navigation.setViewNavController(view, navController)

        view.findViewById<ImageButton>(R.id.back_btn).performClick()

        verify(navController).navigate(R.id.to_student_profile)
    }

    @Test
    fun testSearchButtonAction(){
        val view = fragment.view!!
        val searchTerm = "example"
        val searchInput = view.findViewById<EditText>(R.id.search_username_input)
        searchInput.setText(searchTerm)

        val navController = mock(NavController::class.java)
        Navigation.setViewNavController(view, navController)

        view.findViewById<ImageButton>(R.id.search_user_btn).performClick()
        verify(navController).navigate(R.id.to_search_user)
    }

    @Test
    fun testEmptySearchTermError(){
        val view = fragment.view!!
        view.findViewById<ImageButton>(R.id.search_user_btn).performClick()
        val error = ShadowToast.getTextOfLatestToast()
        assertEquals(fragment.getString(R.string.invalid_user), error)
    }

    class FragmentTestActivity : FragmentActivity() {}

}