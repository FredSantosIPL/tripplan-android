package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class Viagem implements Serializable {
    private int id;

    private int user_id; // <--- Passou para 2º lugar na hierarquia

    @SerializedName("nome_viagem")
    private String nomeViagem;

    @SerializedName("data_inicio")
    private String dataInicio;

    @SerializedName("data_fim")
    private String dataFim;

    // --- LISTAS ---
    private ArrayList<Destino> destinos;
    private ArrayList<Transporte> transportes;

    @SerializedName("estadias")
    private ArrayList<Estadia> estadias;

    @SerializedName("atividades")
    private ArrayList<Atividade> atividades;

    @SerializedName("fotosMemorias")
    private ArrayList<FotoMemoria> listaFotos;

    // --- CONSTRUTOR CORRIGIDO ---
    // Ordem: ID, UserID, Nome, DataInicio, DataFim
    public Viagem(int id, int user_id, String nomeViagem, String dataInicio, String dataFim) {
        this.id = id;
        this.user_id = user_id; // Agora recebe aqui
        this.nomeViagem = nomeViagem;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;

        // Inicializar listas
        this.destinos = new ArrayList<>();
        this.transportes = new ArrayList<>();
        this.estadias = new ArrayList<>();
        this.atividades = new ArrayList<>();
        this.listaFotos = new ArrayList<>();
    }

    // --- GETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return user_id; }
    public void setUserId(int user_id) { this.user_id = user_id; }

    public String getNomeViagem() { return nomeViagem; }
    public void setNomeViagem(String nomeViagem) { this.nomeViagem = nomeViagem; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }

    // Getters das Listas
    public ArrayList<Destino> getDestinos() { return destinos; }
    public ArrayList<Transporte> getTransportes() { return transportes; }
    public ArrayList<Estadia> getEstadias() { return estadias; }
    public ArrayList<Atividade> getAtividades() { return atividades; }
    public ArrayList<FotoMemoria> getListaFotos() { return listaFotos; }

    @Override
    public String toString() {
        return nomeViagem;
    }
}