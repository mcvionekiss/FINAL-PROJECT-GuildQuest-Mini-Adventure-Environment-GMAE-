package guildquest.model;

import guildquest.enums.PermissionType;
import guildquest.enums.VisibilityType;

import java.util.*;

public class GuildQuest {
    private final WorldClock worldClock = new WorldClock();
    private final Map<UUID, User> usersById = new HashMap<>();
    private final Map<String, User> usersByName = new HashMap<>();
    private final Map<UUID, Realm> realmsById = new HashMap<>();
    private final Map<UUID, Campaign> campaignsById = new HashMap<>();
    private final Map<UUID, UUID> campaignOwnerByCampaignId = new HashMap<>(); // campaignId -> ownerUserId
    private final Map<UUID, QuestEvent> eventsById = new HashMap<>();
    private final Map<UUID, UUID> eventOwnerByEventId = new HashMap<>();

    public WorldClock getWorldClock() { return worldClock; }

    public User registerUser(String username) {
        if (usersByName.containsKey(username)) throw new IllegalArgumentException("Username already exists");
        User u = new User(username);
        usersById.put(u.getId(), u);
        usersByName.put(username, u);
        return u;
    }

    public Optional<User> findUserByName(String username) {
        return Optional.ofNullable(usersByName.get(username));
    }

    public Realm createRealm(String name, String description, int offsetMinutes) {
        Realm r = new Realm(name, description, offsetMinutes);
        realmsById.put(r.getId(), r);
        return r;
    }

    public Collection<Realm> getRealms() { return Collections.unmodifiableCollection(realmsById.values()); }
    public Realm getRealm(UUID id) { return realmsById.get(id); }

    public Campaign createCampaign(User owner, String name, VisibilityType visibility) {
        Campaign c = owner.addCampaign(name, visibility);
        campaignsById.put(c.getId(), c);
        campaignOwnerByCampaignId.put(c.getId(), owner.getId());
        return c;
    }

    public QuestEvent createQuestEvent(User owner, UUID campaignId, QuestEvent e) {
        Campaign c = owner.findOwnedCampaign(campaignId);
        c.addQuestEvent(e);

        eventsById.put(e.getId(), e);
        eventOwnerByEventId.put(e.getId(), owner.getId());
        return e;
    }

    public List<Campaign> listVisibleCampaignsFor(User viewer) {
        List<Campaign> visible = new ArrayList<>();

        // public campaigns
        for (Campaign c : campaignsById.values()) {
            if (c.getVisibility() == VisibilityType.PUBLIC && !c.isArchived()) visible.add(c);
        }

        // owned campaigns (private or public)
        for (Campaign c : viewer.getCampaigns()) {
            if (!c.isArchived() && !visible.contains(c)) visible.add(c);
        }

        // shared private campaigns (proxies)
        for (CampaignShare share : viewer.getSharedCampaigns()) {
            Campaign shared = campaignsById.get(share.getSharedCampaignId());
            if (shared != null && !shared.isArchived() && !visible.contains(shared)) visible.add(shared);
        }

        visible.sort(Comparator.comparing(Campaign::getName));
        return visible;
    }

    public void shareCampaign(User owner, UUID campaignId, User recipient, PermissionType permission) {
        Campaign c = owner.findOwnedCampaign(campaignId);
        if (c.getVisibility() == VisibilityType.PUBLIC) return; // no need to share public
        CampaignShare share = new CampaignShare(campaignId, owner.getId(), permission);
        recipient.receiveCampaignShare(share);
    }

    public void shareEvent(User owner, UUID eventId, User recipient) {
        UUID ownerId = eventOwnerByEventId.get(eventId);
        if (ownerId == null || !ownerId.equals(owner.getId())) throw new IllegalArgumentException("Not event owner");
        recipient.receiveEventShare(new EventShare(eventId, owner.getId()));
    }

    public Optional<Campaign> getCampaign(UUID id) { return Optional.ofNullable(campaignsById.get(id)); }
    public Optional<QuestEvent> getEvent(UUID id) { return Optional.ofNullable(eventsById.get(id)); }

    public boolean canEditCampaign(User user, UUID campaignId) {
        UUID owner = campaignOwnerByCampaignId.get(campaignId);
        if (owner != null && owner.equals(user.getId())) return true;

        for (CampaignShare share : user.getSharedCampaigns()) {
            if (share.getSharedCampaignId().equals(campaignId)
                    && share.getPermission() == PermissionType.COLLABORATIVE) return true;
        }
        return false;
    }
}
