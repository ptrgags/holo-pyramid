package io.github.ptrgags.holopyramid

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.ViewGroup
import org.rajawali3d.view.ISurface
import org.rajawali3d.view.SurfaceView

class HoloPyramidActivity : AppCompatActivity() {
    /** An object to handle transforming the model */
    val transformer = ModelTransformer()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set up the activity
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_holopyramid)

        // Create a view for Rajawali rendering
        val surface = SurfaceView(this)
        surface.setFrameRate(60.0)
        surface.renderMode = ISurface.RENDERMODE_WHEN_DIRTY
        addContentView(
                surface,
                ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT))

        // Set up the custom renderer
        val modelId = intent.extras.getInt("model_id")
        val renderer = HoloPyramidRenderer(this, modelId, transformer)
        surface.setSurfaceRenderer(renderer)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return when(keyCode) {
            KeyEvent.KEYCODE_BUTTON_A -> {
                transformer.toggleMode()
                true
            }
            KeyEvent.KEYCODE_BUTTON_START -> {
                transformer.resetManualRotation()
                true
            }
            else -> super.onKeyUp(keyCode, event)
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
            else -> super.onKeyDown(keyCode, event)
        }
    }
}

