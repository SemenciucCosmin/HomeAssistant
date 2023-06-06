package com.example.homeassistant.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.example.homeassistant.R
import com.example.homeassistant.datasource.SettingsDataSource
import com.example.homeassistant.repository.SettingsRepository
import com.example.homeassistant.ui.viewmodel.SettingsViewModel
import com.example.homeassistant.utils.showBluetoothPermissionRationale
import com.example.homeassistant.utils.showLocationPermissionRationale
import com.example.homeassistant.utils.showNotificationsPermissionRationale
import com.example.homeassistant.worker.AddRecordsWorker
import com.example.homeassistant.worker.CleanRecordsWorker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView
import org.joda.time.DateTime
import org.joda.time.Duration
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    companion object {
        private const val ADD_RECORDS_WORKER_TAG = "add_records_worker"
        private const val CLEAN_RECORDS_WORKER_TAG = "clean_records_worker"
        private const val ADD_RECORDS_WORKER_INTERVAL = 24L
        private const val CLEAN_RECORDS_WORKER_INTERVAL = 120L
        private const val WORKER_START_TIME = 12
        private const val WORKER_DELAY_TIME = 25
        const val LATITUDE_KEY = "latitude"
        const val LONGITUDE_KEY = "longitude"
        fun startActivity(activity: StartupChecksActivity) {
            val intent = Intent(activity, MainActivity::class.java)
            activity.startActivity(intent)
        }
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModel.SettingsViewModelFactory(
            SettingsRepository(SettingsDataSource(this))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.navigation_view)
        val navigationController = findNavController(R.id.navigation_host_fragment)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_today,
                R.id.nav_five_days_weather,
                R.id.nav_air_quality,
                R.id.nav_weather_records,
                R.id.nav_air_quality_records,
                R.id.nav_device_management,
                R.id.nav_add_device,
                R.id.nav_settings
            ),
            drawerLayout
        )

        setupActionBarWithNavController(navigationController, appBarConfiguration)
        navigationView.setupWithNavController(navigationController)
        navigationView.setBackgroundColor(
            MaterialColors.getColor(
                this,
                com.google.android.material.R.attr.colorSurface,
                Color.BLACK
            )
        )

        val delay = if (DateTime.now().hourOfDay < WORKER_START_TIME) {
            Duration(
                DateTime.now(),
                DateTime.now().withTimeAtStartOfDay().plusHours(WORKER_START_TIME)
            ).standardMinutes
        } else {
            Duration(
                DateTime.now(),
                DateTime.now().withTimeAtStartOfDay().plusHours(WORKER_DELAY_TIME)
            ).standardMinutes
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        settingsViewModel.saveLocationToPreferenceStore(
                            com.example.homeassistant.domain.settings.Location(
                                location.latitude,
                                location.longitude
                            ),
                            this
                        )

                        val data = Data.Builder()
                        data.putDouble(LATITUDE_KEY, location.latitude)
                        data.putDouble(LONGITUDE_KEY, location.longitude)

                        val addRecordsWorkRequest: PeriodicWorkRequest =
                            PeriodicWorkRequest.Builder(
                                AddRecordsWorker::class.java,
                                ADD_RECORDS_WORKER_INTERVAL,
                                TimeUnit.HOURS
                            )
                                .addTag(ADD_RECORDS_WORKER_TAG)
                                .setInputData(data.build())
                                .setInitialDelay(delay, TimeUnit.MINUTES)
                                .build()

                        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                            ADD_RECORDS_WORKER_TAG,
                            ExistingPeriodicWorkPolicy.KEEP,
                            addRecordsWorkRequest
                        )
                    }
                }
            } else {
                showNotificationsPermissionRationale(this)
            }
        } else {
            showLocationPermissionRationale(this)
        }

        val cleanRecordsWorkRequest: PeriodicWorkRequest = PeriodicWorkRequest.Builder(
            CleanRecordsWorker::class.java,
            CLEAN_RECORDS_WORKER_INTERVAL,
            TimeUnit.DAYS
        )
            .addTag(CLEAN_RECORDS_WORKER_TAG)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            CLEAN_RECORDS_WORKER_TAG,
            ExistingPeriodicWorkPolicy.KEEP,
            cleanRecordsWorkRequest
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navigation_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
