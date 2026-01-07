package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class Viagem {

    @SerializedName("id")
    private int id;

    // Na BD é 'user_id', no Java é 'userId'
    @SerializedName("user_id")
    private int userId;

    // Na BD é 'nome_viagem', no Java é 'nomeViagem'
    @SerializedName("nome_viagem")
    private String nomeViagem;

    // Na BD é 'data_inicio', no Java é 'dataInicio'
    @SerializedName("data_inicio")
    private String dataInicio;

    // Na BD é 'data_fim', no Java é 'dataFim'
    @SerializedName("data_fim")
    private String dataFim;

    private List<Transporte> transportes;

    // --- CONSTRUTORES ---

    public Viagem(int id, int userId, String nomeViagem, String dataInicio, String dataFim) {
        this.id = id;
        this.userId = userId;
        this.nomeViagem = nomeViagem;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.transportes = new ArrayList<>();
    }

    // Construtor simples (útil para testes)
    public Viagem(String nomeViagem, String dataInicio, String dataFim) {
        this(0, 0, nomeViagem, dataInicio, dataFim);
    }

    // --- GETTERS E SETTERS ---
    // Podes manter os nomes dos Getters/Setters como tinhas, o Gson trata do resto!

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getNomeViagem() { return nomeViagem; }
    public void setNomeViagem(String nomeViagem) { this.nomeViagem = nomeViagem; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }

    public List<Transporte> getTransportes() { return transportes; }
    public void setTransportes(List<Transporte> transportes) { this.transportes = transportes; }
}