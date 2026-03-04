package guildquest.model;

import java.util.Objects;

public final class WorldTime implements Comparable<WorldTime> {
    private final long totalMinutes;

    public WorldTime(long totalMinutes) {
        if (totalMinutes < 0) throw new IllegalArgumentException("totalMinutes must be >= 0");
        this.totalMinutes = totalMinutes;
    }

    public static WorldTime of(int days, int hours, int minutes) {
        if (days < 0 || hours < 0 || minutes < 0) throw new IllegalArgumentException("Negative time not allowed");
        return new WorldTime(days * 1440L + hours * 60L + minutes);
    }

    public long totalMinutes() { return totalMinutes; }
    public int days() { return (int)(totalMinutes / 1440L); }
    public int hours() { return (int)((totalMinutes % 1440L) / 60L); }
    public int minutes() { return (int)(totalMinutes % 60L); }

    public WorldTime plusMinutes(long delta) {
        long next = totalMinutes + delta;
        if (next < 0) throw new IllegalArgumentException("Resulting time < 0");
        return new WorldTime(next);
    }

    @Override
    public int compareTo(WorldTime o) {
        return Long.compare(this.totalMinutes, o.totalMinutes);
    }

    @Override
    public String toString() {
        return "Day " + days() + " " +
                String.format("%02d:%02d", hours(), minutes());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldTime)) return false;
        WorldTime worldTime = (WorldTime) o;
        return totalMinutes == worldTime.totalMinutes;
    }

    @Override
    public int hashCode() {
        return Objects.hash(totalMinutes);
    }
}
