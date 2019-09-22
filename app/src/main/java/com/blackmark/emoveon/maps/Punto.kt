package com.blackmark.emoveon.maps

class Punto {

    var latInicial: Double? = null
    var lngInicial: Double? = null
    var latFinal: Double? = null
    var lngFinal: Double? = null

    override fun toString(): String {
        return "Punto{" +
                "latInicial=" + latInicial +
                ", lngInicial=" + lngInicial +
                ", latFinal=" + latFinal +
                ", lngFinal=" + lngFinal +
                '}'.toString()
    }
}