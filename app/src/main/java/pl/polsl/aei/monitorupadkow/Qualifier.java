package pl.polsl.aei.monitorupadkow;

import java.util.Arrays;
import java.util.Collections;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public class Qualifier {

    private Integer[] queueX;
    private Integer[] queueY;
    private Integer[] queueZ;
    // queues mover 2 seconds forward
    private Integer[] queueShadeX;
    private Integer[] queueShadeY;
    private Integer[] queueShadeZ;

    private double[] queueAccX;
    private double[] queueAccY;
    private double[] queueAccZ;
    // queues moved 2 seconds forward
    private double[] queueShadeAccX;
    private double[] queueShadeAccY;
    private double[] queueShadeAccZ;

    private int queueCapacity = 150; //6 seconds of mi band use
    private int queueCounter = 0;
    // counter moved 3 seconds forward (half of queue volume)
    private int queueShadeCounter = 75;
    
    private int queueAccCounter = 0;
    // counter moved 3 seconds forward (half of queue volume)
    private int queueShadeAccCounter = 75;

    // the flag turned on when one of the sensors detects fall
    // if second detector confirms and flag is on, the fall is DETECTED
    private boolean fallWarning = false;

    public Qualifier(){
        //default values of queue's capacity
        //trzeba sprawdzić częstotliwość próbkowania na sekundę i przeskalować
        // ans: ca. 25 per second
        queueX = new Integer[queueCapacity];
        queueY = new Integer[queueCapacity];
        queueZ = new Integer[queueCapacity];

        queueShadeX = new Integer[queueCapacity];
        queueShadeY = new Integer[queueCapacity];
        queueShadeZ = new Integer[queueCapacity];

        //trzeba sprawdzić częstotliwość próbkowania na sekundę i przeskalować
        // ans: ca. 25 per second
        queueAccX = new double[queueCapacity];
        queueAccY = new double[queueCapacity];
        queueAccZ = new double[queueCapacity];

        queueShadeAccX = new double[queueCapacity];
        queueShadeAccY = new double[queueCapacity];
        queueShadeAccZ = new double[queueCapacity];
    }

    public Qualifier(int queueCapacity){
        this.queueCapacity = queueCapacity;
        this.queueCapacity = queueCapacity;

        //trzeba sprawdzić częstotliwość próbkowania na sekundę i przeskalować
        // ans: ca. 25 per second
        queueX = new Integer[queueCapacity];
        queueY = new Integer[queueCapacity];
        queueZ = new Integer[queueCapacity];

        queueShadeX = new Integer[queueCapacity];
        queueShadeY = new Integer[queueCapacity];
        queueShadeZ = new Integer[queueCapacity];

        //trzeba sprawdzić częstotliwość próbkowania na sekundę i przeskalować
        // ans: ca. 25 per second
        queueAccX = new double[queueCapacity];
        queueAccY = new double[queueCapacity];
        queueAccZ = new double[queueCapacity];

        queueShadeAccX = new double[queueCapacity];
        queueShadeAccY = new double[queueCapacity];
        queueShadeAccZ = new double[queueCapacity];
    }

    public void qualifyPhoneAccelerometer(double[] data){
        queueAccX[queueAccCounter] = data[0];
        queueAccY[queueAccCounter] = data[1];
        queueAccZ[queueAccCounter] = data[2];

        queueAccX[queueShadeAccCounter] = data[0];
        queueAccY[queueShadeAccCounter] = data[1];
        queueAccZ[queueShadeAccCounter] = data[2];
        // moment kiedy ma nastąpić kwalifikacja (ustalany czasowo czy liczbą pomiarów)
        if(queueAccCounter == queueCapacity){
            queueAccCounter = 0;
            System.out.println("PHONE!!!");
            // weka and aws qualification
        }
    }

    public void qualifyWearableSensor(Integer[] data){
        queueX[queueCounter] = data[0];
        queueY[queueCounter] = data[1];
        queueZ[queueCounter] = data[2];

        queueShadeX[queueShadeCounter] = data[0];
        queueShadeY[queueShadeCounter] = data[1];
        queueShadeZ[queueShadeCounter] = data[2];
        // moment kiedy ma nastąpić kwalifikacja (ustalany czasowo czy liczbą pomiarów)
        if (queueCounter == queueCapacity) {
            queueCounter = 0;
            System.out.println("MI BAND!!!");
            // weka and aws qualification
        }
    }

    public void qualify(){
        Integer[] x;
        Integer[] y;
        Integer[] z;
        double[] xp;
        double[] yp;
        double[] zp;
        if (queueCounter != 0) {
            x = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueX, queueCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueX, 0, queueCounter - 1)))
                    .toArray(Integer[]::new);
            y = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueY, queueCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueY, 0, queueCounter - 1)))
                    .toArray(Integer[]::new);
            z = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueZ, queueCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueZ, 0, queueCounter - 1)))
                    .toArray(Integer[]::new);
            xp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueAccX, queueAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueAccX, 0, queueAccCounter - 1)))
                    .toArray();
            yp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueAccY, queueAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueAccY, 0, queueAccCounter - 1)))
                    .toArray();
            zp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueAccZ, queueAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueAccZ, 0, queueAccCounter - 1)))
                    .toArray();
        } else {
            x = queueX;
            y = queueY;
            z = queueZ;
            xp = queueAccX;
            yp = queueAccY;
            zp = queueAccZ;
        }
        // qualify to do
    }

    public void qualifyShade(){
        Integer[] x;
        Integer[] y;
        Integer[] z;
        double[] xp;
        double[] yp;
        double[] zp;
        if (queueShadeCounter != 0) {
            x = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeX, queueShadeCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeX, 0, queueShadeCounter - 1)))
                    .toArray(Integer[]::new);
            y = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeY, queueShadeCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeY, 0, queueShadeCounter - 1)))
                    .toArray(Integer[]::new);
            z = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeZ, queueShadeCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeZ, 0, queueShadeCounter - 1)))
                    .toArray(Integer[]::new);
            xp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeAccX, queueShadeAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeAccX, 0, queueShadeAccCounter - 1)))
                    .toArray();
            yp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeAccY, queueShadeAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeAccY, 0, queueShadeAccCounter - 1)))
                    .toArray();
            zp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeAccZ, queueShadeAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeAccZ, 0, queueShadeAccCounter - 1)))
                    .toArray();
        } else {
            x = queueShadeX;
            y = queueShadeY;
            z = queueShadeZ;
            xp = queueShadeAccX;
            yp = queueShadeAccY;
            zp = queueShadeAccZ;
        }
        // qualify to do
    }

}
