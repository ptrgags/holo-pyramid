package io.github.ptrgags.holopyramid
import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import org.rajawali3d.cameras.Camera
import org.rajawali3d.lights.ALight
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Torus
import org.rajawali3d.renderer.Renderer;

/**
 * NOTE: This is temporary to help me learn the Rajawali library
 *
 * This is the tutorial here but done in Kotlin:
 * http://www.clintonmedbery.com/basic-rajawali3d-tutorial-for-android/
 */
class ExampleRenderer : Renderer {
    val model = Torus(1.0f, 0.5f, 20, 10);
    var cameraNum = 0

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

    override fun initScene() {
        // Add two directional lights
        val frontLight = DirectionalLight(1.0, 0.2, -1.0)
        frontLight.setColor(0.5f, 0.0f, 1.0f)
        frontLight.power = 2.0f
        currentScene.addLight(frontLight)
        val backLight = DirectionalLight(-1.0, 0.2, 1.0)
        backLight.setColor(0.0f, 1.0f, 0.0f)
        backLight.power = 2.0f
        currentScene.addLight(backLight)

        //Material for shading the torus
        val material = Material()
        material.enableLighting(true)
        material.diffuseMethod = DiffuseMethod.Lambert()
        material.color = 0xFFFFFF;

        model.material = material
        currentScene.addChild(model)

        //Add the four cameras
        val frontCam = Camera()
        frontCam.position = Vector3(0.0, 0.0, 5.0)
        frontCam.lookAt = Vector3(0.0)
        val leftCam = Camera()
        leftCam.position = Vector3(5.0, 0.0, 0.0)
        leftCam.lookAt = Vector3(0.0)
        val backCam = Camera()
        backCam.position = Vector3(0.0, 0.0, -5.0)
        backCam.lookAt = Vector3(0.0)
        val rightCam = Camera()
        rightCam.position = Vector3(-5.0, 0.0, 0.0)
        rightCam.lookAt = Vector3(0.0)

        // Add the cameras to the scene
        currentScene.clearCameras();
        currentScene.addAndSwitchCamera(frontCam)
        currentScene.addCamera(leftCam)
        currentScene.addCamera(backCam)
        currentScene.addCamera(rightCam)
    }

    override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int) {
    }

}
