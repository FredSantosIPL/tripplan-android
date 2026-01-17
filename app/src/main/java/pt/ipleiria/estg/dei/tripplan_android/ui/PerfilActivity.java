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

        tvNome = findViewById(R.id.tvNomePerfil);
        tvEmail = findViewById(R.id.tvEmailPerfil);
        btnLogout = findViewById(R.id.btnLogout);
        imgAvatar = findViewById(R.id.imgAvatar);

        carregarDadosPerfil();

        btnLogout.setOnClickListener(v -> {
            // Chamar o método do Singleton
            SingletonGestor.getInstance(this).fazerLogout();

            Toast.makeText(this, "Sessão terminada", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(PerfilActivity.this, StartActivity.class);
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