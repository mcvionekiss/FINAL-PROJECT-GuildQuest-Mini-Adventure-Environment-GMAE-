package guildquest.gmae.security;

import guildquest.enums.PermissionType;
import guildquest.enums.VisibilityType;
import guildquest.model.Campaign;
import guildquest.model.QuestEvent;

import java.util.List;
import java.util.UUID;

public class CampaignAccessProxy {

    private final Campaign delegate;
    private final UUID requestingUserId;
    private final PermissionType permission;

    public CampaignAccessProxy(Campaign delegate, UUID userId, PermissionType permission) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate campaign cannot be null");
        }

        this.delegate = delegate;
        this.requestingUserId = userId;
        this.permission = permission;
    }

    public String getName() {
        return delegate.getName();
    }

    public List<QuestEvent> getEvents() {
        return delegate.getEvents();
    }

    public QuestEvent addQuestEvent(QuestEvent e) {
        assertCanWrite();
        return delegate.addQuestEvent(e);
    }

    public boolean removeQuestEvent(UUID id) {
        assertCanWrite();
        return delegate.removeQuestEvent(id);
    }

    public void setName(String name) {
        assertCanWrite();
        delegate.setName(name);
    }

    public void setVisibility(VisibilityType v) {
        assertCanWrite();
        delegate.setVisibility(v);
    }

    public UUID getRequestingUserId() {
        return requestingUserId;
    }

    public PermissionType getPermission() {
        return permission;
    }

    public Campaign getDelegate() {
        return delegate;
    }

    private void assertCanWrite() {
        if (permission != PermissionType.COLLABORATIVE) {
            throw new SecurityException("User does not have permission to modify this campaign.");
        }
    }
}