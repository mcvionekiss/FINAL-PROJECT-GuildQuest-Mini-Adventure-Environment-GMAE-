package guildquest.model;

import java.util.Objects;
import java.util.UUID;

public class Character {
    private final UUID id;
    private String name;
    private String clazz;
    private int level;
    private final Inventory inventory;

    public Character(String name, String clazz, int level, int inventoryCapacity) {
        this.id = UUID.randomUUID();
        this.name = Objects.requireNonNull(name);
        this.clazz = Objects.requireNonNull(clazz);
        this.level = level;
        this.inventory = new Inventory(inventoryCapacity);
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getClazz() { return clazz; }
    public int getLevel() { return level; }
    public Inventory getInventory() { return inventory; }

    public void setName(String name) { this.name = Objects.requireNonNull(name); }
    public void setClazz(String clazz) { this.clazz = Objects.requireNonNull(clazz); }
    public void setLevel(int level) { this.level = level; }

    @Override
    public String toString() {
        return name + " (" + clazz + ", lvl " + level + ") inv "
                + inventory.getCurrentSize() + "/" + inventory.getCapacity();
    }
}
