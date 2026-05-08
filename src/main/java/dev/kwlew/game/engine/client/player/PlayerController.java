package dev.kwlew.game.engine.client.player;

import dev.kwlew.game.engine.client.camera.Camera;
import dev.kwlew.game.engine.client.camera.CameraSnapshot;
import dev.kwlew.game.engine.world.BlockWorld;
import dev.kwlew.game.engine.world.block.Blocks;
import dev.kwlew.game.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.GLFW_CURSOR;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_DISABLED;
import static org.lwjgl.glfw.GLFW.GLFW_CURSOR_NORMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.glfwSetInputMode;

public final class PlayerController {

    private static final float MOVE_SPEED = 5.5f;
    private static final float KEYBOARD_LOOK_SPEED_DEG = 90.0f;
    private static final float MOUSE_SENSITIVITY = 0.12f;
    private static final float GRAVITY = 28.0f;
    private static final float JUMP_VELOCITY = 9.5f;
    private static final float PLAYER_WIDTH = 1.0f;
    private static final float PLAYER_HEIGHT = 2.0f;
    private static final float EYE_HEIGHT = 1.62f;
    private static final float COLLISION_STEP = 0.05f;
    private static final Vector3f WORLD_UP = new Vector3f(0.0f, 1.0f, 0.0f);

    private final Camera camera = new Camera(new Vector3f(0.0f, 0.0f, 0.0f), -90.0f, -18.0f);
    private final Vector3f feetPosition = new Vector3f(0.0f, 2.0f, 6.0f);
    private final Vector3f previousFeetPosition = new Vector3f(feetPosition);
    private final Vector3f velocity = new Vector3f();
    private final Vector3f forward = new Vector3f();
    private final Vector3f right = new Vector3f();
    private final Vector3f previousForward = new Vector3f();
    private final Vector3f currentForward = new Vector3f();
    private final Vector3f interpolatedFeet = new Vector3f();
    private final Vector3f interpolatedForward = new Vector3f();
    private final Vector3f interpolatedEye = new Vector3f();
    private final Vector3f lookTarget = new Vector3f();
    private final Matrix4f interpolatedView = new Matrix4f();

    private boolean captureEnabled;
    private boolean firstMouseSample = true;
    private boolean jumpPressedLastFrame;
    private boolean onGround;
    private double lastMouseX;
    private double lastMouseY;

    public PlayerController() {
        camera.forward(currentForward);
        previousForward.set(currentForward);
        camera.setPosition(feetPosition.x, feetPosition.y + EYE_HEIGHT, feetPosition.z);
    }

    public void setCapture(Window window, boolean enabled) {
        if (captureEnabled == enabled) {
            return;
        }

        captureEnabled = enabled;
        firstMouseSample = true;
        jumpPressedLastFrame = false;
        glfwSetInputMode(window.getHandle(), GLFW_CURSOR, enabled ? GLFW_CURSOR_DISABLED : GLFW_CURSOR_NORMAL);
    }

    public void update(Window window, BlockWorld world, float deltaSeconds) {
        if (!captureEnabled) {
            return;
        }

        previousFeetPosition.set(feetPosition);
        previousForward.set(currentForward);

        updateLook(window, deltaSeconds);
        updateMovement(window, world, deltaSeconds);
        camera.setPosition(feetPosition.x, feetPosition.y + EYE_HEIGHT, feetPosition.z);
        camera.forward(currentForward);
    }

    public CameraSnapshot interpolatedSnapshot(float alpha) {
        float clampedAlpha = Math.max(0.0f, Math.min(1.0f, alpha));
        interpolatedFeet.set(previousFeetPosition).lerp(feetPosition, clampedAlpha);
        interpolatedForward.set(previousForward).lerp(currentForward, clampedAlpha);
        if (interpolatedForward.lengthSquared() < 0.0001f) {
            interpolatedForward.set(currentForward);
        }
        interpolatedForward.normalize();
        interpolatedEye.set(interpolatedFeet.x, interpolatedFeet.y + EYE_HEIGHT, interpolatedFeet.z);
        lookTarget.set(interpolatedEye).add(interpolatedForward);
        interpolatedView.identity().lookAt(interpolatedEye, lookTarget, WORLD_UP);
        return new CameraSnapshot(new Vector3f(interpolatedEye), new Vector3f(interpolatedForward), new Matrix4f(interpolatedView));
    }

    private void updateLook(Window window, float deltaSeconds) {
        double currentX;
        double currentY;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer xBuffer = stack.mallocDouble(1);
            DoubleBuffer yBuffer = stack.mallocDouble(1);
            glfwGetCursorPos(window.getHandle(), xBuffer, yBuffer);
            currentX = xBuffer.get(0);
            currentY = yBuffer.get(0);
        }

        if (firstMouseSample) {
            firstMouseSample = false;
            lastMouseX = currentX;
            lastMouseY = currentY;
        } else {
            float yawOffset = (float) ((currentX - lastMouseX) * MOUSE_SENSITIVITY);
            float pitchOffset = (float) ((lastMouseY - currentY) * MOUSE_SENSITIVITY);
            lastMouseX = currentX;
            lastMouseY = currentY;
            camera.addRotation(yawOffset, pitchOffset);
        }

