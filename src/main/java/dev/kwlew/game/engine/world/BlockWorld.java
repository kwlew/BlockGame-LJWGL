package dev.kwlew.game.engine.world;

import org.joml.Vector3f;

public class BlockWorld {

    private static final float EPSILON = 0.0001f;
    private static final int FLOOR_Y = 0;
    private static final int SIZE = 32;

    private final int minX = -SIZE / 2;
    private final int minZ = -SIZE / 2;
    private final int maxXExclusive = minX + SIZE;
    private final int maxZExclusive = minZ + SIZE;

    public boolean isSolid(int x, int y, int z) {
        return y == FLOOR_Y
                && x >= minX && x < maxXExclusive
                && z >= minZ && z < maxZExclusive;
    }

    public boolean collidesWithPlayerAabb(float centerX, float feetY, float centerZ, float radius, float height) {
        float minXf = centerX - radius;
        float maxXf = centerX + radius;
        float minYf = feetY;
        float maxYf = feetY + height;
        float minZf = centerZ - radius;
        float maxZf = centerZ + radius;

        int minXi = (int) Math.floor(minXf);
        int maxXi = (int) Math.floor(maxXf - EPSILON);
        int minYi = (int) Math.floor(minYf);
        int maxYi = (int) Math.floor(maxYf - EPSILON);
        int minZi = (int) Math.floor(minZf);
        int maxZi = (int) Math.floor(maxZf - EPSILON);

        for (int x = minXi; x <= maxXi; x++) {
            for (int y = minYi; y <= maxYi; y++) {
                for (int z = minZi; z <= maxZi; z++) {
                    if (isSolid(x, y, z)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public float findGroundHeight(float centerX, float centerZ, float radius, float previousFeetY) {
        int minXi = (int) Math.floor(centerX - radius);
        int maxXi = (int) Math.floor(centerX + radius - EPSILON);
        int minZi = (int) Math.floor(centerZ - radius);
        int maxZi = (int) Math.floor(centerZ + radius - EPSILON);

        float highestGround = Float.NEGATIVE_INFINITY;
        for (int x = minXi; x <= maxXi; x++) {
            for (int z = minZi; z <= maxZi; z++) {
                if (!isSolid(x, FLOOR_Y, z)) {
                    continue;
                }

                float topY = FLOOR_Y + 1.0f;
                if (topY <= previousFeetY + 0.25f) {
                    highestGround = topY;
                }
            }
        }

        return highestGround == Float.NEGATIVE_INFINITY ? Float.NaN : highestGround;
    }

    public BlockHit raycast(Vector3f origin, Vector3f direction, float maxDistance) {
        if (direction.lengthSquared() == 0.0f) {
            return null;
        }

        Vector3f dir = new Vector3f(direction).normalize();
        float stepSize = 0.05f;
        Vector3f sample = new Vector3f(origin);

        int lastX = Integer.MIN_VALUE;
        int lastY = Integer.MIN_VALUE;
        int lastZ = Integer.MIN_VALUE;

        for (float traveled = 0.0f; traveled <= maxDistance; traveled += stepSize) {
            int bx = (int) Math.floor(sample.x);
            int by = (int) Math.floor(sample.y);
            int bz = (int) Math.floor(sample.z);

            if (bx != lastX || by != lastY || bz != lastZ) {
                if (isSolid(bx, by, bz)) {
                    return new BlockHit(bx, by, bz);
                }
                lastX = bx;
                lastY = by;
                lastZ = bz;
            }

            sample.fma(stepSize, dir);
        }

        return null;
    }

    public int getMinX() {
        return minX;
    }

    public int getMaxXExclusive() {
        return maxXExclusive;
    }

    public int getMinZ() {
        return minZ;
    }

    public int getMaxZExclusive() {
        return maxZExclusive;
    }
}