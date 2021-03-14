package com.technosales.net.buslocationannouncement.pojo;

public class PassengerCountList {
        public PassengerCountList() {
        }
        public Integer id;
        public Integer passenger_station_position;
        public String passenger_direction;


        @Override
        public String toString() {
                return "PassengerCountList{" +
                        "id=" + id +
                        ", passenger_station_position=" + passenger_station_position +
                        ", passenger_direction='" + passenger_direction + '\'' +
                        '}';
        }
}
