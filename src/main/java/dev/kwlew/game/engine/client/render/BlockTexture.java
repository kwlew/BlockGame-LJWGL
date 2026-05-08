package dev.kwlew.game.engine.client.render;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.GL_NEAREST;
import static org.lwjgl.opengl.GL46.GL_REPEAT;
import static org.lwjgl.opengl.GL46.GL_RGBA;
import static org.lwjgl.opengl.GL46.GL_RGBA8;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL46.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL46.glActiveTexture;
import static org.lwjgl.opengl.GL46.glBindTexture;
import static org.lwjgl.opengl.GL46.glDeleteTextures;
import static org.lwjgl.opengl.GL46.glGenTextures;
import static org.lwjgl.opengl.GL46.glTexImage2D;
import static org.lwjgl.opengl.GL46.glTexParameteri;
import static org.lwjgl.opengl.GL46.GL_TEXTURE0;

final class BlockTexture {

    private int textureId;

    void init() {
        int size = 16;
        ByteBuffer pixels = BufferUtils.createByteBuffer(size * size * 4);
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                boolean dark = ((x / 4) + (y / 4)) % 2 == 0;
                int c = dark ? 180 : 230;
                pixels.put((byte) c);
                pixels.put((byte) c);
                pixels.put((byte) c);
                pixels.put((byte) 255);
            }
        }
        pixels.flip();

        textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, size, size, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
    }

    void bind() {
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, textureId);
    }

    void cleanup() {
        if (textureId != 0) {
            glDeleteTextures(textureId);
        }
    }
}
