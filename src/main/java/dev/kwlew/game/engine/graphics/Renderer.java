package dev.kwlew.game.engine.graphics;

import dev.kwlew.game.engine.world.BlockHit;
import org.joml.Matrix4f;
import dev.kwlew.game.engine.world.BlockWorld;

import static org.lwjgl.opengl.GL46.*;

public class Renderer {

    private Shader shader;
    private CubeMesh cube;
    private Crosshair crosshair;

    public void init() {
        glEnable(GL_DEPTH_TEST);

        shader = new Shader("/shaders/basic.vert", "/shaders/basic.frag");

        cube = new CubeMesh();
        cube.init();

        crosshair = new Crosshair();
        crosshair.init();
    }

    public void render(Camera camera, BlockWorld world, BlockHit aimedBlock, int width, int height) {
        glViewport(0, 0, width, height);
        glClearColor(0.5f, 0.7f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.bind();
        cube.bind();

        shader.setMatrix4("projection", camera.getProjectionMatrix());
        shader.setMatrix4("view", camera.getViewMatrix());

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        shader.setVec3("color", 0.2f, 0.8f, 0.3f);
        drawWorld(world);

        if (aimedBlock != null) {
            shader.setVec3("color", 0.05f, 0.05f, 0.05f);
            drawSingleBlock(aimedBlock.x(), aimedBlock.y(), aimedBlock.z(), true);
        }

        crosshair.render(shader);
    }

    private void drawWorld(BlockWorld world) {
        for (int x = world.getMinX(); x < world.getMaxXExclusive(); x++) {
            for (int z = world.getMinZ(); z < world.getMaxZExclusive(); z++) {
                if (!world.isSolid(x, 0, z)) {
                    continue;
                }

                drawSingleBlock(x, 0, z, false);
            }
        }
    }

    private void drawSingleBlock(int x, int y, int z, boolean outline) {
        Matrix4f model = new Matrix4f().translation(x + 0.5f, y + 0.5f, z + 0.5f);
        if (outline) {
            model.scale(1.01f);
        }
        shader.setMatrix4("model", model);
        if (outline) {
            cube.drawOutline();
            cube.bind();
        } else {
            cube.draw();
        }
    }

    public void cleanup() {
        cube.cleanup();
        shader.cleanup();
        crosshair.cleanup();
    }
}
