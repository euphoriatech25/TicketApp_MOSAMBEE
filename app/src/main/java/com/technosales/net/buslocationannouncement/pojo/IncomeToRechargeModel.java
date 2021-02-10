package com.technosales.net.buslocationannouncement.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IncomeToRechargeModel {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("meta")
    @Expose
    private String meta;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMeta() {
        return meta;
    }

    public void setMeta(String meta) {
        this.meta = meta;
    }
    public class Data {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("device_id")
        @Expose
        private String deviceId;
        @SerializedName("income")
        @Expose
        private Integer income;
        @SerializedName("recharge")
        @Expose
        private Integer recharge;
        @SerializedName("ticket")
        @Expose
        private Integer ticket;
        @SerializedName("trip")
        @Expose
        private Integer trip;
        @SerializedName("current_helper")
        @Expose
        private Integer currentHelper;
        @SerializedName("user_id")
        @Expose
        private Integer userId;
        @SerializedName("bus_id")
        @Expose
        private Integer busId;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public Integer getIncome() {
            return income;
        }

        public void setIncome(Integer income) {
            this.income = income;
        }

        public Integer getRecharge() {
            return recharge;
        }

        public void setRecharge(Integer recharge) {
            this.recharge = recharge;
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

        public Integer getCurrentHelper() {
            return currentHelper;
        }

        public void setCurrentHelper(Integer currentHelper) {
            this.currentHelper = currentHelper;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Integer getBusId() {
            return busId;
        }

        public void setBusId(Integer busId) {
            this.busId = busId;
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

    }
}
