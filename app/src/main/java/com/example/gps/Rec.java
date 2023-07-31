package com.example.gps;
//Classe responsável por inicializar os vetores e matriz utilizados da reconciliação
import java.util.ArrayList;

    public class Rec extends Thread{
        private ArrayList<Double> rec;
        private double[] C;
        private double[] S;
        private double[][] B;

        public Rec(double[] C, double[] S, double[][] B) {
            this.C = C;
            this.S = S;
            this.B = B;
        }

        @Override
        public void run() {

            Reconciliation r = new Reconciliation(C, S, B);
            rec =  r.returnMatrix(r.getReconciledFlow());
        }

        public ArrayList<Double>  getReconciliation() {
            return rec;

        }
    }