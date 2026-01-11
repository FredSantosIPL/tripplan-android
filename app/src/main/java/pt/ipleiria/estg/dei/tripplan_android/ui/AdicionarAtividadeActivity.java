package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Atividade; // Cria este modelo!
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

            if (nome.isEmpty()) {
                Toast.makeText(this, "Escreve o nome!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Cria o objeto (Ajusta o construtor ao teu modelo)
            Atividade nova = new Atividade(0, idViagemAtual, nome, tipo);

            // Chama a API
            SingletonGestor.getInstance(this).adicionarAtividadeAPI(nova);
            finish();
        });
    }
}