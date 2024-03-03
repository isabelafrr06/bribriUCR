package com.example.bribriucr.ui

class TopicCard {

    var name: String? = null
    var image: String? = null
    var percentage: String? = null

    constructor(nombre: String, imagen: String, porcentaje: String) {
        this.name = nombre
        this.image = imagen
        this.percentage = porcentaje
    }
}