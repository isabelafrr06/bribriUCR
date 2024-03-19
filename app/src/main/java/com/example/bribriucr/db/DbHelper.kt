package com.example.bribriucr.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
        /*sqLiteDatabase.execSQL(
            "insert into $Tabla_Categoria(NombreCategoria) values (\"" + ctx!!.getString(R.string.carnes) + "\"),(\""
                    + ctx.getString(R.string.verduras) + "\"),(\"" + ctx.getString(R.string.bebidas) + "\"),(\"" + ctx.getString(R.string.alimento) + "\")," +
                    "(\"" + ctx.getString(R.string.utencilio) + "\"), (\"" + ctx.getString(R.string.paso) + "\")"
        )*/
        sqLiteDatabase.beginTransaction()  // Start a transaction for efficiency
        try {
            val insertStatement = "INSERT INTO $Tabla_Categoria (NombreCategoria, ImagenCategoria) VALUES (?, ?)"
            val compiledStatement = sqLiteDatabase.compileStatement(insertStatement)

            val categories = listOf(
                "Carnes", "Verduras", "Bebidas", "Alimento", "Utencilio", "Recetas"
            )
            // Images representing each category
            val images = listOf(ctx.resources.getIdentifier("carne_asada1", "drawable", ctx.packageName).toString(),
                ctx.resources.getIdentifier("bananomaduro", "drawable", ctx.packageName).toString(),
                ctx.resources.getIdentifier("agua", "drawable", ctx.packageName).toString(),
                ctx.resources.getIdentifier("cacao", "drawable", ctx.packageName).toString(),
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
        try {
            val recetario = JSONObject(recetarioJson)
            var list = recetario.getJSONArray("alimentos")
            for (i in 0 until list.length()) {
                val alimento = list.getJSONObject(i)
                val imageName = alimento.getString("imagen")
                val imageId = ctx.resources.getIdentifier(imageName, "drawable", ctx.packageName)  // Get ID
                insertarAlimento(
                    sqLiteDatabase, alimento.getString("nombre"),
                    imageId.toString(),
                    "android.resource://" + ctx.packageName + "/raw/" + alimento.getString("audio")
                )
            }
            list = recetario.getJSONArray("utensilios")
            for (i in 0 until list.length()) {
                val utensilio = list.getJSONObject(i)
                insertarUtensilio(
                    sqLiteDatabase, utensilio.getString("nombre"),
                    "android.resource://" + ctx.packageName + "/drawable/" + utensilio.getString("imagen"),
                    "android.resource://" + ctx.packageName + "/raw/" + utensilio.getString("audio")
                )
            }
            list = recetario.getJSONArray("pasos")
            for (i in 0 until list.length()) {
                val paso = list.getJSONObject(i)
                insertarPaso(
                    sqLiteDatabase, paso.getString("nombre"),
                    paso.getString("texto"),
                    "android.resource://" + ctx.packageName + "/drawable/" + paso.getString("imagen"),
                    "android.resource://" + ctx.packageName + "/raw/" + paso.getString("audio")
                )
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

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
