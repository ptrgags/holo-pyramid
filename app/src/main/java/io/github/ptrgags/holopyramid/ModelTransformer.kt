package io.github.ptrgags.holopyramid

import org.rajawali3d.Object3D
import org.rajawali3d.math.Quaternion

class ModelTransformer {
    companion object {
        val AUTO_YAW_DELTA = 1.0
        val YAW_DELTA = 3.0
        val PITCH_DELTA = 3.0
        val HEIGHT_DELTA = 0.2
    }

    //TODO: Use a quaternion instead for manual rotation.
    /** Pitch of the model in manual mode */
    private var pitch = 0.0
    /** yaw of the model in manual mode */
    private var yaw = 0.0

    /** yaw in automatic mode */
    private var autoYaw = 0.0

    /** Height of the model. This can be adjusted by the user */
    private var height = 0.0

    /** If true, this will automatically rotate the model */
    private var automatic = true

    fun toggleMode() {
        //Toggle the mode
        automatic = !automatic
    }

    /**
     * Rotate the model a little.
     */
    fun autoRotate() {
        autoYaw += AUTO_YAW_DELTA
    }

    /**
     * Adjust the yaw by one click
     * @param direction +1 for cw (top down), -1 for ccw
     */
    fun adjustYaw(direction: Double) {
        // Just in case
        val sign = Math.signum(direction)
        yaw += sign * YAW_DELTA
    }

    /**
     * Adjust the pitch by one click
     * @param direction +1 for pitch up, -1 for pitch down
     */
    fun adjustPitch(direction: Double) {
        // Just in case
        val sign = Math.signum(direction)
        pitch += sign * PITCH_DELTA
    }

    /**
     * Adjust the height of the model by one click
     * @param direction: +1 for up, -1 for down
     */
    fun adjustHeight(direction: Double) {
        // Just in case
        val sign = Math.signum(direction)
        height += sign * HEIGHT_DELTA
    }

    /**
     * Reset the manual pitch and yaw
     */
    fun resetManualRotation() {
        pitch = 0.0
        yaw = 0.0
    }

    /**
     * Given a model, apply the active rotation to it.
     */
    fun applyTransformation(model: Object3D) {
        //Update the rotation
        val rot = if (automatic) {
            Quaternion().fromEuler(autoYaw, 0.0, 0.0)
        } else {
            Quaternion().fromEuler(yaw, pitch, 0.0)
        }
        model.setRotation(rot)

        //Update the height
        model.y = height
    }
}