package dev.kwlew.game.engine.client.render;

import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL46.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL46.GL_FLOAT;
import static org.lwjgl.opengl.GL46.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL46.GL_TRIANGLES;
import static org.lwjgl.opengl.GL46.glBindBuffer;
import static org.lwjgl.opengl.GL46.glBindVertexArray;
import static org.lwjgl.opengl.GL46.glBufferData;
import static org.lwjgl.opengl.GL46.glDeleteBuffers;
import static org.lwjgl.opengl.GL46.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL46.glDrawArrays;
import static org.lwjgl.opengl.GL46.glDrawArraysInstanced;
import static org.lwjgl.opengl.GL46.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL46.glGenBuffers;
import static org.lwjgl.opengl.GL46.glGenVertexArrays;
import static org.lwjgl.opengl.GL46.glVertexAttribPointer;
import static org.lwjgl.opengl.GL46.glVertexAttribDivisor;

final class CubeMesh {

    private static final int FLOATS_PER_INSTANCE = 19;

    private int vaoId;
    private int vboId;
    private int instanceVboId;

    void init() {
        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();
        instanceVboId = glGenBuffers();

        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, cubeVertices(), GL_STATIC_DRAW);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3L * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, instanceVboId);
        glBufferData(GL_ARRAY_BUFFER, 0L, GL_DYNAMIC_DRAW);

        int instanceStride = FLOATS_PER_INSTANCE * Float.BYTES;
        glVertexAttribPointer(2, 4, GL_FLOAT, false, instanceStride, 0L);
        glEnableVertexAttribArray(2);
        glVertexAttribDivisor(2, 1);

        glVertexAttribPointer(3, 4, GL_FLOAT, false, instanceStride, 4L * Float.BYTES);
        glEnableVertexAttribArray(3);
        glVertexAttribDivisor(3, 1);

        glVertexAttribPointer(4, 4, GL_FLOAT, false, instanceStride, 8L * Float.BYTES);
        glEnableVertexAttribArray(4);
        glVertexAttribDivisor(4, 1);

        glVertexAttribPointer(5, 4, GL_FLOAT, false, instanceStride, 12L * Float.BYTES);
        glEnableVertexAttribArray(5);
        glVertexAttribDivisor(5, 1);

        glVertexAttribPointer(6, 3, GL_FLOAT, false, instanceStride, 16L * Float.BYTES);
        glEnableVertexAttribArray(6);
        glVertexAttribDivisor(6, 1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    void bind() {
        glBindVertexArray(vaoId);
    }

    void unbind() {
        glBindVertexArray(0);
    }

    void uploadInstances(FloatBuffer instances) {
        glBindBuffer(GL_ARRAY_BUFFER, instanceVboId);
        glBufferData(GL_ARRAY_BUFFER, instances, GL_DYNAMIC_DRAW);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
    }

    void drawInstanced(int instanceCount) {
        if (instanceCount > 0) {
            glDrawArraysInstanced(GL_TRIANGLES, 0, 36, instanceCount);
        }
    }

    void cleanup() {
        if (instanceVboId != 0) glDeleteBuffers(instanceVboId);
        if (vboId != 0) glDeleteBuffers(vboId);
        if (vaoId != 0) glDeleteVertexArrays(vaoId);
    }

    private static FloatBuffer cubeVertices() {
        float[] vertices = {
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,   0.5f, -0.5f, 0.5f, 1.0f, 0.0f,   0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                -0.5f, -0.5f, 0.5f, 0.0f, 0.0f,   0.5f, 0.5f, 0.5f, 1.0f, 1.0f,   -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 1.0f, 0.0f,  -0.5f, 0.5f, -0.5f, 1.0f, 1.0f,   0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 1.0f, 0.0f,   0.5f, 0.5f, -0.5f, 0.0f, 1.0f,   0.5f, -0.5f, -0.5f, 0.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,  -0.5f, -0.5f, 0.5f, 1.0f, 0.0f,  -0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 0.0f,  -0.5f, 0.5f, 0.5f, 1.0f, 1.0f,  -0.5f, 0.5f, -0.5f, 0.0f, 1.0f,
                 0.5f, -0.5f, -0.5f, 1.0f, 0.0f,   0.5f, 0.5f, -0.5f, 1.0f, 1.0f,   0.5f, 0.5f, 0.5f, 0.0f, 1.0f,
                 0.5f, -0.5f, -0.5f, 1.0f, 0.0f,   0.5f, 0.5f, 0.5f, 0.0f, 1.0f,   0.5f, -0.5f, 0.5f, 0.0f, 0.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 0.0f,   -0.5f, 0.5f, 0.5f, 0.0f, 1.0f,    0.5f, 0.5f, 0.5f, 1.0f, 1.0f,
                -0.5f, 0.5f, -0.5f, 0.0f, 0.0f,    0.5f, 0.5f, 0.5f, 1.0f, 1.0f,    0.5f, 0.5f, -0.5f, 1.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,   0.5f, -0.5f, -0.5f, 1.0f, 1.0f,   0.5f, -0.5f, 0.5f, 1.0f, 0.0f,
                -0.5f, -0.5f, -0.5f, 0.0f, 1.0f,   0.5f, -0.5f, 0.5f, 1.0f, 0.0f,  -0.5f, -0.5f, 0.5f, 0.0f, 0.0f
        };

        FloatBuffer buffer = BufferUtils.createFloatBuffer(vertices.length);
        buffer.put(vertices).flip();
        return buffer;
    }
}
