package io.github.ptrgags.holopyramid

import android.util.Log
import org.rajawali3d.Object3D
import org.rajawali3d.lights.PointLight
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.methods.DiffuseMethod
import org.rajawali3d.math.vector.Vector3
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.scene.Scene

/**
 * The 3D scene has a model and four cameras that circle around it.
 *
 * Top down view:
 *
 *           back
 *                         +----- + x
 *          cam 2          |
 *            |            |
 *            v            +z
 *  cam 3 -> obj <- cam 1
 *            ^
 *            |
 *           cam 0
 *
 *          front
 */
class HoloPyramidScene3D : Scene {
    companion object {
        // Number of views we need
        val NUM_CAMERAS = 4
        /**
         * Distance from center of object to any one of the cameras
         * since ecameras lie on a circle of this radius
         */
        val CAMERA_RADIUS = 2.0
    }

    var model: Object3D? = null

    constructor(renderer: Renderer, model: Object3D) : super(renderer) {
        this.model = model
        initModel()
        initLights()
        initCameras()
    }

    private fun initModel() {
        // Since the cameras will flip the view vertically, we need to flip
        // all the faces
        model?.isBackSided = true

        // Make the model white with diffuse lighting
        val torusMaterial = Material()
        torusMaterial.enableLighting(true)
        torusMaterial.diffuseMethod = DiffuseMethod.Lambert()
        torusMaterial.color = 0xFFFFFF
        model?.material = torusMaterial
        addChild(model)

        // Calculate the maximum dimension in the xz-plane
        val bbox = model?.boundingBox
        val minDims = bbox?.min ?: Vector3()
        val maxDims = bbox?.max ?: Vector3()
        val dims = maxDims.subtract(minDims)
        val maxSize = Math.max(dims.x, dims.z)

        // Scale down the model so its maximum dimension is 1.
        //TODO: This is not exactly correct.
        val scale = 1.0 / maxSize
        model?.setScale(scale, scale, scale)

        //TODO: Use a slider to adjust the z position of the model.
    }

    private fun initLights() {
        //TODO: Not sure what lights to use?
        // Add a purple light shining from directly above
        val light1 = PointLight();
        light1.setPosition(0.0, 1.5, 0.0)
        light1.setColor(0.5f, 0.0f, 1.0f)
        light1.power = 1.0f
        addLight(light1)

        // Add a green light shining from the left
        val light2 = PointLight();
        light2.setPosition(-1.5, 0.0, 0.0)
        light2.setColor(0.0f, 1.0f, 0.0f)
        light2.power = 1.0f
        addLight(light2)

        // Add an orange light shining from the front
        val light3 = PointLight();
        light3.setPosition(0.0, 0.0, 1.5)
        light3.setColor(1.0f, 0.5f, 0.0f)
        light3.power = 1.0f
        addLight(light3)
    }

    private fun initCameras() {
        clearCameras()
        // Make the four cameras counterclockwise around the y axis
        for (i in 0 until NUM_CAMERAS) {
            val x = CAMERA_RADIUS * Math.sin(i * Math.PI / 2.0)
            val z = CAMERA_RADIUS * Math.cos(i * Math.PI / 2.0)
            val angle = 90.0 * i

            val cam = HoloPyramidCamera(angle)
            cam.position = Vector3(x, 0.0, z)
            cam.lookAt = Vector3(0.0)
            addCamera(cam)
        }
        switchCamera(0)
    }

}