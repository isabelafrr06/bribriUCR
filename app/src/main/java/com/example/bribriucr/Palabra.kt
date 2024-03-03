package com.example.bribriucr

import android.net.Uri
import java.io.Serializable

data class Palabra(val IdPalabra: Int, val NombrePalabra: String, val RutaImagen: String,
                   val RutaAudio: String): Serializable {

    fun getRutaAudio(): Uri {
        return Uri.parse(RutaAudio)
    }

    fun getRutaImagen(): Uri {
        return Uri.parse(RutaImagen)
    }
}
