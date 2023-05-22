package com.example.homeassistant.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import com.example.homeassistant.domain.AirQuality
import com.example.homeassistant.domain.enums.AirQualityEnum
import com.example.homeassistant.domain.enums.AmmoniaEnum
import com.example.homeassistant.domain.enums.CarbonMonoxideEnum
import com.example.homeassistant.domain.enums.CoarseParticlesEnum
import com.example.homeassistant.domain.enums.FineParticlesEnum
import com.example.homeassistant.domain.enums.NitrogenDioxideEnum
import com.example.homeassistant.domain.enums.NitrogenMonoxideEnum
import com.example.homeassistant.domain.enums.OzoneEnum
import com.example.homeassistant.domain.enums.SulphurDioxideEnum
import com.example.homeassistant.repository.DatabaseRepository
import kotlinx.coroutines.flow.map

class DatabaseViewModel(private val databaseRepository: DatabaseRepository) : ViewModel() {
    fun getAirQualityRecords(): LiveData<List<AirQuality>> {
        val records = databaseRepository.getAirQualityRecords()
        return records.map {
            it.map { airQualityEntity ->
                AirQuality(
                    dateTime = airQualityEntity.dateTime,
                    airQualityIndex = airQualityEntity.airQualityIndex,
                    carbonMonoxide = airQualityEntity.carbonMonoxide,
                    nitrogenMonoxide = airQualityEntity.nitrogenMonoxide,
                    nitrogenDioxide = airQualityEntity.nitrogenDioxide,
                    ozone = airQualityEntity.ozone,
                    sulphurDioxide = airQualityEntity.sulphurDioxide,
                    ammonia = airQualityEntity.ammonia,
                    fineParticles = airQualityEntity.fineParticles,
                    coarseParticles = airQualityEntity.coarseParticles,
                    airQualityIndexEnum = AirQualityEnum.getQualityByValue(airQualityEntity.airQualityIndex),
                    carbonMonoxideEnum = CarbonMonoxideEnum.getQualityByValue(airQualityEntity.carbonMonoxide),
                    nitrogenMonoxideEnum = NitrogenMonoxideEnum.getQualityByValue(airQualityEntity.nitrogenMonoxide),
                    nitrogenDioxideEnum = NitrogenDioxideEnum.getQualityByValue(airQualityEntity.nitrogenDioxide),
                    ozoneEnum = OzoneEnum.getQualityByValue(airQualityEntity.ozone),
                    sulphurDioxideEnum = SulphurDioxideEnum.getQualityByValue(airQualityEntity.sulphurDioxide),
                    ammoniaEnum = AmmoniaEnum.getQualityByValue(airQualityEntity.ammonia),
                    fineParticlesEnum = FineParticlesEnum.getQualityByValue(airQualityEntity.fineParticles),
                    coarseParticlesEnum = CoarseParticlesEnum.getQualityByValue(airQualityEntity.coarseParticles)
                )
            }
        }.asLiveData()
    }

    class DatabaseViewModelFactory(
        private val databaseRepository: DatabaseRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DatabaseViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DatabaseViewModel(databaseRepository) as T
            }
            throw IllegalArgumentException("Unable to construct view model")
        }
    }
}