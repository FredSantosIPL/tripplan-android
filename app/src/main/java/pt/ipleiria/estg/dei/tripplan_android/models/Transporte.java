package pt.ipleiria.estg.dei.tripplan_android.models;

import java.io.Serializable;

public class Transporte implements Serializable {
    private int id;
    private String tipo;
    private String origem;
    private String destino;
    private String data_partida;

    // Construtor vazio
    public Transporte() {}

    // Construtor completo
    public Transporte(String tipo, String origem, String destino, String data_partida) {
        this.tipo = tipo;
        this.origem = origem;
        this.destino = destino;
        this.data_partida = data_partida;
    }

    // Getters e Setters (O Java precisa disto)
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getOrigem() { return origem; }
    public void setOrigem(String origem) { this.origem = origem; }
    public String getDestino() { return destino; }
    public void setDestino(String destino) { this.destino = destino; }
    public String getData_partida() { return data_partida; }
    public void setData_partida(String data_partida) { this.data_partida = data_partida; }
}