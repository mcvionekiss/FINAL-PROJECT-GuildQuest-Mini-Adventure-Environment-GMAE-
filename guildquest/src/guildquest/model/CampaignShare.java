package guildquest.model;

import guildquest.enums.PermissionType;

import java.util.Objects;
import java.util.UUID;

public class CampaignShare {
    private final UUID sharedCampaignId;
    private final UUID ownerUserId;
    private final PermissionType permission;

    public CampaignShare(UUID sharedCampaignId, UUID ownerUserId, PermissionType permission) {
        this.sharedCampaignId = Objects.requireNonNull(sharedCampaignId);
        this.ownerUserId = Objects.requireNonNull(ownerUserId);
        this.permission = Objects.requireNonNull(permission);
    }

    public UUID getSharedCampaignId() { return sharedCampaignId; }
    public UUID getOwnerUserId() { return ownerUserId; }
    public PermissionType getPermission() { return permission; }
}
