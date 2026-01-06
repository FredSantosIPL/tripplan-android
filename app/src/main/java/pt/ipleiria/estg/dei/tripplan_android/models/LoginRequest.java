package pt.ipleiria.estg.dei.tripplan_android.models;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    // Estes nomes têm de ser iguais aos que o teu PHP espera no $_POST
    @SerializedName("username") // ou "email", depende do teu LoginForm no Yii2
    private String username;

    @SerializedName("password")
    private String password;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}