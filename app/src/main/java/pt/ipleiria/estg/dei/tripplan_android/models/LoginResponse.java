package pt.ipleiria.estg.dei.tripplan_android.models;

public class LoginResponse {
    private String status;
    private String message;
    private int user_id;
    private String nome;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public int getUser_id() { return user_id; }
    public String getNome() { return nome; }
}