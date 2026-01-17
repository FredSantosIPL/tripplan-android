package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Transporte;

public class AdicionarTransporteActivity extends AppCompatActivity {

    private int idViagemAtual;
    private EditText etDataPartida;
    private final Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_transporte);

        // 1. Receber ID da Viagem
        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);
        if (idViagemAtual == -1) {
            Toast.makeText(this, "Erro: Viagem não identificada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ligar Views
        Spinner spTipo = findViewById(R.id.spTipoTransporte);
        EditText etOrigem = findViewById(R.id.etOrigem);
        EditText etDestino = findViewById(R.id.etDestino);
        etDataPartida = findViewById(R.id.etDataPartida);

        // 3. Configurar UI
        configurarSpinner(spTipo);
        configurarDatePicker(etDataPartida);

        // 4. Botão Guardar
        findViewById(R.id.btnGuardarTransporte).setOnClickListener(v -> {
            String tipo = spTipo.getSelectedItem().toString();
            String origem = etOrigem.getText().toString();
            String destino = etDestino.getText().toString();
            String data = etDataPartida.getText().toString();

            // Validação
            if (origem.isEmpty() || destino.isEmpty() || data.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // 5. CRIAR O OBJETO COM O TEU MODELO NOVO
            // Construtor: (id, planoViagemId, tipo, origem, destino, dataPartida)
            Transporte novoTransporte = new Transporte(
                    0,              // id (API trata)
                    idViagemAtual,  // plano_viagem_id
                    tipo,           // tipo
                    origem,         // origem
                    destino,        // destino
                    data            // data_partida
            );

            // Enviar para API
            SingletonGestor.getInstance(this).adicionarTransporteAPI(novoTransporte);
            finish();
        });
    }

    private void configurarSpinner(Spinner spinner) {
        String[] tipos = {"Avião", "Comboio", "Autocarro", "Carro", "Barco", "Outro"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, tipos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void configurarDatePicker(EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);
        editText.setOnClickListener(v -> {
            int ano = calendario.get(Calendar.YEAR);
            int mes = calendario.get(Calendar.MONTH);
            int dia = calendario.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(this,
                    (view, year, month, dayOfMonth) -> {
                        String dataFormatada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        editText.setText(dataFormatada);
                    }, ano, mes, dia);
            datePicker.show();
        });
    }
}