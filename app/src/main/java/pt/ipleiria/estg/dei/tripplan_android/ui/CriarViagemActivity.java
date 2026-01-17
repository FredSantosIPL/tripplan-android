package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
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
    private int idViagemEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCriarViagemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        idViagemEditar = getIntent().getIntExtra("ID_VIAGEM_EDITAR", -1);

        // --- 0. LÓGICA DE EDIÇÃO: Preencher campos ---
        if(idViagemEditar != -1) {
            binding.tvTituloPagina.setText("Editar Viagem");
            binding.btnGuardar.setText("ATUALIZAR");

            // Vamos buscar a viagem à lista local no Singleton para preencher os campos
            ArrayList<Viagem> viagens = SingletonGestor.getInstance(this).getViagensLocais();
            for (Viagem v : viagens) {
                if (v.getId() == idViagemEditar) {
                    binding.etTitulo.setText(v.getNomeViagem());
                    binding.etDataInicio.setText(v.getDataInicio());
                    binding.etDataFim.setText(v.getDataFim());
                    break;
                }
            }
        }

        // --- 1. CONFIGURAÇÃO DE DATAS ---
        binding.etDataInicio.setFocusable(false);
        binding.etDataInicio.setClickable(true);
        binding.etDataFim.setFocusable(false);
        binding.etDataFim.setClickable(true);

        configurarDatePicker(binding.etDataInicio);
        configurarDatePicker(binding.etDataFim);

        // --- 2. BOTÃO GUARDAR / ATUALIZAR ---
        binding.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = binding.etTitulo.getText().toString();
                String dataInicio = binding.etDataInicio.getText().toString();
                String dataFim = binding.etDataFim.getText().toString();

                if (nome.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty()) {
                    Toast.makeText(CriarViagemActivity.this, "Preenche os dados todos!", Toast.LENGTH_SHORT).show();
                } else {
                    if(idViagemEditar == -1) {
                        enviarViagemParaAPI(nome, dataInicio, dataFim);
                    } else {
                        // Chamamos o novo método de atualização
                        atualizarViagemNaAPI(nome, dataInicio, dataFim);
                    }
                }
            }
        });
    }

    private void enviarViagemParaAPI(String nomeViagem, String dataInicio, String dataFim) {
        int userId = SingletonGestor.getInstance(this).getUserIdLogado();
        Viagem novaViagem = new Viagem(0, userId, nomeViagem, dataInicio, dataFim);

        TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);
        Call<Viagem> call = service.adicionarViagem(novaViagem);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CriarViagemActivity.this, "Viagem criada!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(CriarViagemActivity.this, "Erro: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
                Toast.makeText(CriarViagemActivity.this, "Erro rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // --- NOVO MÉTODO PARA ATUALIZAR ---
    private void atualizarViagemNaAPI(String nomeViagem, String dataInicio, String dataFim) {
        int userId = SingletonGestor.getInstance(this).getUserIdLogado();
        // Criamos o objeto com o ID que recebemos do Intent para a API saber o que editar
        Viagem viagemEditada = new Viagem(idViagemEditar, userId, nomeViagem, dataInicio, dataFim);

        TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);
        // Usamos o método PUT da tua interface API
        Call<Viagem> call = service.atualizarViagem(idViagemEditar, viagemEditada);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(CriarViagemActivity.this, "Viagem atualizada!", Toast.LENGTH_SHORT).show();
                    finish(); // Fecha e volta para os detalhes
                } else {
                    Toast.makeText(CriarViagemActivity.this, "Erro ao atualizar: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
                Toast.makeText(CriarViagemActivity.this, "Erro rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void configurarDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            int ano = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(CriarViagemActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        String dataFormatada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        editText.setText(dataFormatada);
                    }, ano, mes, dia);
            datePicker.show();
        });
    }
}