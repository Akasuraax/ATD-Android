package com.example.atdapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.Locale

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Charger les paramètres de langue et de thème
        loadSettings()

        if (isTokenValid(this)) {
            // Le token est valide, l'utilisateur peut accéder à l'application

            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)

            // Ferme l'activité actuelle
            finish()
        } else {
            // Utilisation d'un Handler pour exécuter un bloc de code après un délai de 3 secondes
            Handler(Looper.getMainLooper()).postDelayed({

                // Lance l'intention pour démarrer la deuxième activité
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)

                // Ferme l'activité actuelle
                finish()
            }, 1000) // 1000 millisecondes = 1 secondes
        }
    }

    private fun loadSettings() {
        val sharedPreferences = getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val nightMode = sharedPreferences.getInt("nightMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        val language = sharedPreferences.getString("language", "fr")

        if (nightMode!= AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) {
            AppCompatDelegate.setDefaultNightMode(nightMode)
        }

        if (language!= null) {
            setLocale(language)
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        recreate() // Redémarre l'activité pour appliquer le changement de langue
    }


    fun isTokenValid(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("MySharedPreferences", Context.MODE_PRIVATE)
        val lastLogin = sharedPreferences.getLong("lastLogin", 0)
        val currentTime = System.currentTimeMillis()
        val oneWeekInMillis = 7 * 24 * 60 * 60 * 1000

        return currentTime - lastLogin < oneWeekInMillis
    }
}