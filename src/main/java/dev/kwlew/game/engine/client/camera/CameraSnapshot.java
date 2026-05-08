package dev.kwlew.game.engine.client.camera;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public record CameraSnapshot(Vector3f position, Vector3f forward, Matrix4f viewMatrix) {
}
