package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import pt.ipleiria.estg.dei.tripplan_android.R;

public class ConfigActivity extends AppCompatActivity {

    // Constantes com os teus URLs exatos
    private static final String URL_EMULADOR = "http://10.0.2.2:8888/tripplan/tripplan/tripplan/backend/web/index.php/";
    private static final String URL_REAL = "http://192.168.1.237/tripplan-web/tripplan/backend/web/index.php/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        EditText etIp = findViewById(R.id.etIpAddress);
        Button btnSalvar = findViewById(R.id.btnSalvarConfig);
        Button btnEmulador = findViewById(R.id.btnPresetEmulador);
        Button btnReal = findViewById(R.id.btnPresetReal);

        // 1. Carregar o que está guardado atualmente
        SharedPreferences prefs = getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);
        String ipAtual = prefs.getString("IP_API", URL_EMULADOR); // Default é o emulador
        etIp.setText(ipAtual);

        // 2. Configurar Botão de Atalho EMULADOR
        btnEmulador.setOnClickListener(v -> {
            etIp.setText(URL_EMULADOR);
            Toast.makeText(this, "URL Emulador preenchido!", Toast.LENGTH_SHORT).show();
        });

        // 3. Configurar Botão de Atalho REAL (Escola/Wi-Fi)
        btnReal.setOnClickListener(v -> {
            etIp.setText(URL_REAL);
            Toast.makeText(this, "URL Wi-Fi preenchido!", Toast.LENGTH_SHORT).show();
        });

        // 4. Botão Guardar (Mantém a lógica que tinhas)
        btnSalvar.setOnClickListener(v -> {
            String novoIp = etIp.getText().toString();

            // Validação simples: garantir que termina em "/"
            if (!novoIp.endsWith("/")) {
                novoIp += "/";
            }

            if (!novoIp.isEmpty()) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("IP_API", novoIp);
                editor.apply();

                Toast.makeText(this, "Configuração Guardada!", Toast.LENGTH_SHORT).show();
                finish(); // Fecha a janela e volta ao Login
            } else {
                Toast.makeText(this, "O URL não pode estar vazio", Toast.LENGTH_SHORT).show();
            }
        });
    }
}