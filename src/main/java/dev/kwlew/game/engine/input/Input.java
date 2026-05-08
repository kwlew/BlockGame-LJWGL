package dev.kwlew.game.engine.input;

import org.lwjgl.glfw.GLFW;

public class Input {
    private final long window;
    private final boolean[] keys = new boolean[GLFW.GLFW_KEY_LAST + 1];
    private final boolean[] justPressed = new boolean[GLFW.GLFW_KEY_LAST + 1];

    private double lastMouseX;
    private double lastMouseY;
    private float deltaMouseX;
    private float deltaMouseY;
    private boolean firstMouse = true;
    private boolean cursorCaptured = true;

    public Input(long window) {
        this.window = window;
    }

    public void init() {
        setCursorCaptured(true);

        GLFW.glfwSetKeyCallback(window, (win, key, scancode, action, mods) -> {
            if (key >= 0 && key < keys.length) {
                if (action == GLFW.GLFW_PRESS) {
                    if (!keys[key]) {
                        justPressed[key] = true;
                    }
                    keys[key] = true;
                } else if (action == GLFW.GLFW_RELEASE) {
                    keys[key] = false;
                }
            }
        });

        GLFW.glfwSetCursorPosCallback(window, (win, x, y) -> {
            if (!cursorCaptured) {
                lastMouseX = x;
                lastMouseY = y;
                return;
            }

            if (firstMouse) {
                lastMouseX = x;
                lastMouseY = y;
                firstMouse = false;
                return;
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

    public boolean wasKeyJustPressed(int key) {
        if (key < 0 || key >= justPressed.length) return false;
        boolean value = justPressed[key];
        justPressed[key] = false;
        return value;
    }

    public void setCursorCaptured(boolean captured) {
        cursorCaptured = captured;
        GLFW.glfwSetInputMode(
                window,
                GLFW.GLFW_CURSOR,
                captured ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL
        );
        firstMouse = true;
        deltaMouseX = 0.0f;
        deltaMouseY = 0.0f;
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
