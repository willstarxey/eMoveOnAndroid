package com.blackmark.emoveon.clases

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class Package(
    var idPack: String = "",
    var idUser: String = "",
    var concept : String = "",
    var gMapsURL: String = "",
    var location: String = "",
    var destinatary: String? = "",
    var dimentions: String? = "",
    var weight: String? = "",
    var cost: String? = "",
    var deliver: String? ="",
    var status: String = ""
)