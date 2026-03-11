package guildquest.gmae.profile;

import guildquest.gmae.enums.AchievementType;
import guildquest.model.WorldTime;

import java.util.Objects;
import java.util.UUID;

public class Achievement {
    private final UUID id;
    private final AchievementType type;
    private final String description;
    private final WorldTime awardedAt;

    public Achievement(AchievementType type, String description, WorldTime awardedAt) {
        this.id = UUID.randomUUID();
        this.type = Objects.requireNonNull(type);
        this.description = Objects.requireNonNull(description);
        this.awardedAt = Objects.requireNonNull(awardedAt);
    }

    public UUID getId() {
        return id;
    }

    public AchievementType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public WorldTime getAwardedAt() {
        return awardedAt;
    }

    @Override
    public String toString() {
        return type + " - " + description + " @ " + awardedAt;
    }
}