package edu.victorlamas.actDemo04.ui

import android.app.Application
import androidx.room.Room
import edu.victorlamas.actDemo04.data.SupersDatabase

// Monta la estructura de la BD
class MyRoomApplication: Application() {
    lateinit var supersDatabase: SupersDatabase
    private set

    override fun onCreate() {
        super.onCreate()
        supersDatabase = Room.databaseBuilder(
            context = this,
            klass = SupersDatabase::class.java,
            name = "supers-db"
        ).build()
    }
}