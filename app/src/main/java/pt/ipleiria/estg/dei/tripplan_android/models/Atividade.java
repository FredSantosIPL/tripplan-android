package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Atividade implements Serializable {
    private int id;

    @SerializedName("destino_id")
    private int destinoId;

    @SerializedName("plano_viagem_id")
    private int planoViagemId;

    @SerializedName("nome_atividade")
    private String nomeAtividade;

    private String tipo;

    public Atividade() {
    }

    // Construtor Completo
    public Atividade(int id, int destinoId, int planoViagemId, String nomeAtividade, String tipo) {
        this.id = id;
        this.destinoId = destinoId;
        this.planoViagemId = planoViagemId;
        this.nomeAtividade = nomeAtividade;
        this.tipo = tipo;
    }

    // --- GETTERS E SETTERS NORMAIS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDestinoId() { return destinoId; }
    public void setDestinoId(int destinoId) { this.destinoId = destinoId; }

    public int getPlanoViagemId() { return planoViagemId; }
    public void setPlanoViagemId(int planoViagemId) { this.planoViagemId = planoViagemId; }

    public String getNomeAtividade() { return nomeAtividade; }
    public void setNomeAtividade(String nomeAtividade) { this.nomeAtividade = nomeAtividade; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public void setTitulo(String titulo) {
        this.nomeAtividade = titulo;
    }

    public String getTitulo() {
        return this.nomeAtividade;
    }

    public void setViagemId(int id) {
        this.planoViagemId = id;
    }

    @Override
    public String toString() {
        return nomeAtividade;
    }
}