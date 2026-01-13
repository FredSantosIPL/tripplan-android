package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText; // Necessário para o método auxiliar
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import pt.ipleiria.estg.dei.tripplan_android.databinding.ActivityCriarViagemBinding;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CriarViagemActivity extends AppCompatActivity {

    private ActivityCriarViagemBinding binding;
    private final Calendar calendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCriarViagemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Configurar os cliques nos campos de data e ícones
        configurarDatePicker(binding.etDataInicio);
        configurarDatePicker(binding.etDataFim);

        binding.ivCalendarInicio.setOnClickListener(v -> binding.etDataInicio.performClick());
        binding.ivCalendarFim.setOnClickListener(v -> binding.etDataFim.performClick());

        // 2. Configurar Botão Criar
        binding.btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = binding.etNomeViagem.getText().toString();
                String dataInicio = binding.etDataInicio.getText().toString();
                String dataFim = binding.etDataFim.getText().toString();

                // Validações básicas
                if (nome.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty()) {
                    Toast.makeText(CriarViagemActivity.this, "Preenche os dados da viagem!", Toast.LENGTH_SHORT).show();
                } else {
                    enviarViagemParaAPI(nome, dataInicio, dataFim);
                }
            }
        });

        // Botão de voltar (clique no logo)
        binding.ivLogo.setOnClickListener(v -> finish());
    }

    private void enviarViagemParaAPI(String nomeViagem, String dataInicio, String dataFim) {
        // Obter ID do User logado
        int userId = SingletonGestor.getInstance(this).getUserIdLogado();

        // Criar Objeto Viagem (ID 0 porque é nova)
        Viagem novaViagem = new Viagem(0, userId, nomeViagem, dataInicio, dataFim);

        // Chamada à API
        TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);
        Call<Viagem> call = service.adicionarViagem(novaViagem);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // SUCESSO!
                    Toast.makeText(CriarViagemActivity.this, "Viagem criada com sucesso!", Toast.LENGTH_SHORT).show();

                    // Fecha a atividade e volta à lista principal
                    finish();
                } else {
                    Toast.makeText(CriarViagemActivity.this, "Erro ao criar viagem: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
                Toast.makeText(CriarViagemActivity.this, "Erro de rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Método auxiliar para abrir o calendário
    private void configurarDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            int ano = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(CriarViagemActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        // Formatar data para AAAA-MM-DD (Formato MySQL)
                        String dataFormatada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        editText.setText(dataFormatada);
                    }, ano, mes, dia);
            datePicker.show();
        });
    }
}