package lib.photocompressor

import android.graphics.Bitmap

class Config {

    private var quality: Int = 0
    private var height: Int = 0
    private var width: Int = 0
    private var format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG

    companion object {

        const val PNG = 1
        const val JPG = 2
        const val WEBP = 3
    }

    open class Builder {

        private var quality: Int = 0
        private var height: Int = 0
        private var width: Int = 0
        private var format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG


        fun setHeight(h: Int): Builder {
            height = h
            return this
        }

        fun setWidth(w: Int): Builder {
            width = w
            return this
        }

        fun setQuality(q: Int): Builder {
            quality = q
            return this
        }

        fun setFormat(newFormat: Int): Builder {

            format = when(newFormat) {

                PNG -> Bitmap.CompressFormat.PNG
                JPG -> Bitmap.CompressFormat.JPEG
                WEBP -> Bitmap.CompressFormat.WEBP
                else -> Bitmap.CompressFormat.PNG
            }
            return this
        }

        fun build(): Config {
            val config = Config()
            config.format = format
            config.width = width
            config.height = height
            config.quality = quality

            return config
        }
    }

    fun getHeight() = height

    fun getWidth() = width

    fun getFormat() = format

    fun getQuality() = quality
}