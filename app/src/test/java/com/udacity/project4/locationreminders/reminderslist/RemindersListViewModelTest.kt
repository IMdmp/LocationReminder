package com.udacity.project4.locationreminders.reminderslist

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var remindersListViewModel: RemindersListViewModel

    @Before
    fun setup() {
        remindersListViewModel =
            RemindersListViewModel(ApplicationProvider.getApplicationContext(), FakeDataSource())
    }

    @Test
    fun loadTaskTest() {

        remindersListViewModel.loadReminders()

        val value = remindersListViewModel.remindersList.getOrAwaitValue()

        assertThat(value, not(nullValue()))
    }
    //TODO: provide testing to the RemindersListViewModel and its live data objects

}