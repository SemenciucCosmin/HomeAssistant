package com.example.homeassistant.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.homeassistant.domain.database.AirQualityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AirQualityDao {

    @Query("SELECT * from air_quality ORDER BY dateTime DESC LIMIT 30")
    fun getAirQualityRecords(): Flow<List<AirQualityEntity>>

    @Query("DELETE from air_quality WHERE dateTime NOT IN (SELECT dateTime from air_quality ORDER BY dateTime DESC LIMIT 30)")
    fun removeUnusedRecords()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(airQualityEntity: AirQualityEntity)
}
