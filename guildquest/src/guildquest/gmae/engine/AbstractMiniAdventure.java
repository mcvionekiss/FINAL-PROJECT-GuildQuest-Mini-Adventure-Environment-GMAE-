package guildquest.gmae.engine;

import guildquest.gmae.enums.GameResult;
import guildquest.gmae.enums.InputType;
import guildquest.gmae.enums.MiniAdventureStatus;
import guildquest.gmae.enums.PlayerSlot;
import guildquest.gmae.profile.PlayerProfile;
import guildquest.model.Realm;
import guildquest.model.WorldClock;

public abstract class AbstractMiniAdventure implements MiniAdventure {
    protected PlayerProfile player1;
    protected PlayerProfile player2;
    protected Realm realm;
    protected WorldClock clock;
    protected AdventureState state;

    @Override
    public void initialize(PlayerProfile player1, PlayerProfile player2, Realm realm, WorldClock clock) {
        this.player1 = player1;
        this.player2 = player2;
        this.realm = realm;
        this.clock = clock;
        this.state = new AdventureState();
    }

    @Override
    public void start() {
        state.setStatus(MiniAdventureStatus.RUNNING);
        onStart();
    }

    @Override
    public void acceptInput(PlayerSlot slot, InputType inputType, String payload) {
        onInput(slot, inputType, payload);
    }

    @Override
    public void advanceTurn() {
        state.setTurnNumber(state.getTurnNumber() + 1);
        onTick();

        GameResult result = evaluateWinCondition();
        state.setResult(result);

        if (result != GameResult.ONGOING) {
            state.setStatus(MiniAdventureStatus.FINISHED);
        }
    }

    @Override
    public AdventureState getState() {
        return state;
    }

    @Override
    public GameResult checkCompletion() {
        return state.getResult();
    }

    @Override
    public void reset() {
        this.state = new AdventureState();
    }

    protected abstract void onStart();

    protected abstract void onInput(PlayerSlot slot, InputType inputType, String payload);

    protected abstract void onTick();

    protected abstract GameResult evaluateWinCondition();
}