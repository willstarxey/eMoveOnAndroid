package com.blackmark.emoveon.dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.blackmark.emoveon.R
import com.blackmark.emoveon.login.LoginActivity
import com.blackmark.emoveon.maps.MapaInit
import com.blackmark.emoveon.mypackages.PackagesActivity
import com.blackmark.emoveon.sendPackage.SendMainActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var nameUser: String
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        val label = findViewById<TextView>(R.id.welcomeName)
        label.text = FirebaseAuth.getInstance().currentUser!!.displayName
        setupUI()
    }


    private fun setupUI() {
        newPack.setOnClickListener {
            startActivity(
                Intent(this, MapaInit::class.java)
            )
        }
        listPacks.setOnClickListener {
            startActivity(
                Intent(this, PackagesActivity::class.java)
            )
        }
        myData.setOnClickListener {

        }
        logOut.setOnClickListener {
            val alertDialog = AlertDialog.Builder(this@DashboardActivity)
            alertDialog.setTitle("Cerrar Sesión")
            alertDialog.setMessage("¿Estas seguro de cerrar tu sesión?")
            alertDialog.setPositiveButton("Si") { dialogInterface, which ->
                auth.signOut()
                startActivity(
                    Intent(this, LoginActivity::class.java)
                )
                finish()
            }
            alertDialog.setNegativeButton("No") { _, _ -> }
            alertDialog.create()
            alertDialog.show()
        }
    }

}
