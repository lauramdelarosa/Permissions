package com.delarosa.permission

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.invoke
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.delarosa.permission.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    lateinit var view: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)


        view.cameraButton.setOnClickListener {
            askPermission(Manifest.permission.CAMERA)
        }

        view.locationButton.setOnClickListener {
            askPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        view.galleryButton.setOnClickListener {
            askMultiplePermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
        }

    }

    private val askMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            for (entry in map.entries) {
                when (entry.value) {
                    false -> askAgain(entry.key)
                    true -> Snackbar.make(view.container, "permission granted", Snackbar.LENGTH_SHORT)
                        .withColor(resources.getColor(R.color.success))
                        .show()
                }
            }
        }

    private val askPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if (result) {
                Snackbar.make(view.container, "permission granted", Snackbar.LENGTH_SHORT)
                    .withColor(resources.getColor(R.color.success))
                    .show()

            } else {
                Snackbar.make(view.container, "No permission", Snackbar.LENGTH_SHORT)
                    .withColor(resources.getColor(R.color.error))
                    .show()
            }
        }

    private fun askAgain(permission: String) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            showDialog("we need this permission for this and this") {
                askMultiplePermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
            }
        } else {
            showDialog("please go to settings and enable the permission manually") {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }

    }

    private fun showDialog(messageText: String, function: () -> Unit): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.apply {
            setMessage(messageText)
            setPositiveButton(R.string.ok) { _, _ ->
                function()
            }
            setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            create()
        }
        return builder.show()
    }

}

fun Snackbar.withColor(colorInt: Int): Snackbar{
    this.view.setBackgroundColor(colorInt)
    return this
}


