package com.technosales.net.buslocationannouncement.RouteStationListModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeviceLoginModel {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("unique_id")
        @Expose
        private String uniqueId;
        @SerializedName("income")
        @Expose
        private Integer income;
        @SerializedName("ticket")
        @Expose
        private Integer ticket;
        @SerializedName("trip")
        @Expose
        private Integer trip;
        @SerializedName("current_passenger_number")
        @Expose
        private Integer currentPassengerNumber;
        @SerializedName("current_helper")
        @Expose
        private Integer currentHelper;
        @SerializedName("mac_address")
        @Expose
        private String macAddress;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("route_id")
        @Expose
        private Integer routeId;
        @SerializedName("vehicle_name")
        @Expose
        private String vehicleName;
        @SerializedName("sim_number")
        @Expose
        private Object simNumber;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getUniqueId() {
            return uniqueId;
        }

        public void setUniqueId(String uniqueId) {
            this.uniqueId = uniqueId;
        }

        public Integer getIncome() {
            return income;
        }

        public void setIncome(Integer income) {
            this.income = income;
        }

        public Integer getTicket() {
            return ticket;
        }

        public void setTicket(Integer ticket) {
            this.ticket = ticket;
        }

        public Integer getTrip() {
            return trip;
        }

        public void setTrip(Integer trip) {
            this.trip = trip;
        }

        public Integer getCurrentPassengerNumber() {
            return currentPassengerNumber;
        }

        public void setCurrentPassengerNumber(Integer currentPassengerNumber) {
            this.currentPassengerNumber = currentPassengerNumber;
        }

        public Integer getCurrentHelper() {
            return currentHelper;
        }

        public void setCurrentHelper(Integer currentHelper) {
            this.currentHelper = currentHelper;
        }

        public String getMacAddress() {
            return macAddress;
        }

        public void setMacAddress(String macAddress) {
            this.macAddress = macAddress;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public Integer getRouteId() {
            return routeId;
        }

        public void setRouteId(Integer routeId) {
            this.routeId = routeId;
        }

        public String getVehicleName() {
            return vehicleName;
        }

        public void setVehicleName(String vehicleName) {
            this.vehicleName = vehicleName;
        }

        public Object getSimNumber() {
            return simNumber;
        }

        public void setSimNumber(Object simNumber) {
            this.simNumber = simNumber;
        }
    }
}
