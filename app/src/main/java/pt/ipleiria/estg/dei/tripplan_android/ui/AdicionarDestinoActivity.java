package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Destino;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarDestinoActivity extends AppCompatActivity {

    private int idViagemAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_destino);

        // Receber ID da Viagem Pai
        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);

        EditText etCidade = findViewById(R.id.etCidade);
        EditText etPais = findViewById(R.id.etPais);
        EditText etData = findViewById(R.id.etDataChegada);

        findViewById(R.id.btnGuardarDestino).setOnClickListener(v -> {
            String cidade = etCidade.getText().toString();
            String pais = etPais.getText().toString();
            String data = etData.getText().toString();

            if (cidade.isEmpty() || pais.isEmpty()) return;

            // Agente ID = 0 (ou podes por o ID do user logado se o backend pedir)
            Destino novoDestino = new Destino(0, 0, cidade, pais, data);

            // ATENÇÃO: Tens de criar este método no Singleton
            // Ele deve enviar também o idViagemAtual para o backend associar
            SingletonGestor.getInstance(this).adicionarDestinoAPI(idViagemAtual, novoDestino);

            finish();
        });
    }
}