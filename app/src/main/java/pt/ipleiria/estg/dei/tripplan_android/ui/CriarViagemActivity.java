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

        // --- Configurar o Botão "CRIAR VIAGEM" ---
        binding.btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Recolher dados da Viagem (Master)
                // Nota: O ID agora é etNomeViagem (antes era etDestino)
                String nome = binding.etNomeViagem.getText().toString();
                String dataIda = binding.etDataInicio.getText().toString();
                String dataVolta = binding.etDataFim.getText().toString();

                // 2. Recolher dados do Transporte (Detail)
                // O Spinner lê-se de forma diferente dos EditText
                String transTipo = binding.spTransporteTipo.getSelectedItem().toString();
                String transOrigem = binding.etTransporteOrigem.getText().toString();
                String transDestino = binding.etTransporteDestino.getText().toString();

                // 3. Validação Simples (Para não enviar vazio)
                if (nome.isEmpty()) {
                    Toast.makeText(CriarViagemActivity.this, "Falta o nome da viagem!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 4. MODO DEMONSTRAÇÃO (Simulação Master/Detail)
                // Preparamos o texto para mostrar ao professor
                String resumo = "VIAGEM (Master):\n" +
                        "- Nome: " + nome + "\n" +
                        "- Datas: " + dataIda + " até " + dataVolta + "\n\n" +
                        "TRANSPORTE (Detail):\n" +
                        "- Tipo: " + transTipo + "\n" +
                        "- Rota: " + transOrigem + " -> " + transDestino;

                // 5. Mostrar Janela de Sucesso
                new androidx.appcompat.app.AlertDialog.Builder(CriarViagemActivity.this)
                        .setTitle("Sucesso (Dados Recolhidos)")
                        .setMessage(resumo)
                        .setPositiveButton("OK - Voltar ao Menu", (dialog, which) -> {
                            // Fecha esta janela e volta atrás
                            finish();
                        })
                        .show();
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

    private void enviarViagemParaAPI(String nomeViagem, String dataInicio, String dataFim) {

        //buscar o ID do utilizador que está logado
        int userId = SingletonGestor.getInstance(this).getUserIdLogado();

        // Criar o objeto Viagem
        Viagem novaViagem = new Viagem(0, userId, nomeViagem, dataInicio, dataFim);

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