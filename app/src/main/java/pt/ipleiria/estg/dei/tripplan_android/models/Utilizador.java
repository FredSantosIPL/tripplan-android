package pt.ipleiria.estg.dei.tripplan_android.models;

import java.io.Serializable;

public class Utilizador implements Serializable {
    private int id;
    private String nome;
    private String email;
    private String password;
    // Telefone e Morada removidos!

    public Utilizador() {
    }

    // CONSTRUTOR SÓ COM 4 COISAS (Para bater certo com o Singleton)
    public Utilizador(int id, String nome, String email, String password) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}