package lib.photocompressor

import android.app.Application
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import java.io.File
import java.util.*
import java.util.concurrent.Executors
import android.graphics.BitmapFactory
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import kotlin.collections.ArrayList

// compressorCallback to communicate error and results
// back to lib user
typealias CompressorCallback = (List<File>?, Throwable?) -> Unit

class Compressor(private val app: Application, var config: Config?) {

    companion object {
        const val FOLDER_NAME = "Compressor"
    }

    // executor to compress images in the background
    private val executor = Executors.newSingleThreadExecutor()
    // handler to pass result through CompressorCallback
    private val handler = Handler(Looper.getMainLooper())

    init {

        // use default config if no config is provided by the caller
        if (config == null) {
            config = defaultConfig()
        }
    }

    // create a sane default config.
    // probably fits in most use cases
    private fun defaultConfig(): Config {
        return Config.Builder().setHeight(300).setWidth(300).setQuality(80).build()
    }

    // compress a list of photos
    // paths: Location of these photos in the android file system
    // callback: Callback to report result or error
    fun compress(paths: List<String>, callback: CompressorCallback) {

        executor.execute {

            val result = ArrayList<File>()
            paths.forEach { path ->

                try {

                    val file = createRandomFile(config?.getFormat())

                    // decode and compress photo
                    val decodedBitmap = rescaleImage(path, config?.getWidth()!!, config?.getHeight()!!)

                    val byteArray = ByteArrayOutputStream()

                    // convert compressed bitmap to byteArray
                    decodedBitmap.compress(config?.getFormat(),
                            config?.getQuality()!!, byteArray)

                    // copy compressed file
                    val fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(byteArray.toByteArray())
                    fileOutputStream.flush()

                    result.add(file)
                }catch (e: Exception) {

                    Log.d("Test", "" + e)
                    // report error
                    handler.post {
                        callback.invoke(null, e)
                    }
                }
            }

            // post result
            handler.post {
                callback.invoke(result, null)
            }
        }
    }

    // rescale image. Find the best scale for the height and width given
    private fun rescaleImage(path: String, width: Int, height: Int): Bitmap {

        val scaleOptions = BitmapFactory.Options()
        scaleOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(path, scaleOptions)
        var scale = 1
        while (scaleOptions.outWidth / scale / 2 >= width && scaleOptions.outHeight / scale / 2 >= height) {
            scale *= 2
        }

        // decode with the sample size
        val outOptions = BitmapFactory.Options()
        outOptions.inSampleSize = scale
        return BitmapFactory.decodeFile(path, outOptions)
    }

    // create a unique file in app cache dir.
    // guaranteed to be unique
    private fun createRandomFile(format: Bitmap.CompressFormat?): File {

        // determine appropriate extension
        val extension = when(format!!) {

            Bitmap.CompressFormat.PNG -> ".png"
            Bitmap.CompressFormat.JPEG -> ".jpg"
            Bitmap.CompressFormat.WEBP -> ".webp"
        }

        // create main compressor folder
        val folderDir = app.cacheDir.absolutePath + File.separator + FOLDER_NAME
        val folder = File(folderDir)
        if (!folder.exists()) {
            folder.mkdirs()
        }

        // return this new unique file
        return File(folder, UUID.randomUUID().toString() + extension)
    }

    // deletes all compressed file. Useful for freeing memory
    fun purge() {

        executor.execute {

            val folderDir = app.cacheDir.absolutePath + File.separator + FOLDER_NAME
            val folder = File(folderDir)
            if (folder.isDirectory) {

                val files = folder.listFiles()
                files?.forEach {
                    it?.delete()
                }
            }
        }
    }
}