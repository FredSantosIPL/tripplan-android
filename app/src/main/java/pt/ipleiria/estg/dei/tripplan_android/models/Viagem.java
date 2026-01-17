package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.ArrayList;

public class Viagem implements Serializable {
    private int id;
    private int user_id;
    private String destino; // Campo auxiliar para o destino principal

    @SerializedName("nome_viagem")
    private String nomeViagem;

    @SerializedName("data_inicio")
    private String dataInicio;

    @SerializedName("data_fim")
    private String dataFim;

    // --- LISTAS ---
    @SerializedName("destinos")
    private ArrayList<Destino> destinos;
    @SerializedName("transportes")
    private ArrayList<Transporte> transportes;

    @SerializedName("estadias")
    private ArrayList<Estadia> estadias;

    @SerializedName("atividades")
    private ArrayList<Atividade> atividades;

    @SerializedName("fotosMemorias")
    private ArrayList<FotoMemoria> listaFotos;



    // --- 1. CONSTRUTOR VAZIO (Importante para a API/Retrofit) ---
    public Viagem() {
        this.destinos = new ArrayList<>();
        this.transportes = new ArrayList<>();
        this.estadias = new ArrayList<>();
        this.atividades = new ArrayList<>();
        this.listaFotos = new ArrayList<>();
    }

    // --- 2. CONSTRUTOR COMPLETO ---
    public Viagem(int id, int user_id, String nomeViagem, String dataInicio, String dataFim) {
        this.id = id;
        this.user_id = user_id;
        this.nomeViagem = nomeViagem;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;

        // Inicializar listas para evitar crashe se estiverem null
        this.destinos = new ArrayList<>();
        this.transportes = new ArrayList<>();
        this.estadias = new ArrayList<>();
        this.atividades = new ArrayList<>();
        this.listaFotos = new ArrayList<>();
    }

    // --- GETTERS E SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return user_id; }
    // Adicionei este para compatibilidade se usaste getUser_id noutro lado
    public int getUser_id() { return user_id; }
    public void setUserId(int user_id) { this.user_id = user_id; }

    public String getNomeViagem() { return nomeViagem; }
    public void setNomeViagem(String nomeViagem) { this.nomeViagem = nomeViagem; }

    public String getDataInicio() { return dataInicio; }
    public void setDataInicio(String dataInicio) { this.dataInicio = dataInicio; }

    public String getDataFim() { return dataFim; }
    public void setDataFim(String dataFim) { this.dataFim = dataFim; }

    public void setDestino(String destino) { this.destino = destino; }
    public String getDestino() { return this.destino; }

    // --- 3. A PARTE QUE FALTAVA (OS SETTERS DAS LISTAS) ---
    // Sem isto, o Helper não consegue guardar o que leu da BD dentro da Viagem

    public ArrayList<Destino> getDestinos() { return destinos; }
    public void setDestinos(ArrayList<Destino> destinos) { this.destinos = destinos; }

    public ArrayList<Atividade> getAtividades() { return atividades; }
    public void setAtividades(ArrayList<Atividade> atividades) { this.atividades = atividades; }

    public ArrayList<Transporte> getTransportes() { return transportes; }
    public void setTransportes(ArrayList<Transporte> transportes) { this.transportes = transportes; }

    public ArrayList<Estadia> getEstadias() { return estadias; }
    public void setEstadias(ArrayList<Estadia> estadias) { this.estadias = estadias; }

    public ArrayList<FotoMemoria> getListaFotos() { return listaFotos; }
    public void setListaFotos(ArrayList<FotoMemoria> listaFotos) { this.listaFotos = listaFotos; }

    @Override
    public String toString() {
        return nomeViagem;
    }
}