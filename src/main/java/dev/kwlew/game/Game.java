package dev.kwlew.game;

import dev.kwlew.game.engine.debug.FpsCounter;
import dev.kwlew.game.engine.graphics.Camera;
import dev.kwlew.game.engine.graphics.Renderer;
import dev.kwlew.game.engine.input.Input;
import dev.kwlew.game.engine.world.BlockHit;
import dev.kwlew.game.engine.world.BlockWorld;
import dev.kwlew.game.window.Window;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Game {

    private final FpsCounter fpsCounter = new FpsCounter();

    private static final float MOVE_SPEED = 4.2f;
    private static final float REACH_DISTANCE = 3.0f;
    private BlockHit aimedBlock;
    private static final float GRAVITY = 22.0f;
    private static final float JUMP_VELOCITY = 8.0f;
    private static final float PLAYER_RADIUS = 0.28f;
    private static final float PLAYER_HEIGHT = 1.8f;
    private static final float EYE_HEIGHT = 1.62f;

    private Window window;
    private Input input;
    private Renderer renderer;
    private Camera camera;
    private BlockWorld world;

    private final Vector3f playerFeet = new Vector3f();
    private float verticalVelocity;
    private boolean grounded;
    private boolean paused;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        window = new Window(1280, 720, "Block Game");
        window.init();

        input = new Input(window.getHandle());
        input.init();

        renderer = new Renderer();
        renderer.init();

        camera = new Camera((float) Math.toRadians(70.0f), window.getAspectRatio(), 0.1f, 100.0f);
        world = new BlockWorld();

        playerFeet.set(0.0f, 1.0f, 6.0f);
        syncCameraToPlayer();

        updateWindowTitle();
    }

    private void loop() {
        double lastTime = GLFW.glfwGetTime();

        while (!window.shouldClose()) {
            double now = GLFW.glfwGetTime();
            float deltaTime = (float) (now - lastTime);
            fpsCounter.frame(deltaTime);
            updateWindowTitle();
            lastTime = now;

            window.pollEvents();
            handlePauseToggle();

            if (window.wasResized()) {
                camera.setAspectRatio(window.getAspectRatio());
                window.clearResizedFlag();
            }

            if (!paused) {
                handleMovement(deltaTime);
            }

            aimedBlock = world.raycast(
                    camera.getPosition(new Vector3f()),
                    camera.getFront(new Vector3f()),
                    REACH_DISTANCE
            );

            renderer.render(camera, world, aimedBlock, window.getWidth(), window.getHeight());
            window.swapBuffers();
        }
    }

    private void handlePauseToggle() {
        if (input.wasKeyJustPressed(GLFW.GLFW_KEY_ESCAPE)) {
            paused = !paused;
            input.setCursorCaptured(!paused);
            window.setTitle(paused
                    ? "Block Game [PAUSED] - ESC resume, Q quit"
                    : "Block Game");
        }

        if (paused && input.wasKeyJustPressed(GLFW.GLFW_KEY_Q)) {
            GLFW.glfwSetWindowShouldClose(window.getHandle(), true);
        }
    }

    private void handleMovement(float deltaTime) {
        float mouseSensitivity = 0.1f;
        camera.addYawPitch(input.consumeMouseDeltaX() * mouseSensitivity, -input.consumeMouseDeltaY() * mouseSensitivity);

        Vector3f movement = new Vector3f();
        Vector3f forward = camera.getForwardOnPlane(new Vector3f());
        Vector3f right = camera.getRightOnPlane(new Vector3f());

        if (input.isKeyDown(GLFW.GLFW_KEY_W)) movement.add(forward);
        if (input.isKeyDown(GLFW.GLFW_KEY_S)) movement.sub(forward);
        if (input.isKeyDown(GLFW.GLFW_KEY_A)) movement.sub(right);
        if (input.isKeyDown(GLFW.GLFW_KEY_D)) movement.add(right);

        if (movement.lengthSquared() > 0.0f) {
            movement.normalize(MOVE_SPEED * deltaTime);
            moveHorizontal(movement.x, movement.z);
        }

        if (grounded && input.wasKeyJustPressed(GLFW.GLFW_KEY_SPACE)) {
            verticalVelocity = JUMP_VELOCITY;
            grounded = false;
        }

        applyGravity(deltaTime);
        syncCameraToPlayer();
    }

    private void moveHorizontal(float deltaX, float deltaZ) {
        float candidateX = playerFeet.x + deltaX;
        if (!world.collidesWithPlayerAabb(candidateX, playerFeet.y, playerFeet.z, PLAYER_RADIUS, PLAYER_HEIGHT)) {
            playerFeet.x = candidateX;
        }

        float candidateZ = playerFeet.z + deltaZ;
        if (!world.collidesWithPlayerAabb(playerFeet.x, playerFeet.y, candidateZ, PLAYER_RADIUS, PLAYER_HEIGHT)) {
            playerFeet.z = candidateZ;
        }
    }

    private void applyGravity(float deltaTime) {
        float previousY = playerFeet.y;
        verticalVelocity -= GRAVITY * deltaTime;

        float candidateY = previousY + verticalVelocity * deltaTime;
        if (!world.collidesWithPlayerAabb(playerFeet.x, candidateY, playerFeet.z, PLAYER_RADIUS, PLAYER_HEIGHT)) {
            playerFeet.y = candidateY;
            grounded = false;
            return;
        }

        if (verticalVelocity < 0.0f) {
            float groundY = world.findGroundHeight(playerFeet.x, playerFeet.z, PLAYER_RADIUS, previousY);
            if (!Float.isNaN(groundY)) {
                playerFeet.y = groundY;
                verticalVelocity = 0.0f;
                grounded = true;
                return;
            }
        }

        verticalVelocity = 0.0f;
    }

    private void syncCameraToPlayer() {
        camera.setPosition(playerFeet.x, playerFeet.y + EYE_HEIGHT, playerFeet.z);
    }

    private void updateWindowTitle() {
        String pausedSuffix = paused ? " [Paused]" : "";
        window.setTitle("Block Game" + pausedSuffix + " | FPS: " + fpsCounter.getFps());
    }

    private void cleanup() {
        renderer.cleanup();
        window.cleanup();
    }
}