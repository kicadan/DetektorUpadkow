package pl.polsl.aei.monitorupadkow;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class Storage {

    public enum Type {
        PRIMARY,
        SHADE
    };

    int wearNumber = 0;
    int phoneNumber = 0;
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

    private int queueCapacity = 300; //6 seconds of mi band use c.a. 200
    private int queueCounter = 0;
    private int queueShadeCounter = 0;
    
    private int queueAccCounter = 0;
    private int queueShadeAccCounter = 0;

    // the flag turned on when one of the sensors detects fall
    // if second detector confirms and flag is on, the fall is DETECTED
    private boolean fallWarning = false;

    private boolean primaryQueueAccesible = true;
    private boolean shadeQueueAccesible = true;

    public Storage(){
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

    public Storage(int queueCapacity){
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

    public void writePhoneAccelerometer(double[] data){
        //if not accesible, can't write
        if (primaryQueueAccesible) {
            queueAccX[queueAccCounter] = data[0];
            queueAccY[queueAccCounter] = data[1];
            queueAccZ[queueAccCounter] = data[2];
            queueAccCounter = (queueAccCounter + 1) % queueCapacity;
        }
        if (shadeQueueAccesible) {
            queueShadeAccX[queueShadeAccCounter] = data[0];
            queueShadeAccY[queueShadeAccCounter] = data[1];
            queueShadeAccZ[queueShadeAccCounter] = data[2];
            queueShadeAccCounter = (queueShadeAccCounter + 1) % queueCapacity;
        }
        phoneNumber++;
    }

    public void writeWearableSensor(Integer[] data){
        //if not accesible, can't write
        if (primaryQueueAccesible) {
            queueX[queueCounter] = data[0];
            queueY[queueCounter] = data[1];
            queueZ[queueCounter] = data[2];
            queueCounter = (queueCounter + 1) % queueCapacity;
        }
        if (shadeQueueAccesible) {
            queueShadeX[queueShadeCounter] = data[0];
            queueShadeY[queueShadeCounter] = data[1];
            queueShadeZ[queueShadeCounter] = data[2];
            queueShadeCounter = (queueShadeCounter + 1) % queueCapacity;
        }
        wearNumber++;
    }

    //transfer data to the cloud
    public void sendToQualify(){
        Integer[] x;
        Integer[] y;
        Integer[] z;
        double[] xp;
        double[] yp;
        double[] zp;
        /*if (queueCounter != 0) {
            //it's circle bufor so sort data chronologically
            x = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueX, queueCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueX, 0, queueCounter - 1)))
                    .toArray(Integer[]::new);
            y = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueY, queueCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueY, 0, queueCounter - 1)))
                    .toArray(Integer[]::new);
            z = Stream.concat(Arrays.stream(Arrays.copyOfRange(queueZ, queueCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueZ, 0, queueCounter - 1)))
                    .toArray(Integer[]::new);
        } else {
            x = queueX;
            y = queueY;
            z = queueZ;
        }
        if (queueAccCounter != 0) {
            xp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueAccX, queueAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueAccX, 0, queueAccCounter - 1)))
                    .toArray();
            yp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueAccY, queueAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueAccY, 0, queueAccCounter - 1)))
                    .toArray();
            zp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueAccZ, queueAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueAccZ, 0, queueAccCounter - 1)))
                    .toArray();
        } else {
            xp = queueAccX;
            yp = queueAccY;
            zp = queueAccZ;
        }*/
        // qualify to do
        // send to the cloud
        System.out.println(queueCounter + " " + wearNumber + " " + queueAccCounter + " " + phoneNumber);
        //parseToJson(x, y, z, xp, yp, zp);
        /*parseToJson(Arrays.copyOfRange(queueX, 0, queueCounter), Arrays.copyOfRange(queueY, 0, queueCounter), Arrays.copyOfRange(queueZ, 0, queueCounter)
                , Arrays.copyOfRange(queueAccX, 0, queueAccCounter), Arrays.copyOfRange(queueAccY, 0, queueAccCounter), Arrays.copyOfRange(queueAccZ, 0, queueAccCounter));*/
        generateJson(Type.PRIMARY);
        queueCounter = 0;
        queueAccCounter = 0;
    }

    public void sendShadeToQualify(){
        /*Integer[] x;
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
        } else {
            x = queueShadeX;
            y = queueShadeY;
            z = queueShadeZ;
        }
        if (queueShadeAccCounter != 0) {
            xp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeAccX, queueShadeAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeAccX, 0, queueShadeAccCounter - 1)))
                    .toArray();
            yp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeAccY, queueShadeAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeAccY, 0, queueShadeAccCounter - 1)))
                    .toArray();
            zp = DoubleStream.concat(Arrays.stream(Arrays.copyOfRange(queueShadeAccZ, queueShadeAccCounter, queueCapacity - 1)), Arrays.stream(Arrays.copyOfRange(queueShadeAccZ, 0, queueShadeAccCounter - 1)))
                    .toArray();
        } else {
            xp = queueShadeAccX;
            yp = queueShadeAccY;
            zp = queueShadeAccZ;
        }*/
        // qualify to do
        // send to the cloud
        /*parseToJson(Arrays.copyOfRange(queueShadeX, 0, queueShadeCounter), Arrays.copyOfRange(queueShadeY, 0, queueShadeCounter), Arrays.copyOfRange(queueShadeZ, 0, queueShadeCounter)
                , Arrays.copyOfRange(queueShadeAccX, 0, queueShadeAccCounter), Arrays.copyOfRange(queueShadeAccY, 0, queueShadeAccCounter), Arrays.copyOfRange(queueShadeAccZ, 0, queueShadeAccCounter));*/
        generateJson(Type.SHADE);
        queueShadeCounter = 0;
        queueShadeAccCounter = 0;
    }

    public void clear() {
        // set counter on 0 is sufficient
        this.queueCounter = 0;
        this.queueAccCounter = 0;
        this.queueShadeCounter = 0;
        this.queueShadeAccCounter = 0;
    }

    public void clear(Type type){
        // set counter on 0 is sufficient
        switch(type){
            case PRIMARY :
                this.queueCounter = 0;
                this.queueAccCounter = 0;
                break;
            case SHADE :
                this.queueShadeCounter = 0;
                this.queueShadeAccCounter = 0;
                break;
        }
    }

    public void block(Type type){
        switch (type){
            case PRIMARY :
                primaryQueueAccesible = false;
                break;
            case SHADE :
                shadeQueueAccesible = false;
                break;
        }
    }

    public void unblock(Type type){
        switch(type){
            case PRIMARY :
                primaryQueueAccesible = true;
                break;
            case SHADE :
                shadeQueueAccesible = true;
                break;
        }
    }

    public void writeToFile(Context context, String filename, Type type) {
        String jsonFilename = filename;
        String csvFilename = filename;
        if (!filename.toUpperCase().endsWith(".JSON"))
            jsonFilename = filename.concat(".json").toUpperCase();
        if (!filename.toUpperCase().endsWith(".CSV"))
            csvFilename = filename.concat(".csv").toUpperCase();
        File path = context.getExternalFilesDir(null);
        File jsonFile = new File(path, jsonFilename);
        File csvFile = new File(path, csvFilename);
        try {
            FileOutputStream stream = new FileOutputStream(jsonFile);
            stream.write(generateJson(type).getBytes());
            stream.flush();
            stream.close();

            stream = new FileOutputStream(csvFile);
            stream.write(generateCsv(type).getBytes());
            stream.flush();
            stream.close();
        } catch(IOException e){
            System.out.println(e.toString());
        }
    }

    public String generateJson(Type type){
        String result = "{\n\t\"measurements\": {\n\t\t\"wearable\": [";
        switch(type){
            case PRIMARY :
                for(int i = 0; i < queueCounter; i++){
                    result += "\n\t\t\t{"
                            + "\n\t\t\t\t\"X\" : " + queueX[i] + ","
                            + "\n\t\t\t\t\"Y\" : " + queueY[i] + ","
                            + "\n\t\t\t\t\"Z\" : " + queueZ[i] + "\n\t\t\t},";
                }
                if (result.endsWith(","))
                    result = result.substring(0, result.lastIndexOf(","));
                result += "\n\t\t],\n\t\t\"phone\": [";
                for(int i = 0; i < queueAccCounter; i++){
                    result += "\n\t\t\t{"
                            + "\n\t\t\t\t\"X\" : " + queueAccX[i]+ ","
                            + "\n\t\t\t\t\"Y\" : " + queueAccY[i] + ","
                            + "\n\t\t\t\t\"Z\" : " + queueAccZ[i] + "\n\t\t\t},";
                }
                if (result.endsWith(","))
                    result = result.substring(0, result.lastIndexOf(","));
                result += "\n\t\t]"
                        + "\n\t}"
                        + "\n}";
                break;
            case SHADE :
                for(int i = 0; i < queueShadeCounter; i++){
                    result += "\n\t\t\t{"
                            + "\n\t\t\t\t\"X\" : " + queueShadeX[i] + ","
                            + "\n\t\t\t\t\"Y\" : " + queueShadeY[i] + ","
                            + "\n\t\t\t\t\"Z\" : " + queueShadeZ[i] + "\n\t\t\t},";
                }
                if (result.endsWith(","))
                    result = result.substring(0, result.lastIndexOf(","));
                result += "\n\t\t],\n\t\t\"phone\": [";
                for(int i = 0; i < queueShadeAccCounter; i++){
                    result += "\n\t\t\t{"
                            + "\n\t\t\t\t\"X\" : " + queueShadeAccX[i]+ ","
                            + "\n\t\t\t\t\"Y\" : " + queueShadeAccY[i] + ","
                            + "\n\t\t\t\t\"Z\" : " + queueShadeAccZ[i] + "\n\t\t\t},";
                }
                if (result.endsWith(","))
                    result = result.substring(0, result.lastIndexOf(","));
                result += "\n\t\t]"
                        + "\n\t}"
                        + "\n}";
                break;
        }
        return result;
    }

    private String generateCsv(Type type){
        String result = "Xw;Yw;Zw;Xp;Yp;Zp\n";
        int counterBound;
        switch(type){
            case PRIMARY :
                counterBound = Math.max(queueCounter, queueAccCounter);
                for(int i = 0; i < counterBound; i++){
                    result = result.concat( i < queueCounter ? queueX[i] + ";" + queueY[i] + ";" + queueZ[i] + ";" : ";;;" ); //check if there is any possible value for wearable sensor
                    result = result.concat( i < queueAccCounter ? queueAccX[i] + ";" + queueAccY[i] + ";" + queueAccZ[i] + "\n" : ";;\n" ); //check if there is any possible value for phone sensor
                }
                break;
            case SHADE :
                counterBound = Math.max(queueShadeCounter, queueShadeAccCounter);
                for(int i = 0; i < counterBound; i++){
                    result = result.concat( i < queueShadeCounter ? queueShadeX[i] + ";" + queueShadeY[i] + ";" + queueShadeZ[i] + ";" : ";;;" ); //check if there is any possible value for wearable sensor
                    result = result.concat( i < queueShadeAccCounter ? queueShadeAccX[i] + ";" + queueShadeAccY[i] + ";" + queueShadeAccZ[i] + "\n" : ";;\n" ); //check if there is any possible value for phone sensor
                }
                break;
        }
        return result;
    }

}
