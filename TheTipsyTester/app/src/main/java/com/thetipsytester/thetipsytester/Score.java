package com.thetipsytester.thetipsytester;

/**
 * Created by Nick on 4/17/2016.
 */
public class Score {
    int id;
    String test;

    public void setId(int id) {
        this.id = id;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public void setBest(int best) {
        this.best = best;
    }

    public void setWorst(int worst) {
        this.worst = worst;
    }

    int best;
    int worst;


    public int getId() {
        return id;
    }

    public int getWorst() {
        return worst;
    }

    public int getBest() {
        return best;
    }

    public String getTest() {
        return test;
    }


}
