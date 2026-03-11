package guildquest.gmae.engine;

import guildquest.gmae.enums.GameResult;
import guildquest.gmae.enums.MiniAdventureStatus;

public class AdventureState {
    private MiniAdventureStatus status;
    private int turnNumber;
    private int player1Score;
    private int player2Score;
    private String message;
    private String boardSnapshot;
    private GameResult result;

    public AdventureState() {
        this.status = MiniAdventureStatus.NOT_STARTED;
        this.turnNumber = 0;
        this.player1Score = 0;
        this.player2Score = 0;
        this.message = "";
        this.boardSnapshot = "";
        this.result = GameResult.ONGOING;
    }

    public MiniAdventureStatus getStatus() {
        return status;
    }

    public void setStatus(MiniAdventureStatus status) {
        this.status = status;
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(int turnNumber) {
        this.turnNumber = turnNumber;
    }

    public int getPlayer1Score() {
        return player1Score;
    }

    public void setPlayer1Score(int player1Score) {
        this.player1Score = player1Score;
    }

    public int getPlayer2Score() {
        return player2Score;
    }

    public void setPlayer2Score(int player2Score) {
        this.player2Score = player2Score;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getBoardSnapshot() {
        return boardSnapshot;
    }

    public void setBoardSnapshot(String boardSnapshot) {
        this.boardSnapshot = boardSnapshot;
    }

    public GameResult getResult() {
        return result;
    }

    public void setResult(GameResult result) {
        this.result = result;
    }
}