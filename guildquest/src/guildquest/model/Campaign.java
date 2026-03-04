package guildquest.model;

import guildquest.enums.TimePeriodType;
import guildquest.enums.VisibilityType;

import java.util.*;

public class Campaign {
    private final UUID id;
    private String name;
    private VisibilityType visibility;
    private boolean archived;

    private final List<QuestEvent> events = new ArrayList<>();

    public Campaign(String name, VisibilityType visibility) {
        this.id = UUID.randomUUID();
        this.name = Objects.requireNonNull(name);
        this.visibility = Objects.requireNonNull(visibility);
        this.archived = false;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public VisibilityType getVisibility() { return visibility; }
    public boolean isArchived() { return archived; }

    public void setName(String name) { this.name = Objects.requireNonNull(name); }
    public void setVisibility(VisibilityType visibility) { this.visibility = Objects.requireNonNull(visibility); }
    public void setArchived(boolean archived) { this.archived = archived; }

    public List<QuestEvent> getEvents() { return Collections.unmodifiableList(events); }

    public QuestEvent addQuestEvent(QuestEvent e) {
        events.add(Objects.requireNonNull(e));
        return e;
    }

    public boolean removeQuestEvent(UUID eventId) {
        return events.removeIf(e -> e.getId().equals(eventId));
    }

    public QuestEvent findEvent(UUID eventId) {
        for (QuestEvent e : events) if (e.getId().equals(eventId)) return e;
        throw new NoSuchElementException("Event not found");
    }

    // Simple World Clock ranges:
    // day=1440, week=10080, month=43200 (30 days), year=525600 (365 days)
    public List<QuestEvent> timeline(TimePeriodType period, WorldTime anchorStart) {
        long span;
        switch (period) {
            case DAY -> span = 1440L;
            case WEEK -> span = 10080L;
            case MONTH -> span = 43200L;
            case YEAR -> span = 525600L;
            default -> throw new IllegalArgumentException("Unknown period");
        }

        long startMin = anchorStart.totalMinutes();
        long endMinExclusive = startMin + span;

        List<QuestEvent> filtered = new ArrayList<>();
        for (QuestEvent e : events) {
            long evStart = e.getStart().totalMinutes();
            // include if event starts in [start, end)
            if (evStart >= startMin && evStart < endMinExclusive) filtered.add(e);
        }
        filtered.sort(Comparator.comparing(QuestEvent::getStart));
        return filtered;
    }

    @Override
    public String toString() {
        return name + " [" + visibility + "]" + (archived ? " (ARCHIVED)" : "");
    }

    public void toggleVisibility() {
        this.visibility = (this.visibility == VisibilityType.PUBLIC)
                ? VisibilityType.PRIVATE
                : VisibilityType.PUBLIC;
    }
}
