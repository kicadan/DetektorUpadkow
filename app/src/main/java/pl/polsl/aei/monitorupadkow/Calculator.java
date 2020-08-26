package pl.polsl.aei.monitorupadkow;

import java.util.Arrays;

public class Calculator {
    public static double standardDeviation(double[] vector){
        double standardDeviation = 0.0;

        double mean = Arrays.stream(vector).average().orElse(Double.NaN);

        for(double number: vector) {
            standardDeviation += Math.pow(number - mean, 2);
        }

        return Math.sqrt(standardDeviation/(Arrays.stream(vector).count()-1));
    }

    public static double standardDeviation(Integer[] vector, int counter){
        double standardDeviation = 0.0;
        int sum = 0;

        for (int i = 0; i < counter; i++){
            sum += vector[i];
        }
        double mean = (double) sum/(counter);

        for(int i = 0; i < counter; i++) {
            standardDeviation += Math.pow(vector[i] - mean, 2);
        }

        return Math.sqrt(standardDeviation/(Arrays.stream(vector).count()-1));
    }

    public static double maxMagnitude(double[] vectorX, double[] vectorY, double[] vectorZ){
        double maxMagnitude = 0;
        double magnitude = 0;

        for(int i = 0; i < vectorX.length; i++){
            magnitude = Math.sqrt(Math.pow(vectorX[i], 2) + Math.pow(vectorY[i], 2) + Math.pow(vectorZ[i], 2));
            if (magnitude > maxMagnitude)
                maxMagnitude = magnitude;
        }

        return maxMagnitude;
    }

    public static double maxMagnitude(Integer[] vectorX, Integer[] vectorY, Integer[] vectorZ, int counter){
        double maxMagnitude = 0;
        double magnitude = 0;

        for(int i = 0; i < counter; i++){
            magnitude = Math.sqrt(Math.pow(vectorX[i], 2) + Math.pow(vectorY[i], 2) + Math.pow(vectorZ[i], 2));
            if (magnitude > maxMagnitude)
                maxMagnitude = magnitude;
        }

        return maxMagnitude;
    }
}
