package pl.polsl.aei.monitorupadkow;

public class Measurements {
    Wearable[] wearable;
    Phone[] phone;

    public Wearable[] getWearable() {
        return wearable;
    }

    public void setWearable(Wearable[] wearable) {
        this.wearable = wearable;
    }

    public Phone[] getPhone() {
        return phone;
    }

    public void setPhone(Phone[] phone) {
        this.phone = phone;
    }

    public Measurements(Wearable[] wearable, Phone[] phone) {
        this.wearable = wearable;
        this.phone = phone;
    }

    public Measurements() {
    }
}
