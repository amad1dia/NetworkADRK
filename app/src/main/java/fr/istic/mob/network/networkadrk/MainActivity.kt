package fr.istic.mob.network.networkadrk

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import fr.istic.mob.network.networkadrk.ui.ActionBottomDialogFragment
import fr.istic.mob.network.networkadrk.ui.CustomView
import java.io.FileNotFoundException


class MainActivity : AppCompatActivity(), View.OnLongClickListener, View.OnTouchListener,
    ActionBottomDialogFragment.ItemClickListener {

    private var longClick = false
    private val PICK_FROM_GALLERY: Int = 50

    var URI: Uri? = null
    private lateinit var mCustomView: CustomView
    private lateinit var toolbar: Toolbar
    private var createObject = false
    private var createConnection = false
    private var updateNetwork = false
    private var updateObjetProperties = false
    private var saveObject = false
    private val TAG = "MainActivity"
    private var import = false

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

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK) {
            URI = data?.data
            if (import && URI != null) {
                try {
                    val inputStream = contentResolver.openInputStream(URI!!)
                    mCustomView.background = Drawable.createFromStream(inputStream, "plan")
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                }
            } else {
                sendNetworkByMail()
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_reinitialize_network -> {
                createObject = false
                createConnection = false
                updateNetwork = false
                updateObjetProperties = false
                mCustomView.reinitializeNetwork()
            }
            R.id.action_create_object -> {
                createObject = true
                createConnection = false
                updateNetwork = false
                updateObjetProperties = false
            }

            R.id.action_create_connection -> {
                createConnection = true
                createObject = false
                updateNetwork = false
                updateObjetProperties = false

            }

            R.id.action_update -> {
                updateNetwork = true
                createConnection = false
                createObject = false
                updateObjetProperties = false
            }

            R.id.send_network -> {
                openFolder()
            }

            R.id.save_network -> {
                updateNetwork = false
                createConnection = false
                createObject = false
                updateObjetProperties = false
                mCustomView.showOrSaveGraphDialog("save")
            }

            R.id.show_saved_network -> {
                mCustomView.showOrSaveGraphDialog("show")
            }

            R.id.import_plan -> {
                importPlan()
            }

            R.id.action_update_object_properties -> {
                updateObjetProperties = true
                updateNetwork = false
                createConnection = false
                createObject = false
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onLongClick(v: View): Boolean {
        Log.d(TAG, "onLongClick: $updateObjetProperties")
        if (createObject) {
            showTextDialog()
        }

        if (updateObjetProperties)
            longClick = true
        return true
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (createConnection) {
            mCustomView.connectOrMoveObject(event)
        }

        if (updateNetwork) {
            mCustomView.connectOrMoveObject(event, true)
        }

        if (longClick) {
            mCustomView.updateObject(supportFragmentManager, event)
            longClick = false
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

    private fun sendNetworkByMail() {
        try {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "plain/text"
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "A graph")
            if (URI != null) {
                emailIntent.putExtra(Intent.EXTRA_STREAM, URI)
            }
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Here is screenshot of a graph")
            this.startActivity(Intent.createChooser(emailIntent, "Sending email..."))
        } catch (t: Throwable) {
            Toast.makeText(this, "Request failed try again: $t", Toast.LENGTH_LONG).show()
        }
    }

    private fun openFolder() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        intent.putExtra("return-data", true)
        startActivityForResult(
            Intent.createChooser(intent, "Complete action using"),
            PICK_FROM_GALLERY
        )
    }

    private fun importPlan() {
        import = true
        openFolder()
    }

    override fun onItemClick(item: String?) {
        when (item) {
            getString(R.string.update_object_label) -> {
                mCustomView.changeLabel()
            }
            getString(R.string.delete_object) -> {
                mCustomView.deleteObject()
            }
            getString(R.string.update_object_color) -> {
                mCustomView.changeObjectColor()
            }


        }
    }
}