package guildquest.gmae.engine;

import guildquest.gmae.enums.GameResult;
import guildquest.gmae.enums.InputType;
import guildquest.gmae.enums.PlayerSlot;
import guildquest.gmae.profile.PlayerProfile;
import guildquest.model.Realm;
import guildquest.model.WorldClock;

public interface MiniAdventure {
    void initialize(PlayerProfile player1, PlayerProfile player2, Realm realm, WorldClock clock);

    void start();

    void acceptInput(PlayerSlot slot, InputType inputType, String payload);

    void advanceTurn();

    AdventureState getState();

    GameResult checkCompletion();

    void reset();

    String getAdventureName();

    String getDescription();
}