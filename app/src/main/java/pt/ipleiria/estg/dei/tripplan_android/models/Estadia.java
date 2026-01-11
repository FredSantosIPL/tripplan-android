package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Estadia implements Serializable {
    private int id;

    // NO DER: A estadia liga-se a um Destino
    @SerializedName("destino_id")
    private int destinoId;

    @SerializedName("nome_alojamento")
    private String nomeAlojamento;

    private String tipo; // Ex: Hotel, Airbnb, Hostel

    @SerializedName("data_checkin") // DATETIME na BD
    private String dataCheckin;

    // Construtor
    public Estadia(int id, int destinoId, String nomeAlojamento, String tipo, String dataCheckin) {
        this.id = id;
        this.destinoId = destinoId;
        this.nomeAlojamento = nomeAlojamento;
        this.tipo = tipo;
        this.dataCheckin = dataCheckin;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getDestinoId() { return destinoId; }
    public void setDestinoId(int destinoId) { this.destinoId = destinoId; }

    public String getNomeAlojamento() { return nomeAlojamento; }
    public void setNomeAlojamento(String nomeAlojamento) { this.nomeAlojamento = nomeAlojamento; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getDataCheckin() { return dataCheckin; }
    public void setDataCheckin(String dataCheckin) { this.dataCheckin = dataCheckin; }
}