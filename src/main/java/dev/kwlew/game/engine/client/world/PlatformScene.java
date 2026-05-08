package dev.kwlew.game.engine.client.world;

import dev.kwlew.game.engine.world.BlockWorld;
import dev.kwlew.game.engine.world.chunk.Chunk;
import dev.kwlew.game.engine.world.chunk.ChunkPos;
import dev.kwlew.game.engine.world.block.BlockId;

import java.util.HashSet;
import java.util.Set;

public final class PlatformScene {

    private static final int[] CHUNK_LAYERS = {-1, 0};
    private final BlockWorld world = new BlockWorld();
    private final Set<Long> generatedColumns = new HashSet<>();

    public PlatformScene() {
        ensureChunkColumnGenerated(0, 0);
    }

    public void forEachChunkInRange(int centerChunkX, int centerChunkZ, int chunkRadius, ChunkConsumer consumer) {
        int minChunkX = centerChunkX - chunkRadius;
        int maxChunkX = centerChunkX + chunkRadius;
        int minChunkZ = centerChunkZ - chunkRadius;
        int maxChunkZ = centerChunkZ + chunkRadius;

        for (int chunkX = minChunkX; chunkX <= maxChunkX; chunkX++) {
            for (int chunkZ = minChunkZ; chunkZ <= maxChunkZ; chunkZ++) {
                ensureChunkColumnGenerated(chunkX, chunkZ);
                for (int chunkY : CHUNK_LAYERS) {
                    Chunk chunk = world.getChunk(chunkX, chunkY, chunkZ);
                    if (chunk != null) {
                        consumer.accept(new ChunkPos(chunkX, chunkY, chunkZ), chunk);
                    }
                }
            }
        }
    }

    public BlockWorld world() {
        return world;
    }

    private void ensureChunkColumnGenerated(int chunkX, int chunkZ) {
        long key = chunkColumnKey(chunkX, chunkZ);
        if (!generatedColumns.add(key)) {
            return;
        }

        int baseX = chunkX * Chunk.SIZE;
        int baseZ = chunkZ * Chunk.SIZE;
        for (int localX = 0; localX < Chunk.SIZE; localX++) {
            for (int localZ = 0; localZ < Chunk.SIZE; localZ++) {
                int worldX = baseX + localX;
                int worldZ = baseZ + localZ;
                world.setBlock(worldX, 0, worldZ, BlockId.GRASS.id());
                world.setBlock(worldX, -1, worldZ, BlockId.DIRT.id());
            }
        }
    }

    private static long chunkColumnKey(int chunkX, int chunkZ) {
        return (((long) chunkX) << 32) ^ (chunkZ & 0xFFFFFFFFL);
    }

    @FunctionalInterface
    public interface ChunkConsumer {
        void accept(ChunkPos chunkPos, Chunk chunk);
    }
}
