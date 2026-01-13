package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList; // <--- Não esquecer este import

public class Viagem implements Serializable {
    private int id;

    @SerializedName("user_id") // Ou utilizador_id, confirma com a tua BD
    private int userId;

    @SerializedName("nome_viagem")
    private String nomeViagem;

    @SerializedName("data_inicio")
    private String dataInicio;

    @SerializedName("data_fim")
    private String dataFim;

    // --- LISTAS PARA OS DETALHES (Master/Detail) ---
    // O @SerializedName deve ser igual à chave do JSON que a API devolve (ex: 'transportes', 'destinos')

    @SerializedName("transportes")
    private ArrayList<Transporte> transportes;

    @SerializedName("destinos")
    private ArrayList<Destino> destinos;

        

    // --- CONSTRUTOR ---
    public Viagem(int id, int userId, String nomeViagem, String dataInicio, String dataFim) {
        this.id = id;
        this.userId = userId;
        this.nomeViagem = nomeViagem;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;

        // Inicializar listas vazias para evitar NullPointerException se a API não enviar nada
        this.transportes = new ArrayList<>();
        this.destinos = new ArrayList<>();
    }

    // --- GETTERS E SETTERS ---

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

    // --- GETTERS E SETTERS DAS LISTAS ---

    public ArrayList<Transporte> getTransportes() {
        // Se for null (porque a API de listagem não enviou), devolve lista vazia
        if (transportes == null) {
            return new ArrayList<>();
        }
        return transportes;
    }

    public void setTransportes(ArrayList<Transporte> transportes) {
        this.transportes = transportes;
    }

    public ArrayList<Destino> getDestinos() {
        // Se for null, devolve lista vazia para não dar erro no size()
        if (destinos == null) {
            return new ArrayList<>();
        }
        return destinos;
    }

    public void setDestinos(ArrayList<Destino> destinos) {
        this.destinos = destinos;
    }

    @Override
    public String toString() {
        return nomeViagem;
    }
}