package guildquest.gmae.profile;

import guildquest.model.Character;
import guildquest.model.Inventory;
import guildquest.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerProfile {
    private UUID linkedUserId;
    private UUID linkedCharacterId;

    private String playerName;
    private String preferredRealmName;
    private String linkedCharacterName;
    private String linkedCharacterClass;
    private int linkedCharacterLevel;

    private final List<Item> inventorySnapshot;
    private final List<String> achievements;

    private int totalWins;
    private int totalLosses;
    private int totalDraws;

    public PlayerProfile(String playerName, String preferredRealmName) {
        this.playerName = playerName;
        this.preferredRealmName = preferredRealmName;
        this.inventorySnapshot = new ArrayList<>();
        this.achievements = new ArrayList<>();
        this.totalWins = 0;
        this.totalLosses = 0;
        this.totalDraws = 0;
    }

    public void syncFromCharacter(Character character) {
        inventorySnapshot.clear();

        if (character == null) {
            linkedCharacterId = null;
            linkedCharacterName = null;
            linkedCharacterClass = null;
            linkedCharacterLevel = 0;
            return;
        }

        linkedCharacterId = character.getId();
        linkedCharacterName = character.getName();
        linkedCharacterClass = character.getClazz();
        linkedCharacterLevel = character.getLevel();

        Inventory inventory = character.getInventory();
        if (inventory != null) {
            inventorySnapshot.addAll(inventory.getItems());
        }
    }

    public void addAchievement(String achievement) {
        if (achievement != null && !achievement.isBlank()) {
            achievements.add(achievement);
        }
    }

    public void recordWin() {
        totalWins++;
    }

    public void recordLoss() {
        totalLosses++;
    }

    public void recordDraw() {
        totalDraws++;
    }

    public UUID getLinkedUserId() {
        return linkedUserId;
    }

    public void setLinkedUserId(UUID linkedUserId) {
        this.linkedUserId = linkedUserId;
    }

    public UUID getLinkedCharacterId() {
        return linkedCharacterId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPreferredRealmName() {
        return preferredRealmName;
    }

    public void setPreferredRealmName(String preferredRealmName) {
        this.preferredRealmName = preferredRealmName;
    }

    public String getLinkedCharacterName() {
        return linkedCharacterName;
    }

    public String getLinkedCharacterClass() {
        return linkedCharacterClass;
    }

    public int getLinkedCharacterLevel() {
        return linkedCharacterLevel;
    }

    public List<Item> getInventorySnapshot() {
        return List.copyOf(inventorySnapshot);
    }

    public List<String> getAchievements() {
        return List.copyOf(achievements);
    }

    public int getTotalWins() {
        return totalWins;
    }

    public int getTotalLosses() {
        return totalLosses;
    }

    public int getTotalDraws() {
        return totalDraws;
    }
}