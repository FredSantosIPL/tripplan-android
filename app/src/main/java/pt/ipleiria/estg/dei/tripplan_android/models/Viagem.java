package pt.ipleiria.estg.dei.tripplan_android.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Viagem implements Serializable {
    private int id;
    private String nome_viagem;
    private String data_inicio;
    private String data_fim;
    private List<Transporte> transportes;

    public Viagem() {
        this.transportes = new ArrayList<>();
    }

    public Viagem(String nome_viagem, String data_inicio, String data_fim) {
        this.nome_viagem = nome_viagem;
        this.data_inicio = data_inicio;
        this.data_fim = data_fim;
        this.transportes = new ArrayList<>();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome_viagem() { return nome_viagem; }
    public void setNome_viagem(String nome_viagem) { this.nome_viagem = nome_viagem; }
    public String getData_inicio() { return data_inicio; }
    public void setData_inicio(String data_inicio) { this.data_inicio = data_inicio; }
    public String getData_fim() { return data_fim; }
    public void setData_fim(String data_fim) { this.data_fim = data_fim; }
    public List<Transporte> getTransportes() { return transportes; }
    public void setTransportes(List<Transporte> transportes) { this.transportes = transportes; }
}