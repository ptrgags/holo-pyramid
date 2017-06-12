package io.github.ptrgags.holopyramid
import android.content.Context
import android.opengl.GLES20
import android.util.Log
import android.view.MotionEvent
import org.rajawali3d.Object3D
import org.rajawali3d.loader.LoaderOBJ
import org.rajawali3d.primitives.Torus
import org.rajawali3d.renderer.RenderTarget
import org.rajawali3d.renderer.Renderer

/**
 * NOTE: This is temporary to help me learn the Rajawali library
 *
 * This is the tutorial here but done in Kotlin:
 * http://www.clintonmedbery.com/basic-rajawali3d-tutorial-for-android/
 */
class HoloPyramidRenderer(
        context: Context?,
        val modelId: Int,
        val transformer: ModelTransformer) : Renderer(context) {

    init {
        setFrameRate(60)
    }

    companion object {
        val NUM_VIEWS = 4
        val VIEWPORT_DIVISOR = 4
        val TAG = "holopyramid-renderer"
    }

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
    override fun onTouchEvent(event: MotionEvent?) {}

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


        val model = loadModel(modelId)
        scene3d = HoloPyramidScene3D(this, model)

        clearScenes()
        addScene(scene3d)
        addScene(scene2d)
        switchScene(0)
    }

    /**
     * Attempt to load a model given a resource ID.
     */
    fun loadModel(resId: Int): Object3D {
        try {
            val loader = LoaderOBJ(
                    mContext.resources, mTextureManager, resId)
            loader.parse()
            return loader.parsedObject
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing OBJ model", e)
            return Torus(1.0f, 0.5f, 40, 20)
        }
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
        //Update the transformer's rotation
        transformer.autoRotate()

        //Apply the rotation to the model before rendering the 3D scene
        scene3d?.transformModel(transformer)

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
