package guildquest.model;

import java.util.*;

public class User {
    private final UUID id;
    private final String username;

    private final List<Campaign> campaigns = new ArrayList<>();
    private final List<CampaignShare> sharedCampaigns = new ArrayList<>();
    private final List<EventShare> sharedEvents = new ArrayList<>();
    private final List<Character> characters = new ArrayList<>();

    private final UserSettings settings = new UserSettings();

    public User(String username) {
        this.id = UUID.randomUUID();
        this.username = Objects.requireNonNull(username);
    }

    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public UserSettings getSettings() { return settings; }

    public List<Campaign> getCampaigns() { return Collections.unmodifiableList(campaigns); }
    public List<CampaignShare> getSharedCampaigns() { return Collections.unmodifiableList(sharedCampaigns); }
    public List<EventShare> getSharedEvents() { return Collections.unmodifiableList(sharedEvents); }
    public List<Character> getCharacters() { return Collections.unmodifiableList(characters); }

    public Campaign addCampaign(String name, guildquest.enums.VisibilityType visibility) {
        Campaign c = new Campaign(name, visibility);
        campaigns.add(c);
        return c;
    }

    public boolean removeCampaign(UUID campaignId) {
        return campaigns.removeIf(c -> c.getId().equals(campaignId));
    }

    public Campaign findOwnedCampaign(UUID campaignId) {
        for (Campaign c : campaigns) if (c.getId().equals(campaignId)) return c;
        throw new NoSuchElementException("Campaign not found");
    }

    public void archiveCampaign(UUID campaignId) {
        Campaign c = findOwnedCampaign(campaignId);
        c.setArchived(true);
    }

    public Character addCharacter(String name, String clazz, int level, int inventoryCapacity) {
        Character ch = new Character(name, clazz, level, inventoryCapacity);
        characters.add(ch);
        return ch;
    }

    public Character findCharacter(UUID characterId) {
        for (Character c : characters) if (c.getId().equals(characterId)) return c;
        throw new NoSuchElementException("Character not found");
    }

    public void receiveCampaignShare(CampaignShare share) { sharedCampaigns.add(share); }
    public void receiveEventShare(EventShare share) { sharedEvents.add(share); }

    @Override
    public String toString() {
        return username + " (" + id + ")";
    }
}
