package dev.kwlew.game.engine.client.render;

import dev.kwlew.game.engine.client.camera.CameraSnapshot;
import dev.kwlew.game.engine.client.world.PlatformScene;
import dev.kwlew.game.engine.world.block.BlockId;
import dev.kwlew.game.engine.world.chunk.Chunk;
import dev.kwlew.game.window.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL46.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL46.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL46.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL46.glClear;
import static org.lwjgl.opengl.GL46.glClearColor;
import static org.lwjgl.opengl.GL46.glEnable;

public final class BlockPlatformRenderer {

    private static final int RENDER_DISTANCE_CHUNKS = 6;
    private static final int ALWAYS_RENDER_NEAR_CHUNKS = 1;
    private static final float FORWARD_CULL_DOT_THRESHOLD = -0.15f;
    private static final int FLOATS_PER_INSTANCE = 19;

    private final BasicShaderProgram shaderProgram = new BasicShaderProgram();
    private final CubeMesh cubeMesh = new CubeMesh();
    private final BlockTexture blockTexture = new BlockTexture();
    private final Matrix4f projection = new Matrix4f();
    private final Matrix4f view = new Matrix4f();
    private final Vector3f cameraPosition = new Vector3f();
    private final Vector3f cameraForward = new Vector3f();
    private final Vector3f toChunk = new Vector3f();
    private FloatBuffer instanceBuffer = BufferUtils.createFloatBuffer(1 << 15);
    private int instanceCount;

    public void init() {
        shaderProgram.init();
        cubeMesh.init();
        blockTexture.init();
    }

    public void render(Window window, CameraSnapshot camera, PlatformScene scene) {
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.50f, 0.75f, 1.0f, 1.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        float aspectRatio = window.getHeight() <= 0 ? 1.0f : (float) window.getWidth() / (float) window.getHeight();
        projection.identity().perspective((float) Math.toRadians(65.0), aspectRatio, 0.1f, 200.0f);
        view.set(camera.viewMatrix());

        shaderProgram.use();
        shaderProgram.setProjection(projection);
        shaderProgram.setView(view);
        blockTexture.bind();
        cubeMesh.bind();
        instanceBuffer.clear();
        instanceCount = 0;

        cameraPosition.set(camera.position());
        cameraForward.set(camera.forward());
        cameraForward.y = 0.0f;
        if (cameraForward.lengthSquared() > 0.0001f) {
            cameraForward.normalize();
        }

        int cameraChunkX = Math.floorDiv((int) Math.floor(cameraPosition.x), Chunk.SIZE);
        int cameraChunkZ = Math.floorDiv((int) Math.floor(cameraPosition.z), Chunk.SIZE);

        scene.forEachChunkInRange(cameraChunkX, cameraChunkZ, RENDER_DISTANCE_CHUNKS, (chunkPos, chunk) -> {
            int chunkDistanceX = Math.abs(chunkPos.x() - cameraChunkX);
            int chunkDistanceZ = Math.abs(chunkPos.z() - cameraChunkZ);
            int chunkDistance = Math.max(chunkDistanceX, chunkDistanceZ);
            if (chunkDistance > ALWAYS_RENDER_NEAR_CHUNKS) {
                float centerX = chunkPos.x() * Chunk.SIZE + (Chunk.SIZE * 0.5f);
                float centerZ = chunkPos.z() * Chunk.SIZE + (Chunk.SIZE * 0.5f);
                toChunk.set(centerX - cameraPosition.x, 0.0f, centerZ - cameraPosition.z);
                if (toChunk.lengthSquared() > 0.0001f) {
                    toChunk.normalize();
                    if (cameraForward.dot(toChunk) <= FORWARD_CULL_DOT_THRESHOLD) {
                        return;
                    }
                }
            }

            chunk.forEachNonAir((lx, ly, lz, blockId) -> {
                int wx = chunkPos.x() * Chunk.SIZE + lx;
                int wy = chunkPos.y() * Chunk.SIZE + ly;
                int wz = chunkPos.z() * Chunk.SIZE + lz;
                appendInstance(wx, wy, wz, blockId);
            });
        });

        instanceBuffer.flip();
        cubeMesh.uploadInstances(instanceBuffer);
        cubeMesh.drawInstanced(instanceCount);

        cubeMesh.unbind();
        shaderProgram.stop();
    }

    public void cleanup() {
        blockTexture.cleanup();
        cubeMesh.cleanup();
        shaderProgram.cleanup();
    }

    private void appendInstance(float x, float y, float z, short blockId) {
        ensureInstanceCapacity(FLOATS_PER_INSTANCE);

        float r;
        float g;
        float b;
        if (blockId == BlockId.GRASS.id()) {
            r = 0.35f;
            g = 0.72f;
            b = 0.34f;
        } else if (blockId == BlockId.DIRT.id()) {
            r = 0.45f;
            g = 0.30f;
            b = 0.18f;
        } else {
            r = 0.6f;
            g = 0.6f;
            b = 0.6f;
        }

        instanceBuffer.put(1.0f).put(0.0f).put(0.0f).put(0.0f);
        instanceBuffer.put(0.0f).put(1.0f).put(0.0f).put(0.0f);
        instanceBuffer.put(0.0f).put(0.0f).put(1.0f).put(0.0f);
        instanceBuffer.put(x).put(y).put(z).put(1.0f);
        instanceBuffer.put(r).put(g).put(b);
        instanceCount++;
    }

    private void ensureInstanceCapacity(int floatsToAdd) {
        if (instanceBuffer.remaining() >= floatsToAdd) {
            return;
        }
        int required = instanceBuffer.position() + floatsToAdd;
        int newCapacity = Math.max(instanceBuffer.capacity() * 2, required);
        FloatBuffer newBuffer = BufferUtils.createFloatBuffer(newCapacity);
        instanceBuffer.flip();
        newBuffer.put(instanceBuffer);
        instanceBuffer = newBuffer;
    }
}
