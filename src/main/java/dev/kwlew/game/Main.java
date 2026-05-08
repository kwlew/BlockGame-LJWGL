package dev.kwlew.game;

import dev.kwlew.game.engine.graphics.Shader;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL30.*;

public class Main {

    private long window;

    private int vao;
    private int vbo;

    private Shader shader;

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {

        if (!GLFW.glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        window = GLFW.glfwCreateWindow(
                1280,
                720,
                "Block Game",
                0,
                0
        );

        GLFW.glfwMakeContextCurrent(window);

        GL.createCapabilities();

        shader = new Shader(
                "/shaders/basic.vert",
                "/shaders/basic.frag"
        );

        float[] vertices = {
                0.0f,  0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };

        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();

        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glVertexAttribPointer(
                0,
                3,
                GL_FLOAT,
                false,
                3 * Float.BYTES,
                0
        );

        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    private void loop() {

        while (!GLFW.glfwWindowShouldClose(window)) {

            glClearColor(0.5f, 0.7f, 1.0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            shader.bind();

            glBindVertexArray(vao);

            glDrawArrays(GL_TRIANGLES, 0, 3);

            GLFW.glfwSwapBuffers(window);
            GLFW.glfwPollEvents();
        }
    }

    private void cleanup() {
        shader.cleanup();

        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);

        GLFW.glfwDestroyWindow(window);
        GLFW.glfwTerminate();
    }

    public static void main(String[] args) {
        new Main().run();
    }
}