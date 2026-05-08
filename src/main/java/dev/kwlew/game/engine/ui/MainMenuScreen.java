package dev.kwlew.game.engine.ui;

import dev.kwlew.game.window.Window;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.opengl.GL46.*;

public class MainMenuScreen {

    public enum Action {
        NONE,
        START_GAME,
        QUIT_GAME
    }

    private static final UiRect FULLSCREEN_RECT = new UiRect(0.0f, 0.0f, 1.0f, 1.0f);
    private static final UiRect PLAY_RECT = new UiRect(0.35f, 0.42f, 0.30f, 0.12f);
    private static final UiRect QUIT_RECT = new UiRect(0.35f, 0.58f, 0.30f, 0.12f);
    private static final UiRect TITLE_RECT = new UiRect(0.22f, 0.14f, 0.56f, 0.12f);
    private static final UiRect HELP_RECT = new UiRect(0.25f, 0.28f, 0.50f, 0.05f);
    private static final UiRect PLAY_TEXT_RECT = new UiRect(0.425f, 0.455f, 0.15f, 0.05f);
    private static final UiRect QUIT_TEXT_RECT = new UiRect(0.44f, 0.615f, 0.12f, 0.05f);

    private int programId;
    private int vaoId;
    private int vboId;

    private int backgroundTextureId;
    private int playTextureId;
    private int quitTextureId;
    private int titleTextureId;
    private int helpTextureId;
    private int playTextTextureId;
    private int quitTextTextureId;

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

        backgroundTextureId = loadTextureOrFallback("/textures/menu/background.png", 0x20242CFF);
        playTextureId = loadTextureOrFallback("/textures/menu/button_play.png", 0x5FB06EFF);
        quitTextureId = loadTextureOrFallback("/textures/menu/button_quit.png", 0xB75C5CFF);

        titleTextureId = TextTextureFactory.createTexture("BLOCK GAME", 900, 160, new Font("Arial", Font.BOLD, 110), new Color(250, 252, 255, 255));
        helpTextureId = TextTextureFactory.createTexture("Press PLAY to enter the world", 700, 80, new Font("Arial", Font.PLAIN, 48), new Color(230, 236, 247, 255));
        playTextTextureId = TextTextureFactory.createTexture("PLAY", 360, 90, new Font("Arial", Font.BOLD, 60), new Color(255, 255, 255, 255));
        quitTextTextureId = TextTextureFactory.createTexture("QUIT", 360, 90, new Font("Arial", Font.BOLD, 60), new Color(255, 255, 255, 255));
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
            if (hoveredRect == PLAY_RECT) {
                action = Action.START_GAME;
            } else if (hoveredRect == QUIT_RECT) {
                action = Action.QUIT_GAME;
            }
        }

        leftMousePressedLastFrame = leftMousePressed;
        return action;
    }

    public void render(Window window) {
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glClearColor(0.08f, 0.10f, 0.14f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        UiRect hoveredRect = getHoveredRect(window, window.getWidth(), window.getHeight());

        glUseProgram(programId);
        glBindVertexArray(vaoId);
        glActiveTexture(GL_TEXTURE0);

        drawTexturedQuad(backgroundTextureId, FULLSCREEN_RECT, 1.0f, 1.0f, 1.0f);
        drawTexturedQuad(titleTextureId, TITLE_RECT, 1.0f, 1.0f, 1.0f);
        drawTexturedQuad(helpTextureId, HELP_RECT, 1.0f, 1.0f, 1.0f);

        boolean playHovered = hoveredRect == PLAY_RECT;
        boolean quitHovered = hoveredRect == QUIT_RECT;
        drawTexturedQuad(playTextureId, PLAY_RECT, playHovered ? 1.10f : 1.0f, playHovered ? 1.10f : 1.0f, playHovered ? 1.10f : 1.0f);
        drawTexturedQuad(quitTextureId, QUIT_RECT, quitHovered ? 1.10f : 1.0f, quitHovered ? 1.10f : 1.0f, quitHovered ? 1.10f : 1.0f);
        drawTexturedQuad(playTextTextureId, PLAY_TEXT_RECT, 1.0f, 1.0f, 1.0f);
        drawTexturedQuad(quitTextTextureId, QUIT_TEXT_RECT, 1.0f, 1.0f, 1.0f);

        glBindVertexArray(0);
        glUseProgram(0);
        glDisable(GL_BLEND);
        glEnable(GL_DEPTH_TEST);
    }

    public void cleanup() {
        if (backgroundTextureId != 0) glDeleteTextures(backgroundTextureId);
        if (playTextureId != 0) glDeleteTextures(playTextureId);
        if (quitTextureId != 0) glDeleteTextures(quitTextureId);
        if (titleTextureId != 0) glDeleteTextures(titleTextureId);
        if (helpTextureId != 0) glDeleteTextures(helpTextureId);
        if (playTextTextureId != 0) glDeleteTextures(playTextTextureId);
        if (quitTextTextureId != 0) glDeleteTextures(quitTextTextureId);
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

        if (PLAY_RECT.contains(normalizedX, normalizedY)) {
            return PLAY_RECT;
        }
        if (QUIT_RECT.contains(normalizedX, normalizedY)) {
            return QUIT_RECT;
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

    private int loadTextureOrFallback(String path, int rgbaFallback) {
        try (InputStream stream = getClass().getResourceAsStream(path)) {
            if (stream == null) {
                System.out.printf("Menu texture missing: %s (using fallback color)%n", path);
                return createFallbackTexture(rgbaFallback);
            }

            byte[] imageBytes = stream.readAllBytes();
            ByteBuffer imageBuffer = MemoryUtil.memAlloc(imageBytes.length);
            imageBuffer.put(imageBytes).flip();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer widthBuffer = stack.mallocInt(1);
                IntBuffer heightBuffer = stack.mallocInt(1);
                IntBuffer channelsBuffer = stack.mallocInt(1);

                ByteBuffer pixelBuffer = STBImage.stbi_load_from_memory(imageBuffer, widthBuffer, heightBuffer, channelsBuffer, 4);
                if (pixelBuffer == null) {
                    throw new IllegalStateException("Failed to decode texture: " + path + " (" + STBImage.stbi_failure_reason() + ")");
                }

                int textureId = glGenTextures();
                glBindTexture(GL_TEXTURE_2D, textureId);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
                glTexImage2D(
                        GL_TEXTURE_2D,
                        0,
                        GL_RGBA8,
                        widthBuffer.get(0),
                        heightBuffer.get(0),
                        0,
                        GL_RGBA,
                        GL_UNSIGNED_BYTE,
                        pixelBuffer
                );
                STBImage.stbi_image_free(pixelBuffer);
                return textureId;
            } finally {
                MemoryUtil.memFree(imageBuffer);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed loading texture: " + path, e);
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
