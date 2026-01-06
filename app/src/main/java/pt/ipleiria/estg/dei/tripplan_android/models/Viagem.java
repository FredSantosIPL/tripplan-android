package pt.ipleiria.estg.dei.tripplan_android.models;

import java.util.ArrayList;
import java.util.List;

public class Viagem {
    private int id;
    private int userId;
    private String nomeViagem;
    private String dataInicio;
    private String dataFim;
    private List<Transporte> transportes;

    // CONSTRUTOR 1: Completo (Usa este na CriarViagemActivity)
    public Viagem(int id, int userId, String nomeViagem, String dataInicio, String dataFim) {
        this.id = id;
        this.userId = userId;
        this.nomeViagem = nomeViagem;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.transportes = new ArrayList<>();
    }

    // CONSTRUTOR 2: Simples (Usa este na MainActivity para dados de teste)
    public Viagem(String nomeViagem, String dataInicio, String dataFim) {
        this(0, 0, nomeViagem, dataInicio, dataFim); // Define IDs como 0 automaticamente
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

    public List<Transporte> getTransportes() { return transportes; }
    public void setTransportes(List<Transporte> transportes) { this.transportes = transportes; }
}