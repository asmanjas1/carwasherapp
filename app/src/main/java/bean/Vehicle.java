package bean;

import java.io.Serializable;

import bean.Consumer;

/**
 * Created by Dell on 8/13/2019.
 */

public class Vehicle {
    private Integer vehicleId;
    private String vehicleName;
    private String vehicleNumber;
    private String vehicleType;
    private Consumer consumer;

    public Vehicle(Integer vehicleId, String vehicleName, String vehicleNumber, String vehicleType, Consumer consumer) {
        this.vehicleId = vehicleId;
        this.vehicleName = vehicleName;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.consumer = consumer;
    }

    public Integer getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Integer vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }
}
