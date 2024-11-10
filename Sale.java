class Sale {
    private final String saleId;
    private final String carId;
    private final String customerId;
    private final double totalPrice;

    public Sale(String saleId, String carId, String customerId, double totalPrice) {
        this.saleId = saleId;
        this.carId = carId;
        this.customerId = customerId;
        this.totalPrice = totalPrice;
    }

    public String getSaleId() {
        return saleId;
    }

    public String getCarId() {
        return carId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }
}