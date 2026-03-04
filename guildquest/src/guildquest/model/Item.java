package guildquest.model;

import guildquest.enums.RarityType;

import java.util.Objects;
import java.util.UUID;

public class Item {
    private final UUID id;
    private String name;
    private RarityType rarity;
    private String description;

    public Item(String name, RarityType rarity, String description) {
        this.id = UUID.randomUUID();
        this.name = Objects.requireNonNull(name);
        this.rarity = Objects.requireNonNull(rarity);
        this.description = description;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public RarityType getRarity() { return rarity; }
    public String getDescription() { return description; }

    public void setName(String name) { this.name = Objects.requireNonNull(name); }
    public void setRarity(RarityType rarity) { this.rarity = Objects.requireNonNull(rarity); }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return name + " [" + rarity + "]" + (description == null ? "" : " - " + description);
    }
}
