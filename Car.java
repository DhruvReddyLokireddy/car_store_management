class Car {
    private final String id;
    private final String make;
    private final String model;
    private final double price;
    private final String feature;

    public Car(String id, String make, String model, double price, String feature) {
        this.id = id;
        this.make = make;
        this.model = model;
        this.price = price;
        this.feature = feature;
    }

    public String getId() {
        return id;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public double getPrice() {
        return price;
    }

    public String getFeature() {
        return feature;
    }
}