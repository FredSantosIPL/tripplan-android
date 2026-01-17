package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.StartActivity;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class PerfilActivity extends AppCompatActivity {

    private TextView tvNome, tvEmail;
    private AppCompatButton btnLogout;
    private ImageView imgAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // 1. Ligar as Views (IDs do XML moderno que fizemos antes)
        tvNome = findViewById(R.id.tvNomePerfil);
        tvEmail = findViewById(R.id.tvEmailPerfil);
        btnLogout = findViewById(R.id.btnLogout);
        imgAvatar = findViewById(R.id.imgAvatar);

        // 2. Carregar Dados do Utilizador
        carregarDadosPerfil();

        // 3. Configurar Botão Logout
        btnLogout.setOnClickListener(v -> {
            // Chamar o método do Singleton
            SingletonGestor.getInstance(this).fazerLogout();

            Toast.makeText(this, "Sessão terminada", Toast.LENGTH_SHORT).show();

            // Redirecionar para o Ecrã Inicial (StartActivity)
            Intent intent = new Intent(PerfilActivity.this, StartActivity.class);

            // Flags para limpar o histórico (para o user não poder voltar atrás com o botão "Back")
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
            finish();
        });
    }

    private void carregarDadosPerfil() {
        SharedPreferences prefs = getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);

        // Se tiveres guardado o nome no registo/login, usa-o.
        // Como a API de login simples geralmente só devolve token,
        // usamos o Email como identificador principal por enquanto.
        String email = prefs.getString("EMAIL_USER", "Utilizador");

        // Extrair o nome do email (ex: "fred" de "fred@mail.com") para ficar mais bonito
        String nomeExibicao = "Viajante";
        if (email.contains("@")) {
            nomeExibicao = email.substring(0, email.indexOf("@"));
            // Capitalizar a primeira letra (estética)
            nomeExibicao = nomeExibicao.substring(0, 1).toUpperCase() + nomeExibicao.substring(1);
        }

        tvNome.setText(nomeExibicao);
        tvEmail.setText(email);

        // Se quiseres ser pro, podes meter o ID do user num texto escondido ou debug
        // int id = SingletonGestor.getInstance(this).getUserIdLogado();
    }
}