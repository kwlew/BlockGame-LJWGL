package dev.kwlew.game.engine.client.render;

import org.joml.Matrix4f;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.opengl.GL46.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL46.GL_FALSE;
import static org.lwjgl.opengl.GL46.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL46.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL46.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL46.glAttachShader;
import static org.lwjgl.opengl.GL46.glCompileShader;
import static org.lwjgl.opengl.GL46.glCreateProgram;
import static org.lwjgl.opengl.GL46.glCreateShader;
import static org.lwjgl.opengl.GL46.glDeleteProgram;
import static org.lwjgl.opengl.GL46.glDeleteShader;
import static org.lwjgl.opengl.GL46.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL46.glGetProgrami;
import static org.lwjgl.opengl.GL46.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL46.glGetShaderi;
import static org.lwjgl.opengl.GL46.glGetUniformLocation;
import static org.lwjgl.opengl.GL46.glLinkProgram;
import static org.lwjgl.opengl.GL46.glShaderSource;
import static org.lwjgl.opengl.GL46.glUniform1i;
import static org.lwjgl.opengl.GL46.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL46.glUseProgram;

final class BasicShaderProgram {

    private final float[] matrixElements = new float[16];

    private int programId;
    private int projectionLocation;
    private int viewLocation;

    void init() {
        int vertexShaderId = compileShader(GL_VERTEX_SHADER, loadText("/shaders/basic.vert"));
        int fragmentShaderId = compileShader(GL_FRAGMENT_SHADER, loadText("/shaders/basic.frag"));

        programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            String error = glGetProgramInfoLog(programId);
            glDeleteShader(vertexShaderId);
            glDeleteShader(fragmentShaderId);
            glDeleteProgram(programId);
            throw new IllegalStateException("Failed to link basic shader program: " + error);
        }

        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);

        projectionLocation = glGetUniformLocation(programId, "projection");
        viewLocation = glGetUniformLocation(programId, "view");

        glUseProgram(programId);
        int textureLocation = glGetUniformLocation(programId, "diffuseTexture");
        glUniform1i(textureLocation, 0);
        glUseProgram(0);
    }

    void use() {
        glUseProgram(programId);
    }

    void stop() {
        glUseProgram(0);
    }

    void cleanup() {
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    void setProjection(Matrix4f matrix) {
        uploadMatrix(projectionLocation, matrix);
    }

    void setView(Matrix4f matrix) {
        uploadMatrix(viewLocation, matrix);
    }

    private void uploadMatrix(int location, Matrix4f matrix) {
        matrix.get(matrixElements);
        glUniformMatrix4fv(location, false, matrixElements);
    }

    private static int compileShader(int type, String source) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, source);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            String error = glGetShaderInfoLog(shaderId);
            glDeleteShader(shaderId);
            throw new IllegalStateException("Failed to compile shader: " + error);
        }
        return shaderId;
    }

    private String loadText(String path) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                throw new IllegalStateException("Missing resource: " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading resource: " + path, e);
        }
    }
}
