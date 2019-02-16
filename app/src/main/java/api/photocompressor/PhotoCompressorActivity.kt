package api.photocompressor

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.example_activity.*
import lib.photocompressor.Compressor
import java.io.File

class PhotoCompressorActivity: AppCompatActivity() {

    // create compressor object
    // you can pass null as config or use your own config like below
    // val config = Config.Builder().setHeight(height).setWidth(width).setQuality(quality).build()

    private lateinit var compressor: Compressor

    companion object {

        const val RC_PERMISSION = 10
        const val RC_PICK_PHOTO = 11
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_activity)
        compressor = Compressor(application, null)

        checkPermissionAndPickPhoto()
    }


    private fun isPermissionGranted() =
            ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission() =
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), RC_PERMISSION)

    private fun checkPermissionAndPickPhoto() {

        if (isPermissionGranted()) {
            openPhotoPicker()
            return
        }

        requestPermission()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != RC_PERMISSION) return

        if (grantResults.size == 0) return

        val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (granted) {

            openPhotoPicker()
        }else {
            Toast.makeText(this, "Permission not granted", Toast.LENGTH_LONG).show()
        }
    }

    private fun openPhotoPicker() {

        val pickerIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(Intent.createChooser(pickerIntent,
                "Pick Photo"), RC_PICK_PHOTO)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode != RC_PICK_PHOTO) return

        if(resultCode != Activity.RESULT_OK) return

        val uri = data?.data
        val path = getImagePath(uri)

        path?.let {

            Log.d("Test", it)
            val file = File(it)

            val beforeCompress = "Before Compress, Size => ${file.length() / 1000}KB"
            tvBeforeCompress.text = beforeCompress

            val paths = listOf(it)
            compressor.compress(paths) { data, error ->

                when {
                    error != null -> {
                        Toast.makeText(this, "Failed to compress photo ${error.message}", Toast.LENGTH_LONG).show()
                    }

                    data?.isNotEmpty() == true -> {

                        val compressed = data[0]
                        val afterCompressed = "After compressed, Size => ${compressed.length() / 1000}KB"
                        tvAfterCompress.text = afterCompressed
                    }
                }
            }
        }
    }

    private fun getImagePath(uri: Uri?): String? {

        try {

            val projections = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = contentResolver?.query(uri, projections, null, null, null)

            return if (cursor == null) {
                ""
            }else {

                cursor.moveToFirst()
                val columnIndex = cursor.getColumnIndex(projections[0])
                val path = cursor.getString(columnIndex)
                cursor.close()

                path
            }
        }catch (e: Exception) {
            Log.d("Test", "" + e)
        }

        return ""
    }
}