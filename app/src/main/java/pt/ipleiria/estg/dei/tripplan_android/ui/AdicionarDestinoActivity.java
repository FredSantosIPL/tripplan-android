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
    private EditText etData;
    private final Calendar calendario = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_destino);

        // 1. Receber o ID da Viagem (Passado pela Activity anterior)
        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);

        EditText etCidade = findViewById(R.id.etCidade);
        EditText etPais = findViewById(R.id.etPais);
        etData = findViewById(R.id.etDataChegada);

        // Configurar Calendário (Tal como na Estadia)
        etData.setOnClickListener(v -> mostrarCalendario());

        findViewById(R.id.btnSalvarDestino).setOnClickListener(v -> {
            String cidade = etCidade.getText().toString();
            String pais = etPais.getText().toString();
            String data = etData.getText().toString();

            if (cidade.isEmpty() || pais.isEmpty() || data.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Obter o ID do utilizador logado
            int idUser = SingletonGestor.getInstance(this).getUserIdLogado();

            // 2. CRIAR DESTINO COM O ID DA VIAGEM
            Destino novoDestino = new Destino(
                    0,
                    idViagemAtual, // <--- O CAMPO QUE FALTAVA (plano_viagem_id)
                    idUser,        // agente_viagem_id
                    cidade,
                    pais,
                    data
            );

            SingletonGestor.getInstance(this).adicionarDestinoAPI(idViagemAtual, novoDestino);
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