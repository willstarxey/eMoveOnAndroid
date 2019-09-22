package com.blackmark.emoveon.maps

import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast

import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.blackmark.emoveon.R
import com.blackmark.emoveon.sendPackage.SendMainActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

class MapaEnd : FragmentActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerDragListener {

    private lateinit var mMap: GoogleMap
    var fin: Point? = null
    private lateinit var inicio: Point
    lateinit var gmapsURL: String

    internal var btn: Button? = null

    //VARIABLES PARA LA LLAMADA A LA API DE GOOGLE
    internal var jsonObjectRequest: JsonObjectRequest? = null
    internal var request: RequestQueue? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mapa_end)
        request = Volley.newRequestQueue(applicationContext)
        btn = findViewById(R.id.next_map_end)
        inicio = Point(
            intent.getDoubleExtra("inicioLat", 0.toDouble()),
            intent.getDoubleExtra("inicioLng", 0.toDouble())
        )
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapEnd) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        btn!!.setOnClickListener {
            if (fin?.lat == 0.toDouble() || fin?.lng == 0.toDouble()) {
                Toast.makeText(
                    this@MapaEnd,
                    "Por favor, Marca un punto final para tu ruta",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Utilidades.coordenadas.latInicial = inicio.lat
                Utilidades.coordenadas.lngInicial = inicio.lng
                Utilidades.coordenadas.latFinal = fin?.lat
                Utilidades.coordenadas.lngFinal = fin?.lng
                webServiceGetRoute(
                    inicio.lat.toString(),
                    inicio.lng.toString(),
                    fin!!.lat.toString(),
                    fin!!.lng.toString()
                )
                val intent = Intent(this, SendMainActivity::class.java)
                intent.putExtra("inicioLat", inicio.lat)
                intent.putExtra("inicioLng", inicio.lng)
                intent.putExtra("finLat", fin!!.lat)
                intent.putExtra("finLng", fin!!.lng)
                intent.putExtra("gmapsURL", gmapsURL)
                /*Toast.makeText(
                    this,
                    inicio.lat.toString() + "," + inicio.lng.toString() + "\n" + fin?.lat.toString() + "," + fin?.lng,
                    Toast.LENGTH_SHORT
                ).show()*/
                startActivity(intent)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
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
        mMap.isMyLocationEnabled = true
        mMap.setOnMarkerDragListener(this)
        mMap.setOnMapLongClickListener(this)
    }

    override fun onMarkerDragStart(marker: Marker) {
        Toast.makeText(this, "Moviendo marcador", Toast.LENGTH_SHORT).show()
    }

    override fun onMarkerDrag(marker: Marker) {

    }

    override fun onMarkerDragEnd(marker: Marker) {
        fin = Point(marker.position.latitude, marker.position.longitude)
    }

    override fun onMapLongClick(latLng: LatLng) {
        mMap.clear()
        mMap.addMarker(
            MarkerOptions().position(latLng).title("Fin de Ruta").icon(
                BitmapDescriptorFactory.fromResource(R.drawable.point)
            ).draggable(true)
        )
        mMap.projection.toScreenLocation(latLng)
        fin = Point(latLng.latitude, latLng.longitude)
        Toast.makeText(this, "Nuevo punto marcado", Toast.LENGTH_SHORT).show()
    }

    private fun decodePoly(encoded: String): List<LatLng> {

        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0

        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }

        return poly
    }

    private fun webServiceGetRoute(
        latitudInicial: String,
        longitudInicial: String,
        latitudFinal: String,
        longitudFinal: String
    ) {
        val url =
            ("https://maps.googleapis.com/maps/api/directions/json?key=AIzaSyDTOXtUAxRJO1-Pef4KVpriKapjZ-iRB0M&origin=" + latitudInicial + "," + longitudInicial
                    + "&destination=" + latitudFinal + "," + longitudFinal)
        gmapsURL = url
//AIzaSyBv3LJ0kBCqiHAfkD5RZp4ojz-R2GTF8EE
        jsonObjectRequest =
            JsonObjectRequest(Request.Method.GET, url, null, Response.Listener { response ->
                //Toast.makeText(this@MapaEnd, response.toString(), Toast.LENGTH_LONG).show()
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                var jRoutes: JSONArray? = null
                var jLegs: JSONArray? = null
                var jSteps: JSONArray? = null

                try {

                    jRoutes = response.getJSONArray("routes")

                    /** Traversing all routes  */

                    /** Traversing all routes  */
                    for (i in 0 until jRoutes!!.length()) {
                        jLegs = (jRoutes!!.get(i) as JSONObject).getJSONArray("legs")
                        val path = ArrayList<HashMap<String, String>>()

                        /** Traversing all legs  */

                        /** Traversing all legs  */
                        for (j in 0 until jLegs!!.length()) {
                            jSteps = (jLegs!!.get(j) as JSONObject).getJSONArray("steps")

                            /** Traversing all steps  */

                            /** Traversing all steps  */
                            for (k in 0 until jSteps!!.length()) {
                                var polyline = ""
                                polyline =
                                    ((jSteps!!.get(k) as JSONObject).get("polyline") as JSONObject).get(
                                        "points"
                                    ) as String
                                val list = decodePoly(polyline)

                                /** Traversing all points  */

                                /** Traversing all points  */
                                for (l in list.indices) {
                                    val hm = HashMap<String, String>()
                                    hm["lat"] = java.lang.Double.toString(list[l].latitude)
                                    hm["lng"] = java.lang.Double.toString(list[l].longitude)
                                    path.add(hm)
                                }
                            }
                            Utilidades.routes.add(path)
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                }
            }, Response.ErrorListener { error ->
                Toast.makeText(this, "No se puede conectar $error", Toast.LENGTH_LONG)
                    .show()
                println()
                Log.d("ERROR: ", error.toString())
            }
            )

        request?.add(jsonObjectRequest)
    }
}
