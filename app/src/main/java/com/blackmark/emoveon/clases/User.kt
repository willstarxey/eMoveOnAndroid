package com.blackmark.emoveon.clases

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*

@IgnoreExtraProperties
data class User(
    var userId: String? = "",
    var userName: String? = "",
    var email: String? = "",
    var stripeAcc: String? = ""
)