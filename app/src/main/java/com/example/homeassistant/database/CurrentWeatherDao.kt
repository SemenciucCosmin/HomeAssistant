package com.example.homeassistant.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.homeassistant.domain.database.CurrentWeatherEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CurrentWeatherDao {

    @Query("SELECT * from current_weather ORDER BY dateTime DESC LIMIT 30")
    fun getCurrentWeatherRecords(): Flow<List<CurrentWeatherEntity>>

    @Query("DELETE from current_weather WHERE dateTime NOT IN (SELECT dateTime from current_weather ORDER BY dateTime DESC LIMIT 30)")
    fun removeUnusedRecords()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(currentWeatherEntity: CurrentWeatherEntity)
}
