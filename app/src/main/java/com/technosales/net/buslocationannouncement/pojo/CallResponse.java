package com.technosales.net.buslocationannouncement.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CallResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<Object> data = null;
    @SerializedName("message")
    @Expose
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }



    public class GetServerNumber{

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

            @SerializedName("server_number")
            @Expose
            private String serverNumber;

            public String getServerNumber() {
                return serverNumber;
            }

            public void setServerNumber(String serverNumber) {
                this.serverNumber = serverNumber;
            }

        }
    }
}

