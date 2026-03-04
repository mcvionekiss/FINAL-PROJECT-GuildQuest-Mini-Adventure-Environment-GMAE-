package guildquest.model;

public class WorldClock {
    private WorldTime now = new WorldTime(0);

    public WorldTime now() { return now; }

    public void advanceMinutes(long minutes) {
        now = now.plusMinutes(minutes);
    }

    public void set(WorldTime newTime) {
        if (newTime == null) throw new IllegalArgumentException("newTime null");
        now = newTime;
    }
}
