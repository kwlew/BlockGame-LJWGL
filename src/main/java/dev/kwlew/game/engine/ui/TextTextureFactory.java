package dev.kwlew.game.engine.ui;

import org.lwjgl.BufferUtils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL46.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL46.GL_LINEAR;
import static org.lwjgl.opengl.GL46.GL_RGBA;
import static org.lwjgl.opengl.GL46.GL_RGBA8;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_MAG_FILTER;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_MIN_FILTER;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL46.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL46.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL46.glBindTexture;
import static org.lwjgl.opengl.GL46.glGenTextures;
import static org.lwjgl.opengl.GL46.glTexImage2D;
import static org.lwjgl.opengl.GL46.glTexParameteri;

final class TextTextureFactory {

    private TextTextureFactory() {
    }

    static int createTexture(String text, int width, int height, Font font, Color color) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Text texture dimensions must be positive");
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setComposite(AlphaComposite.Clear);
        graphics.fillRect(0, 0, width, height);
        graphics.setComposite(AlphaComposite.SrcOver);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setFont(font);
        graphics.setColor(color);

        int textWidth = graphics.getFontMetrics().stringWidth(text);
        int textHeight = graphics.getFontMetrics().getAscent();
        int x = Math.max(0, (width - textWidth) / 2);
        int y = Math.max(textHeight, (height + textHeight) / 2 - 4);
        graphics.drawString(text, x, y);
        graphics.dispose();

        ByteBuffer pixels = BufferUtils.createByteBuffer(width * height * 4);
        int[] argbPixels = new int[width * height];
        image.getRGB(0, 0, width, height, argbPixels, 0, width);
        for (int pixel : argbPixels) {
            pixels.put((byte) ((pixel >> 16) & 0xFF));
            pixels.put((byte) ((pixel >> 8) & 0xFF));
            pixels.put((byte) (pixel & 0xFF));
            pixels.put((byte) ((pixel >> 24) & 0xFF));
        }
        pixels.flip();

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);
        return textureId;
    }
}
