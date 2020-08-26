package pl.polsl.aei.monitorupadkow;

public class Request {

    Measurements measurements;
    Features features;

    public Measurements getMeasurements() {
        return measurements;
    }

    public void setMeasurements(Measurements measurements) {
        this.measurements = measurements;
    }

    public Features getFeatures() {
        return features;
    }

    public void setFeatures(Features features) {
        this.features = features;
    }

    public Request(Measurements measurements, Features features) {
        this.measurements = measurements;
        this.features = features;
    }

    public Request(){

    }
}
