package guildquest.gmae.integration;

import guildquest.gmae.profile.PlayerProfile;
import guildquest.model.Character;
import guildquest.model.GuildQuest;
import guildquest.model.Realm;
import guildquest.model.User;

import java.util.List;
import java.util.UUID;

public class GuildQuestProfileLoader {
    private final GuildQuest system;

    public GuildQuestProfileLoader(GuildQuest system) {
        if (system == null) {
            throw new IllegalArgumentException("system cannot be null");
        }
        this.system = system;
    }

    public PlayerProfile loadProfileForUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("user cannot be null");
        }

        Realm preferredRealm = resolveUserRealm(user);
        String preferredRealmName = preferredRealm == null ? "Unknown Realm" : preferredRealm.getName();

        PlayerProfile profile = new PlayerProfile(user.getUsername(), preferredRealmName);
        profile.setLinkedUserId(user.getId());

        Character selectedCharacter = selectPrimaryCharacter(user);
        if (selectedCharacter != null) {
            profile.syncFromCharacter(selectedCharacter);
        }

        return profile;
    }

    public PlayerProfile loadGuestProfile(String guestName, Realm realm) {
        String realmName = realm == null ? "Unknown Realm" : realm.getName();
        return new PlayerProfile(guestName, realmName);
    }

    public Realm resolveUserRealm(User user) {
        if (user == null || user.getSettings() == null) {
            return fallbackRealm();
        }

        UUID realmId = user.getSettings().getCurrentRealmId();
        if (realmId == null) {
            return fallbackRealm();
        }

        Realm realm = system.getRealm(realmId);
        return realm != null ? realm : fallbackRealm();
    }

    public Character selectPrimaryCharacter(User user) {
        List<Character> characters = user.getCharacters();
        if (characters == null || characters.isEmpty()) {
            return null;
        }
        return characters.get(0);
    }

    private Realm fallbackRealm() {
        return system.getRealms().stream().findFirst().orElse(null);
    }
}