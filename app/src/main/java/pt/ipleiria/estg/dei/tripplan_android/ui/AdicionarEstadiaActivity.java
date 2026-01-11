package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Estadia; // Cria este modelo!
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarEstadiaActivity extends AppCompatActivity {

    private int idViagemAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_estadia);

        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);

        EditText etNome = findViewById(R.id.etNomeEstadia);
        EditText etCheckIn = findViewById(R.id.etCheckIn);
        EditText etMorada = findViewById(R.id.etMorada);

        findViewById(R.id.btnGuardarEstadia).setOnClickListener(v -> {
            // ... validações ...

            // Ajusta o construtor conforme o teu modelo Estadia.java
            Estadia nova = new Estadia(0, idViagemAtual,
                    etNome.getText().toString(),
                    etCheckIn.getText().toString(),
                    etMorada.getText().toString()
            );

            SingletonGestor.getInstance(this).adicionarEstadiaAPI(nova);
            finish();
        });
    }
}