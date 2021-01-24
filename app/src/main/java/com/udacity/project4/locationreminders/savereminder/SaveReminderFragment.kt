package com.udacity.project4.locationreminders.savereminder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSaveReminderBinding
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import timber.log.Timber

class SaveReminderFragment : BaseFragment() {
    //Get the view model this time as a single to be shared with the another fragment
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSaveReminderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_save_reminder, container, false)

        setDisplayHomeAsUpEnabled(true)

        binding.viewModel = _viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.selectLocation.setOnClickListener {
            //            Navigate to another fragment to get the user location
            _viewModel.navigationCommand.value =
                NavigationCommand.To(SaveReminderFragmentDirections.actionSaveReminderFragmentToSelectLocationFragment())
        }

        binding.saveReminder.setOnClickListener {
            val title = _viewModel.reminderTitle.value
            val description = _viewModel.reminderDescription
            val location = _viewModel.reminderSelectedLocationStr.value
            val latitude = _viewModel.latitude
            val longitude = _viewModel.longitude.value

//            TODO: use the user entered reminder details to:
//             1) add a geofencing request
//             2) save the reminder to the local db

            //pending geofence code.
//            val geofence = Geofence.Builder()
//                .setRequestId(currentGeofenceData.id)
//                .setCircularRegion(currentGeofenceData.latLong.latitude,
//                    currentGeofenceData.latLong.longitude,
//                    GeofencingConstants.GEOFENCE_RADIUS_IN_METERS
//                )
//                .setExpirationDuration(GeofencingConstants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
//                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
//                .build()
//
//            val geofencingRequest = GeofencingRequest.Builder()
//                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
//                .addGeofence(geofence)
//                .build()
//
//            geofencingClient.addGeofences(geofencingRequest, geofencePendingIntent)?.run {
//                addOnSuccessListener {
//                    Timber.d("Add Geofence", geofence.requestId)
//                    viewModel.geofenceActivated()
//                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //make sure to clear the view model after destroy, as it's a single view model.
        _viewModel.onClear()
    }
}
