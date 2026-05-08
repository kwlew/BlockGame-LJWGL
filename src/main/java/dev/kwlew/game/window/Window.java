package dev.kwlew.game.window;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

public class Window {

    private int width;
    private int height;
    private final String title;
    private long handle;
    private boolean resized;

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;
    }

    public void init() {
        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        handle = GLFW.glfwCreateWindow(width, height, title, 0, 0);
        if (handle == 0) {
            throw new IllegalStateException("Unable to create GLFW window");
        }

        GLFW.glfwMakeContextCurrent(handle);
        GL.createCapabilities();

        GLFW.glfwSetFramebufferSizeCallback(handle, (win, w, h) -> {
            width = w;
            height = h;
            resized = true;
        });
    }

    public void setTitle(String newTitle) {
        GLFW.glfwSetWindowTitle(handle, newTitle);
    }

    public boolean shouldClose() {
        return GLFW.glfwWindowShouldClose(handle);
    }

    public void pollEvents() {
        GLFW.glfwPollEvents();
    }

    public void swapBuffers() {
        GLFW.glfwSwapBuffers(handle);
    }

    public void cleanup() {
        GLFW.glfwDestroyWindow(handle);
        GLFW.glfwTerminate();
    }

    public long getHandle() { return handle; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public float getAspectRatio() { return (float) width / (float) height; }
    public boolean wasResized() { return resized; }
    public void clearResizedFlag() { resized = false; }
}