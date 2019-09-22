package com.blackmark.emoveon.maps

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.blackmark.emoveon.R

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.blackmark.emoveon.clases.Package
import com.blackmark.emoveon.dashboard.DashboardActivity
import com.google.android.gms.maps.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_details_package.*
import java.util.*
import kotlin.collections.ArrayList

class DetailsPackageActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var pack: Package
    private lateinit var inicio: Point
    private lateinit var fin: Point
    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details_package)
        //Detalles del paquete
        //Initializa Firebase Database
        database = FirebaseDatabase.getInstance()
        ref = database.reference
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapDetails) as SupportMapFragment
        mapFragment.getMapAsync(this)
        detailsPackage()
        setUpButton()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        /////////////
        var center: LatLng? = null
        var points: ArrayList<LatLng>? = null
        var lineOptions: PolylineOptions? = null
        // recorriendo todas las rutas
        for (i in Utilidades.routes.indices) {
            points = ArrayList()
            lineOptions = PolylineOptions()
            // Obteniendo el detalle de la ruta
            val path = Utilidades.routes[i]
            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for (j in path.indices) {
                val point = path[j]
                val lat = java.lang.Double.parseDouble(point["lat"]!!)
                val lng = java.lang.Double.parseDouble(point["lng"]!!)
                val position = LatLng(lat, lng)
                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = LatLng(lat, lng)
                }
                points.add(position)
            }
            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points)
            //Definimos el grosor de las Polilíneas
            lineOptions.width(4f)
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLACK)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        // Dibujamos las Polilineas en el Google Map para cada ruta
        if (lineOptions == null) {
            Toast.makeText(this, "La ruta no se ha podido trazar", Toast.LENGTH_LONG).show()
        } else {
            mMap.addPolyline(lineOptions)
        }

        mMap.isMyLocationEnabled = true
        val inicioMarker = LatLng(inicio.lat, inicio.lng)
        val finMarker = LatLng(fin.lat, fin.lng)
        mMap.addMarker(
            MarkerOptions().position(inicioMarker).title("Inicio de Ruta").icon(
                BitmapDescriptorFactory.fromResource(R.drawable.point)
            )
        )
        mMap.addMarker(
            MarkerOptions().position(finMarker).title("Fin de Ruta").icon(
                BitmapDescriptorFactory.fromResource(R.drawable.point)
            )
        )
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(inicioMarker, 14f))
    }

    private fun setUpButton() {
        confirmBtn.setOnClickListener {
            ref.child("packages").child(pack.idPack).setValue(pack)
            startActivity(
                Intent(this, DashboardActivity::class.java)
            )

        }
    }

    private fun detailsPackage() {
        if (intent.getBooleanExtra("envio", true)) {
            pack = Package(
                UUID.randomUUID().toString(),
                FirebaseAuth.getInstance().currentUser!!.uid,
                intent.getStringExtra("concept"),
                intent.getStringExtra("gmapsURL"),
                "",
                intent.getStringExtra("destinatary"),
                intent.getStringExtra("dimentions"),
                intent.getStringExtra("weight"),
                "",
                "",
                "espera"
            )
            findViewById<TextView>(R.id.deliver).visibility = View.GONE
            findViewById<TextView>(R.id.status).visibility = View.GONE
            findViewById<TextView>(R.id.textView7).visibility = View.GONE
            findViewById<TextView>(R.id.textView8).visibility = View.GONE
        } else {
            pack = Package(
                intent.getStringExtra("idPack"),
                intent.getStringExtra("idUser"),
                intent.getStringExtra("concept"),
                intent.getStringExtra("gmapsURL"),
                intent.getStringExtra("location"),
                intent.getStringExtra("destinatary"),
                intent.getStringExtra("dimentions"),
                intent.getStringExtra("weight"),
                intent.getStringExtra("cost"),
                intent.getStringExtra("deliver"),
                intent.getStringExtra("status")
            )
            findViewById<TextView>(R.id.deliver).visibility = View.VISIBLE
            findViewById<TextView>(R.id.status).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textView7).visibility = View.VISIBLE
            findViewById<TextView>(R.id.textView8).visibility = View.VISIBLE
            findViewById<TextView>(R.id.confirmBtn).visibility = View.GONE
        }
        //Seteo de datos a los campos
        findViewById<TextView>(R.id.concept).text = pack.concept
        findViewById<TextView>(R.id.cost).text = pack.cost
        findViewById<TextView>(R.id.destinatary).text = pack.destinatary
        findViewById<TextView>(R.id.dimentions).text = pack.dimentions
        findViewById<TextView>(R.id.weight).text = pack.weight
        findViewById<TextView>(R.id.deliver).text = pack.deliver
        if (pack.status == "espera") {
            findViewById<TextView>(R.id.status).text = "En espera"
        } else if (pack.status == "camino") {
            findViewById<TextView>(R.id.status).text = "En camino"
        } else {
            findViewById<TextView>(R.id.status).text = "Entregado"
        }
        inicio = Point(
            intent.getDoubleExtra("inicioLat", 0.toDouble()),
            intent.getDoubleExtra("inicioLng", 0.toDouble())
        )
        fin = Point(
            intent.getDoubleExtra("finLat", 0.toDouble()),
            intent.getDoubleExtra("finLng", 0.toDouble())
        )
    }

}

