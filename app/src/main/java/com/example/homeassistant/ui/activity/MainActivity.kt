package com.example.homeassistant.ui.activity

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.homeassistant.R
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

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
                R.id.nav_ten_days_weather,
                R.id.nav_air_quality,
                R.id.nav_sensor_records,
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

        StartupChecksActivity.startActivity(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navigation_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
