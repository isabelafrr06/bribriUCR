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
import com.example.bribriucr.ui.learn.FragmentLearnModel
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
/**
 * Helper class for managing the application's database interactions.
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
        private const val TABLA_PALABRA = "PALABRA"
        private const val TABLA_CATEGORIA = "CATEGORIA"
        private const val ColumnaNombre = "NombrePalabra"
        private const val ColumnaAudio = "RutaImagen"
        private const val ColumnaImagen = "RutaAudio"
        private const val ColumnaCategoria = "NombreCategoria"
    }

    private val ctx: Context = context
    // Table creation statements
    private val createTablapalabra =
        "CREATE TABLE $TABLA_PALABRA( IdPalabra INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$ColumnaNombre TEXT NOT NULL, " +
                "$ColumnaImagen TEXT NOT NULL, " +
                "$ColumnaAudio TEXT NOT NULL, " +
                "Aprendido BOOLEAN DEFAULT 0," +
                "NombreCategoria TEXT, " +
                "CONSTRAINT FKCATEGORIA FOREIGN KEY(NombreCategoria) REFERENCES $TABLA_CATEGORIA(IdCategoria));"
    private val createTablaCategorias =
        "CREATE TABLE $TABLA_CATEGORIA( IdCategoria INTEGER PRIMARY KEY AUTOINCREMENT," +
                "NombreCategoria TEXT NOT NULL, ImagenCategoria TEXT, PorcentajeCategoria INTEGER);"

    /**
     * Inserts a new item into the database.
     *
     * @param db The writable database instance.
     * @param nombre The name of the food item.
     * @param rutaImagen The path to the image resource for the food item.
     * @param rutaAudio The path to the audio resource for the food item.
     * @param categoria The category of the word item.
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
        val inserted = db.insert(TABLA_PALABRA, null, values)
        // Return true if insertion succeeded (row ID greater than 0), false otherwise
        return inserted != -1L
    }

    /**
     * Called when the database is first created.
     * This method creates the tables for storing words (food items, utensils, steps) and categories.
     * It also inserts initial category data from the "recetario.json" asset file.
     *
     * @param sqLiteDatabase The database instance.
     */
    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {
        sqLiteDatabase.execSQL(createTablaCategorias)
        sqLiteDatabase.execSQL(createTablapalabra)
        insertInitialCategoryData(sqLiteDatabase)

        try {
            // Open the "recetario.json" file from the assets folder (using "use")
            this.ctx.assets.open("recetario.json").use { input ->
                // Create a buffered reader to read the file (using "use")
                BufferedReader(InputStreamReader(input)).use { reader ->
                    val stringBuilder = StringBuilder()
                    var line: String?
                    while (reader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    val recetarioJson = stringBuilder.toString()

                    // Parse the JSON string into a JSONObject
                    val recetario = JSONObject(recetarioJson)
                    // Insert data from the parsed JSON into database
                    insertDataFromRecetario(sqLiteDatabase, recetario)
                }
            }
        } catch (e: IOException) {
            // Log descriptive error message with tag "TAG" for IOException
            Log.e(TAG, "Error processing recetario.json", e)
        } catch (e: JSONException) {
            // Log a descriptive error message for JSONException
            Log.e(TAG, "Error parsing JSON", e)
        }
    }

    private fun insertInitialCategoryData(db: SQLiteDatabase) {
        db.beginTransaction()  // Start a transaction for efficiency
        try {
            val insertStatement = "INSERT INTO $TABLA_CATEGORIA (NombreCategoria, ImagenCategoria) VALUES (?, ?)"
            val compiledStatement = db.compileStatement(insertStatement)
            val categories = listOf("Animales", "Vegetales", "Utensilios")
            // Images representing each category
            val images = listOf(R.drawable.sawe.toString(),
                R.drawable.quelite.toString(),
                R.drawable.cuchillo.toString())

            for (i in categories.indices) {
                compiledStatement.bindString(1, categories[i])
                compiledStatement.bindString(2, images[i])
                compiledStatement.executeInsert()
            }
            db.setTransactionSuccessful()  // Commit changes if no errors
        } catch (e: Exception) {
            // Handle potential insert errors (optional)
        } finally {
            db.endTransaction()  // End transaction regardless of success or failure
        }
    }

    /**
     * Inserts data from a recipe JSON object into a SQLite database.
     *
     * This function iterates through predefined categories ("Animales", "Vegetales", "Utensilios", "Pasos")
     * within the provided `recetario` JSONObject. For each category, it extracts a JSONArray and loops
     * through its items (JSONObject). Each item's attributes ("nombre", "imagen", "audio") are retrieved
     * and used to potentially insert data into the database using the `insertarPalabra` function.
     *
     * @param db The SQLiteDatabase instance to insert data into.
     * @param recetario The JSONObject containing recipe data.
     */
    private fun insertDataFromRecetario(db: SQLiteDatabase, recetario: JSONObject) {
        val categories = listOf("Animales", "Vegetales", "Utensilios")
        for (category in categories) {
            val list = recetario.getJSONArray(category)
            for (i in 0 until list.length()) {
                val item = list.getJSONObject(i)
                val nombre = item.getString("nombre")
                var imageName = item.getString("imagen")
                if (imageName == "") {imageName = "0"}  // Set to "0" if not found
                val imageId = ctx.resources.getIdentifier(imageName, "drawable", ctx.packageName)
                var rutaAudio = item.getString("audio")// "android.resource://" + ctx.packageName + "/raw/" + item.getString("audio")
                if (rutaAudio == "") {rutaAudio = "0"}  // Set to "0" if not found
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
     * This method drops the existing table and recreates it with the new schema.
     *
     * @param sqLiteDatabase The database instance.
     * @param oldVersion The old version number of the database.
     * @param newVersion The new version number of the database.
     */
    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS $TABLA_PALABRA")
        onCreate(sqLiteDatabase)
    }

    /** Retrieves all categories from the database.
     *
     * @param sqLiteDatabase The database instance.
     * @return A list of `Categoria` objects representing all categories.
     */
    fun getCategories(sqLiteDatabase: SQLiteDatabase): ArrayList<Categoria> {
        val query = "select * from $TABLA_CATEGORIA;"
        val cursor: Cursor = sqLiteDatabase.rawQuery(query, null)
        val categorias = ArrayList<Categoria>()
        for (i in 0 until cursor.count) {
            cursor.moveToPosition(i) // Move to the current position
            val id = cursor.getInt(0) // Obtain values from columns
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
            ) // Create a Category object
        }
        cursor.close()
        return categorias
    }

    /** Retrieves images and associated data for a given category from the database.
     * @param db The SQLiteDatabase instance to query.
     * @param category The name of the category to retrieve images for.
     * @return An ArrayList of Palabra objects representing the retrieved images and data,
     *          or an empty list if no images are found for the category.
     */
    fun getImagesForCategory(db: SQLiteDatabase, category: String): ArrayList<Palabra> {
        val imagesList = ArrayList<Palabra>()
        val query: String
        val cursor: Cursor
        // Query to fetch words for a specific category
        if(category != "") {
            query = "SELECT * FROM $TABLA_PALABRA WHERE NombreCategoria = ?"
            // Execute the query with the provided category
            cursor = db.rawQuery(query, arrayOf(category))
        }else{ // For all words
            query = "SELECT * FROM $TABLA_PALABRA"
            // Execute the query
            cursor = db.rawQuery(query, null)
        }
        // Iterate through the results
        for (i in 0 until cursor.count) {
            if (cursor.moveToPosition(i)) {
                // Extract data from the current row
                val id = cursor.getInt(0)  // ID is the first column
                val nombre = cursor.getString(1)
                val imagen = cursor.getString(2)
                val audio = cursor.getString(3)
                // Create a new Palabra object with the extracted data
                val palabra = Palabra(id, nombre, imagen, audio)
                // Add the Palabra object to the list
                imagesList.add(palabra)
            }
        }
        // Ensure cursor is closed to release resources
        cursor.close()
        // Return the list of retrieved Palabra objects
        return imagesList
    }

    /**
     * Updates the "Aprendido" status of a word item in the database.
     *
     * @param db The writable database instance.
     * @param itemName The name of the word item.
     * @param aprendido The new "Aprendido" status (true/false).
     * @param categoria The category of the word item.
     */
    fun updateAprendido(db: SQLiteDatabase, itemName: String, aprendido: Boolean, categoria: String) {
        val updateQuery = "UPDATE $TABLA_PALABRA SET Aprendido = ? WHERE $ColumnaNombre = ?"
        val contentValues = ContentValues().apply {
            put("Aprendido", aprendido)
            put(ColumnaNombre, itemName)  // Assuming 'nombre' is the unique identifier for items
        }
        db.update(TABLA_PALABRA, contentValues, "$ColumnaNombre = ?", arrayOf(itemName))
        updateCategoriaPorcentaje(db, categoria)
    }

    /**
     * Calculates the percentage of learned words within a specific category.
     *
     * @param db The SQLiteDatabase instance.
     * @param categoria The name of the category.
     * @return The percentage of learned words (rounded to integer).
     */
    private fun calculatePorcentaje(db: SQLiteDatabase, categoria: String): Int {
        val totalWordsQuery = "SELECT COUNT(*) FROM $TABLA_PALABRA WHERE NombreCategoria = ?"
        val learnedWordsQuery = "SELECT COUNT(*) FROM $TABLA_PALABRA WHERE NombreCategoria = ? AND Aprendido = 1"

        val compiledTotalStatement = db.compileStatement(totalWordsQuery)
        val compiledLearnedStatement = db.compileStatement(learnedWordsQuery)

        compiledTotalStatement.bindString(1, categoria)
        compiledLearnedStatement.bindString(1, categoria)

        val totalCursor = db.rawQuery(totalWordsQuery, arrayOf(categoria))  // Execute the query
        val learnedCursor = db.rawQuery(learnedWordsQuery, arrayOf(categoria))  // Execute the query

        var totalWords = 0
        var learnedWords = 0

        if (totalCursor.moveToFirst()) {
            totalWords = totalCursor.getInt(0)  // Assuming the first column is the count
        }

        if (learnedCursor.moveToFirst()) {
            learnedWords = learnedCursor.getInt(0)  // Assuming the first column is the count
        }

        totalCursor.close()
        learnedCursor.close()
        var porcentaje = 0F
        if (totalWords > 0) {
            porcentaje = (learnedWords.toFloat() / totalWords) * 100
        } else {
            Log.w("Calculate Percentage", "Category not found")
        }
        return porcentaje.toInt()
    }

    /**
     * Updates the "PorcentajeCategoria" value for a specific category in the database.
     *
     * @param db The SQLiteDatabase instance.
     * @param categoria The name of the category.
     */
    private fun updateCategoriaPorcentaje(db: SQLiteDatabase, categoria: String) {
        val porcentaje = calculatePorcentaje(db, categoria)
        val updateStatement = "UPDATE $TABLA_CATEGORIA SET PorcentajeCategoria = ? WHERE NombreCategoria = ?"
        val compiledStatement = db.compileStatement(updateStatement)
        compiledStatement.bindLong(1, porcentaje.toLong())
        compiledStatement.bindString(2, categoria)
        compiledStatement.executeUpdateDelete()  // Execute the update
    }

    /**
     * Retrieves all words from the database.
     *
     * @param sqLiteDatabase The database instance.
     * @return A list of `FragmentLearnModel` objects representing all words.
     */
    fun getWords(sqLiteDatabase: SQLiteDatabase): ArrayList<FragmentLearnModel> {
        val query = "select * from $TABLA_PALABRA;"
        val cursor: Cursor = sqLiteDatabase.rawQuery(query, null)
        val palabras = ArrayList<FragmentLearnModel>()
        for (i in 0 until cursor.count) {
            cursor.moveToPosition(i) // Move to the current position
            val id = cursor.getInt(0) // Obtain values from columns
            val nombre = cursor.getString(1)
            var imagen = ""
            if (cursor.getString(2) != null) {
                imagen = cursor.getString(2)
            }
            var audio = ""
            if (cursor.getString(3) != null) {
                audio = cursor.getString(3)
            }
            var isLearn = cursor.getString(4).toBoolean()
            palabras.add(
                FragmentLearnModel(
                    nombre,
                    imagen.toInt(),
                    audio.toInt(),
                    isLearn
                )
            ) // Create an object
        }
        cursor.close()
        return palabras
    }
}
