package com.technosales.net.buslocationannouncement.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionModel {
    @SerializedName("transactionType")
    @Expose
    private String transactionType;
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
    private Boolean isOnline;
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

     @SerializedName("device_id")
    @Expose
    private String device_id;
     @SerializedName("helper_id")
    @Expose
    private String helper_id;
     @SerializedName("passenger_id")
    @Expose
    private String passenger_id;


    public TransactionModel(String transactionType, String transactionMedium, String transactionAmount, String transactionFee, String transactionCommission, Boolean isOnline, String offlineRefId, String status, String referenceId, String referenceHash, String device_id, String helper_id, String passenger_id) {
        this.transactionType = transactionType;
        this.transactionMedium = transactionMedium;
        this.transactionAmount = transactionAmount;
        this.transactionFee = transactionFee;
        this.transactionCommission = transactionCommission;
        this.isOnline = isOnline;
        this.offlineRefId = offlineRefId;
        this.status = status;
        this.referenceId = referenceId;
        this.referenceHash = referenceHash;
        this.device_id = device_id;
        this.helper_id = helper_id;
        this.passenger_id = passenger_id;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
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
        this.transactionCommission = transactionCommission; }

    public Boolean getOnline() {
        return isOnline;
    }

    public void setOnline(Boolean online) {
        isOnline = online;
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

    public String getDevice_id() {
        return device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    public String getHelper_id() {
        return helper_id;
    }

    public void setHelper_id(String helper_id) {
        this.helper_id = helper_id;
    }

    public String getPassenger_id() {
        return passenger_id;
    }

    public void setPassenger_id(String passenger_id) {
        this.passenger_id = passenger_id;
    }




    public class TransactionResponse {
            @SerializedName("device_id")
            @Expose
            private String deviceId;
            @SerializedName("helper_id")
            @Expose
            private String helperId;
            @SerializedName("isOnline")
            @Expose
            private Boolean isOnline;
            @SerializedName("offlineRefId")
            @Expose
            private String offlineRefId;
            @SerializedName("passenger_id")
            @Expose
            private String passengerId;
            @SerializedName("referenceHash")
            @Expose
            private String referenceHash;
            @SerializedName("referenceId")
            @Expose
            private String referenceId;
            @SerializedName("status")
            @Expose
            private String status;
            @SerializedName("transactionAmount")
            @Expose
            private String transactionAmount;
            @SerializedName("transactionCommission")
            @Expose
            private String transactionCommission;
            @SerializedName("transactionFee")
            @Expose
            private String transactionFee;
            @SerializedName("transactionMedium")
            @Expose
            private String transactionMedium;
            @SerializedName("transactionType")
            @Expose
            private String transactionType;
            @SerializedName("updated_at")
            @Expose
            private String updatedAt;
            @SerializedName("created_at")
            @Expose
            private String createdAt;
            @SerializedName("id")
            @Expose
            private Integer id;

            public String getDeviceId() {
                return deviceId;
            }

            public void setDeviceId(String deviceId) {
                this.deviceId = deviceId;
            }

            public String getHelperId() {
                return helperId;
            }

            public void setHelperId(String helperId) {
                this.helperId = helperId;
            }

            public Boolean getIsOnline() {
                return isOnline;
            }

            public void setIsOnline(Boolean isOnline) {
                this.isOnline = isOnline;
            }

            public String getOfflineRefId() {
                return offlineRefId;
            }

            public void setOfflineRefId(String offlineRefId) {
                this.offlineRefId = offlineRefId;
            }

            public String getPassengerId() {
                return passengerId;
            }

            public void setPassengerId(String passengerId) {
                this.passengerId = passengerId;
            }

            public String getReferenceHash() {
                return referenceHash;
            }

            public void setReferenceHash(String referenceHash) {
                this.referenceHash = referenceHash;
            }

            public String getReferenceId() {
                return referenceId;
            }

            public void setReferenceId(String referenceId) {
                this.referenceId = referenceId;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getTransactionAmount() {
                return transactionAmount;
            }

            public void setTransactionAmount(String transactionAmount) {
                this.transactionAmount = transactionAmount;
            }

            public String getTransactionCommission() {
                return transactionCommission;
            }

            public void setTransactionCommission(String transactionCommission) {
                this.transactionCommission = transactionCommission;
            }

            public String getTransactionFee() {
                return transactionFee;
            }

            public void setTransactionFee(String transactionFee) {
                this.transactionFee = transactionFee;
            }

            public String getTransactionMedium() {
                return transactionMedium;
            }

            public void setTransactionMedium(String transactionMedium) {
                this.transactionMedium = transactionMedium;
            }

            public String getTransactionType() {
                return transactionType;
            }

            public void setTransactionType(String transactionType) {
                this.transactionType = transactionType;
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
