package io.github.ptrgags.holopyramid

import org.rajawali3d.cameras.Camera
import org.rajawali3d.math.Matrix4
import org.rajawali3d.math.Quaternion
import org.rajawali3d.math.vector.Vector3

/**
 * This is like a regular perspective camera except these two changes:
 * 1. Since the pyramid mirror will be placed on top of the phone, the
 *    projection must be flipped upside down.
 * 2. Each viewport must be rotated 90 degrees with respect to the previous
 *    one
 *
 * IMPORTANT: this flips faces backwards, so make sure to use
 * glCullFace(GL_FRONT) before rendering
 *
 * All this is done by overriding the results of getProjectionMatrix
 */
class HoloPyramidCamera : Camera {
    /**
     * Each viewport is rotated a different multiple of 90 degrees.
     */
    var rotAngle = 0.0

    constructor(rotAngle: Double): super() {
        this.rotAngle = rotAngle
    }

    /**
     * Take the projection matrix and apply a rotation and flip to get the
     * viewport ready for viewing in the pyramid mirror
     */
    override fun getProjectionMatrix(): Matrix4 {
        return super.getProjectionMatrix()
                .rotate(Vector3.Axis.Z, -rotAngle) // rotate view as needed
                .scale(1.0, -1.0, 1.0) // Flip upside-down
    }
}