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

        // 1. Receber o ID da Viagem
        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);
        if (idViagemAtual == -1) {
            Toast.makeText(this, "Erro: Viagem não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ligar aos IDs do XML
        EditText etNome = findViewById(R.id.etNomeAtividade);
        EditText etTipo = findViewById(R.id.etTipoAtividade);

        // 3. Botão Guardar
        findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String tipo = etTipo.getText().toString();

            if (nome.isEmpty() || tipo.isEmpty()) {
                Toast.makeText(this, "Preenche o nome e o tipo!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 4. CRIAR ATIVIDADE (De acordo com o teu Modelo)
            // Ordem do Construtor: (id, destinoId, planoViagemId, nomeAtividade, tipo)
            Atividade novaAtividade = new Atividade(
                    0,              // id (API gera)
                    0,              // destinoId (Enviamos 0 como diz o comentário do modelo)
                    idViagemAtual,  // plano_viagem_id (Importante!)
                    nome,           // nome_atividade
                    tipo            // tipo
            );

            // 5. Enviar para a API
            SingletonGestor.getInstance(this).adicionarAtividadeAPI(novaAtividade);

            finish(); // Volta para os detalhes
        });
    }
}