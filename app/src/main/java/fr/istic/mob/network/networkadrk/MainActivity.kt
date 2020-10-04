package fr.istic.mob.network.networkadrk

import android.os.Bundle
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fr.istic.mob.network.networkadrk.ui.CustomView

class MainActivity : AppCompatActivity(), View.OnLongClickListener, View.OnTouchListener {

    private lateinit var mCustomView: CustomView
    private lateinit var toolbar: Toolbar
    private var createObject = false
    private var createConnection = false
    private var updateNetwork = false
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

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
            R.id.action_reinitialize_network -> {
                createObject = false
                createConnection = false
                updateNetwork = false
                mCustomView.reinitializeNetwork()
            }
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
            showTextDialog()
        }
        return true
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (createConnection) {
            mCustomView.connectOrMoveObject(event)
        }

        if (updateNetwork) {
            mCustomView.connectOrMoveObject(event, true)
        }

        return super.onTouchEvent(event)
    }


    private fun showTextDialog() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setMessage(R.string.dialog_message)
            .setPositiveButton(R.string.ok_button) { dialog, _ ->
                val objectName = input.text

                mCustomView.drawRect(objectName.toString())
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }

        builder.create()
        builder.show()
    }
}