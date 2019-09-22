package com.blackmark.emoveon.sendPackage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import com.blackmark.emoveon.R
import com.blackmark.emoveon.maps.DetailsPackageActivity
import com.blackmark.emoveon.maps.Point
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_send_main.*

class SendMainActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    private lateinit var concept: String
    private lateinit var destinatary: String
    private lateinit var dimentions: String
    private lateinit var weight: String
    private lateinit var gmapsURL: String
    private lateinit var inicio: Point
    private lateinit var fin: Point


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance()
        ref = database.reference
        setContentView(R.layout.activity_send_main)
        inicio = Point(
            intent.getDoubleExtra("inicioLat",0.toDouble()),
            intent.getDoubleExtra("inicioLng",0.toDouble())
        )
        fin = Point(
            intent.getDoubleExtra("finLat",0.toDouble()),
            intent.getDoubleExtra("finLng",0.toDouble())
        )
        setupUI()
    }

    private fun setupUI() {
        nextSend.setOnClickListener {
            concept = findViewById<EditText>(R.id.conceptS).text.toString()
            destinatary = findViewById<EditText>(R.id.destinataryS).text.toString()
            dimentions = findViewById<EditText>(R.id.dimentionsS).text.toString()
            weight = findViewById<EditText>(R.id.weightS).text.toString()
            destinatary = findViewById<EditText>(R.id.destinataryS).text.toString()
            gmapsURL = intent.getStringExtra("gmapsURL")
            startActivity(
                Intent(this, DetailsPackageActivity::class.java)
                    .putExtra("concept", concept)
                    .putExtra("destinatary", destinatary)
                    .putExtra("dimentions", dimentions)
                    .putExtra("weight", weight)
                    .putExtra("envio", true)
                    .putExtra("inicioLat", inicio.lat)
                    .putExtra("inicioLng", inicio.lng)
                    .putExtra("finLat", fin!!.lat)
                    .putExtra("finLng", fin!!.lng)
                    .putExtra("gmapsURL", gmapsURL)
            )
        }
    }
}
