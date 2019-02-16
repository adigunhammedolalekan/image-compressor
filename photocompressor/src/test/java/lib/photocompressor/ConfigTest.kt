package lib.photocompressor

import android.graphics.Bitmap
import org.junit.Assert
import org.junit.Test

class ConfigTest {


    @Test
    fun test_init_config() {

        val config = Config.Builder().setWidth(10).setHeight(10).setQuality(80).setFormat(Config.JPG).build()

        Assert.assertEquals(10, config.getHeight())
        Assert.assertEquals(10, config.getWidth())
        Assert.assertEquals(Bitmap.CompressFormat.JPEG, config.getFormat())
        Assert.assertEquals(80, config.getQuality())
    }
}