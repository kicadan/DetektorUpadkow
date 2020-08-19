package pl.polsl.aei.monitorupadkow;

class Wearable {
    Integer X;
    Integer Y;
    Integer Z;

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

    public Wearable(Integer x, Integer y, Integer z) {
        X = x;
        Y = y;
        Z = z;
    }

    public Wearable() {
    }
}
