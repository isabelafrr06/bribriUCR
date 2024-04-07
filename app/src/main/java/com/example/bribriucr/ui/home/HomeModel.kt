package com.example.bribriucr.ui.home

import android.database.sqlite.SQLiteDatabase
import com.example.bribriucr.db.DbHelper
import com.example.bribriucr.ui.TopicCard

class HomeModel(private val dbHelper: DbHelper) {
    /** Fetches topic cards from the database.
     *
     * @return A list of TopicCard objects representing the retrieved data.
     */
    fun fetchTopicCards(): List<TopicCard> {
        val db: SQLiteDatabase = dbHelper.readableDatabase
        val rawTopics = dbHelper.getCategorias(db)
        // Convert raw data to TopicCard objects if data exists
        return rawTopics.map { TopicCard(it.nombreCategoria, it.rutaImagen, it.porcentaje.toString()) }
    }
}
