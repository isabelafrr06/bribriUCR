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
/**
 * Helper class for managing the application's database interactions.
 *
 * This class handles creating tables, inserting data, retrieving data, and
 * upgrading the database schema when necessary.
 */
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

    private val ctx: Context = context
    // Table creation statements
    val createTablapalabra =
        "CREATE TABLE $Tabla_Palabra( IdPalabra INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ColumnaNombre TEXT NOT NULL, " +
                "$ColumnaImagen TEXT NOT NULL, " +
                "$ColumnaAudio TEXT NOT NULL, " +
                "Aprendido BOOLEAN DEFAULT 0," +
                "NombreCategoria TEXT, " +
                "CONSTRAINT FKCATEGORIA FOREIGN KEY(NombreCategoria) REFERENCES $Tabla_Categoria(IdCategoria));"
    val createTablaCategoriasReceta =
        "CREATE TABLE $Tabla_Categoria( IdCategoria INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NombreCategoria TEXT NOT NULL, ImagenCategoria TEXT, PorcentajeCategoria INTEGER);"
    /**
     * Inserts a new food item into the database.
     *
     * @param db The writable database instance.
     * @param nombre The name of the food item.
     * @param rutaImagen The path to the image resource for the food item.
     * @param rutaAudio The path to the audio resource for the food item.
     * @return True if the insertion was successful, false otherwise.
     */
    private fun insertarPalabra(db: SQLiteDatabase, nombre: String, rutaImagen: String,
                                rutaAudio: String, categoria: String): Boolean {

        val values = ContentValues().apply {
            put(ColumnaNombre, nombre)
            put(ColumnaImagen, rutaImagen)
            put(ColumnaAudio, rutaAudio)
            put(ColumnaCategoria, categoria)
        }
        val inserted = db.insert(Tabla_Palabra, null, values)
        // Return true if insertion succeeded (row ID greater than 0), false otherwise
        return inserted != -1L
    }

    /**
     * Called when the database is first created.
     *
     * This method creates the tables for storing words (food items, utensils, steps) and categories.
     * It also inserts initial category data from the "recetario.json" asset file.
     *
     * @param sqLiteDatabase The database instance.
     */
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(createTablaCategoriasReceta)
        sqLiteDatabase.execSQL(createTablapalabra)
        sqLiteDatabase.beginTransaction()  // Start a transaction for efficiency
        try {
            val insertStatement = "INSERT INTO $Tabla_Categoria (NombreCategoria, ImagenCategoria) VALUES (?, ?)"
            val compiledStatement = sqLiteDatabase.compileStatement(insertStatement)
            val categories = listOf("Animales", "Vegetales", "Utensilios", "Recetas")
            // Images representing each category
            val images = listOf(ctx.resources.getIdentifier("sawe", "drawable", ctx.packageName).toString(),
                ctx.resources.getIdentifier("quelite", "drawable", ctx.packageName).toString(),
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
        val categories = listOf("Animales", "Vegetales", "Utensilios", "Pasos")
        for (category in categories) {
            val list = recetario.getJSONArray(category)
            for (i in 0 until list.length()) {
                val item = list.getJSONObject(i)
                val nombre = item.getString("nombre")
                val imageName = item.getString("imagen")
                val imageId = ctx.resources.getIdentifier(imageName, "drawable", ctx.packageName)
                val rutaAudio = item.getString("audio")// "android.resource://" + ctx.packageName + "/raw/" + item.getString("audio")
                val audioId = ctx.resources.getIdentifier(rutaAudio, "raw", ctx.packageName)
                if (category in categories) {
                    insertarPalabra(db, nombre, imageId.toString(), audioId.toString(), category)  // Insert item
                } else {
                    Log.e(TAG, "Unknown category: $category")
                }
            }
        }
    }
    /**
     * Called when the database needs to be upgraded.
     *
     * This method drops the existing table and recreates it with the new schema.
     *
     * @param sqLiteDatabase The database instance.
     * @param oldVersion The old version number of the database.
     * @param newVersion The new version number of the database.
     */
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $Tabla_Palabra")
        onCreate(sqLiteDatabase)
    }

    /* Inserts a new word (food item, utensil, step) into the database.
    *
    * @param palabra The word object containing information to insert.
    */
    fun insertarPalabra(palabra: Palabra ){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(ColumnaNombre, palabra.nombrePalabra)
            put(ColumnaImagen, palabra.rutaImagen)
            put(ColumnaAudio, palabra.rutaAudio)
        }
        db.insert(Tabla_Palabra, null, values)
        db.close()
    }
    /**
     * Retrieves all categories from the database.
     *
     * @param sqLiteDatabase The database instance.
     * @return A list of `Categoria` objects representing all categories.
     */
    fun getCategorias(sqLiteDatabase: SQLiteDatabase): ArrayList<Categoria> {
        val query = "select * from $Tabla_Categoria;"
        val cursor: Cursor = sqLiteDatabase.rawQuery(query, null)
        val categorias = ArrayList<Categoria>()
        for (i in 0 until cursor.count) {
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
        cursor.close()
        return categorias
    }

    fun getImagesForCategory(db: SQLiteDatabase, category: String): ArrayList<Palabra> {
        val imagesList = ArrayList<Palabra>()
        val query = "SELECT * FROM $Tabla_Palabra WHERE NombreCategoria = ?"
        val cursor = db.rawQuery(query, arrayOf(category))

        for (i in 0 until cursor.count) {
            if (cursor.moveToPosition(i)) {
                val id = cursor.getInt(0) // Assuming the first column is the ID
                val nombre = cursor.getString(1)
                val imagen = cursor.getString(2)
                val audio = cursor.getString(3)

                val palabra = Palabra(id, nombre, imagen, audio)
                imagesList.add(palabra)
            }
        }

        cursor.close()
        return imagesList
    }

    fun getAllImages(db: SQLiteDatabase): ArrayList<Palabra> {
        val imagesList = ArrayList<Palabra>()
        val query = "SELECT * FROM $Tabla_Palabra"
        val cursor = db.rawQuery(query, null)
        for (i in 0 until cursor.count) {
            if (cursor.moveToPosition(i)) {
                val id = cursor.getInt(0) // Assuming the first column is the ID
                val nombre = cursor.getString(1)
                val imagen = cursor.getString(2)
                val audio = cursor.getString(3)

                val palabra = Palabra(id, nombre, imagen, audio)
                imagesList.add(palabra)
            }
        }
        cursor.close()
        return imagesList
    }
}
