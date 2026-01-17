package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Destino;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarDestinoActivity extends AppCompatActivity {

    private int idViagemAtual;
    private EditText etDataChegada; // Removido etDataPartida
    private final Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_destino);

        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);
        if (idViagemAtual == -1) {
            Toast.makeText(this, "Erro: Viagem inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        EditText etCidade = findViewById(R.id.etCidade);
        EditText etPais = findViewById(R.id.etPais);
        etDataChegada = findViewById(R.id.etDataChegada);
        // Removido findViewById(R.id.etDataPartida)

        configurarDatePicker(etDataChegada);

        findViewById(R.id.btnGuardar).setOnClickListener(v -> {
            String cidade = etCidade.getText().toString();
            String pais = etPais.getText().toString();
            String dataC = etDataChegada.getText().toString();

            if (cidade.isEmpty() || pais.isEmpty() || dataC.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            int idUser = SingletonGestor.getInstance(this).getUserIdLogado();

            // Criar Destino (Construtor original de 6 parâmetros)
            Destino novoDestino = new Destino(
                    0,              // id
                    idViagemAtual,  // plano_viagem_id
                    idUser,         // agente_viagem_id
                    cidade,         // nome_cidade
                    pais,           // pais
                    dataC           // data_chegada
            );

            SingletonGestor.getInstance(this).adicionarDestinoAPI(idViagemAtual, novoDestino);
            finish();
        });
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