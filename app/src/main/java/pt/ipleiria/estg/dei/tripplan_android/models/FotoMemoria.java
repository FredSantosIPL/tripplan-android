package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class FotoMemoria implements Serializable {
    private int id;

    @SerializedName("plano_viagem_id")
    private int planoViagemId;

    private String comentario;

    // --- PARA ENVIAR (Upload) ---
    @SerializedName("imagem_base64")
    private String imagemBase64;

    // --- PARA RECEBER (Download) ---
    // Este é o campo que faltava e que o Adapter está a tentar ler!
    @SerializedName("foto")
    private String foto;

    // Construtor
    public FotoMemoria(int planoViagemId, String comentario, String imagemBase64) {
        this.planoViagemId = planoViagemId;
        this.comentario = comentario;
        this.imagemBase64 = imagemBase64;
    }

    // --- GETTERS E SETTERS ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlanoViagemId() {
        return planoViagemId;
    }

    public String getComentario() {
        return comentario;
    }

    public String getImagemBase64() {
        return imagemBase64;
    }

    // --- O MÉTODO QUE O ADAPTER ESTÁ À PROCURA ---
    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}