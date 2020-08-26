package pl.polsl.aei.monitorupadkow;

class Features {
    //featery z besta FE11W+P
    double wearableStdDevX;
    double wearableStdDevY;
    double wearableStdDevZ;

    double phoneStdDevX;
    double phoneStdDevY;
    double phoneStdDevZ;

    public double getWearableStdDevX() {
        return wearableStdDevX;
    }

    public void setWearableStdDevX(double wearableStdDevX) {
        this.wearableStdDevX = wearableStdDevX;
    }

    public double getWearableStdDevY() {
        return wearableStdDevY;
    }

    public void setWearableStdDevY(double wearableStdDevY) {
        this.wearableStdDevY = wearableStdDevY;
    }

    public double getWearableStdDevZ() {
        return wearableStdDevZ;
    }

    public void setWearableStdDevZ(double wearableStdDevZ) {
        this.wearableStdDevZ = wearableStdDevZ;
    }

    public double getPhoneStdDevX() {
        return phoneStdDevX;
    }

    public void setPhoneStdDevX(double phoneStdDevX) {
        this.phoneStdDevX = phoneStdDevX;
    }

    public double getPhoneStdDevY() {
        return phoneStdDevY;
    }

    public void setPhoneStdDevY(double phoneStdDevY) {
        this.phoneStdDevY = phoneStdDevY;
    }

    public double getPhoneStdDevZ() {
        return phoneStdDevZ;
    }

    public void setPhoneStdDevZ(double phoneStdDevZ) {
        this.phoneStdDevZ = phoneStdDevZ;
    }

    public Features(double wearableStdDevX, double wearableStdDevY, double wearableStdDevZ, double phoneStdDevX, double phoneStdDevY, double phoneStdDevZ) {
        this.wearableStdDevX = wearableStdDevX;
        this.wearableStdDevY = wearableStdDevY;
        this.wearableStdDevZ = wearableStdDevZ;
        this.phoneStdDevX = phoneStdDevX;
        this.phoneStdDevY = phoneStdDevY;
        this.phoneStdDevZ = phoneStdDevZ;
    }

    public Features(){

    }
}
