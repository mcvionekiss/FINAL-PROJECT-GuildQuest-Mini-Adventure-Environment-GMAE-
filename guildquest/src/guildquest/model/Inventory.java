package guildquest.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Inventory {
    private final int capacity;
    private final List<Item> items = new ArrayList<>();

    public Inventory(int capacity) {
        if (capacity < 0) throw new IllegalArgumentException("capacity must be >= 0");
        this.capacity = capacity;
    }

    public int getCapacity() { return capacity; }
    public int getCurrentSize() { return items.size(); }
    public List<Item> getItems() { return Collections.unmodifiableList(items); }

    public boolean addItem(Item item) {
        if (items.size() >= capacity) return false;
        return items.add(item);
    }

    public boolean removeItemById(java.util.UUID id) {
        return items.removeIf(i -> i.getId().equals(id));
    }
}
