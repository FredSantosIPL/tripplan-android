package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    // --- CAMPOS QUE VÊM DO PHP ---

    // 1. O PHP envia "token", não "auth_key"
    @SerializedName("token")
    private String token;

    @SerializedName("id")
    private int id;

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    // --- GETTERS (USADOS PELA APP) ---

    public String getToken() {
        return token;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    // --- MÉTODOS DE COMPATIBILIDADE ---
    // Estes métodos existem só para o teu código antigo não dar erro vermelho

    // Se o teu código pede .getNome(), devolvemos o username
    public String getNome() {
        return username;
    }

    // Se o teu código pede .getStatus(), devolvemos "OK" (porque se chegou aqui, o login funcionou)
    public String getStatus() {
        return "OK";
    }
}