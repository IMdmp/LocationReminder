package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import com.udacity.project4.utils.wrapEspressoIdlingResource
import org.koin.android.ext.android.inject
import timber.log.Timber

class SelectLocationFragment : BaseFragment(), OnMapReadyCallback,
    OnCompleteListener<LocationSettingsResponse> {


    companion object {
        private const val PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1
        private const val DEFAULT_ZOOM = 15

        private const val REQUEST_CHECK_SETTINGS =90

    }

    private var selectedMarker: Marker?=null
    private var selectedTitle: String?=null
    private var lastKnownLocation: Location? = null
    private lateinit var mFusedLocationProviderClient: FusedLocationProviderClient
    private var locationPermissionGranted: Boolean = false
    private lateinit var map: GoogleMap

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private var selectedLatLng:LatLng?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)
        mFusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity());


        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        wrapEspressoIdlingResource {
            getLocationPermission()
            checkLocationSettings()
        }

        _viewModel.mapSelectedEvent.observe(viewLifecycleOwner, Observer { isSelected ->
            binding.btnConfirm.isEnabled = isSelected
        })

        binding.btnConfirm.setOnClickListener {
            selectedLatLng?.let {
                wrapEspressoIdlingResource {
                    onLocationSelected(it)
                }
            }
        }

        return binding.root
    }

    private fun checkLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(requireActivity())
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            getDeviceLocation()

            Timber.d("done. ${locationSettingsResponse}")
        }

        task.addOnFailureListener{ exception->
            if (exception is ResolvableApiException){
                Timber.d("request denied.")
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(requireActivity(),
                        REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
        task.addOnCanceledListener {
            _viewModel.showErrorMessage.value="Request denied."

        }
    }

    private fun onLocationSelected(latLng: LatLng) {

        _viewModel.updateLocation(latLng,selectedTitle)
        _viewModel.navigationCommand.value = NavigationCommand.Back
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID

            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

//        TODO: add style to the map
        setMapLongClick(map)
        setPoiClick(map)

    }

    override fun onComplete(task: Task<LocationSettingsResponse>) {
        wrapEspressoIdlingResource {
            task.result?.let {
                zoomToPosition(it)
            }
        }
    }

    private fun setMapLongClick(map: GoogleMap) {
        //        TODO: put a marker to location that the user selected
        map.setOnMapLongClickListener { latLng ->
            selectedMarker?.remove()
            selectedMarker = map.addMarker(
                MarkerOptions()
                    .position(latLng)
            )
            _viewModel.mapSelected()
            selectedLatLng = latLng
        }
    }
    private fun setPoiClick(map: GoogleMap) {
        selectedMarker?.remove()

        map.setOnPoiClickListener { poi ->
            selectedMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )
            selectedMarker!!.showInfoWindow()
            _viewModel.mapSelected()
            selectedLatLng = poi.latLng
            selectedTitle = poi.name
        }
    }
    private fun zoomToPosition(result: LocationSettingsResponse) {
    }

    private fun getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(
                requireActivity().applicationContext,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    locationPermissionGranted = true
                }
            }
        }
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                    val locationResult = mFusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Set the map's camera position to the current location of the device.
                        lastKnownLocation = task.result
                        Timber.d("last known location? $lastKnownLocation")
                        lastKnownLocation?.let {
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        it.latitude,
                                        it.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            );

                        }
                    } else {
                        Timber.e("Exception:${task.exception}")
                        Timber.d("Current location is null. Using defaults.")
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}
