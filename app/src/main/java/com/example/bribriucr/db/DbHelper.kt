package com.example.bribriucr.db

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.bribriucr.Categoria
import com.example.bribriucr.Palabra
import com.example.bribriucr.R
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        // Database version and name
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "bribri.db"
        // Table and column names
        private const val Tabla_Palabra = "PALABRA"
        private const val Tabla_Categoria = "CATEGORIA"
        private const val ColumnaNombre = "NombrePalabra"
        private const val ColumnaAudio = "RutaImagen"
        private const val ColumnaImagen = "RutaAudio"
        private const val ColumnaCategoria = "NombreCategoria"
    }

    val ctx: Context = context
    // Table creation statements
    val create_TablaPalabra =
        "CREATE TABLE $Tabla_Palabra( IdPalabra INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ColumnaNombre TEXT NOT NULL, " +
                "$ColumnaImagen TEXT NOT NULL, " +
                "$ColumnaAudio TEXT NOT NULL, " +
                "NombreCategoria TEXT, " +
                "CONSTRAINT FKCATEGORIA FOREIGN KEY(NombreCategoria) REFERENCES $Tabla_Categoria(NombreCategoria));"
    val create_TablaCategoriasReceta =
        "CREATE TABLE $Tabla_Categoria( IdCategoria INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NombreCategoria TEXT NOT NULL, ImagenCategoria TEXT, PorcentajeCategoria INTEGER);"

    private fun insertarAlimento(db: SQLiteDatabase, nombre: String, rutaImagen: String,
                                 rutaAudio: String): Boolean {

        val values = ContentValues().apply {
            put(ColumnaNombre, nombre)
            put(ColumnaImagen, rutaImagen)
            put(ColumnaAudio, rutaAudio)
            put(ColumnaCategoria, R.string.alimento)
        }
        val inserted = db.insert(Tabla_Palabra, null, values)
        // Return true if insertion succeeded (row ID greater than 0), false otherwise
        return inserted != -1L
    }
    private fun insertarPaso(
        db:SQLiteDatabase,
        nombre: String,
        texto: String,
        rutaImagen: String,
        rutaAudio: String
    ): Boolean {
        val values = ContentValues().apply {
            put(ColumnaNombre, nombre)
            put(ColumnaImagen, rutaImagen)
            put(ColumnaAudio, rutaAudio)
            put(ColumnaCategoria, R.string.paso)
        }
        val inserted = db.insert(Tabla_Palabra, null, values)
        // Return true if insertion succeeded (row ID greater than 0), false otherwise
        return inserted != -1L
    }

    /**
     * Metodo encargado de formular una sentencia SQL para este quede almacenado en la base de datos
     * @param nombre, el nombre del utencilio por insertar
     * @param rutaImagen, contiene la ruta de la ubicacion de la imagen en los archivos de la aplicacion
     * @param rutaAudio, contiene la ruta de la ubicacion de los audios en los archivos de la aplicacion
     */
    fun insertarUtensilio(db: SQLiteDatabase, nombre: String, rutaImagen: String, rutaAudio: String): Boolean {
        val values = ContentValues().apply {
            put(ColumnaNombre, nombre)
            put(ColumnaImagen, rutaImagen)
            put(ColumnaAudio, rutaAudio)
            put(ColumnaCategoria, R.string.utencilio)
        }
        val inserted = db.insert(Tabla_Palabra, null, values)
        // Return true if insertion succeeded (row ID greater than 0), false otherwise
        return inserted != -1L
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(create_TablaCategoriasReceta)
        sqLiteDatabase.execSQL(create_TablaPalabra)
        sqLiteDatabase.beginTransaction()  // Start a transaction for efficiency
        try {
            val insertStatement = "INSERT INTO $Tabla_Categoria (NombreCategoria, ImagenCategoria) VALUES (?, ?)"
            val compiledStatement = sqLiteDatabase.compileStatement(insertStatement)
            val categories = listOf("Animales", "Vegetales", "Utencilios", "Recetas")
            // Images representing each category
            val images = listOf(ctx.resources.getIdentifier("carne_asada1", "drawable", ctx.packageName).toString(),
                ctx.resources.getIdentifier("bananomaduro", "drawable", ctx.packageName).toString(),
                ctx.resources.getIdentifier("cuchillo", "drawable", ctx.packageName).toString(),
                ctx.resources.getIdentifier("chichas4", "drawable", ctx.packageName).toString())

            for (i in categories.indices) {
                compiledStatement.bindString(1, categories[i])
                compiledStatement.bindString(2, images[i])
                compiledStatement.executeInsert()
            }
            sqLiteDatabase.setTransactionSuccessful()  // Commit changes if no errors
        } catch (e: Exception) {
            // Handle potential insert errors (optional)
        } finally {
            sqLiteDatabase.endTransaction()  // End transaction regardless of success or failure
        }

        var recetarioJson = ""
        try {
            val input: InputStream = this.ctx.assets.open("recetario.json")
            val bufferedReader = BufferedReader(InputStreamReader(input))
            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            bufferedReader.close()
            input.close()
            recetarioJson = stringBuilder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // Process data from recetario.json (with resource management)
        try {
            val input = ctx.assets.open("recetario.json")
            val reader = BufferedReader(InputStreamReader(input))
            val stringBuilder = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                stringBuilder.append(line)
            }
            reader.close()
            input.close()
            val recetarioJson = stringBuilder.toString()

            val recetario = JSONObject(recetarioJson)
            insertDataFromRecetario(sqLiteDatabase, recetario)
        } catch (e: IOException) {
            Log.e(TAG, "Error processing recetario.json", e)
        } catch (e: JSONException) {
            Log.e(TAG, "Error parsing JSON", e)
        }

        fun eliminarPalabra(nombre: String) {
            sqLiteDatabase.delete(
                Tabla_Palabra,
                "$ColumnaNombre = \'$nombre\'", null
            )
        }
    }

    private fun insertDataFromRecetario(db: SQLiteDatabase, recetario: JSONObject) {
        val categories = listOf("alimentos", "utensilios", "pasos")
        for (category in categories) {
            val list = recetario.getJSONArray(category)
            for (i in 0 until list.length()) {
                val item = list.getJSONObject(i)
                val nombre = item.getString("nombre")
                val imageName = item.getString("imagen")
                val imageId = ctx.resources.getIdentifier(imageName, "drawable", ctx.packageName)
                val rutaAudio = "android.resource://" + ctx.packageName + "/raw/" + item.getString("audio")

                when (category) {
                    "alimentos" -> insertarAlimento(db, nombre, imageId.toString(), rutaAudio)  // Insert food item
                    "utensilios" -> insertarUtensilio(db, nombre, imageId.toString(), rutaAudio)  // Insert utensil
                    "pasos" -> insertarPaso(db, nombre, item.getString("texto"), imageId.toString(), rutaAudio)  // Insert step
                    else -> Log.e(TAG, "Unknown category: $category")
                }
            }
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

    fun getCategorias(sqLiteDatabase: SQLiteDatabase): ArrayList<Categoria> {
        val query = "select * from $Tabla_Categoria;"
        val cursor: Cursor = sqLiteDatabase.rawQuery(query, null)
        val categorias = ArrayList<Categoria>()
        for (i in 0 until cursor.count) {
            if (cursor != null) {
                cursor.moveToPosition(i) // Move to the current position
                val id = cursor.getInt(0) // Se obtienen los valores de las 4 columnas
                val nombre = cursor.getString(1)
                var imagen = ""
                if (cursor.getString(2) != null) {
                    imagen = cursor.getString(2)
                }
                val porcentaje = cursor.getInt(3)
                categorias.add(
                    Categoria(
                        id,
                        nombre,
                        imagen,
                        porcentaje
                    )
                ) // Se crea un objeto Categoria con los datos
            }
        }
        cursor.close()
        return categorias
    }
}
