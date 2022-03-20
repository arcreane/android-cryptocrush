package fr.dantindurand.cryptocrush;

public class Game {
    protected boolean started = false;
    private int score;
    protected int score_to_win;

    public void start() {
        started = true;
    }

    public boolean gameStatus(){
        return started;
    }

    protected void setScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
    }
    public Game() {

    }

}
