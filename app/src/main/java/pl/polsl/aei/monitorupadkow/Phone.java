package pl.polsl.aei.monitorupadkow;

class Phone {
    double X;
    double Y;
    double Z;

    double stdDevX;
    double stdDevY;
    double stdDevZ;

    double maxMagnitude;

    public double getX() {
        return X;
    }

    public void setX(double x) {
        X = x;
    }

    public double getY() {
        return Y;
    }

    public void setY(double y) {
        Y = y;
    }

    public double getZ() {
        return Z;
    }

    public void setZ(double z) {
        Z = z;
    }

    public double getStdDevX() {
        return stdDevX;
    }

    public void setStdDevX(double stdDevX) {
        this.stdDevX = stdDevX;
    }

    public double getStdDevY() {
        return stdDevY;
    }

    public void setStdDevY(double stdDevY) {
        this.stdDevY = stdDevY;
    }

    public double getStdDevZ() {
        return stdDevZ;
    }

    public void setStdDevZ(double stdDevZ) {
        this.stdDevZ = stdDevZ;
    }

    public double getMaxMagnitude() {
        return maxMagnitude;
    }

    public void setMaxMagnitude(double maxMagnitude) {
        this.maxMagnitude = maxMagnitude;
    }

    public Phone(double x, double y, double z, double stdDevX, double stdDevY, double stdDevZ, double maxMagnitude) {
        X = x;
        Y = y;
        Z = z;
        this.stdDevX = stdDevX;
        this.stdDevY = stdDevY;
        this.stdDevZ = stdDevZ;
        this.maxMagnitude = maxMagnitude;
    }

    public Phone(){
    }
}
