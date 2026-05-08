package dev.kwlew.game.engine.input;

import org.lwjgl.glfw.GLFW;

public class Input {
    private final long window;
    private final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private double lastMouseX;
    private double lastMouseY;
    private float deltaMouseX;
    private float deltaMouseY;
    private boolean firstMouse = true;

    public Input(long window) {
        this.window = window;
    }

    public void init() {
        GLFW.glfwSetInputMode(window, GLFW.GLFW_CURSOR, GLFW.GLFW_CURSOR_DISABLED);

        GLFW.glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key >= 0 && key < keys.length) {
                keys[key] = action != GLFW.GLFW_RELEASE;
            }
            if (key == GLFW.GLFW_KEY_ESCAPE && action == GLFW.GLFW_PRESS) {
                GLFW.glfwSetWindowShouldClose(win, true);
            }
        });

        GLFW.glfwSetCursorPosCallback(window, (win, x, y) -> {
            if (firstMouse) {
                lastMouseX = x;
                lastMouseY = y;
                firstMouse = false;
            }

            deltaMouseX += (float) (x - lastMouseX);
            deltaMouseY += (float) (y - lastMouseY);

            lastMouseX = x;
            lastMouseY = y;
        });
    }

    public boolean isKeyDown(int key) {
        return key >= 0 && key < keys.length && keys[key];
    }

    public float consumeMouseDeltaX() {
        float value = deltaMouseX;
        deltaMouseX = 0.0f;
        return value;
    }

    public float consumeMouseDeltaY() {
        float value = deltaMouseY;
        deltaMouseY = 0.0f;
        return value;
    }
}