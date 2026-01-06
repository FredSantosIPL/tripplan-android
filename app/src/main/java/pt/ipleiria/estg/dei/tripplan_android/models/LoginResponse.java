package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    // O Token de autenticação (auth_key na BD)
    @SerializedName("auth_key")
    private String token;

    // --- NOVOS CAMPOS QUE O TEU LOGIN ACTIVITY PEDE ---

    // Para resolver o erro .getStatus()
    // Nota: A tua API PHP tem de enviar um JSON com "status": "sucesso"
    @SerializedName("status")
    private String status;

    // Para resolver o erro .getNome()
    // Nota: A tua API PHP tem de enviar um JSON com "nome": "Ricardo"
    @SerializedName("nome")
    private String nome;

    // Outros dados úteis
    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    // --- GETTERS E SETTERS ---

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    // Estes são os métodos que faltavam e davam erro a vermelho:
    public String getStatus() {
        return status;
    }

    public String getNome() {
        return nome;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
}