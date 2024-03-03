package com.example.bribriucr.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.bribriucr.Palabra
import com.example.bribriucr.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class DbHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "bribri.db"
        private const val Tabla_Palabra = "PALABRA"
        private const val Tabla_Categoria = "CATEGORIA"
        private const val ColumnaNombre = "NombrePalabra"
        private const val ColumnaAudio = "RutaImagen"
        private const val ColumnaImagen = "RutaAudio"
    }

    private val ctx: Context? = context


    //se crean las tablas
    val create_TablaPalabra =
        "CREATE TABLE $Tabla_Palabra( IdPalabra INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ColumnaNombre TEXT NOT NULL, " +
                "$ColumnaImagen TEXT NOT NULL, " +
                "$ColumnaAudio TEXT NOT NULL, " +
                "NombreCategoria TEXT, " +
                "CONSTRAINT FKCATEGORIA FOREIGN KEY(NombreCategoria) REFERENCES $Tabla_Categoria(NombreCategoria));"
    val create_TablaCategoriasReceta =
        "CREATE TABLE $Tabla_Categoria( IdCategoria INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NombreCategoria TEXT NOT NULL);"

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(create_TablaCategoriasReceta)
        sqLiteDatabase.execSQL(create_TablaPalabra)
        sqLiteDatabase.execSQL(
            "insert into $Tabla_Categoria(NombreCategoria) values (\"" + ctx!!.getString(R.string.carnes) + "\"),(\""
                    + ctx.getString(R.string.verduras) + "\"),(\"" + ctx.getString(R.string.bebidas) + "\")"
        )

        var recetarioJson = ""
        try {
            val `in`: InputStream = this.ctx.assets.open("recetario.json")
            val bufferedReader = BufferedReader(InputStreamReader(`in`))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
            `in`.close()
            recetarioJson = stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        /*fun getCategorias(): ArrayList<String>? {
            val query =
                "select * from " + Tabla_Categoria + ";"
            val cursor: Cursor = sqLiteDatabase.rawQuery(query, null)
            val palabras = ArrayList<String>()
            cursor.moveToFirst()
            do {
                palabras.add(cursor.getString(cursor.getColumnIndex("NombreCategoria")))
            } while (cursor.moveToNext())
            cursor.close()
            return palabras
        }*/

        fun eliminarPalabra(nombre: String) {
            sqLiteDatabase.delete(
                Tabla_Palabra,
                "$ColumnaNombre = \'$nombre\'", null
            )
        }

    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $Tabla_Palabra")
        onCreate(sqLiteDatabase)
    }

    fun insertarPalabra(palabra: Palabra ){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ColumnaNombre, palabra.NombrePalabra)
            put(ColumnaImagen, palabra.RutaImagen)
            put(ColumnaAudio, palabra.RutaAudio)
        }
        db.insert(Tabla_Palabra, null, values)
        db.close()
    }
}