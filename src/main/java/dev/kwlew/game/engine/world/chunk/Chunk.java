package dev.kwlew.game.engine.world.chunk;

public class Chunk {

    public static final int SIZE = 16;
    private final short[] blocks = new short[SIZE * SIZE * SIZE];

    public short get(int lx, int ly, int lz) {
        return blocks[index(lx, ly, lz)];
    }

    public void set(int lx, int ly, int lz, short blockId) {
        blocks[index(lx, ly, lz)] = blockId;
    }

    public void forEachNonAir(BlockConsumer consumer) {
        for (int ly = 0; ly < SIZE; ly++) {
            for (int lz = 0; lz < SIZE; lz++) {
                for (int lx = 0; lx < SIZE; lx++) {
                    short blockId = blocks[lx + (lz * SIZE) + (ly * SIZE * SIZE)];
                    if (blockId != 0) {
                        consumer.accept(lx, ly, lz, blockId);
                    }
                }
            }
        }
    }

    private int index(int x, int y, int z) {
        validateLocalCoordinate(x, "x");
        validateLocalCoordinate(y, "y");
        validateLocalCoordinate(z, "z");
        return x + (z * SIZE) + (y * SIZE * SIZE);
    }

    private static void validateLocalCoordinate(int coordinate, String axis) {
        if (coordinate < 0 || coordinate >= SIZE) {
            throw new IllegalArgumentException(axis + " must be in [0, " + (SIZE - 1) + "], got " + coordinate);
        }
    }

    @FunctionalInterface
    public interface BlockConsumer {
        void accept(int lx, int ly, int lz, short blockId);
    }

}
