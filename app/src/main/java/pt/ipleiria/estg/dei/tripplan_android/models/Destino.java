package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Destino implements Serializable {
    private int id;

    // No DER existe este campo. Se não for usado, envia 0 ou o ID do user.
    @SerializedName("agente_viagem_id")
    private int agenteViagemId;

    @SerializedName("nome_cidade")
    private String nomeCidade;

    private String pais;

    @SerializedName("data_chegada") // Formato: AAAA-MM-DD
    private String dataChegada;

    // Construtor
    public Destino(int id, int agenteViagemId, String nomeCidade, String pais, String dataChegada) {
        this.id = id;
        this.agenteViagemId = agenteViagemId;
        this.nomeCidade = nomeCidade;
        this.pais = pais;
        this.dataChegada = dataChegada;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAgenteViagemId() { return agenteViagemId; }
    public void setAgenteViagemId(int agenteViagemId) { this.agenteViagemId = agenteViagemId; }

    public String getNomeCidade() { return nomeCidade; }
    public void setNomeCidade(String nomeCidade) { this.nomeCidade = nomeCidade; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getDataChegada() { return dataChegada; }
    public void setDataChegada(String dataChegada) { this.dataChegada = dataChegada; }

    @Override
    public String toString() {
        return nomeCidade + ", " + pais;
    }
}