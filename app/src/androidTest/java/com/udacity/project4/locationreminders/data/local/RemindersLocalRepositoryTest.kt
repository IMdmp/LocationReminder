package com.udacity.project4.locationreminders.data.local

import FakeDataSource
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {


    val reminder1 = ReminderDTO("title", "desc", "location", 1.0, 2.0)
    val reminder2= ReminderDTO("title2", "desc2", "location2", 1.0, 2.0)
    val reminder3 = ReminderDTO("title3", "desc3", "location3", 1.0, 2.0)


    private val remoteReminders = listOf<ReminderDTO>(reminder1,reminder2)
    private val localRemidners = listOf<ReminderDTO>(reminder3)

    private lateinit var tasksRemoteDataSource:FakeDataSource
    private lateinit var tasksLocalDataSource: FakeDataSource


    
//
//    TODO: Add testing implementation to the RemindersLocalRepository.kt

}