package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class Viagem implements Serializable {

    private int id;

    @SerializedName("user_id") // CORRIGIDO: No teu SQL a coluna é 'user_id'
    private int userId;

    @SerializedName("nome_viagem")
    private String nomeViagem;

    @SerializedName("data_inicio")
    private String dataInicio;

    @SerializedName("data_fim")
    private String dataFim;

    // --- Relação Master/Detail (SIS) ---
    // A API Yii2 vai enviar isto se usares o ->with(['transportes'])
    @SerializedName("transportes")
    private ArrayList<Transporte> transportes;

    // Construtor
    public Viagem(int id, int userId, String nomeViagem, String dataInicio, String dataFim) {
        this.id = id;
        this.userId = userId;
        this.nomeViagem = nomeViagem;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.transportes = new ArrayList<>();
    }

    // Getters e Setters
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

    public ArrayList<Transporte> getTransportes() { return transportes; }
    public void setTransportes(ArrayList<Transporte> transportes) { this.transportes = transportes; }
}