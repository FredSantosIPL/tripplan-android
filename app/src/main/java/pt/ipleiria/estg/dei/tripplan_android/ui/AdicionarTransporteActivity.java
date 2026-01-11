package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Transporte;

public class AdicionarTransporteActivity extends AppCompatActivity {

    private EditText etTipo, etOrigem, etDestino, etData;
    private int idViagemAtual; // ID da Viagem "Pai" (PlanoViagem)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_transporte);

        // 1. Receber o ID da viagem onde vamos adicionar o transporte
        // Certifica-te que quem chama esta Activity envia o "ID_VIAGEM"
        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);

        if (idViagemAtual == -1) {
            Toast.makeText(this, "Erro: Viagem não identificada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ligar as Views
        etTipo = findViewById(R.id.etTipoTransporte);
        etOrigem = findViewById(R.id.etOrigem);
        etDestino = findViewById(R.id.etDestino);
        etData = findViewById(R.id.etDataPartida);
        Button btnGuardar = findViewById(R.id.btnGuardarTransporte);

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarTransporte();
            }
        });
    }

    private void guardarTransporte() {
        String tipo = etTipo.getText().toString();
        String origem = etOrigem.getText().toString();
        String destino = etDestino.getText().toString();
        String data = etData.getText().toString(); // Formato esperado: "2026-01-04 23:22:00"

        // Validação simples
        if (tipo.isEmpty() || origem.isEmpty() || destino.isEmpty() || data.isEmpty()) {
            Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- CRIAÇÃO DO OBJETO ---
        // id = 0 (A base de dados gera o ID automático)
        // planoViagemId = idViagemAtual (Vem do Intent)
        Transporte novoTransporte = new Transporte(
                0,
                idViagemAtual,
                tipo,
                origem,
                destino,
                data
        );

        // Enviar para a API via Singleton
        // Nota: Garante que tens o método adicionarTransporteAPI no SingletonGestor
        SingletonGestor.getInstance(getApplicationContext()).adicionarTransporteAPI(novoTransporte);

        // Feedback e fechar
        Toast.makeText(this, "A guardar...", Toast.LENGTH_SHORT).show();
        finish();
    }
}