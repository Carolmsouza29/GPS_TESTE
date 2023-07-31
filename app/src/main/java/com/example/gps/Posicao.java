package com.example.gps;
//Clase responsável por manter e acessar as informações de latitude e longitude
public class Posicao {
        private double latitude;
        private double longitude;
        private long timestamp;

    public Posicao(double latitude, double longitude, long timestamp) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.timestamp = timestamp;
    }

        public double getLatitude() {

            return latitude;
        }
        public double getLongitude() {

            return longitude;
        }
        public void setLatitude(double latitude) {

            this.latitude = latitude;
        }
        public void setLongitude(double longitude) {

            this.longitude = longitude;
        }
        public Long getTimestamp() {

            return timestamp;
        }
        public void setTimestamp(long timestamp) {
            this.timestamp = timestamp;
        }
}
