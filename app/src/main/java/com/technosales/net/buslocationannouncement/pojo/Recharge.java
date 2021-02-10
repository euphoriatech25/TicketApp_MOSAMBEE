package com.technosales.net.buslocationannouncement.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Recharge {

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

        @SerializedName("transaction")
        @Expose
        private Transaction transaction;
        @SerializedName("helper_amount")
        @Expose
        private Integer helperAmount;
        @SerializedName("passenger_amount")
        @Expose
        private Integer passengerAmount;

        public Transaction getTransaction() {
            return transaction;
        }

        public void setTransaction(Transaction transaction) {
            this.transaction = transaction;
        }

        public Integer getHelperAmount() {
            return helperAmount;
        }

        public void setHelperAmount(Integer helperAmount) {
            this.helperAmount = helperAmount;
        }

        public Integer getPassengerAmount() {
            return passengerAmount;
        }

        public void setPassengerAmount(Integer passengerAmount) {
            this.passengerAmount = passengerAmount;
        }

    }
    public class Transaction {

        @SerializedName("transactionCommission")
        @Expose
        private Object transactionCommission;
        @SerializedName("referenceId")
        @Expose
        private Object referenceId;
        @SerializedName("transactionType")
        @Expose
        private String transactionType;
        @SerializedName("userType")
        @Expose
        private String userType;
        @SerializedName("isOnline")
        @Expose
        private String isOnline;
        @SerializedName("transactionMedium")
        @Expose
        private String transactionMedium;
        @SerializedName("ticket_id")
        @Expose
        private String ticketId;
        @SerializedName("referenceHash")
        @Expose
        private Object referenceHash;
        @SerializedName("lng")
        @Expose
        private String lng;
        @SerializedName("device_id")
        @Expose
        private String deviceId;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("helper_id")
        @Expose
        private String helperId;
        @SerializedName("transactionFee")
        @Expose
        private Object transactionFee;
        @SerializedName("transactionAmount")
        @Expose
        private String transactionAmount;
        @SerializedName("device_time")
        @Expose
        private String deviceTime;
        @SerializedName("passenger_id")
        @Expose
        private String passengerId;
        @SerializedName("offlineRefId")
        @Expose
        private String offlineRefId;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("id")
        @Expose
        private Integer id;

        public Object getTransactionCommission() {
            return transactionCommission;
        }

        public void setTransactionCommission(Object transactionCommission) {
            this.transactionCommission = transactionCommission;
        }

        public Object getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(Object referenceId) {
            this.referenceId = referenceId;
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(String isOnline) {
            this.isOnline = isOnline;
        }

        public String getTransactionMedium() {
            return transactionMedium;
        }

        public void setTransactionMedium(String transactionMedium) {
            this.transactionMedium = transactionMedium;
        }

        public String getTicketId() {
            return ticketId;
        }

        public void setTicketId(String ticketId) {
            this.ticketId = ticketId;
        }

        public Object getReferenceHash() {
            return referenceHash;
        }

        public void setReferenceHash(Object referenceHash) {
            this.referenceHash = referenceHash;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getHelperId() {
            return helperId;
        }

        public void setHelperId(String helperId) {
            this.helperId = helperId;
        }

        public Object getTransactionFee() {
            return transactionFee;
        }

        public void setTransactionFee(Object transactionFee) {
            this.transactionFee = transactionFee;
        }

        public String getTransactionAmount() {
            return transactionAmount;
        }

        public void setTransactionAmount(String transactionAmount) {
            this.transactionAmount = transactionAmount;
        }

        public String getDeviceTime() {
            return deviceTime;
        }

        public void setDeviceTime(String deviceTime) {
            this.deviceTime = deviceTime;
        }

        public String getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(String passengerId) {
            this.passengerId = passengerId;
        }

        public String getOfflineRefId() {
            return offlineRefId;
        }

        public void setOfflineRefId(String offlineRefId) {
            this.offlineRefId = offlineRefId;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

    }
}
