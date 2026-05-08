package dev.kwlew.game.engine.graphics;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position = new Vector3f();
    private final Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f right = new Vector3f(1.0f, 0.0f, 0.0f);
    private final Vector3f worldUp = new Vector3f(0.0f, 1.0f, 0.0f);

    private float yaw = -90.0f;
    private float pitch = 0.0f;

    private final Matrix4f projection = new Matrix4f();
    private final float fovRadians;
    private final float near;
    private final float far;

    public Camera(float fovRadians, float aspect, float near, float far) {
        this.fovRadians = fovRadians;
        this.near = near;
        this.far = far;
        setAspectRatio(aspect);
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public void moveForward(float amount) {
        position.fma(amount, front);
    }

    public void moveRight(float amount) {
        position.fma(amount, right);
    }

    public void moveUp(float amount) {
        position.fma(amount, worldUp);
    }

    public void addYawPitch(float deltaYaw, float deltaPitch) {
        yaw += deltaYaw;
        pitch += deltaPitch;

        if (pitch > 89.0f) pitch = 89.0f;
        if (pitch < -89.0f) pitch = -89.0f;

        updateVectors();
    }

    public void setAspectRatio(float aspect) {
        projection.identity().perspective(fovRadians, aspect, near, far);
    }

    public Matrix4f getProjectionMatrix() {
        return new Matrix4f(projection);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, new Vector3f(position).add(front), up);
    }

    private void updateVectors() {
        front.x = (float) (Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.y = (float) Math.sin(Math.toRadians(pitch));
        front.z = (float) (Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)));
        front.normalize();

        front.cross(worldUp, right).normalize();
        right.cross(front, up).normalize();
    }
}