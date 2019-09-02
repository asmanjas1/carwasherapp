package bean;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Dell on 8/13/2019.
 */

public class Carwasher {
    private Integer carwasherId;
    @SerializedName("name")
    private String name;
    @SerializedName("email")
    private String email;
    @SerializedName("phoneNumber")
    private String phoneNumber;
    @SerializedName("password")
    private String password;
    private String registrationDate;
    private String lastUpdateDate;
    private CarwasherAddress carwasherAddress;

    public Integer getCarwasherId() {
        return carwasherId;
    }

    public void setCarwasherId(Integer carwasherId) {
        this.carwasherId = carwasherId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public CarwasherAddress getCarwasherAddress() {
        return carwasherAddress;
    }

    public void setCarwasherAddress(CarwasherAddress carwasherAddress) {
        this.carwasherAddress = carwasherAddress;
    }
}
