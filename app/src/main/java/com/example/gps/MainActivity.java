package com.example.gps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    String valorRec;
    TextView latitudeView, longitudeView, reconcView, tempoTotalView, distTotalView, DistPercorridaView, velociAtualView, velocEsperadaView, CombConsumidoView, tempPercorridoView;
    Button updatesB, iniciarB;

    com.example.gps.minhaLocalizacao minhaLocalizacao;
    Thread minhaLocThread;
    private double latitudeUfla = -21.2331532026257369;
    private double longitudeUfla = -45.004392675340775;
    private int aux = 0;
    private Criptografia crip;
    private FirebaseFirestore bancoDados;
    private PublicKey publicKey;
    private double tempoTotal = 0;
    private JSONObject jsonObject;
    private Administrador admin;
    private double distanciaCalculo;
    private double tempoCalculo;

    int cont1 =0;


    private String jsonTeste;
    private ArrayList<PadronizacaoRec> googleLocais;

        @SuppressLint("MissingInflatedId")
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            instance = this;
            crip = new Criptografia();
            bancoDados = FirebaseFirestore.getInstance();
            admin = new Administrador();
            getPublicKeyFromFirestore();
            FirebaseApp.initializeApp(this); // Inicialize o Firebase App (caso ainda não tenha sido feito)
            latitudeView = findViewById(R.id.latitudeView);
            longitudeView = findViewById(R.id.longitudeView);
            reconcView = findViewById(R.id.reconcView);
            tempoTotalView = findViewById(R.id.tempoTotalView);
            distTotalView = findViewById(R.id.distTotalView);
            velociAtualView = findViewById(R.id.velociAtualView);
            velocEsperadaView = findViewById(R.id.velocEsperadaView);
            CombConsumidoView = findViewById(R.id.CombConsumidoView);
            updatesB = findViewById(R.id.updatesB);
            iniciarB = findViewById(R.id.iniciarB);
            DistPercorridaView = findViewById(R.id.DistPercorridaView);
            tempPercorridoView = findViewById(R.id.tempPercorridoView);
            updatesB.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    if (minhaLocThread == null || !minhaLocThread.isAlive()) {

                        Context threadContext = MainActivity.this;
                        minhaLocalizacao = new minhaLocalizacao(threadContext);
                        minhaLocalizacao.setNome("1a");
                        minhaLocThread = new Thread(minhaLocalizacao, "MinhalOCALIZACAOThread");
                        minhaLocThread.start();


                    }
                }
            });

            iniciarB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (minhaLocalizacao != null) {
                        if (aux == 0) {
                            double lat = minhaLocalizacao.getPosAtual().getLatitude();
                            double longi = minhaLocalizacao.getPosAtual().getLongitude();
                            minhaLocalizacao.setPosicaoI(new Posicao(lat, longi, 0));
                            minhaLocalizacao.getPosicaoI().setTimestamp(System.currentTimeMillis() / 1000);
                            aux = 1;
                            //mostrarGoogleMaps(minhaLocalizacao.getPosAtual().getLatitude(),minhaLocalizacao.getPosAtual().getLongitude());
                        }
                    }
                }
            });

        }



    public void Tela() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                latitudeView.setText(String.valueOf(minhaLocalizacao.getPosAtual().getLatitude()));
                longitudeView.setText(String.valueOf(minhaLocalizacao.getPosAtual().getLongitude()));

                Google googleRoutes = new Google(instance, false);
                googleRoutes.execute(getDirectionsUrl());

                if (aux != 0) {
                    Google googleRoutess = new Google(instance, true, true);
                    googleRoutess.execute(getDirectionsUrlNow());
                    long time = System.currentTimeMillis() / 1000 - minhaLocalizacao.getPosAtual().getTimestamp();
                    tempPercorridoView.setText(String.valueOf(time));
                }

                //transferir da api do google para a localização
                distTotalView.setText(String.valueOf(minhaLocalizacao.getDisTotal()));
                tempoTotalView.setText(String.valueOf(minhaLocalizacao.getTempo()));
                reconcView.setText(String.valueOf(valorRec));
                velociAtualView.setText(String.valueOf(minhaLocalizacao.getVelocidade()));
                /*tv_ConsumoDeCombustivel.setText(String.valueOf(calcularConsumoCombustivel(minhaLocalizacao.getVelocidade())));*/
                DistPercorridaView.setText(String.valueOf(minhaLocalizacao.getDisTotal()));
                if (tempoTotal != 0) {
                    velocEsperadaView.setText(String.valueOf((tempoCalculo/distanciaCalculo) * 3.6));
                }

                String msgOriginal = "{\n" +
                        "  \"Lat\": " + minhaLocalizacao.getPosAtual().getLatitude() + ",\n" +
                        "  \"Log\": " + minhaLocalizacao.getPosAtual().getLongitude() + ",\n" +
                        "  \"Duracao\": " + minhaLocalizacao.getTempo()  + ",\n" +
                        "  \"TempoTotal\": " + tempoTotal + ",\n" +
                        "  \"vel\": " + minhaLocalizacao.getVelocidade() + "\n" +
                        "}";
                cont1 = cont1 + 1;

                dataBaseWrite(crip.criptografarTexto(msgOriginal,publicKey),admin.getFuncionarios().get(0));
                String nova = readFromFirebase("Usuarios",admin.getFuncionarios().get(0));
                if(nova != "") {
                    String resultadoCrypto = crip.descriptografarTexto(nova);
                    //CombConsumidoView.setText(resultadoCrypto);
                    try {
                        jsonObject = new JSONObject(resultadoCrypto);
                        CombConsumidoView.setText(jsonObject.getString("Lat") +"\n"+ jsonObject.getString("Log")+"\n"+ jsonObject.getString("vel")+ "\n"+jsonObject.getString("Duracao")+ "\n"+jsonObject.getString("TempoTotal"));

                        if(minhaLocalizacao.getTempo() - jsonObject.getDouble("Duracao") <= 0){
                            tempoTotal = minhaLocalizacao.getTempo();
                        }else{
                            tempoTotal = jsonObject.getInt("Duracao");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
    public void updateTela() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                cont1 = cont1 + 1;
               String msgOriginal = "" + cont1;
                crip.criptografarTexto(msgOriginal,publicKey);
                dataBaseWrite(crip.criptografarTexto(msgOriginal,publicKey),admin.getFuncionarios().get(0));

            }
        });
    }
    public void dataBaseWrite(String textoCriptografado, String document) {


        cont1++;
        String name = document;

        Map<String, Object> user = new HashMap<>();
        user.put("PublicKey",crip.getPublicKeyString());
        user.put("MsgCriptografada",textoCriptografado);



        // Add a new document with a generated ID in "novoTeste" collection,  código disponibilizado no firebase para escrita
        bancoDados.collection("Usuarios")
                .document(name)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("valorReal", "DocumentSnapshot added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("valorReal", "Error adding document", e);
                    }
                });
    }
    String teste = "";


    public String readFromFirebase(String collectionName,String documentNames) {
        String documentName =documentNames;
        final String[] textoCriptografado = {""};

        String nova = "";

        bancoDados.collection(collectionName)
                .document(documentNames)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("valorReal", "Listen failed.", e);
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists()) {

                            Log.d("valorReal", "Current data: " + documentSnapshot.getData());
                            Object criptografadosObj = documentSnapshot.get("MsgCriptografada");
                            if (criptografadosObj != null) {
                                teste =  criptografadosObj.toString();
                                crip.descriptografarTexto(textoCriptografado[0]);
                                Log.d("valorRealdovalor", "Current data: " + teste);

                            }

                        } else {
                            Log.d("valorReal", "Current data: null");
                        }
                    }
                });

        String resultado;
        //resultado = textoCriptografado[0].toString();

        Log.d("valorRealdovalor1", "Current data: " + teste);
        String Ajuda = teste;
        return Ajuda;
    }



    public void getPublicKeyFromFirestore() {
        // Acessar o documento do Firestore que contém a chave pública, código do firebase, de como mandar a chave publica
        bancoDados.collection("Usuarios")
                .document(admin.getFuncionarios().get(0))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // Extrair o valor da chave pública do documento
                            String publicKeyString = documentSnapshot.getString("PublicKey");

                            if (publicKeyString != null) {
                                // Decodificar a string Base64 da chave pública para obter um array de bytes
                                byte[] publicKeyBytes = new byte[0];
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                    publicKeyBytes = Base64.getDecoder().decode(publicKeyString);
                                }

                                try {
                                    // Construir uma nova instância de PublicKey a partir do array de bytes decodificado
                                    publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(publicKeyBytes));
                                    //Salvo a chave pública para usar em outras partes do código

                                } catch (Exception e) {
                                    Log.e("valorReal", "Error converting PublicKey", e);
                                }
                            }
                        } else {
                            Log.d("valorReal", "Document not found");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("valorReal", "Error getting document", e);
                    }
                });
    }


    private String getDirectionsUrl() {
        // Obtém a hora atual em milissegundos, url posição atual
        long currentTimeMillis = System.currentTimeMillis();
        double arrivalTimeMillis = currentTimeMillis + tempoTotal; // Adiciona uma hora ao tempo atual

        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + minhaLocalizacao.getPosAtual().getLatitude() + "," + minhaLocalizacao.getPosAtual().getLongitude());
        googleDirectionsUrl.append("&destination=" + latitudeUfla + "," + longitudeUfla);
        googleDirectionsUrl.append("&departure_time=" + currentTimeMillis);// Define a hora de partida com o valor atual em milissegundos
        googleDirectionsUrl.append("&key=AIzaSyCdSX9hq8cQ3DMdJMxkdH8mbLKkKxuJS10");


        return googleDirectionsUrl.toString();

    }


    private String getDirectionsUrlNow() {
//url posicao inicial
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin=" + minhaLocalizacao.getPosicaoI().getLatitude() + "," + minhaLocalizacao.getPosicaoI().getLongitude());
        googleDirectionsUrl.append("&destination=" + minhaLocalizacao.getPosAtual().getLatitude() + "," + minhaLocalizacao.getPosAtual().getLongitude());
        googleDirectionsUrl.append("&key=AIzaSyCdSX9hq8cQ3DMdJMxkdH8mbLKkKxuJS10" );

        return googleDirectionsUrl.toString();
    }




        public void setJson(String s) {
        //Só pra salvar a String realizada na classe google
            jsonTeste = s;
        }

    public void setValor(ArrayList<Double> rec){
        //Formatação rec
        valorRec = "";
        for (int i = 0; i < rec.size(); i++) {
            valorRec += rec.get(i) + " | ";
        }
        Log.d("Reconc", String.valueOf(valorRec));
    }

    public void setveloc(String[] directionsList, boolean b) {
        if (b) {
            minhaLocalizacao.setTempo(Double.parseDouble(directionsList[1]));
            minhaLocalizacao.setDisTotal(Double.parseDouble(directionsList[0]));
        }
        else{
            minhaLocalizacao.setDisTotal(Double.parseDouble(directionsList[1]));
            minhaLocalizacao.setTempo(Double.parseDouble(directionsList[0]));
        }

        }

    public void getGoogleLocais(ArrayList<PadronizacaoRec> test) {
        //Salva os valores intermediários
        double aux = 0;
        googleLocais.get(1).setDuracao(1);
        for (int i = 2; i <= test.size(); i++) {
            for (int j = 1; j <= i; j++) {
                aux = aux + test.get(j).getDuracao();
            }
            googleLocais.get(i).setDuracao(aux);
            aux = 0;
        }
    }


    public void setDistancia(double distancia) {
        distanciaCalculo = distancia;
    }

    public void setTempo(double duracao) {
        tempoCalculo = duracao;
    }
    /*public void mostrarGoogleMaps(double latitude, double longitude) {
        WebView wv = findViewById(R.id.webv);
        wv.getSettings().setJavaScriptEnabled(true);
        wv.loadUrl("https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude);

    }*/
}