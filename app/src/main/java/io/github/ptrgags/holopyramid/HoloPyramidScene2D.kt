package io.github.ptrgags.holopyramid

import android.util.Log
import org.rajawali3d.materials.Material
import org.rajawali3d.materials.textures.ATexture
import org.rajawali3d.primitives.ScreenQuad
import org.rajawali3d.renderer.RenderTarget
import org.rajawali3d.renderer.Renderer
import org.rajawali3d.scene.Scene

/**
 * A 2D scene with the four ScreenQuads that are textured
 * with the four views of the object.
 *
 *  Scene layout
 *
 *         quad 2 (cam 2)
 *
 *  quad 3 (cam 3)     quad 1 (cam 1)
 *
 *          quad 0 (cam 0)
 *
 *   +y
 *   |
 *   |
 *   |
 *   +------- +x
 *
 * @param renderer the parent renderer (needed by Scene)
 * @param renderTargets list of the four render targets, one for each
 *      view of the objectt
 * @param aspectRatio aspect ratio. This is used to position the screen quads
 */
class HoloPyramidScene2D(
        renderer: Renderer,
        renderTargets: List<RenderTarget>,
        val aspectRatio: Double) : Scene(renderer) {

    companion object {
        /** There are four views for the four sides of the pyramid */
        val NUM_QUADS = 4
        /** shrink each quad a bit so we can fit them all on screen*/
        val QUAD_SCALE = 0.25
        /**
         * The quads are arranged at the four corners of a circle
         * with this radius
         */
        val QUAD_RADIUS = 0.25
        /** Logging tag */
        val TAG = "holopyramid-scene2d"
    }

    init {
        createQuads(renderTargets)
        createCenterMarker()
    }


    /**
     * Create four ScreenQuads, one for each view of the object
     * @param targets the render targets that are used to texture
     *      the ScreenQuads
     */
    private fun createQuads(targets: List<RenderTarget>) {
        for (i in 0 until NUM_QUADS) {
            // Make a texture from the render target
            val mat = Material()
            mat.colorInfluence = 0.0f
            try {
                mat.addTexture(targets[i].texture)
            } catch (e: ATexture.TextureException) {
                Log.e(TAG, "Error adding material", e)
            }

            // Make the quad and attach the render target's texture.
            // The quad is scaled down since we need to fit four
            // of them on the screen
            val quad = ScreenQuad()
            quad.setScale(QUAD_SCALE, QUAD_SCALE, 1.0)
            quad.material = mat

            // Position the quads.
            quad.x = QUAD_RADIUS * Math.sin(i * Math.PI / 2.0)
            quad.y = QUAD_RADIUS * aspectRatio * -Math.cos(i * Math.PI / 2.0)
            addChild(quad)
        }
    }

    /**
     * Make another screen quad in the center of the screen.
     * This will make it easier to line up the pyramid mirror
     */
    private fun createCenterMarker() {
        // Simple solid color material. It's dark
        // so it doesn't show up much in the mirror yet it stands out
        // against the black background
        val mat = Material()
        mat.color = 0x202020

        // Add a small quad to mark the center of the screen
        val quad = ScreenQuad()
        val MARKER_SCALE = 0.1
        quad.setScale(MARKER_SCALE, MARKER_SCALE * aspectRatio, 1.0)
        quad.material = mat
        addChild(quad)
    }
}