package com.blackmark.emoveon.mypackages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.blackmark.emoveon.R
import com.blackmark.emoveon.clases.Package
import com.blackmark.emoveon.maps.DetailsPackageActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.ArrayList


class PackagesActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var ref: DatabaseReference
    var packages: ArrayList<Package> = ArrayList()
    var lista: RecyclerView? = null
    var layoutManager: RecyclerView.LayoutManager? = null
    var adapter: PackageAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_packages)
        database = FirebaseDatabase.getInstance()
        ref = database.reference
        lista = findViewById(R.id.list)
        getPackageInfo()
        setUpPackage()
    }

    private fun setUpPackage() {
        layoutManager = LinearLayoutManager(this)
        adapter = PackageAdapter(packages, object : ClickListener {
            override fun onClick(vista: View, position: Int) {
                Toast.makeText(
                    this@PackagesActivity,
                    packages.get(position).idPack,
                    Toast.LENGTH_LONG
                ).show()
                startActivity(
                    Intent(
                        this@PackagesActivity,
                        DetailsPackageActivity::class.java
                    )
                        .putExtra("idPack", packages.get(position).idPack)
                        .putExtra("idUser", packages.get(position).idUser)
                        .putExtra("concept", packages.get(position).concept)
                        .putExtra("gmapsURL", packages.get(position).gMapsURL)
                        .putExtra("location", packages.get(position).location)
                        .putExtra("destinatary", packages.get(position).destinatary)
                        .putExtra("dimentions", packages.get(position).dimentions)
                        .putExtra("weight", packages.get(position).weight)
                        .putExtra("cost", packages.get(position).cost)
                        .putExtra("deliver", packages.get(position).deliver)
                        .putExtra("status", packages.get(position).status)
                        .putExtra("envio",false)
                )
            }

        })
        lista!!.layoutManager = layoutManager
        lista!!.adapter = adapter
    }

    private fun getPackageInfo(){
        ref.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (dataPack in p0.child("packages").children) {
                    val d = dataPack.getValue(Package::class.java)
                    val p: Package = d!!
                    Log.i("SUCCESS", p.toString())
                    if (p.idUser == FirebaseAuth.getInstance().currentUser.toString()) {
                        Log.i("SUCCESS INTO IF", p.toString())
                        packages.add(p)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })

        packages.add(
            Package(
                UUID.randomUUID().toString(),
                FirebaseAuth.getInstance().currentUser.toString(),
                "AquiToy"
            )
        )
        packages.add(
            Package(
                UUID.randomUUID().toString(),
                FirebaseAuth.getInstance().currentUser.toString(),
                "AquiToy2"
            )
        )
    }
}
