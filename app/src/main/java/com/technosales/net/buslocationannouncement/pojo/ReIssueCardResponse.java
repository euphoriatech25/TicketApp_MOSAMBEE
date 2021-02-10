package com.technosales.net.buslocationannouncement.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ReIssueCardResponse {
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

        @SerializedName("card")
        @Expose
        private Card card;
        @SerializedName("previous_hash")
        @Expose
        private String previousHash;

        public Card getCard() {
            return card;
        }

        public void setCard(Card card) {
            this.card = card;
        }

        public String getPreviousHash() {
            return previousHash;
        }

        public void setPreviousHash(String previousHash) {
            this.previousHash = previousHash;
        }

    }

    public class Card {

        @SerializedName("id")
        @Expose
        private Integer id;
        @SerializedName("identificationId")
        @Expose
        private String identificationId;
        @SerializedName("mobileNo")
        @Expose
        private String mobileNo;
        @SerializedName("firstName")
        @Expose
        private String firstName;
        @SerializedName("middleName")
        @Expose
        private Object middleName;
        @SerializedName("lastName")
        @Expose
        private String lastName;
        @SerializedName("contactNo")
        @Expose
        private String contactNo;
        @SerializedName("emailAddress")
        @Expose
        private String emailAddress;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("userType")
        @Expose
        private String userType;
        @SerializedName("deviceId")
        @Expose
        private String deviceId;
        @SerializedName("deviceUserId")
        @Expose
        private String deviceUserId;
        @SerializedName("amount")
        @Expose
        private Integer amount;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getIdentificationId() {
            return identificationId;
        }

        public void setIdentificationId(String identificationId) {
            this.identificationId = identificationId;
        }

        public String getMobileNo() {
            return mobileNo;
        }

        public void setMobileNo(String mobileNo) {
            this.mobileNo = mobileNo;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public Object getMiddleName() {
            return middleName;
        }

        public void setMiddleName(Object middleName) {
            this.middleName = middleName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getContactNo() {
            return contactNo;
        }

        public void setContactNo(String contactNo) {
            this.contactNo = contactNo;
        }

        public String getEmailAddress() {
            return emailAddress;
        }

        public void setEmailAddress(String emailAddress) {
            this.emailAddress = emailAddress;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public void setDeviceId(String deviceId) {
            this.deviceId = deviceId;
        }

        public String getDeviceUserId() {
            return deviceUserId;
        }

        public void setDeviceUserId(String deviceUserId) {
            this.deviceUserId = deviceUserId;
        }

        public Integer getAmount() {
            return amount;
        }

        public void setAmount(Integer amount) {
            this.amount = amount;
        }

    }
}