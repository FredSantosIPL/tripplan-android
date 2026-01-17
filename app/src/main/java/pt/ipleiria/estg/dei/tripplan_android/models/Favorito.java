package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Favorito implements Serializable {
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("destino_id")
    private int planoViagemId;


    @SerializedName("viagem")
    private Viagem viagem;

    // Construtor
    public Favorito() {
    }
    public Favorito(int id, int userId, int planoViagemId) {
        this.id = id;
        this.userId = userId;
        this.planoViagemId = planoViagemId;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    // Mantemos o getter com este nome porque já alterámos o Adapter e a Activity para o usar
    public int getPlanoViagemId() {
        return planoViagemId;
    }

    public void setPlanoViagemId(int planoViagemId) {
        this.planoViagemId = planoViagemId;
    }

    public Viagem getViagem() {
        return viagem;
    }
}