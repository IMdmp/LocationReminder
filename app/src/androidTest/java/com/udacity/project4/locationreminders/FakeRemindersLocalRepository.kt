package com.udacity.project4.locationreminders

import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import java.lang.Exception

class FakeRemindersLocalRepository : ReminderDataSource {

    val reminderDtoList = mutableListOf<ReminderDTO>()

    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        return reminderDtoList?.let {
            Result.Success(it)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        reminderDtoList.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        return try {
            Result.Success(reminderDtoList.first {
                it.id == id
            })
        } catch (exception: Exception) {
            Result.Error("No data found.")
        }
    }

    override suspend fun deleteAllReminders() {
        reminderDtoList.clear()
    }

}