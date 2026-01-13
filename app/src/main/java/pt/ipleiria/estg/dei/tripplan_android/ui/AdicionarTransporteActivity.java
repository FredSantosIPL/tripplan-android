package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter; // Importante para o Spinner
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
    private EditText etData;
    private Spinner spTipo; // Agora é um Spinner, não EditText
    private final Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_transporte);

        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);

        if (idViagemAtual == -1) {
            finish();
            return;
        }

        // 1. Ligar as Views
        spTipo = findViewById(R.id.spTipoTransporte);
        EditText etOrigem = findViewById(R.id.etOrigem);
        EditText etDestino = findViewById(R.id.etDestino);
        etData = findViewById(R.id.etDataPartida);

        // 2. CONFIGURAR O SPINNER (Lista de opções)
        String[] opcoesTransporte = {"Avião", "Comboio", "Autocarro", "Carro", "Barco"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoesTransporte
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTipo.setAdapter(adapter);

        // 3. Configurar Calendário
        etData.setOnClickListener(v -> mostrarCalendario());

        // 4. Botão Guardar
        findViewById(R.id.btnGuardarTransporte).setOnClickListener(v -> {
            // Obter o valor selecionado no Spinner
            String tipoSelecionado = spTipo.getSelectedItem().toString();

            String origem = etOrigem.getText().toString();
            String destino = etDestino.getText().toString();
            String data = etData.getText().toString();

            if (origem.isEmpty() || destino.isEmpty() || data.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            String dataSQL = data + " 00:00:00";

            Transporte novo = new Transporte(
                    0,
                    idViagemAtual,
                    tipoSelecionado, // Passamos o valor do dropdown
                    origem,
                    destino,
                    dataSQL
            );

            SingletonGestor.getInstance(this).adicionarTransporteAPI(novo);
            finish();
        });
    }

    private void mostrarCalendario() {
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String dataFormatada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                    etData.setText(dataFormatada);
                }, ano, mes, dia);
        datePicker.show();
    }
}