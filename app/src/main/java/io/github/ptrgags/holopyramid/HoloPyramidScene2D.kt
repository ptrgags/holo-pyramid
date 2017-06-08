package io.github.ptrgags.holopyramid

import android.util.Log
import io.github.ptrgags.holopyramid.HoloPyramidRenderer.Companion.TAG
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
 */
class HoloPyramidScene2D : Scene {
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

    constructor(
            renderer: Renderer,
            renderTargets: List<RenderTarget>,
            aspectRatio: Double) : super(renderer) {
        createQuads(renderTargets, aspectRatio)
    }

    private fun createQuads(targets: List<RenderTarget>, aspectRatio: Double) {
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
}