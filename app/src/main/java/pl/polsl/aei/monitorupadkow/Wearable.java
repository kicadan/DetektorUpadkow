package pl.polsl.aei.monitorupadkow;

class Wearable {
    Integer X;
    Integer Y;
    Integer Z;

    double stdDevX;
    double stdDevY;
    double stdDevZ;

    double maxMagnitude;

    public Integer getX() {
        return X;
    }

    public void setX(Integer x) {
        X = x;
    }

    public Integer getY() {
        return Y;
    }

    public void setY(Integer y) {
        Y = y;
    }

    public Integer getZ() {
        return Z;
    }

    public void setZ(Integer z) {
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

    public Wearable(Integer x, Integer y, Integer z, double stdDevX, double stdDevY, double stdDevZ, double maxMagnitude) {
        X = x;
        Y = y;
        Z = z;
        this.stdDevX = stdDevX;
        this.stdDevY = stdDevY;
        this.stdDevZ = stdDevZ;
        this.maxMagnitude = maxMagnitude;
    }

    public Wearable() {
    }
}
