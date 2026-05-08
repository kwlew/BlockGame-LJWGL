package dev.kwlew.game;

import dev.kwlew.game.engine.graphics.Camera;
import dev.kwlew.game.engine.graphics.Renderer;
import dev.kwlew.game.engine.input.Input;
import dev.kwlew.game.window.Window;
import org.lwjgl.glfw.GLFW;

public class Game {

    private Window window;
    private Input input;
    private Renderer renderer;
    private Camera camera;

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
        camera.setPosition(0.0f, 0.0f, 3.0f);
    }

    private void loop() {
        double lastTime = GLFW.glfwGetTime();

        while (!window.shouldClose()) {
            double now = GLFW.glfwGetTime();
            float deltaTime = (float) (now - lastTime);
            lastTime = now;

            window.pollEvents();

            if (window.wasResized()) {
                camera.setAspectRatio(window.getAspectRatio());
                window.clearResizedFlag();
            }

            handleMovement(deltaTime);

            renderer.render(camera, (float) now, window.getWidth(), window.getHeight());
            window.swapBuffers();
        }
    }

    private void handleMovement(float deltaTime) {
        float speed = 3.0f * deltaTime;
        float mouseSensitivity = 0.1f;

        if (input.isKeyDown(GLFW.GLFW_KEY_W)) camera.moveForward(speed);
        if (input.isKeyDown(GLFW.GLFW_KEY_S)) camera.moveForward(-speed);
        if (input.isKeyDown(GLFW.GLFW_KEY_A)) camera.moveRight(-speed);
        if (input.isKeyDown(GLFW.GLFW_KEY_D)) camera.moveRight(speed);
        if (input.isKeyDown(GLFW.GLFW_KEY_SPACE)) camera.moveUp(speed);
        if (input.isKeyDown(GLFW.GLFW_KEY_LEFT_SHIFT)) camera.moveUp(-speed);

        camera.addYawPitch(input.consumeMouseDeltaX() * mouseSensitivity, -input.consumeMouseDeltaY() * mouseSensitivity);
    }

    private void cleanup() {
        renderer.cleanup();
        window.cleanup();
    }

}