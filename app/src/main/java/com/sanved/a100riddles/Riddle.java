package com.sanved.a100riddles;

/**
 * Created by Sanved on 16-09-2017.
 */

public class Riddle {

    String riddle, answer, explain;
    int level, rank;

    public Riddle(String riddle, String answer, int level, int rank, String explain){
        this.riddle = riddle;
        this.answer = answer;
        this.level = level;
        this.rank = rank;
        this.explain = explain;
    }

    public int getLevel() {
        return level;
    }

    public String getAnswer() {
        return answer;
    }

    public String getRiddle() {
        return riddle;
    }

    public int getRank() {
        return rank;
    }

    public String getExplain() {
        return explain;
    }
}