        float lookStep = KEYBOARD_LOOK_SPEED_DEG * deltaSeconds;
        if (isPressed(window, GLFW_KEY_LEFT)) {
            camera.addRotation(-lookStep, 0.0f);
        }
        if (isPressed(window, GLFW_KEY_RIGHT)) {
            camera.addRotation(lookStep, 0.0f);
        }
        if (isPressed(window, GLFW_KEY_UP)) {
            camera.addRotation(0.0f, lookStep);
        }
        if (isPressed(window, GLFW_KEY_DOWN)) {
            camera.addRotation(0.0f, -lookStep);
        }
    }

    private void updateMovement(Window window, BlockWorld world, float deltaSeconds) {
        camera.forward(forward);
        camera.right(right);

        forward.y = 0.0f;
        if (forward.lengthSquared() > 0.0001f) {
            forward.normalize();
        }
        right.y = 0.0f;
        if (right.lengthSquared() > 0.0001f) {
            right.normalize();
        }

        float moveX = 0.0f;
        float moveZ = 0.0f;
        if (isPressed(window, GLFW_KEY_W)) {
            moveX += forward.x;
            moveZ += forward.z;
        }
        if (isPressed(window, GLFW_KEY_S)) {
            moveX -= forward.x;
            moveZ -= forward.z;
        }
        if (isPressed(window, GLFW_KEY_D)) {
            moveX += right.x;
            moveZ += right.z;
        }
        if (isPressed(window, GLFW_KEY_A)) {
            moveX -= right.x;
            moveZ -= right.z;
        }

        float moveLen = (float) Math.sqrt(moveX * moveX + moveZ * moveZ);
        if (moveLen > 0.0001f) {
            moveX /= moveLen;
            moveZ /= moveLen;
        }
        velocity.x = moveX * MOVE_SPEED;
        velocity.z = moveZ * MOVE_SPEED;

        boolean jumpPressed = isPressed(window, GLFW_KEY_SPACE);
        if (jumpPressed && !jumpPressedLastFrame && onGround) {
            velocity.y = JUMP_VELOCITY;
            onGround = false;
        }
        jumpPressedLastFrame = jumpPressed;

        if (isPressed(window, GLFW_KEY_LEFT_CONTROL)) {
            velocity.y -= MOVE_SPEED * 0.35f;
        }

        velocity.y -= GRAVITY * deltaSeconds;

        moveAxisX(world, velocity.x * deltaSeconds);
        moveAxisZ(world, velocity.z * deltaSeconds);
        onGround = false;
        moveAxisY(world, velocity.y * deltaSeconds);

    }

    private void moveAxisX(BlockWorld world, float deltaX) {
        if (deltaX == 0.0f) {
            return;
        }
        int steps = Math.max(1, (int) Math.ceil(Math.abs(deltaX) / COLLISION_STEP));
        float step = deltaX / steps;
        for (int i = 0; i < steps; i++) {
            feetPosition.x += step;
            if (collides(world)) {
                feetPosition.x -= step;
                velocity.x = 0.0f;
                return;
            }
        }
    }

    private void moveAxisY(BlockWorld world, float deltaY) {
        if (deltaY == 0.0f) {
            return;
        }
        int steps = Math.max(1, (int) Math.ceil(Math.abs(deltaY) / COLLISION_STEP));
        float step = deltaY / steps;
        for (int i = 0; i < steps; i++) {
            feetPosition.y += step;
            if (collides(world)) {
                feetPosition.y -= step;
                if (step < 0.0f) {
                    onGround = true;
                }
                velocity.y = 0.0f;
                return;
            }
        }
    }

    private void moveAxisZ(BlockWorld world, float deltaZ) {
        if (deltaZ == 0.0f) {
            return;
        }
        int steps = Math.max(1, (int) Math.ceil(Math.abs(deltaZ) / COLLISION_STEP));
        float step = deltaZ / steps;
        for (int i = 0; i < steps; i++) {
            feetPosition.z += step;
            if (collides(world)) {
                feetPosition.z -= step;
                velocity.z = 0.0f;
                return;
            }
        }
    }

    private boolean collides(BlockWorld world) {
        float halfWidth = PLAYER_WIDTH * 0.5f;
        float minX = feetPosition.x - halfWidth;
        float maxX = feetPosition.x + halfWidth;
        float minY = feetPosition.y;
        float maxY = feetPosition.y + PLAYER_HEIGHT;
        float minZ = feetPosition.z - halfWidth;
        float maxZ = feetPosition.z + halfWidth;

        int ix0 = (int) Math.floor(minX);
        int ix1 = (int) Math.floor(maxX - 0.0001f);
        int iy0 = (int) Math.floor(minY);
        int iy1 = (int) Math.floor(maxY - 0.0001f);
        int iz0 = (int) Math.floor(minZ);
        int iz1 = (int) Math.floor(maxZ - 0.0001f);

        for (int y = iy0; y <= iy1; y++) {
            for (int z = iz0; z <= iz1; z++) {
                for (int x = ix0; x <= ix1; x++) {
                    if (Blocks.byId(world.getBlock(x, y, z)).solid()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isPressed(Window window, int key) {
        return glfwGetKey(window.getHandle(), key) == GLFW_PRESS;
    }
}
