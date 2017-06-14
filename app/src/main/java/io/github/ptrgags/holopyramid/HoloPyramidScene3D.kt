package io.github.ptrgags.holopyramid

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
 * @param renderer the parent Renderer (used by Scene)
 * @param model the 3D model to display in the screen
 */
class HoloPyramidScene3D(
        renderer: Renderer,
        val model: Object3D) : Scene(renderer) {

    companion object {
        // Number of views we need
        val NUM_CAMERAS = 4
        /**
         * Distance from center of object to any one of the cameras
         * since cameras lie on a circle of this radius
         */
        val CAMERA_RADIUS = 5.0
    }

    init {
        initModel()
        initLights()
        initCameras()
    }

    /**
     * Recursively make sure that the model and all its
     * children have front-face culling enabled. This is because
     * the camera's vertical flip will flip all the faces, so back face
     * culling becomes incorrect
     */
    private fun frontFaceCulling(m: Object3D) {
        // This turns on front face culling
        m.isBackSided = true

        // Recursively set front face culling
        for (i in 0 until m.numChildren) {
            val child = m.getChildAt(i)
            frontFaceCulling(child)
        }
    }

    private fun initModel() {
        // Since the camera flips the projection verttically, we need to
        // switch from backface culling to frontface culling.
        frontFaceCulling(model)

        // If no material was specified in the OBJ model, make a default
        // material of white
        if (model.material == null) {
            val mat = Material()
            mat.enableLighting(true)
            mat.diffuseMethod = DiffuseMethod.Lambert()
            mat.color = 0xFFFFFF
            model.material = mat
        }

        // Calculate the maximum dimension in the xz plane
        val bbox = model.boundingBox
        val minDims = bbox?.min ?: Vector3()
        val maxDims = bbox?.max ?: Vector3()
        val dims = maxDims.subtract(minDims)
        val maxSize = Math.max(dims.x, dims.z)

        // We want the model to fit within the unit box, which has a
        // width of 2. So divide by the max dimension and multiply by 2
        val scale = 2.0 / maxSize
        model.setScale(scale, scale, scale)

        // Finally, add the model to the scene
        addChild(model)
    }

    private fun initLights() {
        //TODO: what lighting should the scene have?
        // Add a purple light shining from directly above
        val light1 = PointLight()
        light1.setPosition(0.0, 1.5, 0.0)
        //light1.setColor(0.5f, 0.0f, 1.0f)
        //light1.power = 1.0f
        addLight(light1)

        // Add a green light shining from the left
        val light2 = PointLight()
        light2.setPosition(-1.5, 0.0, 0.0)
        //light2.setColor(0.0f, 1.0f, 0.0f)
        //light2.power = 1.0f
        addLight(light2)

        // Add an orange light shining from the front
        val light3 = PointLight()
        light3.setPosition(0.0, 0.0, 1.5)
        //light3.setColor(1.0f, 0.5f, 0.0f)
        //light3.power = 1.0f
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

            // The model should fit within the unit sphere, so
            // set the near and far planes accordingly
            cam.nearPlane = CAMERA_RADIUS - 2.0
            cam.farPlane = CAMERA_RADIUS + 2.0
            addCamera(cam)
        }
        switchCamera(0)
    }

    fun  transformModel(transformer: ModelTransformer) {
        transformer.applyTransformation(model)
    }


}