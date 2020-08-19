package pl.polsl.aei.monitorupadkow;

class Phone {
    double X;
    double Y;
    double Z;

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

    public Phone(double x, double y, double z) {
        X = x;
        Y = y;
        Z = z;
    }

    public Phone(){
    }
}
