package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.R;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        TextView tvNome = findViewById(R.id.tvNomePerfil);
        TextView tvEmail = findViewById(R.id.tvEmailPerfil);

        // Exemplo: Buscar dados ao Singleton (se tiveres lá guardado o objeto User)
        // Utilizador user = SingletonGestor.getInstance(this).getUtilizadorLogado();
        // if(user != null) {
        //     tvNome.setText(user.getNome());
        //     tvEmail.setText(user.getEmail());
        // }

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            // Lógica de Logout (limpar token, voltar ao login)
            finish();
        });
    }
}