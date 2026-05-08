package dev.kwlew.game.engine.graphics;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.*;

public class Crosshair {
    private static final float[] VERTICES = {
            // horizontal (with center gap)
            -0.020f, 0.000f, 0.0f,  -0.006f, 0.000f, 0.0f,
            0.006f, 0.000f, 0.0f,   0.020f, 0.000f, 0.0f,
            // vertical (with center gap)
            0.000f,-0.020f, 0.0f,   0.000f,-0.006f, 0.0f,
            0.000f, 0.006f, 0.0f,   0.000f, 0.020f, 0.0f
    };

    private final Matrix4f identity = new Matrix4f();
    private int vao;
    private int vbo;

    public void init() {
        vao = glGenVertexArrays();
        glBindVertexArray(vao);

        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);

        FloatBuffer buffer = BufferUtils.createFloatBuffer(VERTICES.length);
        buffer.put(VERTICES).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public void render(Shader shader) {
        glDisable(GL_DEPTH_TEST);

        shader.setMatrix4("projection", identity);
        shader.setMatrix4("view", identity);
        shader.setMatrix4("model", identity);
        shader.setVec3("color", 1.0f, 1.0f, 1.0f);

        glBindVertexArray(vao);
        glDrawArrays(GL_LINES, 0, 8);
        glBindVertexArray(0);

        glEnable(GL_DEPTH_TEST);
    }

    public void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}