package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale; // Importante para formatar a data

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Estadia;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarEstadiaActivity extends AppCompatActivity {

    private int idViagemAtual;
    private EditText etCheckIn; // Variável global para acedermos no DatePicker
    private final Calendar calendario = Calendar.getInstance(); // Objeto para gerir datas

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_estadia);

        // 1. Receber o ID da Viagem
        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);

        // 2. Ligar os campos do XML
        EditText etNome = findViewById(R.id.etNomeEstadia);
        EditText etTipo = findViewById(R.id.etMorada); // Estamos a usar este como "Tipo"
        etCheckIn = findViewById(R.id.etCheckIn);

        // 3. CONFIGURAR O CALENDÁRIO (Ao clicar no campo da data)
        etCheckIn.setOnClickListener(v -> mostrarCalendario());

        // 4. CONFIGURAR O BOTÃO DE GUARDAR
        findViewById(R.id.btnGuardarEstadia).setOnClickListener(v -> {
            String nome = etNome.getText().toString();
            String tipo = etTipo.getText().toString();
            String data = etCheckIn.getText().toString();

            // Validação simples
            if (nome.isEmpty() || tipo.isEmpty() || data.isEmpty()) {
                Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Criar o objeto (Atenção à ordem: nome, TIPO, DATA)
            Estadia nova = new Estadia(0, idViagemAtual, nome, tipo, data);

            // Enviar para a API
            SingletonGestor.getInstance(this).adicionarEstadiaAPI(nova);
            finish();
        });
    }

    private void mostrarCalendario() {
        // Pega na data atual para abrir o calendário no dia de hoje
        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        // Cria o pop-up do calendário
        DatePickerDialog datePicker = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    // O mês começa em 0 (Jan = 0), por isso somamos 1
                    // Usamos String.format para garantir o zero à esquerda (ex: 05 em vez de 5)
                    // Formato final: "2026-05-20"
                    String dataFormatada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);

                    // Escreve a data no campo de texto
                    etCheckIn.setText(dataFormatada);
                },
                ano, mes, dia);

        datePicker.show(); // Mostra o calendário no ecrã
    }
}