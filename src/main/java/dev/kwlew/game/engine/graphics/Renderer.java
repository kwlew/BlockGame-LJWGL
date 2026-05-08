package dev.kwlew.game.engine.graphics;

import org.joml.Matrix4f;

import static org.lwjgl.opengl.GL46.*;

public class Renderer {

    private Shader shader;
    private CubeMesh cube;

    public void init() {
        glEnable(GL_DEPTH_TEST);

        shader = new Shader("/shaders/basic.vert", "/shaders/basic.frag");

        cube = new CubeMesh();
        cube.init();
    }

    public void render(Camera camera, float time, int width, int height) {
        glViewport(0, 0, width, height);
        glClearColor(0.5f, 0.7f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        shader.bind();
        cube.bind();

        Matrix4f model = new Matrix4f().rotate(time, 0.5f, 1.0f, 0.0f);
        Matrix4f outlineModel = new Matrix4f(model).scale(1.03f);

        shader.setMatrix4("projection", camera.getProjectionMatrix());
        shader.setMatrix4("view", camera.getViewMatrix());

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        shader.setVec3("color", 0.05f, 0.05f, 0.05f);
        shader.setMatrix4("model", outlineModel);
        cube.draw();

        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public void cleanup() {
        cube.cleanup();
        shader.cleanup();
    }
}