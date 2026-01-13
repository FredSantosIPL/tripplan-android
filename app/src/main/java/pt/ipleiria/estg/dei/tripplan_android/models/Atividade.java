package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Atividade implements Serializable {
    private int id;

    // Enviamos 0 para o PHP saber que tem de procurar sozinho
    @SerializedName("destino_id")
    private int destinoId;

    // CAMPO NOVO: Obrigatório para a nossa batota funcionar
    @SerializedName("plano_viagem_id")
    private int planoViagemId;

    @SerializedName("nome_atividade")
    private String nomeAtividade;

    private String tipo;

    // Construtor Atualizado
    public Atividade(int id, int destinoId, int planoViagemId, String nomeAtividade, String tipo) {
        this.id = id;
        this.destinoId = destinoId;
        this.planoViagemId = planoViagemId;
        this.nomeAtividade = nomeAtividade;
        this.tipo = tipo;
    }

    // Getters e Setters
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

    @Override
    public String toString() {
        return "Atividade{" +
                "id=" + id +
                ", nome='" + nomeAtividade + '\'' +
                ", destinoID=" + destinoId +
                ", planoViagemID=" + planoViagemId + // <--- ISTO É O QUE QUEREMOS VER
                '}';
    }
}