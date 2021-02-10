package com.technosales.net.buslocationannouncement.userregistration;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CreateAccountModel {

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
    private String middleName;
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


    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lng")
    @Expose
    private String lng;

    @SerializedName("device_time")
    @Expose
    private String device_time;

    @SerializedName("ticket_id")
    @Expose
    private String ticketId;

    public CreateAccountModel(String identificationId, String mobileNo, String firstName, String middleName, String lastName, String contactNo, String emailAddress, String address, String userType, String deviceId, String deviceUserId, String lat, String lng, String device_time, String ticketId) {
        this.identificationId = identificationId;
        this.mobileNo = mobileNo;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.contactNo = contactNo;
        this.emailAddress = emailAddress;
        this.address = address;
        this.userType = userType;
        this.deviceId = deviceId;
        this.deviceUserId = deviceUserId;
        this.lat = lat;
        this.lng = lng;
        this.device_time = device_time;
        this.ticketId = ticketId;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
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


    public class CreateAccountResponse {

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
            private String middleName;
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
            @SerializedName("id")
            @Expose
            private Integer id;
            @SerializedName("referenceHash")
            @Expose
            private String referenceHash;

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

            public String getMiddleName() {
                return middleName;
            }

            public void setMiddleName(String middleName) {
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

            public Integer getId() {
                return id;
            }

            public void setId(Integer id) {
                this.id = id;
            }

            public String getReferenceHash() {
                return referenceHash;
            }

            public void setReferenceHash(String referenceHash) {
                this.referenceHash = referenceHash;
            }


        }
    }
}
