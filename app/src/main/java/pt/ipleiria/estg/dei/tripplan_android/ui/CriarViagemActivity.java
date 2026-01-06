package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

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
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCriarViagemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inicializar o calendário
        calendar = Calendar.getInstance();

// Quando clicar no campo Data Início
        binding.etDataInicio.setOnClickListener(v -> mostrarCalendario(true));

// Quando clicar no campo Data Fim
        binding.etDataFim.setOnClickListener(v -> mostrarCalendario(false));

        // --- Configurar o Botão "CRIAR VIAGEM" ---
        binding.btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = binding.etNomeViagem.getText().toString();
                String dataIda = binding.etDataInicio.getText().toString();
                String dataVolta = binding.etDataFim.getText().toString();

                // 1. Validar se os campos estão preenchidos
                if (validarCampos(nome, dataIda, dataVolta)) {
                    // 2. CHAMAR A API (Isto é o que conta para SIS!)
                    enviarViagemParaAPI(nome, dataIda, dataVolta);
                }
            }
        });


        // --- Configurar o Menu de Baixo (Opcional, para não dar erro se clicares) ---
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            Toast.makeText(this, "Menu clicado: " + item.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });
    }

    private void mostrarCalendario(final boolean isDataInicio) {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Formatar a data para YYYY-MM-DD
                String dataFormatada = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                if (isDataInicio) {
                    binding.etDataInicio.setText(dataFormatada);
                } else {
                    binding.etDataFim.setText(dataFormatada);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private boolean validarCampos(String destino, String inicio, String fim) {
        if (destino.isEmpty() || inicio.isEmpty() || fim.isEmpty()) {
            Toast.makeText(this, "Preenche todos os campos!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void enviarViagemParaAPI(String nomeViagem, String dataInicio, String dataFim) {

        //buscar o ID do utilizador que está logado
        int userId = SingletonGestor.getInstance(this).getUserIdLogado();

        // Criar o objeto Viagem
        Viagem novaViagem = new Viagem( 0, userId, nomeViagem, dataInicio, dataFim);

        // Chamar a API
        TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);
        Call<Viagem> call = service.adicionarViagem(novaViagem);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CriarViagemActivity.this, "Viagem criada com sucesso!", Toast.LENGTH_LONG).show();
                    finish(); // Fecha a atividade
                } else {
                    Toast.makeText(CriarViagemActivity.this, "Erro: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
                Toast.makeText(CriarViagemActivity.this, "Falha na rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}