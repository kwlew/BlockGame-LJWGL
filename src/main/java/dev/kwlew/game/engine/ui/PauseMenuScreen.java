package dev.kwlew.game.engine.ui;

import dev.kwlew.game.window.Window;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.opengl.GL46.*;

public class PauseMenuScreen {

    public enum Action {
        NONE,
        RESUME_GAME,
        GO_TO_MAIN_MENU
    }

    private static final UiRect FULLSCREEN_RECT = new UiRect(0.0f, 0.0f, 1.0f, 1.0f);
    private static final UiRect RESUME_RECT = new UiRect(0.35f, 0.45f, 0.30f, 0.12f);
    private static final UiRect MAIN_MENU_RECT = new UiRect(0.35f, 0.61f, 0.30f, 0.12f);
    private static final UiRect TITLE_RECT = new UiRect(0.30f, 0.18f, 0.40f, 0.12f);
    private static final UiRect HELP_RECT = new UiRect(0.30f, 0.31f, 0.40f, 0.05f);
    private static final UiRect RESUME_TEXT_RECT = new UiRect(0.40f, 0.485f, 0.20f, 0.05f);
    private static final UiRect MAIN_MENU_TEXT_RECT = new UiRect(0.37f, 0.645f, 0.26f, 0.05f);

    private int programId;
    private int vaoId;
    private int vboId;

    private int backgroundTextureId;
    private int resumeTextureId;
    private int mainMenuTextureId;
    private int titleTextureId;
    private int helpTextureId;
    private int resumeTextTextureId;
    private int mainMenuTextTextureId;

    private int rectLocation;
    private int tintLocation;

    private boolean leftMousePressedLastFrame;

