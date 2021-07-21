package com.technosales.net.buslocationannouncement.RouteStationListModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FareList {
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
        @SerializedName("route_id")
        @Expose
        private Integer routeId;
        @SerializedName("normal_ticket_rate")
        @Expose
        private Integer normalTicketRate;
        @SerializedName("discounted_ticket_rate")
        @Expose
        private String discountedTicketRate;
        @SerializedName("min_distance")
        @Expose
        private String minDistance;
        @SerializedName("distance_up_to")
        @Expose
        private String distanceUpTo;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getRouteId() {
            return routeId;
        }

        public void setRouteId(Integer routeId) {
            this.routeId = routeId;
        }

        public Integer getNormalTicketRate() {
            return normalTicketRate;
        }

        public void setNormalTicketRate(Integer normalTicketRate) {
            this.normalTicketRate = normalTicketRate;
        }

        public String getDiscountedTicketRate() {
            return discountedTicketRate;
        }

        public void setDiscountedTicketRate(String discountedTicketRate) {
            this.discountedTicketRate = discountedTicketRate;
        }

        public String getMinDistance() {
            return minDistance;
        }

        public void setMinDistance(String minDistance) {
            this.minDistance = minDistance;
        }

        public String getDistanceUpTo() {
            return distanceUpTo;
        }

        public void setDistanceUpTo(String distanceUpTo) {
            this.distanceUpTo = distanceUpTo;
        }

    }
}
