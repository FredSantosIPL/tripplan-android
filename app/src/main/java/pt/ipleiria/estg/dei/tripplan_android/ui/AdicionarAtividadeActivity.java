package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Atividade;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarAtividadeActivity extends AppCompatActivity {

    private int idViagemAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_atividade);

        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);

        EditText etNome = findViewById(R.id.etNomeAtividade);
        EditText etTipo = findViewById(R.id.etTipoAtividade);

        findViewById(R.id.btnGuardarAtividade).setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String tipo = etTipo.getText().toString();

            if (nome.isEmpty() || tipo.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // AQUI ESTÁ A CORREÇÃO:
            // destino_id = 0 (Para o PHP ignorar e procurar sozinho)
            // plano_viagem_id = idViagemAtual (Para o PHP saber onde procurar)
            Atividade nova = new Atividade(
                    0,
                    0,              // destino_id
                    idViagemAtual,  // plano_viagem_id
                    nome,
                    tipo
            );

            // Chama a API
            SingletonGestor.getInstance(this).adicionarAtividadeAPI(nova);
            finish();
        });
    }
}