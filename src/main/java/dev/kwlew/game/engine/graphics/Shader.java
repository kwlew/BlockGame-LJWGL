package dev.kwlew.game.engine.graphics;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryStack;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.util.Objects;
import java.util.stream.Collectors;



import static org.lwjgl.opengl.GL46.*;

public class Shader {

    private final int programId;

    public Shader(String vertexPath, String fragmentPath) {
        String vertexSource = loadResource(vertexPath);
        String fragmentSource = loadResource(fragmentPath);

        int vertexId = createShader(vertexSource, GL_VERTEX_SHADER);
        int fragmentId = createShader(fragmentSource, GL_FRAGMENT_SHADER);

        programId = glCreateProgram();

        glAttachShader(programId, vertexId);
        glAttachShader(programId, fragmentId);

        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new RuntimeException(glGetProgramInfoLog(programId));
        }

        glDeleteShader(vertexId);
        glDeleteShader(fragmentId);
    }

    public void setMatrix4(String name, Matrix4f matrix) {

        int location = glGetUniformLocation(programId, name);

        try (MemoryStack stack = MemoryStack.stackPush()) {

            FloatBuffer buffer = stack.mallocFloat(16);

            matrix.get(buffer);

            glUniformMatrix4fv(location, false, buffer);
        }

    }

    public void setVec3(String name, float x, float y, float z) {
        int location = glGetUniformLocation(programId, name);
        glUniform3f(location, x, y, z);
    }

    private int createShader(String source, int type) {
        int shaderId = glCreateShader(type);

        glShaderSource(shaderId, source);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new RuntimeException(glGetShaderInfoLog(shaderId));
        }

        return shaderId;
    }

    private String loadResource(String path) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        Objects.requireNonNull(getClass().getResourceAsStream(path))
                )
        )) {
            return reader.lines().collect(Collectors.joining("\n"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void cleanup() {
        glDeleteProgram(programId);
    }
}