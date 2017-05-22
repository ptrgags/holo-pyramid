package io.github.ptrgags.holopyramid

import android.content.Context
import android.os.Bundle
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import org.rajawali3d.view.ISurface
import org.rajawali3d.view.SurfaceView

class MainActivity : AppCompatActivity() {
    var renderer: ExampleRenderer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val surface = SurfaceView(this)
        surface.setFrameRate(60.0)
        surface.renderMode = ISurface.RENDERMODE_WHEN_DIRTY;

        addContentView(
                surface,
                ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT))

        renderer = ExampleRenderer(this)
        surface.setSurfaceRenderer(renderer)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        renderer?.onTouchEvent(event)
        return true
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        renderer?.onKeyUp(keyCode, event)
        return true
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        renderer?.onKeyDown(keyCode, event)
        return true
    }
}

