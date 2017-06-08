package io.github.ptrgags.holopyramid

import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import org.rajawali3d.view.ISurface
import org.rajawali3d.view.SurfaceView

class MainActivity : AppCompatActivity() {
    var renderer: HoloPyramidRenderer? = null

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

        // TODO: Select a 3D model to display in the holopyramid

        // Set up the custom renderer
        // TODO: pass the 3D model to the renderer
        renderer = HoloPyramidRenderer(this)
        surface.setSurfaceRenderer(renderer)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        renderer?.onTouchEvent(event)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return renderer?.onKeyUp(keyCode, event) ?: false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return renderer?.onKeyDown(keyCode, event) ?: false
    }
}

