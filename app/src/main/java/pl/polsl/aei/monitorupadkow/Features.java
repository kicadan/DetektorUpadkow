package pl.polsl.aei.monitorupadkow;

class Features {
    Wearable wearable;
    Phone phone;

    public Wearable getWearable() {
        return wearable;
    }

    public void setWearable(Wearable wearable) {
        this.wearable = wearable;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }

    public Features(Wearable wearable, Phone phone) {
        this.wearable = wearable;
        this.phone = phone;
    }

    public Features(){

    }
}
