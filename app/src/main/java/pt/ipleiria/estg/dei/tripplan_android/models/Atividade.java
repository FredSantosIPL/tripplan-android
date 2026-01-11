package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Atividade implements Serializable {
    private int id;

    // NO DER: A atividade liga-se a um Destino, não ao Plano diretamente
    @SerializedName("destino_id")
    private int destinoId;

    @SerializedName("nome_atividade")
    private String nomeAtividade;

    private String tipo;

    // Construtor
    public Atividade(int id, int destinoId, String nomeAtividade, String tipo) {
        this.id = id;
        this.destinoId = destinoId;
        this.nomeAtividade = nomeAtividade;
        this.tipo = tipo;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDestinoId() { return destinoId; }
    public void setDestinoId(int destinoId) { this.destinoId = destinoId; }

    public String getNomeAtividade() { return nomeAtividade; }
    public void setNomeAtividade(String nomeAtividade) { this.nomeAtividade = nomeAtividade; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return nomeAtividade;
    }
}