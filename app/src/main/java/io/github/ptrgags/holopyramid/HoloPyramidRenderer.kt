package io.github.ptrgags.holopyramid
import android.content.Context
import android.opengl.GLES20
import android.view.KeyEvent
import android.view.MotionEvent
import org.rajawali3d.cameras.Camera
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.lights.PointLight
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.ScreenQuad
import org.rajawali3d.primitives.Torus
import org.rajawali3d.renderer.RenderTarget
import org.rajawali3d.renderer.Renderer;
import org.rajawali3d.scene.Scene

/**
 * NOTE: This is temporary to help me learn the Rajawali library
 *
 * This is the tutorial here but done in Kotlin:
 * http://www.clintonmedbery.com/basic-rajawali3d-tutorial-for-android/
 */
class HoloPyramidRenderer : Renderer {
    companion object {
        val NUM_DIRECTIONS = 4;
    }

    val model = Torus(1.0f, 0.5f, 40, 20);
    var cameraNum = 0

    /**
     * This scene is to hold the four planes that represent
     * the four views of the object. It uses a default camera
     */
    var scene2d: Scene? = null
    /**
     * This scene has the object and the four cameras
     */
    var scene3d: Scene? = null
    val holoTargets: MutableList<RenderTarget> = mutableListOf()

    constructor(context: Context?): super(context) {
        setFrameRate(60)
    }

    override fun onTouchEvent(event: MotionEvent?) {
    }

    fun onKeyUp(keyCode: Int, event: KeyEvent?) {
        if (keyCode == KeyEvent.KEYCODE_BUTTON_A) {
            cameraNum++
            cameraNum %= currentScene.cameraCount
            currentScene.switchCamera(cameraNum)
        }
    }

    fun onKeyDown(keyCode: Int, event: KeyEvent?) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
            model.rotate(Vector3.Axis.Y, 3.0)
        else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
            model.rotate(Vector3.Axis.Y, -3.0)
    }

    fun initScene2d() {
        scene2d = Scene(this)

        //Make the screen quads
        for (i in 0 until NUM_DIRECTIONS) {
            //Make a render target
            val target = RenderTarget(
                    "holoView${i}",
                    mDefaultViewportWidth / 4,
                    mDefaultViewportHeight / 4)
            target.fullscreen = false
            addRenderTarget(target)
            holoTargets.add(target)

            //Make the material
            val mat = Material()
            mat.colorInfluence = 0.0f
            try {
                mat.addTexture(target.texture)
            } catch (e: ATexture.TextureException) {
                e.printStackTrace()
            }

            //Make the quad
            val quad = ScreenQuad()
            quad.scaleY = 0.25
            quad.scaleX = 0.25
            quad.y = -0.25 - 0.125 + 0.25 * i
            quad.material = mat
            scene2d?.addChild(quad)
        }
    }

    fun initScene3d() {
        //Make the scene and remove all cameras
        scene3d = Scene(this)
        scene3d?.clearCameras()

        //Make the model use back faces since the camera will be flipped
        model.isBackSided = true;

        // Add a purple light shining from directly above
        val light1 = PointLight();
        light1.setPosition(0.0, 1.5, 0.0)
        light1.setColor(0.5f, 0.0f, 1.0f)
        light1.power = 2.0f
        scene3d?.addLight(light1)

        // Add a green light shining from directly below
        val light2 = PointLight();
        light2.setPosition(0.0, -1.5, 0.0)
        light2.setColor(0.0f, 1.0f, 0.0f)
        light2.power = 2.0f
        scene3d?.addLight(light2)

        // Add a white light shining from the front
        val light3 = PointLight();
        light3.setPosition(0.0, 0.0, 1.5)
        light3.setColor(1.0f, 1.0f, 1.0f)
        light3.power = 1.5f
        scene3d?.addLight(light3)

        // Make the torus white with diffuse lighting
        val torusMaterial = Material()
        torusMaterial.enableLighting(true)
        torusMaterial.diffuseMethod = DiffuseMethod.Lambert()
        torusMaterial.color = 0xFFFFFF
        model.material = torusMaterial
        scene3d?.addChild(model)

        // Make the four cameras counterclockwise around the y axis
        for (i in 0 until NUM_DIRECTIONS) {
            val CAMERA_RADIUS = 7.0;
            val x = CAMERA_RADIUS * Math.cos(i * Math.PI / 2.0)
            val z = CAMERA_RADIUS * Math.sin(i * Math.PI / 2.0)
            val angle = 90.0 + 90.0 * i

            val cam = HoloPyramidCamera(angle)
            cam.position = Vector3(x, 0.0, z)
            cam.lookAt = Vector3(0.0)
            cam.setProjectionMatrix(
                    mDefaultViewportWidth / 4, mDefaultViewportHeight / 4)
            scene3d?.addCamera(cam)
        }

        scene3d?.switchCamera(0)
    }

    override fun initScene() {
        // Make the new scenes
        initScene2d()
        initScene3d()

        //Replace the default scene with the two scenes
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
        model.rotate(Vector3.Axis.Y, 1.0);

        // Switch to the 3D scene to render the four textures
        switchSceneDirect(scene3d)

        // Since the cameras flip the faces upside down, switch to
        // front-face culling instead of back-face

        //Shrink the viewport to something smaller.
        GLES20.glViewport(
                0, 0, mDefaultViewportWidth / 4, mDefaultViewportHeight / 4)

        // Render each plane
        for (i in 0 until NUM_DIRECTIONS) {
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
