package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Estadia implements Serializable {
    private int id;

    @SerializedName("destino_id")
    private int destinoId;

    @SerializedName("plano_viagem_id")
    private int planoViagemId;

    @SerializedName("nome_alojamento")
    private String nomeAlojamento;

    private String tipo;

    @SerializedName("data_checkin")
    private String dataCheckin;

    // Construtor Atualizado
    public Estadia(int id, int planoViagemId, String nomeAlojamento, String tipo, String dataCheckin) {
        this.id = id;
        this.destinoId = 0;
        this.planoViagemId = planoViagemId;
        this.nomeAlojamento = nomeAlojamento;
        this.tipo = tipo;
        this.dataCheckin = dataCheckin;
    }

    // --- GETTERS E SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDestinoId() { return destinoId; }
    public void setDestinoId(int destinoId) { this.destinoId = destinoId; }

    public int getPlanoViagemId() { return planoViagemId; }
    public void setPlanoViagemId(int planoViagemId) { this.planoViagemId = planoViagemId; }

    public String getNomeAlojamento() { return nomeAlojamento; }
    public void setNomeAlojamento(String nomeAlojamento) { this.nomeAlojamento = nomeAlojamento; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(String dataCheckin) { this.dataCheckin = dataCheckin; }
}