package pl.polsl.aei.monitorupadkow;

import java.util.Arrays;

public class Calculator {
    public static double standardDeviation(double[] vector){
        double standardDeviation = 0.0;

        double mean = Arrays.stream(vector).average().orElse(Double.NaN);

        for(double num: vector) {
            standardDeviation += Math.pow(num - mean, 2);
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
}
