package com.centomila;

import java.util.Random;

public class VelocityShape {
    public static String[] velocityShapes = new String[] {
        "---DISABLED---","Random", "Flat (Value by Min Velocity)", "Linear Inc", "Linear Dec", "Arc", "Sine", "Cosine",
        "Double Cosine", "Alternate Min Vel and High Vel", "Alternate High and Min Vel"
    };

    /**
     * @param pattern
     * @param velocityType "Random", "Flat (Value by Min Velocity)", "Linear Inc",
     *                     "Linear Dec", "Arc", "Sine", "Cosine", "Double Cosine", "Alternate Min Vel and High Vel",
     *                     "Alternate High and Min Vel"
     * @param minVelocity
     * @param maxVelocity
     */
    public static void applyVelocityShape(int[] pattern, String velocityType, int minVelocity, int maxVelocity) {
        // Count non-zero steps first
        int activeStepsCount = 0;
        for (int i = 0; i < pattern.length; i++) {
            if (pattern[i] > 0) {
                activeStepsCount++;
            }
        }

        switch (velocityType) {
            case "Random":
                Random random = new Random();
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        pattern[i] = minVelocity + random.nextInt(maxVelocity - minVelocity + 1);
                    }
                }
                break;
            case "Flat (Value by Min Velocity)":
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        pattern[i] = minVelocity;
                    }
                }
                break;
            case "Linear Inc":
                int stepCount = 0;
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        pattern[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * stepCount
                                        / (double) (activeStepsCount - 1));
                        stepCount++;
                    }
                }
                break;
            case "Linear Dec":
                stepCount = 0;
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        pattern[i] = maxVelocity
                                - (int) Math.round((maxVelocity - minVelocity) * stepCount
                                        / (double) (activeStepsCount - 1));
                        stepCount++;
                    }
                }
                break;
            case "Arc":
                stepCount = 0;
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        pattern[i] = minVelocity
                                + (int) Math.round(
                                        (maxVelocity - minVelocity)
                                                * Math.sin(Math.PI * stepCount / (activeStepsCount - 1)));
                        stepCount++;
                    }
                }
                break;
            case "Sine":
                stepCount = 0;
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        double normalizedValue = (Math.sin(2 * Math.PI * stepCount / (activeStepsCount - 1)) + 1) * 0.5;
                        pattern[i] = minVelocity
                                + (int) Math.round((maxVelocity - minVelocity) * normalizedValue);
                        stepCount++;
                    }
                }
                break;
            case "Cosine":
                stepCount = 0;
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        double normalizedValue = (Math.cos(2 * Math.PI * stepCount / (activeStepsCount - 1))) * 0.5
                                + 0.5;
                        pattern[i] = Math.max(1,
                                minVelocity + (int) Math.round((maxVelocity - minVelocity) * normalizedValue));
                        stepCount++;
                    }
                }
                break;
            case "Double Cosine":
                stepCount = 0;
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        double normalizedValue = (Math.cos(4 * Math.PI * stepCount / (activeStepsCount - 1))) * 0.5
                                + 0.5;
                        pattern[i] = Math.max(1,
                                minVelocity + (int) Math.round((maxVelocity - minVelocity) * normalizedValue));
                        stepCount++;
                    }
                }
                break;
            case "Alternate Min Vel and High Vel":
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        pattern[i] = i % 2 == 0 ? minVelocity : maxVelocity;
                    }
                }
                break;
            case "Alternate High and Min Vel":
                for (int i = 0; i < pattern.length; i++) {
                    if (pattern[i] > 0) {
                        pattern[i] = i % 2 == 0 ? maxVelocity : minVelocity;
                    }
                }
                break;
            default:
                break;
        }
    }

}