package com.udacity.project4.locationreminders.data.local

import android.app.Application
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
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
// Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {
    private lateinit var application: Application
    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    private val newReminder = ReminderDTO("Title1", "Description1", "Location1", 1.1, 1.2)

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(application, RemindersDatabase::class.java)
            .allowMainThreadQueries().build()
        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun cleanUp() = database.close()

    @Test
    fun saveReminder_retrieveReminder() = runBlocking {
        // GIVEN - a new saved reminder

        repository.saveReminder(newReminder)

        // WHEN - reminder retrieved by id
        val result = repository.getReminder(newReminder.id) as Result.Success

        // THEN - same reminder is returned
        assertThat(result.data.title, `is`(newReminder.title))
        assertThat(result.data.description, `is`(newReminder.description))
        assertThat(result.data.location, `is`(newReminder.location))
        assertThat(result.data.latitude, `is`(newReminder.latitude))
        assertThat(result.data.longitude, `is`(newReminder.longitude))
        assertThat(result.data.id, `is`(newReminder.id))
    }

    @Test
    fun getReminder_returnError() = runBlocking {
        repository.deleteAllReminders()

        val result = repository.getReminder(newReminder.id) as Result.Error

        assertThat(result.message, `is`("Reminder not found!"))
    }
}