package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Favorito implements Serializable {
    private int id;

    @SerializedName("user_id")
    private int userId;

    @SerializedName("destino_id")
    private int destinoId;

    // Construtor
    public Favorito(int id, int userId, int destinoId) {
        this.id = id;
        this.userId = userId;
        this.destinoId = destinoId;
    }

    // Getters e Setters...
    public int getId() { return id; }
    public int getDestinoId() { return destinoId; }
}