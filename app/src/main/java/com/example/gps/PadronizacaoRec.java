package com.example.gps;
//Classe responsável por manter e acessar os valores de duracao e distancia
    public class PadronizacaoRec {
        private double duracao;
        private double distancia;


        public PadronizacaoRec(double duracao, double distancia) {
            this.duracao = duracao;
            this.distancia = distancia;
        }

        public double getDistance() {

            return distancia;
        }


        public double getDuracao() {

            return duracao;
        }

        public void setDuracao(double duracao) {

            this.duracao = duracao;
        }
    }
