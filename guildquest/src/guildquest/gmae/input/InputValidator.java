package guildquest.gmae.input;

import guildquest.gmae.engine.MiniAdventure;
import guildquest.gmae.enums.InputType;
import guildquest.gmae.enums.MiniAdventureStatus;
import guildquest.gmae.enums.PlayerSlot;

public class InputValidator {
    private final int maxPayloadLength;

    public InputValidator() {
        this.maxPayloadLength = 50;
    }

    public InputValidator(int maxPayloadLength) {
        if (maxPayloadLength <= 0) {
            throw new IllegalArgumentException("maxPayloadLength must be > 0");
        }
        this.maxPayloadLength = maxPayloadLength;
    }

    public boolean isValid(MiniAdventure adventure, PlayerSlot slot, InputType inputType, String payload) {
        if (adventure == null) {
            return false;
        }
        if (slot == null || inputType == null) {
            return false;
        }
        if (payload == null) {
            return false;
        }
        if (payload.length() > maxPayloadLength) {
            return false;
        }
        if (adventure.getState() == null) {
            return false;
        }
        return adventure.getState().getStatus() == MiniAdventureStatus.RUNNING;
    }

    public int getMaxPayloadLength() {
        return maxPayloadLength;
    }
}