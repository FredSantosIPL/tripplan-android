package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class FotoMemoria implements Serializable {
    private int id;

    @SerializedName("utilizador_id") // Se o backend precisar que envies manualmente
    private int utilizadorId;

    @SerializedName("plano_viagem_id")
    private int planoViagemId;

    private String comentario;

    // Nota: O ficheiro em si não é guardado neste objeto Java da mesma forma.
    // A API vai devolver provavelmente o caminho (URL) da imagem no servidor depois do upload.
    // Podes adicionar aqui um campo tipo: private String urlFoto;

    // Construtor para enviar (sem ID)
    public FotoMemoria(int planoViagemId, String comentario) {
        this.planoViagemId = planoViagemId;
        this.comentario = comentario;
        // O utilizadorId geralmente o backend tira do token de autenticação
    }

    // Getters e Setters...
    public String getComentario() { return comentario; }
}