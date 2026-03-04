package guildquest.model;

import java.util.Objects;
import java.util.UUID;

public class EventShare {
    private final UUID sharedEventId;
    private final UUID ownerUserId;

    public EventShare(UUID sharedEventId, UUID ownerUserId) {
        this.sharedEventId = Objects.requireNonNull(sharedEventId);
        this.ownerUserId = Objects.requireNonNull(ownerUserId);
    }

    public UUID getSharedEventId() { return sharedEventId; }
    public UUID getOwnerUserId() { return ownerUserId; }
}