    public void init() {
        programId = createProgram();

        vaoId = glGenVertexArrays();
        vboId = glGenBuffers();

        glBindVertexArray(vaoId);
        glBindBuffer(GL_ARRAY_BUFFER, vboId);

        float[] vertices = {
                0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 0.0f, 0.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,
                0.0f, 1.0f, 0.0f, 1.0f
        };

        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.length);
        vertexBuffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2L * Float.BYTES);
        glEnableVertexAttribArray(1);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        rectLocation = glGetUniformLocation(programId, "uRect");
        tintLocation = glGetUniformLocation(programId, "uTint");

        glUseProgram(programId);
        int textureLocation = glGetUniformLocation(programId, "uTexture");
        glUniform1i(textureLocation, 0);
        glUseProgram(0);

        backgroundTextureId = createFallbackTexture(0x141821FF);
        resumeTextureId = createFallbackTexture(0x5DAB7DFF);
        mainMenuTextureId = createFallbackTexture(0x6077C2FF);

        titleTextureId = TextTextureFactory.createTexture("PAUSED", 700, 160, new Font("Arial", Font.BOLD, 110), new Color(252, 253, 255, 255));
        helpTextureId = TextTextureFactory.createTexture("Press ESC or Resume to continue", 760, 80, new Font("Arial", Font.PLAIN, 46), new Color(225, 231, 247, 255));
        resumeTextTextureId = TextTextureFactory.createTexture("RESUME", 420, 90, new Font("Arial", Font.BOLD, 58), new Color(255, 255, 255, 255));
        mainMenuTextTextureId = TextTextureFactory.createTexture("MAIN MENU", 520, 90, new Font("Arial", Font.BOLD, 56), new Color(255, 255, 255, 255));
    }

    public Action tick(Window window) {
        int width = window.getWidth();
        int height = window.getHeight();
        if (width <= 0 || height <= 0) {
            return Action.NONE;
        }

        boolean leftMousePressed = glfwGetMouseButton(window.getHandle(), GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;
        Action action = Action.NONE;

        if (leftMousePressed && !leftMousePressedLastFrame) {
            UiRect hoveredRect = getHoveredRect(window, width, height);
            if (hoveredRect == RESUME_RECT) {
                action = Action.RESUME_GAME;
            } else if (hoveredRect == MAIN_MENU_RECT) {
                action = Action.GO_TO_MAIN_MENU;
            }
        }

        leftMousePressedLastFrame = leftMousePressed;
        return action;
    }

    public void render(Window window) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.06f, 0.06f, 0.08f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        UiRect hoveredRect = getHoveredRect(window, window.getWidth(), window.getHeight());

        glUseProgram(programId);
        glBindVertexArray(vaoId);
        glActiveTexture(GL_TEXTURE0);

        drawTexturedQuad(backgroundTextureId, FULLSCREEN_RECT, 1.0f, 1.0f, 1.0f);
        drawTexturedQuad(titleTextureId, TITLE_RECT, 1.0f, 1.0f, 1.0f);
        drawTexturedQuad(helpTextureId, HELP_RECT, 1.0f, 1.0f, 1.0f);

        boolean resumeHovered = hoveredRect == RESUME_RECT;
        boolean mainMenuHovered = hoveredRect == MAIN_MENU_RECT;
        drawTexturedQuad(resumeTextureId, RESUME_RECT, resumeHovered ? 1.08f : 1.0f, resumeHovered ? 1.08f : 1.0f, resumeHovered ? 1.08f : 1.0f);
        drawTexturedQuad(mainMenuTextureId, MAIN_MENU_RECT, mainMenuHovered ? 1.08f : 1.0f, mainMenuHovered ? 1.08f : 1.0f, mainMenuHovered ? 1.08f : 1.0f);
        drawTexturedQuad(resumeTextTextureId, RESUME_TEXT_RECT, 1.0f, 1.0f, 1.0f);
        drawTexturedQuad(mainMenuTextTextureId, MAIN_MENU_TEXT_RECT, 1.0f, 1.0f, 1.0f);

        glBindVertexArray(0);
        glUseProgram(0);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    public void cleanup() {
        if (backgroundTextureId != 0) glDeleteTextures(backgroundTextureId);
        if (resumeTextureId != 0) glDeleteTextures(resumeTextureId);
        if (mainMenuTextureId != 0) glDeleteTextures(mainMenuTextureId);
        if (titleTextureId != 0) glDeleteTextures(titleTextureId);
        if (helpTextureId != 0) glDeleteTextures(helpTextureId);
        if (resumeTextTextureId != 0) glDeleteTextures(resumeTextTextureId);
        if (mainMenuTextTextureId != 0) glDeleteTextures(mainMenuTextTextureId);
        if (vboId != 0) glDeleteBuffers(vboId);
        if (vaoId != 0) glDeleteVertexArrays(vaoId);
        if (programId != 0) glDeleteProgram(programId);
    }

    private UiRect getHoveredRect(Window window, int width, int height) {
        if (width <= 0 || height <= 0) {
            return null;
        }

        double mouseX;
        double mouseY;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer xBuffer = stack.mallocDouble(1);
            DoubleBuffer yBuffer = stack.mallocDouble(1);
            glfwGetCursorPos(window.getHandle(), xBuffer, yBuffer);
            mouseX = xBuffer.get(0);
            mouseY = yBuffer.get(0);
        }

        float normalizedX = (float) (mouseX / (double) width);
        float normalizedY = (float) (mouseY / (double) height);

        if (RESUME_RECT.contains(normalizedX, normalizedY)) {
            return RESUME_RECT;
        }
        if (MAIN_MENU_RECT.contains(normalizedX, normalizedY)) {
            return MAIN_MENU_RECT;
        }
        return null;
    }

    private void drawTexturedQuad(int textureId, UiRect rect, float r, float g, float b) {
        glBindTexture(GL_TEXTURE_2D, textureId);
        glUniform4f(rectLocation, rect.x(), rect.y(), rect.width(), rect.height());
        glUniform4f(tintLocation, r, g, b, 1.0f);
        glDrawArrays(GL_TRIANGLES, 0, 6);
    }

    private int createProgram() {
        String vertexSource = loadText("/shaders/ui_menu.vert");
        String fragmentSource = loadText("/shaders/ui_menu.frag");

        int vertexShaderId = compileShader(GL_VERTEX_SHADER, vertexSource);
        int fragmentShaderId = compileShader(GL_FRAGMENT_SHADER, fragmentSource);

        int createdProgramId = glCreateProgram();
        glAttachShader(createdProgramId, vertexShaderId);
        glAttachShader(createdProgramId, fragmentShaderId);
        glLinkProgram(createdProgramId);

        if (glGetProgrami(createdProgramId, GL_LINK_STATUS) == GL_FALSE) {
            String error = glGetProgramInfoLog(createdProgramId);
            glDeleteShader(vertexShaderId);
            glDeleteShader(fragmentShaderId);
            glDeleteProgram(createdProgramId);
            throw new IllegalStateException("Failed to link menu shader program: " + error);
        }

        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
        return createdProgramId;
    }

    private int compileShader(int type, String source) {
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

    private int createFallbackTexture(int rgba) {
        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        ByteBuffer pixel = BufferUtils.createByteBuffer(4);
        pixel.put((byte) ((rgba >> 24) & 0xFF));
        pixel.put((byte) ((rgba >> 16) & 0xFF));
        pixel.put((byte) ((rgba >> 8) & 0xFF));
        pixel.put((byte) (rgba & 0xFF));
        pixel.flip();

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 1, 1, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixel);
        return textureId;
    }

    private record UiRect(float x, float y, float width, float height) {
        private boolean contains(float px, float py) {
            return px >= x && px <= x + width && py >= y && py <= y + height;
        }
    }
}
