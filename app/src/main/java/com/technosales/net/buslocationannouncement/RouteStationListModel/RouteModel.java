package com.technosales.net.buslocationannouncement.RouteStationListModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RouteModel {
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
        @SerializedName("route_number")
        @Expose
        private Integer routeNumber;
        @SerializedName("route_name")
        @Expose
        private String routeName;
        @SerializedName("route_nepali")
        @Expose
        private String routeNepali;
        @SerializedName("num_bus")
        @Expose
        private Integer numBus;
        @SerializedName("start")
        @Expose
        private String start;
        @SerializedName("end")
        @Expose
        private String end;
        @SerializedName("pivot")
        @Expose
        private Pivot pivot;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getRouteNumber() {
            return routeNumber;
        }

        public void setRouteNumber(Integer routeNumber) {
            this.routeNumber = routeNumber;
        }

        public String getRouteName() {
            return routeName;
        }

        public void setRouteName(String routeName) {
            this.routeName = routeName;
        }

        public String getRouteNepali() {
            return routeNepali;
        }

        public void setRouteNepali(String routeNepali) {
            this.routeNepali = routeNepali;
        }

        public Integer getNumBus() {
            return numBus;
        }

        public void setNumBus(Integer numBus) {
            this.numBus = numBus;
        }

        public String getStart() {
            return start;
        }

        public void setStart(String start) {
            this.start = start;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public Pivot getPivot() {
            return pivot;
        }

//        public void setPivot(Pivot pivot) {
//            this.pivot = pivot;
//        }

    }

    private class Pivot {
        @SerializedName("pos_machine_id")
        @Expose
        private String posMachineId;
        @SerializedName("route_id")
        @Expose
        private Integer routeId;

        public String getPosMachineId() {
            return posMachineId;
        }

        public void setPosMachineId(String posMachineId) {
            this.posMachineId = posMachineId;
        }

        public Integer getRouteId() {
            return routeId;
        }

        public void setRouteId(Integer routeId) {
            this.routeId = routeId;
        }
    }
}
