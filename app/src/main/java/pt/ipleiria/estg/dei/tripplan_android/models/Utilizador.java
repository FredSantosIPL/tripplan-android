package pt.ipleiria.estg.dei.tripplan_android.models;

public class Utilizador {
    private long id;
    private String nome;
    private String email;
    private String password;
    private String telefone;
    private String morada;

    public Utilizador(String nome, String email, String password, String telefone, String morada) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.telefone = telefone;
        this.morada = morada;
    }

    public Utilizador(long id, String nome, String email, String password, String telefone, String morada) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.telefone = telefone;
        this.morada = morada;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id;}

    public String getNome() {return nome;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getTelefone() {return telefone;}
    public String getMorada() {return morada;}


}
