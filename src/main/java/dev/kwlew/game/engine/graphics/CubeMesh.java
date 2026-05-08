package dev.kwlew.game.engine.graphics;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL46.*;

public class CubeMesh {

    private static final float[] VERTICES = {
            // Front
            -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f, -0.5f,  0.5f,
            // Back
            -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f,
            0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
            // Left
            -0.5f,  0.5f,  0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,  0.5f,
            // Right
            0.5f,  0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,  0.5f,  0.5f, -0.5f,  0.5f,  0.5f,  0.5f,
            // Top
            -0.5f,  0.5f, -0.5f, -0.5f,  0.5f,  0.5f,  0.5f,  0.5f,  0.5f,
            0.5f,  0.5f,  0.5f,  0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f,
            // Bottom
            -0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f,  0.5f,
            0.5f, -0.5f,  0.5f, -0.5f, -0.5f,  0.5f, -0.5f, -0.5f, -0.5f
    };

    private int vao;
    private int vbo;

    private int edgeVao;
    private int edgeVbo;
    private int edgeEbo;

    private static final float[] EDGE_VERTICES = {
            -0.5f, -0.5f, -0.5f, // 0
            0.5f, -0.5f, -0.5f, // 1
            0.5f,  0.5f, -0.5f, // 2
            -0.5f,  0.5f, -0.5f, // 3
            -0.5f, -0.5f,  0.5f, // 4
            0.5f, -0.5f,  0.5f, // 5
            0.5f,  0.5f,  0.5f, // 6
            -0.5f,  0.5f,  0.5f  // 7
    };

    private static final int[] EDGE_INDICES = {
            0,1, 1,2, 2,3, 3,0, // back
            4,5, 5,6, 6,7, 7,4, // front
            0,4, 1,5, 2,6, 3,7  // connections
    };

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

        edgeVao = glGenVertexArrays();
        glBindVertexArray(edgeVao);

        edgeVbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, edgeVbo);
        FloatBuffer edgeVertexBuffer = BufferUtils.createFloatBuffer(EDGE_VERTICES.length);
        edgeVertexBuffer.put(EDGE_VERTICES).flip();
        glBufferData(GL_ARRAY_BUFFER, edgeVertexBuffer, GL_STATIC_DRAW);

        edgeEbo = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, edgeEbo);
        IntBuffer edgeIndexBuffer = BufferUtils.createIntBuffer(EDGE_INDICES.length);
        edgeIndexBuffer.put(EDGE_INDICES).flip();
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, edgeIndexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);

        glBindVertexArray(0);
    }

    public void bind() {
        glBindVertexArray(vao);
    }

    public void draw() {
        glDrawArrays(GL_TRIANGLES, 0, 36);
    }

    public void drawOutline() {
        glBindVertexArray(edgeVao);
        glDrawElements(GL_LINES, EDGE_INDICES.length, GL_UNSIGNED_INT, 0);
    }

    public void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
        glDeleteBuffers(edgeEbo);
        glDeleteBuffers(edgeVbo);
        glDeleteVertexArrays(edgeVao);
    }
}