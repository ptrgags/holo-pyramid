package io.github.ptrgags.holopyramid
import android.content.Context
import android.view.MotionEvent
import org.rajawali3d.lights.ALight
import org.rajawali3d.lights.DirectionalLight
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.primitives.Sphere
import org.rajawali3d.renderer.Renderer;

/**
 * NOTE: This is temporary to help me learn the Rajawali library
 *
 * This is the tutorial here but done in Kotlin:
 * http://www.clintonmedbery.com/basic-rajawali3d-tutorial-for-android/
 */
class ExampleRenderer : Renderer {
    val sphere = Sphere(1.0f, 24, 24)

    constructor(context: Context?): super(context) {
        setFrameRate(60)
    }

    override fun onTouchEvent(event: MotionEvent?) {}

    override fun initScene() {
        //Initialize the directional light
        val directionalLight = DirectionalLight(1.0, 0.2, -1.0)
        directionalLight.setColor(1.0f, 1.0f, 1.0f)
        directionalLight.power = 2.0f
        currentScene.addLight(directionalLight)

        //Initialize our sphere light
        val material = Material()
        material.enableLighting(true)
        material.diffuseMethod = DiffuseMethod.Lambert()
        material.color = 0xFF0000

        sphere.material = material
        currentScene.addChild(sphere)

        currentCamera.z = 4.2
    }

    override fun onRender(ellapsedRealtime: Long, deltaTime: Double) {
        super.onRender(ellapsedRealtime, deltaTime)
        sphere.rotate(Vector3.Axis.Y, 1.0)
    }

    override fun onOffsetsChanged(
            xOffset: Float,
            yOffset: Float,
            xOffsetStep: Float,
            yOffsetStep: Float,
            xPixelOffset: Int,
            yPixelOffset: Int) {}

}
