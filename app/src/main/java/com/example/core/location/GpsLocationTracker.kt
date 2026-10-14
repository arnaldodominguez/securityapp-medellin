package com.example.core.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class GpsStatus {
    object Searching : GpsStatus()
    data class Locked(val latitude: Double, val longitude: Double, val accuracy: Float) : GpsStatus()
    object NoSignal : GpsStatus()
    object PermissionRequired : GpsStatus()
}

class GpsLocationTracker(private val context: Context) {
    private val _gpsStatus = MutableStateFlow<GpsStatus>(GpsStatus.Searching)
    val gpsStatus: StateFlow<GpsStatus> = _gpsStatus.asStateFlow()

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private var locationCallback: LocationCallback? = null
    private var legacyListener: LocationListener? = null
    private var isTracking = false

    fun hasLocationPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        return fine || coarse
    }

    @SuppressLint("MissingPermission")
    fun startTracking() {
        if (!hasLocationPermission()) {
            _gpsStatus.value = GpsStatus.PermissionRequired
            return
        }

        if (isTracking) return
        isTracking = true
        _gpsStatus.value = GpsStatus.Searching

        // Check if GPS provider is enabled
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        if (!isGpsEnabled && !isNetworkEnabled) {
            _gpsStatus.value = GpsStatus.NoSignal
            return
        }

        // Try getting last known location first for immediate lock
        fusedClient.lastLocation.addOnSuccessListener { loc ->
            if (loc != null) {
                _gpsStatus.value = GpsStatus.Locked(loc.latitude, loc.longitude, loc.accuracy)
            }
        }.addOnFailureListener {
            // Keep searching
        }

        // Fused Location Request (High accuracy, 3s interval)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 3000L)
            .setMinUpdateIntervalMillis(1500L)
            .setMinUpdateDistanceMeters(2.0f)
            .build()

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                val loc = result.lastLocation
                if (loc != null) {
                    _gpsStatus.value = GpsStatus.Locked(loc.latitude, loc.longitude, loc.accuracy)
                } else {
                    _gpsStatus.value = GpsStatus.NoSignal
                }
            }
        }

        fusedClient.requestLocationUpdates(request, locationCallback!!, Looper.getMainLooper())

        // Fallback Native LocationManager listener
        legacyListener = object : LocationListener {
            override fun onLocationChanged(loc: Location) {
                if (_gpsStatus.value !is GpsStatus.Locked) {
                    _gpsStatus.value = GpsStatus.Locked(loc.latitude, loc.longitude, loc.accuracy)
                }
            }
            override fun onProviderDisabled(provider: String) {
                if (provider == LocationManager.GPS_PROVIDER && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    _gpsStatus.value = GpsStatus.NoSignal
                }
            }
            override fun onProviderEnabled(provider: String) {}
            @Deprecated("Deprecated in Java")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        try {
            if (isGpsEnabled) {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000L,
                    2.0f,
                    legacyListener!!,
                    Looper.getMainLooper()
                )
            }
        } catch (_: Exception) {}
    }

    fun stopTracking() {
        isTracking = false
        locationCallback?.let { fusedClient.removeLocationUpdates(it) }
        legacyListener?.let { locationManager.removeUpdates(it) }
        locationCallback = null
        legacyListener = null
    }

    /**
     * Allows simulated GPS location for testing or emulator environments
     */
    fun setManualLocation(latitude: Double, longitude: Double) {
        _gpsStatus.value = GpsStatus.Locked(latitude, longitude, 5.0f)
    }

    fun simulateNoSignal() {
        _gpsStatus.value = GpsStatus.NoSignal
    }
}
