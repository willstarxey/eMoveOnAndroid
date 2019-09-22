package com.blackmark.emoveon.maps

import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.blackmark.emoveon.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapaInit : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerDragListener {

    private lateinit var mMap: GoogleMap
    private lateinit var inicio: Point
    private lateinit var lastLocation: Location
    private var fusedLocationClient: FusedLocationProviderClient? = null

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_init)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapInit) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        mMap.isMyLocationEnabled = true
        /*fusedLocationClient!!.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }*/
        mMap.setOnMarkerDragListener(this)
        mMap.setOnMapLongClickListener(this)
    }

    override fun onMarkerDragStart(marker: Marker) {
        Toast.makeText(this, "Moviendo marcador", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerDrag(marker: Marker) {

    }

    override fun onMarkerDragEnd(marker: Marker) {
        inicio.lat = marker.position.latitude
        inicio.lng = marker.position.longitude
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(
            MarkerOptions().position(latLng).title("Inicio de Ruta").icon(
                BitmapDescriptorFactory.fromResource(R.drawable.point)
            ).draggable(true)
        )
        mMap.projection.toScreenLocation(latLng)
        inicio = Point(latLng.latitude, latLng.longitude)
        Toast.makeText(this, "Inicio de ruta marcado", Toast.LENGTH_SHORT).show()
    }

    fun onClickInit(view: View) {
        if (inicio.lat == 0.0 || inicio.lng == 0.0) {
            Toast.makeText(
                this,
                "Por favor, Marca un punto de inicio para tu ruta",
                Toast.LENGTH_LONG
            ).show()
        } else {
            val intent = Intent(this, MapaEnd::class.java)
            intent.putExtra("inicioLat", inicio.lat)
            intent.putExtra("inicioLng", inicio.lng)
            //Toast.makeText(this, inicio.lat.toString()+","+inicio.lng, Toast.LENGTH_SHORT).show()
            startActivity(intent)
        }
    }
}
