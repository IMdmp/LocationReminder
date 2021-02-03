package com.udacity.project4.locationreminders.savereminder

import android.annotation.SuppressLint
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PointOfInterest
import com.udacity.project4.R
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.ReminderDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.geofence.GeofenceBroadcastReceiver
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.launch
import timber.log.Timber

class SaveReminderViewModel(val app: Application, val dataSource: ReminderDataSource) :
    BaseViewModel(app) {
    val reminderTitle = MutableLiveData<String>()
    val reminderDescription = MutableLiveData<String>()
    val reminderSelectedLocationStr = MutableLiveData<String>()
    val selectedPOI = MutableLiveData<PointOfInterest>()
    val latitude = MutableLiveData<Double>()
    val longitude = MutableLiveData<Double>()

    val mapSelectedEvent = MutableLiveData<Boolean>()
    private lateinit var geofencingClient: GeofencingClient

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(app, GeofenceBroadcastReceiver::class.java)
        intent.action = SaveReminderFragment.ACTION_GEOFENCE_EVENT
        // Use FLAG_UPDATE_CURRENT so that you get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        PendingIntent.getBroadcast(app, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }



    init {
        mapSelectedEvent.value = false
        geofencingClient = LocationServices.getGeofencingClient(app)


    }
    /**
     * Clear the live data objects to start fresh next time the view model gets called
     */
    fun onClear() {
        reminderTitle.value = null
        reminderDescription.value = null
        reminderSelectedLocationStr.value = null
        selectedPOI.value = null
        latitude.value = null
        longitude.value = null
    }

    /**
     * Validate the entered data then saves the reminder data to the DataSource
     */
    fun validateAndSaveReminder(reminderData: ReminderDataItem) {
        if (validateEnteredData(reminderData)) {
            setGeoFence(reminderData)
            saveReminder(reminderData)
        }
    }


    /**
     * Save the reminder to the data source
     */
    private fun saveReminder(reminderData: ReminderDataItem) {
        showLoading.value = true
        viewModelScope.launch {
            dataSource.saveReminder(
                ReminderDTO(
                    reminderData.title,
                    reminderData.description,
                    reminderData.location,
                    reminderData.latitude,
                    reminderData.longitude,
                    reminderData.id
                )
            )
            showLoading.value = false
            showToast.value = app.getString(R.string.reminder_saved)
            navigationCommand.value = NavigationCommand.Back
        }
    }

    /**
     * Validate the entered data and show error to the user if there's any invalid data
     */
    private fun validateEnteredData(reminderData: ReminderDataItem): Boolean {
        if (reminderData.title.isNullOrEmpty()) {
            showSnackBarInt.value = R.string.err_enter_title
            return false
        }

        if (reminderData.location.isNullOrEmpty()) {
             showSnackBarInt.value = R.string.err_select_location
            return false
        }

        if (reminderData.latitude ==null) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }

        if (reminderData.longitude==null) {
            showSnackBarInt.value = R.string.err_select_location
            return false
        }
        return true
    }

    fun mapSelected() {
        mapSelectedEvent.value = true
    }

    fun updateLocation(
        latLng: LatLng,
        selectedLatLng: String?
    ) {
        latitude.value = latLng.latitude
        longitude.value = latLng.longitude
        reminderSelectedLocationStr.value = selectedLatLng?:latLng.toString()
    }


    @SuppressLint("MissingPermission")
    private fun setGeoFence(reminderData:ReminderDataItem) {

        //pending geofence code.
        val geofence = Geofence.Builder()
            .setCircularRegion(
                reminderData.latitude!!,
                reminderData.longitude!!,
                GEOFENCE_RADIUS_IN_METERS
            )
            .setRequestId(reminderData.location)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
            .setExpirationDuration(GEOFENCE_EXPIRATION_IN_MILLISECONDS)
            .build()

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geofence)
            .build()

        geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
            addOnSuccessListener {
                Timber.d("Add Geofence ${geofence.requestId}")
            }

            addOnFailureListener {
                Timber.e("Error adding geofence")

            }
        }
    }
}