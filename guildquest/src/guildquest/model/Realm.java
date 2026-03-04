package guildquest.model;

import java.util.Objects;
import java.util.UUID;

public class Realm {
    private final UUID id;
    private String name;
    private String description; // optional
    private int offsetMinutes; // fixed offset from world time

    public Realm(String name, String description, int offsetMinutes) {
        this.id = UUID.randomUUID();
        this.name = Objects.requireNonNull(name);
        this.description = description;
        this.offsetMinutes = offsetMinutes;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getOffsetMinutes() { return offsetMinutes; }

    public void setName(String name) { this.name = Objects.requireNonNull(name); }
    public void setDescription(String description) { this.description = description; }
    public void setOffsetMinutes(int offsetMinutes) { this.offsetMinutes = offsetMinutes; }

    public WorldTime toLocalTime(WorldTime worldTime) {
        return worldTime.plusMinutes(offsetMinutes);
    }

    @Override
    public String toString() {
        return name + " (offset " + offsetMinutes + " min)";
    }
}
