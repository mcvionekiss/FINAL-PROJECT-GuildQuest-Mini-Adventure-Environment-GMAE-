package guildquest.gmae.engine;

import guildquest.gmae.enums.GameResult;
import guildquest.gmae.enums.InputType;
import guildquest.gmae.enums.PlayerSlot;
import guildquest.gmae.input.InputValidator;
import guildquest.gmae.profile.PlayerProfile;
import guildquest.model.Realm;
import guildquest.model.WorldClock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MiniAdventureEngine {
    private final List<MiniAdventure> registry;
    private final InputValidator inputValidator;

    private MiniAdventure currentAdventure;
    private PlayerProfile player1;
    private PlayerProfile player2;

    public MiniAdventureEngine() {
        this.registry = new ArrayList<>();
        this.inputValidator = new InputValidator();
    }

    // registry methods
    public void registerAdventure(MiniAdventure adventure) {
        if (adventure == null) {
            throw new IllegalArgumentException("adventure cannot be null");
        }
        registry.add(adventure);
    }

    public List<MiniAdventure> getRegisteredAdventures() {
        return Collections.unmodifiableList(registry);
    }

    public MiniAdventure getAdventureByIndex(int index) {
        if (index < 0 || index >= registry.size()) {
            throw new IndexOutOfBoundsException("Invalid adventure index: " + index);
        }
        return registry.get(index);
    }

    public void selectAdventure(int index) {
        currentAdventure = getAdventureByIndex(index);
    }

    public MiniAdventure getCurrentAdventure() {
        return currentAdventure;
    }

    public void setPlayers(PlayerProfile player1, PlayerProfile player2) {
        if (player1 == null || player2 == null) {
            throw new IllegalArgumentException("Both player profiles are required");
        }
        this.player1 = player1;
        this.player2 = player2;
    }

    public PlayerProfile getPlayer1() {
        return player1;
    }

    public PlayerProfile getPlayer2() {
        return player2;
    }

    public void initializeSelectedAdventure(Realm realm, WorldClock clock) {
        if (currentAdventure == null) {
            throw new IllegalStateException("No adventure selected");
        }
        if (player1 == null || player2 == null) {
            throw new IllegalStateException("Players must be set before initializing adventure");
        }

        currentAdventure.initialize(player1, player2, realm, clock);
        currentAdventure.start();
    }

    public boolean submitInput(PlayerSlot slot, InputType inputType, String payload) {
        if (currentAdventure == null) {
            return false;
        }

        if (!inputValidator.isValid(currentAdventure, slot, inputType, payload)) {
            return false;
        }

        currentAdventure.acceptInput(slot, inputType, payload);
        return true;
    }

    public void advanceAdventure() {
        if (currentAdventure == null) {
            throw new IllegalStateException("No adventure selected");
        }
        currentAdventure.advanceTurn();
    }

    public AdventureState getCurrentState() {
        if (currentAdventure == null) {
            return null;
        }
        return currentAdventure.getState();
    }

    public GameResult getCurrentResult() {
        if (currentAdventure == null) {
            return null;
        }
        return currentAdventure.checkCompletion();
    }

    public boolean isAdventureFinished() {
        if (currentAdventure == null) {
            return false;
        }
        return currentAdventure.checkCompletion() != GameResult.ONGOING;
    }

    public void resetCurrentAdventure() {
        if (currentAdventure != null) {
            currentAdventure.reset();
        }
    }
}