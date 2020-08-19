package pl.polsl.aei.monitorupadkow;

public class Body {
    Measurements measurements;

    public Measurements getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Measurements measurements) {
        this.measurements = measurements;
    }

    public Body(Measurements measurements) {
        this.measurements = measurements;
    }

    public Body(){

    }
}
