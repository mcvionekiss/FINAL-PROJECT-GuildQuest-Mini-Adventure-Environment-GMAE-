package guildquest.model;

import guildquest.enums.VisibilityType;

import java.util.*;

public class QuestEvent {
    private final UUID id;
    private String title;
    private WorldTime start;
    private WorldTime end; // optional (nullable)
    private Realm realm;
    private VisibilityType visibility;

    // optional RPG fields
    private final Set<UUID> participantCharacterIds = new HashSet<>();
    private final List<Item> rewardItems = new ArrayList<>();

    public QuestEvent(String title, WorldTime start, WorldTime end, Realm realm, VisibilityType visibility) {
        this.id = UUID.randomUUID();
        this.title = Objects.requireNonNull(title);
        this.start = Objects.requireNonNull(start);
        this.end = end;
        this.realm = Objects.requireNonNull(realm);
        this.visibility = Objects.requireNonNull(visibility);
        validateTimes();
    }

    private void validateTimes() {
        if (end != null && end.compareTo(start) < 0) {
            throw new IllegalArgumentException("end time cannot be before start time");
        }
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public WorldTime getStart() { return start; }
    public WorldTime getEnd() { return end; }
    public Realm getRealm() { return realm; }
    public VisibilityType getVisibility() { return visibility; }

    public void setTitle(String title) { this.title = Objects.requireNonNull(title); }
    public void setStart(WorldTime start) { this.start = Objects.requireNonNull(start); validateTimes(); }
    public void setEnd(WorldTime end) { this.end = end; validateTimes(); }
    public void setRealm(Realm realm) { this.realm = Objects.requireNonNull(realm); }
    public void setVisibility(VisibilityType visibility) { this.visibility = Objects.requireNonNull(visibility); }

    public void addParticipant(Character c) { participantCharacterIds.add(c.getId()); }
    public void removeParticipant(Character c) { participantCharacterIds.remove(c.getId()); }
    public Set<UUID> getParticipantCharacterIds() { return Collections.unmodifiableSet(participantCharacterIds); }

    public void addRewardItem(Item item) { rewardItems.add(Objects.requireNonNull(item)); }
    public List<Item> getRewardItems() { return Collections.unmodifiableList(rewardItems); }

    @Override
    public String toString() {
        return title + " [" + visibility + "] "
                + "Start: " + start + (end == null ? "" : " End: " + end)
                + " @ " + realm.getName();
    }
}
