package com.udacity.project4.locationreminders.reminderslist

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.FakeRemindersLocalRepository
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
//UI Testing
@MediumTest
class ReminderListFragmentTest {

    private lateinit var sampleRepo: FakeRemindersLocalRepository


    @Before
    fun setUpRepository() {
        sampleRepo = FakeRemindersLocalRepository()
    }

    @Test
    fun getActiveTasksTest() = runBlockingTest {

        // GIVEN - Add active task to the DB
        val mockTitle = "title"
        val sampleData = ReminderDTO(mockTitle, "desc", "location", 1.0, 2.0)
        sampleRepo.saveReminder(sampleData)


        // WHEN - Details fragment launched to display task
        launchFragmentInContainer<ReminderListFragment>(themeResId = R.style.AppTheme)

        onView(ViewMatchers.withText(mockTitle)).check(
            ViewAssertions.matches(
                ViewMatchers.withText(
                    sampleData.title
                )
            )
        )

    }

    @Test
    fun clickAddReminderButton_navigateToSaveReminderFragment() {
        val scenario =
            launchFragmentInContainer<ReminderListFragment>(themeResId = R.style.AppTheme)

        val navController = mock(NavController::class.java)
        scenario.onFragment {
            Navigation.setViewNavController(it.view!!, navController)
        }


        onView(withId(R.id.addReminderFAB)).perform(click())

        verify(navController).navigate(
            ReminderListFragmentDirections.toSaveReminder()
        )
    }
}