package io.github.ptrgags.holopyramid

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.KeyEvent
import android.view.ViewGroup
import org.rajawali3d.Object3D
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.loader.ParsingException
import org.rajawali3d.materials.textures.TextureManager
import org.rajawali3d.primitives.Torus
import org.rajawali3d.view.ISurface
import org.rajawali3d.view.SurfaceView

class HoloPyramidActivity : AppCompatActivity() {
    /** The custom renderer. This cannot be initialized until onCreate() */
    var renderer: HoloPyramidRenderer? = null
    /** The model to display */
    var model: Object3D? = null
    /** An object to handle transforming the model */
    val transformer = ModelTransformer()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up the activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create a view for Rajawali rendering
        val surface = SurfaceView(this)
        surface.setFrameRate(60.0)
        surface.renderMode = ISurface.RENDERMODE_WHEN_DIRTY
        addContentView(
                surface,
                ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT))

        //Load the model and attach it to the transformer
        loadModel()

        // Set up the custom renderer
        renderer = HoloPyramidRenderer(this, model!!, transformer)
        surface.setSurfaceRenderer(renderer)
    }

    /**
     * Loaad the teapot model. If it fails, use a torus.
     *
     * TODO: Load a model from a URL or get a raw resource by name.
     */
    fun loadModel() {
        try {
            val loader = LoaderOBJ(
                    resources, TextureManager.getInstance(), R.raw.utah_teapot)
            loader.parse()
            model = loader.parsedObject
        } catch (e: ParsingException) {
            Log.e("holopyramid", "Error parsing OBJ model", e)
            model = Torus(1.0f, 0.5f, 40, 20)
        }
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when(keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                transformer.toggleMode()
                true
            }
            KeyEvent.KEYCODE_BUTTON_B -> {
                transformer.resetManualRotation()
                true
            }
            else -> false
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                transformer.adjustYaw(-1.0)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                transformer.adjustYaw(1.0)
                true
            }
            KeyEvent.KEYCODE_DPAD_UP -> {
                transformer.adjustPitch(1.0)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                transformer.adjustPitch(-1.0)
                true
            }
            KeyEvent.KEYCODE_BUTTON_L1 -> {
                transformer.adjustHeight(1.0)
                true
            }
            KeyEvent.KEYCODE_BUTTON_R1 -> {
                transformer.adjustHeight(-1.0)
                true
            }
            else -> false
        }
    }
}

