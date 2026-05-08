package dev.kwlew.game.engine.graphics;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL21.*;

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