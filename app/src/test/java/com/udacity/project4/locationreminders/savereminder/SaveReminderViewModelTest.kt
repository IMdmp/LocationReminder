package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.TestCase.assertEquals

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var saveReminderViewModel: SaveReminderViewModel

    @Before
    fun setup() {

    }


    @Test
    fun clearDataTest() {
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(), FakeDataSource()
        )
        saveReminderViewModel.onClear()

        val lat = saveReminderViewModel.latitude.getOrAwaitValue()
        val long = saveReminderViewModel.longitude.getOrAwaitValue()
        val reminderTitle = saveReminderViewModel.reminderTitle.getOrAwaitValue()
        val reminderDescription = saveReminderViewModel.reminderDescription.getOrAwaitValue()
        val reminderSelectedLocationStr =
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue()
        val selectedPOI = saveReminderViewModel.selectedPOI.getOrAwaitValue()

        MatcherAssert.assertThat(lat, CoreMatchers.nullValue())
        MatcherAssert.assertThat(long, CoreMatchers.nullValue())
        MatcherAssert.assertThat(reminderTitle, CoreMatchers.nullValue())
        MatcherAssert.assertThat(reminderDescription, CoreMatchers.nullValue())
        MatcherAssert.assertThat(reminderSelectedLocationStr, CoreMatchers.nullValue())
        MatcherAssert.assertThat(selectedPOI, CoreMatchers.nullValue())
    }


    @Test
    fun emptyTitleShowsError() {

        val reminderDataItem = ReminderDataItem(null, "desc", "loca", 1.0, 1.0)

        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(), FakeDataSource()
        )

        saveReminderViewModel.validateAndSaveReminder(reminderDataItem)
        val value = saveReminderViewModel.showSnackBarInt.getOrAwaitValue()


        val titleError = R.string.err_enter_title

        assertEquals(value, titleError)
    }


}