## Image-Compressor
#### A tiny and elegant image compressor for android

* Simple API
* Use background thread to compress images
* Able to recognize an already compressed images

### How to use
* Download/clone repo
* Import into your project `implementation project('photocompressor')`

```Kotlin

class CompressActivity: AppCompatActivity() {

	private lateinit var compressor: Compressor
	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_activity)

        // init compressor object
        // you can pass null as config or use your own config like below
        // val config = Config.Builder().setHeight(height).setWidth(width).setQuality(quality).build()
        compressor = Compressor(application, null) // or compressor = Compressor(application, config)

        // grab paths of images to compress
        val paths = listOf<String>(photo1, photo2, photo3....)

        // compress them and process result
        compressor.compress(paths) { data, error -> 

        	when {

        		error != null -> { 
        			// handle error
        		}

        		data?.isNotEmpty() == true -> {

        			data.forEach {

        				// handle compressed images
        			}
        		}
        	}
        }
    }
}
```