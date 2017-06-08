package io.github.ptrgags.holopyramid
import android.content.Context
import android.opengl.GLES20
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import org.rajawali3d.Object3D
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.loader.ParsingException
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Torus
import org.rajawali3d.renderer.RenderTarget
import org.rajawali3d.renderer.Renderer

/**
 * NOTE: This is temporary to help me learn the Rajawali library
 *
 * This is the tutorial here but done in Kotlin:
 * http://www.clintonmedbery.com/basic-rajawali3d-tutorial-for-android/
 */
class HoloPyramidRenderer(context: Context?) : Renderer(context) {
    init {
        setFrameRate(60)
    }

    companion object {
        val NUM_VIEWS = 4
        val VIEWPORT_DIVISOR = 4
    }

    var model: Object3D? = null

    /**
     * This scene is to hold the four planes that represent
     * the four views of the object. It uses a default camera
     */
    var scene2d: HoloPyramidScene2D? = null
    /** This scene has the object and the four cameras */
    var scene3d: HoloPyramidScene3D? = null

    /** List of render targets */
    val holoTargets: MutableList<RenderTarget> = mutableListOf()

    /**
     * Touching the left half of the screen rotates the model
     * one way, the other half rotates it the other way.
     * TODO: Maybe remove this and have two rotation modes?
     */
    override fun onTouchEvent(event: MotionEvent?) {
        val x = event?.x ?: 0.0f
        val percent = x / mDefaultViewportWidth
        val rotAmount = if (percent > 0.5) -3.0 else 3.0
        model?.rotate(Vector3.Axis.Y, rotAmount)
    }

    /**
     * Rajawalli only handles touch events, it would be nice if it did
     * keyboard events too since I'm using a controller.
     * TODO: Maybe have the A button switch between manual/automatic
     * rotation?
     */
    fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return false
    }

    /**
     * When the D-pad is held down, rotate the model.
     */
    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        //Rotate with the D-Pad.
        //TODO: Instead of rotating the models, move the cameras along a sphere?
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            model?.rotate(Vector3.Axis.Y, 3.0)
            return true
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            model?.rotate(Vector3.Axis.Y, -3.0)
            return true
        } else
            return false
    }

    /**
     * Since there are four views of the same object, we need to render
     * to four separate buffers. The render holds on to these render
     * targets, though they are also passed to the 2D scene to texture
     * the screen quads.
     *
     * This populates holoTargets
     */
    fun createRenderTargets() {
        for (i in 0 until NUM_VIEWS) {
            //Make a render target and add to the list
            val target = RenderTarget(
                    "holoView$i",
                    mDefaultViewportWidth / VIEWPORT_DIVISOR,
                    mDefaultViewportHeight / VIEWPORT_DIVISOR)
            target.fullscreen = false
            addRenderTarget(target)
            holoTargets.add(target)
        }
    }

    override fun initScene() {
        // Calculate the aspect ratio of the screen
        val aspectRatio = (
                mDefaultViewportWidth.toDouble() / mDefaultViewportHeight)

        createRenderTargets()
        scene2d = HoloPyramidScene2D(this, holoTargets, aspectRatio)

        //Load the model
        //TODO: The model should be loaded in the Activity
        try {
            val loader = LoaderOBJ(
                    mContext.resources, mTextureManager, R.raw.utah_teapot)
            loader.parse()
            model = loader.parsedObject
        } catch (e: ParsingException) {
            Log.e("holopyramid", "Error parsing OBJ model", e)
            model = Torus(1.0f, 0.5f, 40, 20)
        }

        scene3d = HoloPyramidScene3D(this, model!!)

        clearScenes()
        addScene(scene3d)
        addScene(scene2d)
        switchScene(0)
    }

    override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int) {
    }

    override fun onRender(ellapsedRealtime: Long, deltaTime: Double) {
        // Switch to the 3D scene to render the four textures
        switchSceneDirect(scene3d)

        //Shrink the viewport to something smaller.
        GLES20.glViewport(
                0,
                0,
                mDefaultViewportWidth / VIEWPORT_DIVISOR,
                mDefaultViewportHeight / VIEWPORT_DIVISOR)

        // Render each cameraa view
        for (i in 0 until NUM_VIEWS) {
            currentScene.switchCamera(i)
            renderTarget = holoTargets[i]
            render(ellapsedRealtime, deltaTime)
        }

        // Switch to the 2D scene to render to the screen
        switchSceneDirect(scene2d)

        //Reset the viewport
        GLES20.glViewport(0, 0, mDefaultViewportWidth, mDefaultViewportHeight)

        renderTarget = null
        render(ellapsedRealtime, deltaTime)
    }
}
