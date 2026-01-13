package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest {


    @SerializedName("username")
    private String nome;

    @SerializedName("email")
    private String email;

    @SerializedName("password")
    private String password;

    public RegisterRequest(String nome, String email, String password) {
        this.nome = nome;
        this.email = email;
        this.password = password;
    }
}