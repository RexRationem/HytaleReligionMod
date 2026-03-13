package religion.api;

public record BlockPos(String worldId, int x, int y, int z) {

    public double distanceTo(BlockPos other) {
        if (!worldId.equals(other.worldId())) {
            return Double.MAX_VALUE;
        }
        long dx = (long) x - other.x();
        long dy = (long) y - other.y();
        long dz = (long) z - other.z();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public int chunkX() {
        return x >> 4;
    }

    public int chunkZ() {
        return z >> 4;
    }

    public double chunkCenterDistanceTo(BlockPos other) {
        if (!worldId.equals(other.worldId())) {
            return Double.MAX_VALUE;
        }
        double cx1 = chunkX() + 0.5;
        double cz1 = chunkZ() + 0.5;
        double cx2 = other.chunkX() + 0.5;
        double cz2 = other.chunkZ() + 0.5;
        double dx = cx1 - cx2;
        double dz = cz1 - cz2;
        return Math.sqrt(dx * dx + dz * dz);
    }
}