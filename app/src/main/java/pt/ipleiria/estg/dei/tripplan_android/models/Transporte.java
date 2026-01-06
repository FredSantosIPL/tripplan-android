package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Transporte implements Serializable {
    private int id;

    @SerializedName("plano_viagem_id") // Coluna FK no SQL
    private int planoViagemId;

    private String tipo;    // "Avião", "Comboio", etc.

    private String origem;

    private String destino;

    @SerializedName("data_partida") // DATETIME no SQL ("2026-01-04 23:22:00")
    private String dataPartida;

    // Construtor
    public Transporte(int id, int planoViagemId, String tipo, String origem, String destino, String dataPartida) {
        this.id = id;
        this.planoViagemId = planoViagemId;
        this.tipo = tipo;
        this.origem = origem;
        this.destino = destino;
        this.dataPartida = dataPartida;
    }

    // Getters e Setters (Gera com Alt+Insert se precisares de mais)
    public String getTipo() { return tipo; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public String getDataPartida() { return dataPartida; }
}