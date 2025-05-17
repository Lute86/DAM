package com.example.clubdeportivo

import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle

open class BaseActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Esta línea debe ir primero para cargar el layout
        setContentView(R.layout.activity_base)

        // Ahora sí puedes acceder a las vistas del layout
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup del menú
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        // Sincroniza el ícono de hamburguesa con el Drawer
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Transparencia de la barra de navegación y estado
        window.navigationBarColor = android.graphics.Color.TRANSPARENT
        window.statusBarColor = android.graphics.Color.TRANSPARENT

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.base_container)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        navigationView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_principal -> {
                    startActivity(Intent(this, MainMenuActivity::class.java))
                    true
                }
                R.id.nav_listas -> {
                    startActivity(Intent(this, ListSocioNoSocioActActivity::class.java))
                    true
                }
                R.id.nav_cerrarSesion -> {
                    startActivity(Intent(this, LoginActivity::class.java))
                    true
                }
                R.id.nav_salir -> {
                    finishAffinity()
                    true
                }
                else -> false
            }.also {
                drawerLayout.closeDrawers()
            }
        }
    }

    fun setActivityLayout(layoutResId: Int) {
        val container = findViewById<FrameLayout>(R.id.base_container)
        layoutInflater.inflate(layoutResId, container, true)
    }
}
