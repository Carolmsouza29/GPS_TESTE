package com.example.gps;


import java.util.ArrayList;

public class Administrador {

    private ArrayList<String> funcionarios;
    private ArrayList<String> cargas;

    public Administrador(){
        funcionarios = new ArrayList<String>();
        cargas = new ArrayList<String>();
        cadastroFuncionarios();
        cadastroCargas();
    }

    private void cadastroFuncionarios(){
        funcionarios.add("Breno");
        funcionarios.add("Carol");
    }

    private void cadastroCargas(){
        cargas.add("Carga 1");
        cargas.add("Carga 2");
    }

    public ArrayList<String> getFuncionarios() {
        return funcionarios;
    }

    public void setFuncionarios(ArrayList<String> funcionarios) {
        this.funcionarios = funcionarios;
    }

    public ArrayList<String> getCargas() {
        return cargas;
    }

    public void setCargas(ArrayList<String> cargas) {
        this.cargas = cargas;
    }
}
