package dev.kwlew.game.engine.client.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public final class Camera {

    private final Vector3f position;
    private final Vector3f front = new Vector3f();
    private final Vector3f up = new Vector3f();
    private final Vector3f right = new Vector3f();
    private final Vector3f worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f target = new Vector3f();
    private float yaw;
    private float pitch;

    public Camera(Vector3f spawnPosition, float yaw, float pitch) {
        this.position = new Vector3f(spawnPosition);
        this.yaw = yaw;
        this.pitch = pitch;
        updateVectors();
    }

    public void addRotation(float yawOffset, float pitchOffset) {
        yaw += yawOffset;
        pitch = Math.max(-89.0f, Math.min(89.0f, pitch + pitchOffset));
        updateVectors();
    }

    public void moveForward(float amount) {
        Vector3f flatForward = new Vector3f(front.x, 0.0f, front.z);
        if (flatForward.lengthSquared() < 0.0001f) {
            return;
        }
        flatForward.normalize();
        position.fma(amount, flatForward);
    }

    public void moveRight(float amount) {
        Vector3f flatRight = new Vector3f(right.x, 0.0f, right.z);
        if (flatRight.lengthSquared() < 0.0001f) {
            return;
        }
        flatRight.normalize();
        position.fma(amount, flatRight);
    }

    public void moveUp(float amount) {
        position.y += amount;
    }

    public void setPosition(float x, float y, float z) {
        position.set(x, y, z);
    }

    public Vector3f forward(Vector3f destination) {
        return destination.set(front);
    }

    public Vector3f right(Vector3f destination) {
        return destination.set(right);
    }

    public Vector3f position(Vector3f destination) {
        return destination.set(position);
    }

    public Matrix4f viewMatrix(Matrix4f destination) {
        return destination.identity().lookAt(position, target.set(position).add(front), up);
    }

    private void updateVectors() {
        float yawRad = (float) Math.toRadians(yaw);
        float pitchRad = (float) Math.toRadians(pitch);

        front.set(
                (float) (Math.cos(yawRad) * Math.cos(pitchRad)),
                (float) Math.sin(pitchRad),
                (float) (Math.sin(yawRad) * Math.cos(pitchRad))
        ).normalize();

        right.set(front).cross(worldUp).normalize();
        up.set(right).cross(front).normalize();
    }
}
