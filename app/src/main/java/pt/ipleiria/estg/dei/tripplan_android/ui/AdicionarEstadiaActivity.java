package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Estadia;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarEstadiaActivity extends AppCompatActivity {

    private int idViagemAtual;
    private EditText etCheckIn;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_estadia);

        // 1. Receber ID da Viagem
        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);
        if (idViagemAtual == -1) {
            Toast.makeText(this, "Erro: Viagem não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ligar Views
        EditText etNome = findViewById(R.id.etNomeEstadia);
        EditText etTipo = findViewById(R.id.etTipoEstadia);
        etCheckIn = findViewById(R.id.etCheckIn);

        // 3. Configurar Data Check-in
        configurarDatePicker(etCheckIn);

        // 4. Botão Guardar
        findViewById(R.id.btnGuardarEstadia).setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String tipo = etTipo.getText().toString();
            String data = etCheckIn.getText().toString();

            if (nome.isEmpty() || tipo.isEmpty() || data.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 5. CRIAR OBJETO ESTADIA
            // Construtor: (id, planoViagemId, nomeAlojamento, tipo, dataCheckin)
            Estadia novaEstadia = new Estadia(
                    0,              // id
                    idViagemAtual,  // planoViagemId
                    nome,           // nomeAlojamento
                    tipo,           // tipo
                    data            // dataCheckin
            );

            // Enviar para API
            SingletonGestor.getInstance(this).adicionarEstadiaAPI(novaEstadia);
            finish();
        });
    }

    private void configurarDatePicker(EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setOnClickListener(v -> {
            int ano = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String dataFormatada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        editText.setText(dataFormatada);
                    }, ano, mes, dia);
            dialog.show();
        });
    }
}