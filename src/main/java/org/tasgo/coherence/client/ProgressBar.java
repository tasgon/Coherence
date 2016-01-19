package org.tasgo.coherence.client;

/**
 * Created by Tasgo on 1/18/2016.
 */
public interface ProgressBar {
    public void increment(int stepsToIncrement);

    public int getTotalSteps();

    public int getSteps();

    public double getPercentage();

    public String getFormattedPercentage(String formatting);
}
