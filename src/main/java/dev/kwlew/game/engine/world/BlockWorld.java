package dev.kwlew.game.engine.world;

import dev.kwlew.game.engine.world.block.BlockId;
import dev.kwlew.game.engine.world.chunk.Chunk;
import dev.kwlew.game.engine.world.chunk.ChunkPos;

import java.util.HashMap;
import java.util.Map;

public class BlockWorld {

    private final Map<ChunkPos, Chunk> chunks = new HashMap<>();

    public short getBlock(int wx, int wy, int wz) {
        ChunkPos chunkPos = toChunkPos(wx, wy, wz);
        Chunk chunk = chunks.get(chunkPos);

        if (chunk == null) return BlockId.AIR.id();

        int lx = Math.floorMod(wx, Chunk.SIZE);
        int ly = Math.floorMod(wy, Chunk.SIZE);
        int lz = Math.floorMod(wz, Chunk.SIZE);
        return chunk.get(lx, ly, lz);
    }

    public void setBlock(int wx, int wy, int wz, short blockId) {
        ChunkPos chunkPos = toChunkPos(wx, wy, wz);
        Chunk chunk = chunks.computeIfAbsent(chunkPos, k -> new Chunk());

        int lx = Math.floorMod(wx, Chunk.SIZE);
        int ly = Math.floorMod(wy, Chunk.SIZE);
        int lz = Math.floorMod(wz, Chunk.SIZE);
        chunk.set(lx, ly, lz, blockId);
    }

    public void forEachChunk(ChunkConsumer consumer) {
        for (Map.Entry<ChunkPos, Chunk> entry : chunks.entrySet()) {
            consumer.accept(entry.getKey(), entry.getValue());
        }
    }

    public Chunk getChunk(int chunkX, int chunkY, int chunkZ) {
        return chunks.get(new ChunkPos(chunkX, chunkY, chunkZ));
    }

    private ChunkPos toChunkPos(int wx, int wy, int wz) {
        return new ChunkPos(
                Math.floorDiv(wx, Chunk.SIZE),
                Math.floorDiv(wy, Chunk.SIZE),
                Math.floorDiv(wz, Chunk.SIZE)
        );
    }

    @FunctionalInterface
    public interface ChunkConsumer {
        void accept(ChunkPos chunkPos, Chunk chunk);
    }
}
