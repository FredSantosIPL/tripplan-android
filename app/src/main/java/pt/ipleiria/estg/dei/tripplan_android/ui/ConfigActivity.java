package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor; // <--- Importante!

public class ConfigActivity extends AppCompatActivity {

    // --- CAMINHOS AJUSTADOS (BONECA RUSSA) ---
    private static final String URL_EMULADOR = "http://10.0.2.2:8888/TripPlan/tripplan/tripplan/backend/web/index.php/";

    // ATENÇÃO: Quando fores para a escola, confirma se o teu IP do PC é este!
    private static final String URL_REAL = "http://172.22.21.246/tripplan-web/tripplan/backend/web/index.php/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        EditText etIp = findViewById(R.id.etIpAddress);
        Button btnSalvar = findViewById(R.id.btnSalvarConfig);
        Button btnEmulador = findViewById(R.id.btnPresetEmulador);
        Button btnReal = findViewById(R.id.btnPresetReal);

        SharedPreferences prefs = getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);

        // Carrega o IP que está guardado (ou o do emulador se não houver nada)
        String ipAtual = prefs.getString("IP_API", URL_EMULADOR);
        etIp.setText(ipAtual);

        // --- BOTÕES DE ATALHO ---
        btnEmulador.setOnClickListener(v -> {
            etIp.setText(URL_EMULADOR);
            Toast.makeText(this, "Predefinição Emulador carregada!", Toast.LENGTH_SHORT).show();
        });

        btnReal.setOnClickListener(v -> {
            etIp.setText(URL_REAL);
            Toast.makeText(this, "Predefinição Wi-Fi carregada!", Toast.LENGTH_SHORT).show();
        });

        // --- BOTÃO GUARDAR ---
        btnSalvar.setOnClickListener(v -> {
            String novoIp = etIp.getText().toString().trim(); // .trim() remove espaços acidentais

            // Validação: O Retrofit precisa que o URL termine em "/"
            if (!novoIp.endsWith("/")) {
                novoIp += "/";
            }

            if (!novoIp.isEmpty()) {
                // 1. Guardar nas Preferências
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("IP_API", novoIp);

                // Opcional: Limpar o token para obrigar a fazer login de novo no novo servidor
                // editor.remove("TOKEN_API");

                editor.apply();

                // 2. AVISAR O SINGLETON (A LINHA MÁGICA 🪄)
                // Isto obriga o Singleton a ler o novo IP e reconstruir o ServiceBuilder
                SingletonGestor.getInstance(this).lerIpDasPreferencias();

                Toast.makeText(this, "Configuração Guardada e API Reiniciada!", Toast.LENGTH_SHORT).show();
                finish(); // Volta para o ecrã anterior
            } else {
                Toast.makeText(this, "O URL não pode estar vazio", Toast.LENGTH_SHORT).show();
            }
        });
    }
}