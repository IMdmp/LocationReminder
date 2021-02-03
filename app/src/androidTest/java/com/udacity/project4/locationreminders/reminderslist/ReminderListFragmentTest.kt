package com.udacity.project4.locationreminders.reminderslist

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.R
import com.udacity.project4.locationreminders.FakeRemindersLocalRepository
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.local.RemindersLocalRepositoryTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
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

//    TODO: test the navigation of the fragments.
//    TODO: test the displayed data on the UI.
//    TODO: add testing for the error messages.
//navigation command can be used to ttest
    private lateinit var remindersLocalRepositoryTest: RemindersLocalRepositoryTest
    private lateinit var sampleRepo: FakeRemindersLocalRepository



    @Before
    fun setUpRepository() {
        sampleRepo = FakeRemindersLocalRepository()
    }

    fun getActiveTasksTest()= runBlocking {
        // GIVEN - Add active task to the DB
        val sampleData = ReminderDTO("title", "desc", "location", 1.0, 2.0)
        sampleRepo.saveReminder(sampleData)


        // WHEN - Details fragment launched to display task
        val bundle = ReminderListFragment()
        launchFragmentInContainer<ReminderListFragment>( themeResId = R.style.AppTheme)

    }
}