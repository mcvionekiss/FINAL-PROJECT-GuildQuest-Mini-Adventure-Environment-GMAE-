package guildquest.gmae.adventures.test;

import guildquest.gmae.engine.AbstractMiniAdventure;
import guildquest.gmae.enums.GameResult;
import guildquest.gmae.enums.InputType;
import guildquest.gmae.enums.PlayerSlot;

public class TestAdventure extends AbstractMiniAdventure {

    private static final int MAX_TURNS = 4;

    @Override
    protected void onStart() {
        String realmName = (realm == null) ? "Unknown Realm" : realm.getName();
        String worldTime = (clock == null) ? "Unknown Time" : clock.now().toString();

        String localTime = "Unknown Local Time";
        if (realm != null && clock != null) {
            localTime = realm.toLocalTime(clock.now()).toString();
        }

        state.setMessage("Test Adventure started in " + realmName
                + " | world time: " + worldTime
                + " | local time: " + localTime);

        state.setBoardSnapshot(
                "P1 inventory items: " + player1.getInventorySnapshot().size()
                        + " | P2 inventory items: " + player2.getInventorySnapshot().size()
        );
    }

    @Override
    protected void onInput(PlayerSlot slot, InputType inputType, String payload) {
        if (payload == null || payload.isBlank()) {
            state.setMessage("Empty input was ignored.");
            return;
        }

        if (slot == PlayerSlot.P1) {
            state.setPlayer1Score(state.getPlayer1Score() + 1);
        } else {
            state.setPlayer2Score(state.getPlayer2Score() + 1);
        }

        state.setMessage("Accepted input from " + slot + ": " + payload);
    }

    @Override
    protected void onTick() {
        state.setBoardSnapshot(
                "Turn " + state.getTurnNumber()
                        + "/" + MAX_TURNS
                        + " | P1=" + state.getPlayer1Score()
                        + " | P2=" + state.getPlayer2Score()
        );
    }

    @Override
    protected GameResult evaluateWinCondition() {
        if (state.getTurnNumber() >= MAX_TURNS) {
            if (state.getPlayer1Score() > state.getPlayer2Score()) {
                return GameResult.PLAYER1_WIN;
            }
            if (state.getPlayer2Score() > state.getPlayer1Score()) {
                return GameResult.PLAYER2_WIN;
            }
            return GameResult.DRAW;
        }

        return GameResult.ONGOING;
    }

    @Override
    public String getAdventureName() {
        return "Test Adventure";
    }

    @Override
    public String getDescription() {
        return "Simple two-player CLI test for engine validation.";
    }
}