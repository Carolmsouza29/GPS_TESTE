package com.example.gps;
//Classe que implementa a Thread, responsável por obter os dados do GPS e atualizar a cada 5 segundos
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import com.google.android.gms.location.LocationRequest;
import android.os.Build;
import android.os.Looper;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;


public class minhaLocalizacao extends Thread{
                private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
                public static final int INTERVAL_MILLIS = 5000;
                private FusedLocationProviderClient fusedLocation;
                private LocationRequest locationRequest;
                private LocationCallback locationCallB;
                private Location loc;
                private Context texto;
                private String nome;
                private double distInicial;
                private double disTotal;
                private double velocidade;
                private Posicao posicaoI;
                private Posicao posAtual;

    public double getTempo() {
        return tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    private double tempo;

                public minhaLocalizacao(Context texto) {
                        this.texto = texto;
                        distInicial = 0;
                        fusedLocation = LocationServices.getFusedLocationProviderClient(texto);
                        locationRequest = LocationRequest.create();
                        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                        locationRequest.setInterval(INTERVAL_MILLIS);
                        posAtual = new Posicao(0,0,0);
                        posicaoI = new Posicao(0,0,0);
                }


                public void iniciar() {

                locationCallB = new MyLocationCallback();
                }

                @Override
                public void run() {
                        iniciar();
                        if (verificarPermissao()) {
                                atualizarLocalizacao();
                        } else {
                                permissoes();
                        }
                }

                private boolean verificarPermissao() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                int permissionCheck = ContextCompat.checkSelfPermission(texto, Manifest.permission.ACCESS_FINE_LOCATION);
                                return permissionCheck == PackageManager.PERMISSION_GRANTED;
                        }
                        return false;
                }

                private void permissoes() {
                        if (texto instanceof MainActivity) {
                                ActivityCompat.requestPermissions((MainActivity) texto,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        LOCATION_PERMISSION_REQUEST_CODE);
                        }
                }

                private void atualizarLocalizacao() {
                        if (ActivityCompat.checkSelfPermission(texto, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(texto, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                return;
                        }
                        fusedLocation.requestLocationUpdates(locationRequest, locationCallB, Looper.getMainLooper());
                }

                private class MyLocationCallback extends LocationCallback {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                                if (locationResult != null && locationResult.getLastLocation() != null) {
                                        loc = locationResult.getLastLocation();
                                        posAtual.setLatitude(loc.getLatitude());
                                        posAtual.setLongitude(loc.getLongitude());
                                        velocidade = (loc.getSpeed());

                                        if(texto instanceof MainActivity){
                                               ((MainActivity) texto).Tela();

                                        }
                                }
                        }
                }

                public FusedLocationProviderClient getFusedLocation() {
                        return fusedLocation;
                }

                public void setFusedLocation(FusedLocationProviderClient fusedLocation) {
                        this.fusedLocation = fusedLocation;
                }

                public LocationRequest getLocationRequest() {
                        return locationRequest;
                }

                public void setLocationRequest(LocationRequest locationRequest) {
                        this.locationRequest = locationRequest;
                }

                public LocationCallback getLocationCallB() {
                        return locationCallB;
                }

                public void setLocationCallB(LocationCallback locationCallB) {
                        this.locationCallB = locationCallB;
                }

                public Location getLoc() {
                        return loc;
                }

                public void setLoc(Location loc) {
                        this.loc = loc;
                }

                public Context getTexto() {
                        return texto;
                }

                public void setTexto(Context texto) {
                        this.texto = texto;
                }

                public String getNome() {
                        return nome;
                }

                public void setNome(String nome) {
                        this.nome = nome;
                }

                public double getDistInicial() {
                        return distInicial;
                }

                public void setDistInicial(double distInicial) {
                        this.distInicial = distInicial;
                }

                public double getDisTotal() {
                        return disTotal;
                }

                public void setDisTotal(double disTotal) {
                        this.disTotal = disTotal;
                }

                public double getVelocidade() {
                        return velocidade;
                }

                public void setVelocidade(double velocidade) {
                        this.velocidade = velocidade;
                }

                public Posicao getPosicaoI() {
                        return posicaoI;
                }

                public void setPosicaoI(Posicao posicaoI) {
                        this.posicaoI = posicaoI;
                }

                public Posicao getPosAtual() {
                        return posAtual;
                }

                public void setPosAtual(Posicao posAtual) {
                        this.posAtual = posAtual;
                }
}
