package guildquest.model;

import guildquest.enums.ThemeType;
import guildquest.enums.TimeDisplayMode;

import java.util.UUID;

public class UserSettings {
    private UUID currentRealmId; // user’s “located” realm
    private ThemeType theme = ThemeType.CLASSIC;
    private TimeDisplayMode timeDisplayMode = TimeDisplayMode.BOTH;

    public UUID getCurrentRealmId() { return currentRealmId; }
    public ThemeType getTheme() { return theme; }
    public TimeDisplayMode getTimeDisplayMode() { return timeDisplayMode; }

    public void setCurrentRealmId(UUID currentRealmId) { this.currentRealmId = currentRealmId; }
    public void setTheme(ThemeType theme) { this.theme = theme; }
    public void setTimeDisplayMode(TimeDisplayMode mode) { this.timeDisplayMode = mode; }
}
