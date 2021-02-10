package com.technosales.net.buslocationannouncement.transactionstatement;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionStatementModel {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public class Datum {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("helper_id")
        @Expose
        private Integer helperId;
        @SerializedName("ticket_id")
        @Expose
        private String ticketId;
        @SerializedName("device_id")
        @Expose
        private String deviceId;
        @SerializedName("transactionType")
        @Expose
        private String transactionType;
        @SerializedName("userType")
        @Expose
        private String userType;
        @SerializedName("passenger_id")
        @Expose
        private String passengerId;
        @SerializedName("transactionMedium")
        @Expose
        private String transactionMedium;
        @SerializedName("transactionAmount")
        @Expose
        private String transactionAmount;
        @SerializedName("transactionFee")
        @Expose
        private String transactionFee;
        @SerializedName("transactionCommission")
        @Expose
        private String transactionCommission;
        @SerializedName("isOnline")
        @Expose
        private String isOnline;
        @SerializedName("offlineRefId")
        @Expose
        private String offlineRefId;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("referenceId")
        @Expose
        private String referenceId;
        @SerializedName("referenceHash")
        @Expose
        private String referenceHash;
        @SerializedName("remarks")
        @Expose
        private String remarks;
        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("lng")
        @Expose
        private String lng;
        @SerializedName("device_time")
        @Expose
        private String deviceTime;
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

        public Integer getHelperId() {
            return helperId;
        }

        public void setHelperId(Integer helperId) {
            this.helperId = helperId;
        }

        public String getTicketId() {
            return ticketId;
        }

        public void setTicketId(String ticketId) {
            this.ticketId = ticketId;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
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

        public String getPassengerId() {
            return passengerId;
        }

        public void setPassengerId(String passengerId) {
            this.passengerId = passengerId;
        }

        public String getTransactionMedium() {
            return transactionMedium;
        }

        public void setTransactionMedium(String transactionMedium) {
            this.transactionMedium = transactionMedium;
        }

        public String getTransactionAmount() {
            return transactionAmount;
        }

        public void setTransactionAmount(String transactionAmount) {
            this.transactionAmount = transactionAmount;
        }

        public String getTransactionFee() {
            return transactionFee;
        }

        public void setTransactionFee(String transactionFee) {
            this.transactionFee = transactionFee;
        }

        public String getTransactionCommission() {
            return transactionCommission;
        }

        public void setTransactionCommission(String transactionCommission) {
            this.transactionCommission = transactionCommission;
        }

        public String getIsOnline() {
            return isOnline;
        }

        public void setIsOnline(String isOnline) {
            this.isOnline = isOnline;
        }

        public String getOfflineRefId() {
            return offlineRefId;
        }

        public void setOfflineRefId(String offlineRefId) {
            this.offlineRefId = offlineRefId;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(String referenceId) {
            this.referenceId = referenceId;
        }

        public String getReferenceHash() {
            return referenceHash;
        }

        public void setReferenceHash(String referenceHash) {
            this.referenceHash = referenceHash;
        }

        public String getRemarks() {
            return remarks;
        }

        public void setRemarks(String remarks) {
            this.remarks = remarks;
        }

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }

        public String getDeviceTime() {
            return deviceTime;
        }

        public void setDeviceTime(String deviceTime) {
            this.deviceTime = deviceTime;
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
