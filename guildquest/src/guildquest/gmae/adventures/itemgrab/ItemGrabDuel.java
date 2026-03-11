package guildquest.gmae.adventures.itemgrab;


import guildquest.gmae.engine.AbstractMiniAdventure;
import guildquest.gmae.enums.GameResult;
import guildquest.gmae.enums.InputType;
import guildquest.gmae.enums.PlayerSlot;

public class ItemGrabDuel extends AbstractMiniAdventure {

    private int maxTurns;

    @Override
    protected void onStart() {
        maxTurns = 6;

        state.setPlayer1Score(0);
        state.setPlayer2Score(0);

        String realmName = (realm == null) ? "Unknown Realm" : realm.getName();
        String worldTime = (clock == null) ? "Unknown Time" : clock.now().toString();

        state.setMessage("Started in " + realmName + " at " + worldTime);
        state.setBoardSnapshot("Game board goes here.");
    }

    @Override
    protected void onInput(PlayerSlot slot, InputType inputType, String payload) {
        if (payload == null || payload.isBlank()) {
            state.setMessage("Input was empty.");
            return;
        }

        if (slot == PlayerSlot.P1) {
            state.setPlayer1Score(state.getPlayer1Score() + 1);
        } else {
            state.setPlayer2Score(state.getPlayer2Score() + 1);
        }

        state.setMessage(slot + " entered: " + payload);
    }

    @Override
    protected void onTick() {
        state.setBoardSnapshot(
                "Turn " + state.getTurnNumber()
                        + " | P1=" + state.getPlayer1Score()
                        + " | P2=" + state.getPlayer2Score()
        );
    }

    @Override
    protected GameResult evaluateWinCondition() {
        if (state.getTurnNumber() >= maxTurns) {
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
        return "ItemGrabDuel";
    }

    @Override
    public String getDescription() {
        return "Replace with one-sentence description.";
    }
}
