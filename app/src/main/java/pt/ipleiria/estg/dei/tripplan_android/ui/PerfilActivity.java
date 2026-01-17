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
        SingletonGestor gestor = SingletonGestor.getInstance(this);
        String nomeReal = gestor.getUsernameLogado();
        String emailReal = gestor.getEmailLogado();

        tvNome.setText(nomeReal);
        tvEmail.setText(emailReal);
    }
}