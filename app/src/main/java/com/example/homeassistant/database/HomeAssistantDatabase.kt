package com.example.homeassistant.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.homeassistant.domain.database.AirQualityEntity

@Database(entities = [AirQualityEntity::class], version = 1, exportSchema = false)
abstract class HomeAssistantDatabase : RoomDatabase() {
    abstract fun airQualityDao(): AirQualityDao

    companion object {
        @Volatile
        private var INSTANCE: HomeAssistantDatabase? = null

        fun getDatabase(context: Context): HomeAssistantDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HomeAssistantDatabase::class.java,
                    "air_quality_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
