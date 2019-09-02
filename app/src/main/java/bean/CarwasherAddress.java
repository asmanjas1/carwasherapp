package bean;

/**
 * Created by Dell on 8/13/2019.
 */

public class CarwasherAddress {

    private Integer addressId;
    private String addressLine;
    private String locality;
    private String city;
    private String state;
    private Integer pincode;
    private Carwasher carwasher;
    private String addressType;

    public Integer getAddressId() {
        return addressId;
    }

    public void setAddressId(Integer addressId) {
        this.addressId = addressId;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getPincode() {
        return pincode;
    }

    public void setPincode(Integer pincode) {
        this.pincode = pincode;
    }

    public Carwasher getCarwasher() {
        return carwasher;
    }

    public void setCarwasher(Carwasher carwasher) {
        this.carwasher = carwasher;
    }

    public String getAddressType() {
        return addressType;
    }

    public void setAddressType(String addressType) {
        this.addressType = addressType;
    }
}
