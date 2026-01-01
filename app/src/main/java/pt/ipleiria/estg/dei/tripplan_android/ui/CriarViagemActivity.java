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
        // Configuração do ViewBinding em Java
        binding = ActivityCriarViagemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        calendar = Calendar.getInstance();

        // 1. Cliques para abrir o calendário
        binding.etDataInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarCalendario(true);
            }
        });
        binding.ivCalendarInicio.setOnClickListener(v -> mostrarCalendario(true));

        binding.etDataFim.setOnClickListener(v -> mostrarCalendario(false));
        binding.ivCalendarFim.setOnClickListener(v -> mostrarCalendario(false));

        // 2. Clique no botão Criar
        binding.btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String destino = binding.etDestino.getText().toString();
                String dataInicio = binding.etDataInicio.getText().toString();
                String dataFim = binding.etDataFim.getText().toString();

                if (validarCampos(destino, dataInicio, dataFim)) {
                    enviarViagemParaAPI(destino, dataInicio, dataFim);
                }
            }
        });
    }

    private void mostrarCalendario(final boolean isDataInicio) {
        new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                // Formatar a data para YYYY-MM-DD
                String dataFormatada = year + "-" + (month + 1) + "-" + dayOfMonth;
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

    private void enviarViagemParaAPI(String destino, String inicio, String fim) {
        // Criar o objeto Viagem
        Viagem novaViagem = new Viagem(destino, inicio, fim);

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