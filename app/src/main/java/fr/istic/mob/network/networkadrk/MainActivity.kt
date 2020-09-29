package fr.istic.mob.network.networkadrk

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import fr.istic.mob.network.networkadrk.ui.CustomView

class MainActivity : AppCompatActivity(), View.OnLongClickListener, View.OnTouchListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var mCustomView: CustomView
    private var createObject = false
    private var createConnection = false
    private var updateNetwork = false
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        mCustomView = findViewById(R.id.network)
        mCustomView.setOnLongClickListener(this)
        mCustomView.setOnTouchListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_create_object -> {
                createObject = true
                createConnection = false
                updateNetwork = false
            }

            R.id.action_create_connection -> {
                createConnection = true
                createObject = false
                updateNetwork = false

            }

            R.id.action_update -> {
                updateNetwork = true
                createConnection = false
                createObject = false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLongClick(v: View?): Boolean {
        if (createObject) {
            Log.d(TAG, "onLongClick: ")
            mCustomView.drawRect()
        }


        return true
    }


    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (createConnection) {
            mCustomView.connectObject(event)
        }
        return super.onTouchEvent(event)
    }
}