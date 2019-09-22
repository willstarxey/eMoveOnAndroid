package com.blackmark.emoveon.maps

class Point {

    var lat: Double = 0.toDouble()
    var lng: Double = 0.toDouble()

    constructor() {}

    constructor(lat: Double, lng: Double) {
        this.lat = lat
        this.lng = lng
    }

}
