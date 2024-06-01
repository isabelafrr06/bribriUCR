package com.example.bribriucr

import android.net.Uri
import java.io.Serializable

data class Palabra(val idPalabra: Int, val nombrePalabra: String, val rutaImagen: String,
                   val rutaAudio: String): Serializable {

    fun getRutaAudio(): Uri {
        return Uri.parse(rutaAudio)
    }

    fun getRutaImagen(): Uri {
        return Uri.parse(rutaImagen)
    }
}
