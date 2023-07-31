package com.example.gps;
//Classe principal da reconciliação, responsavel pelas principais funções como verificar se está na primeira reconciliação ou não e realizar as operações
import java.util.ArrayList;
import android.content.Context;
import android.os.AsyncTask;



    public class Google extends AsyncTask<Object,String,String>{
        private String url;
        private String Json;
        private  boolean rec;
        private boolean rec1;
        int aux;
        private Context texto;


//Possui dois construtores, o primeiro, caso seja a primeira reconciliação e o segundo caso nã seja a primeira
        public Google(Context texto,boolean rec,boolean rec1) {
            aux = 0;
            this.texto = texto;
            this.rec1 = rec;
            this.rec = rec1;

        }
        public Google(Context texto,boolean rec1) {
            aux = 1;
            this.texto = texto;
            this.rec1 = rec1;
            rec = false;

        }


        @Override
        protected String doInBackground(Object... objects) {

            url = (String)objects[0];

            GoogleUrl downloadUrl = new GoogleUrl();
            try {
                Json = downloadUrl.readUrl(url);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return Json;
        }


        @Override
        protected void onPostExecute(String s) {
            if(rec == false) {
                if (texto instanceof MainActivity) {
                    ((MainActivity) texto).setJson(s);
                }


                GooglePtPercurso p = new GooglePtPercurso(s);
                ArrayList<PadronizacaoRec> test = (ArrayList<PadronizacaoRec>) p.placedurationspaces();


                if(aux == 0){
                    if (texto instanceof MainActivity) {
                        ((MainActivity) texto).getGoogleLocais(test);
                    }
                }

                int size = test.size();
                double[] y = new double[size];
                double[] val = new double[size];
                double[][] A = new double[1][size];

                for (int i = 0; i < test.size(); i++) {
                    if (i == 0)
                        A[0][i] = 1;
                    else
                        A[0][i] = -1;

                    y[i] = test.get(i).getDuracao();
                    val[i] = 0.0001;

                }

                Rec recMain = new Rec(y, val, A);
                Thread recT = new Thread(recMain, "RecT");
                recT.start();

                try {
                    recT.join(); //Aguarda a reconciliação terminar para só depois mandar as informações para main

                    if (texto instanceof MainActivity) {

                        ((MainActivity) texto).setValor(recMain.getReconciliation());

                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


                String[] directionsList;
                AuxGoogleJson parserasa = new AuxGoogleJson();
                directionsList = parserasa.parseDirections(s); //Vetor com as distancias e tempos

                if (texto instanceof MainActivity) {
                    ((MainActivity) texto).setveloc(directionsList,true);
                }
                if (texto instanceof MainActivity) {
                    ((MainActivity) texto).setDistancia(test.get(1).getDistance());
                }

                if (texto instanceof MainActivity) {
                    ((MainActivity) texto).setTempo(test.get(1).getDuracao());
                }
            }else{
                String[] directionsList;
                AuxGoogleJson parserasa = new AuxGoogleJson();
                directionsList = parserasa.parseDirections(s);

                if (texto instanceof MainActivity) {
                    ((MainActivity) texto).setveloc(directionsList,false);
                }
            }

        }
    }
