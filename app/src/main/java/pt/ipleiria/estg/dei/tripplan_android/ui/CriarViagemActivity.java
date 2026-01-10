package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import pt.ipleiria.estg.dei.tripplan_android.databinding.ActivityCriarViagemBinding; // Confirma se o nome do XML é activity_criar_viagem.xml
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Transporte;
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

        calendar = Calendar.getInstance();

        // 1. Configurar os cliques nos calendários
        configurarDatePicker(binding.etDataInicio);
        configurarDatePicker(binding.etDataFim);
        // Também permitir clicar nos ícones
        binding.ivCalendarInicio.setOnClickListener(v -> binding.etDataInicio.performClick());
        binding.ivCalendarFim.setOnClickListener(v -> binding.etDataFim.performClick());

        // 2. Configurar Botão Criar
        binding.btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nome = binding.etNomeViagem.getText().toString();
                String dataInicio = binding.etDataInicio.getText().toString();
                String dataFim = binding.etDataFim.getText().toString();

                if (nome.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty()) {
                    Toast.makeText(CriarViagemActivity.this, "Preenche os dados da viagem!", Toast.LENGTH_SHORT).show();
                } else {
                    enviarViagemParaAPI(nome, dataInicio, dataFim);
                }
            }
        });

        // Botão de voltar (seta ou logo)
        binding.ivLogo.setOnClickListener(v -> finish());
    }

    private void enviarViagemParaAPI(String nomeViagem, String dataInicio, String dataFim) {
        // Obter ID do User (Segurança para não ir 0)
        int userId = SingletonGestor.getInstance(this).getUserIdLogado();
        if (userId == 0) userId = 1;

        // Criar Objeto Viagem
        Viagem novaViagem = new Viagem(0, userId, nomeViagem, dataInicio, dataFim);

        // Chamada à API
        TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);
        Call<Viagem> call = service.adicionarViagem(novaViagem);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // SUCESSO! A viagem foi criada.
                    int idNovaViagem = response.body().getId();

                    // Agora tentamos criar o Transporte associado
                    criarTransporte(idNovaViagem, dataInicio);
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

    private void criarTransporte(int idViagem, String dataPartida) {
        // --- AQUI ESTAVAM OS TEUS ERROS, AGORA CORRIGIDOS COM OS IDs DO XML ---

        // 1. Ler dados dos campos corretos
        String origem = binding.etTransporteOrigem.getText().toString();
        String destino = binding.etTransporteDestino.getText().toString();

        // Ler do Spinner (Assumindo que tens o array criado nos resources)
        String tipo = "";
        if (binding.spTransporteTipo.getSelectedItem() != null) {
            tipo = binding.spTransporteTipo.getSelectedItem().toString();
        }

        // 2. Se preencheu transporte, envia
        if (!origem.isEmpty() && !destino.isEmpty()) {

            // Construtor com 6 argumentos (id, plano_viagem_id, tipo, origem, destino, data)
            Transporte novoTransporte = new Transporte(0, idViagem, tipo, origem, destino, dataPartida);

            TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);
            Call<Transporte> call = service.adicionarTransporte(novoTransporte);

            call.enqueue(new Callback<Transporte>() {
                @Override
                public void onResponse(Call<Transporte> call, Response<Transporte> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(CriarViagemActivity.this, "Viagem e Transporte criados!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(CriarViagemActivity.this, "Viagem OK, Transporte Falhou.", Toast.LENGTH_SHORT).show();
                    }
                    finish(); // Fecha a janela
                }

                @Override
                public void onFailure(Call<Transporte> call, Throwable t) {
                    Toast.makeText(CriarViagemActivity.this, "Erro rede no transporte", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
        } else {
            // Se não preencheu transporte, finaliza só com a viagem
            Toast.makeText(CriarViagemActivity.this, "Viagem criada (sem transporte)", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // Método auxiliar para mostrar o calendário
    private void configurarDatePicker(final android.widget.EditText editText) {
        editText.setOnClickListener(v -> {
            new DatePickerDialog(CriarViagemActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        // Formatar a data para YYYY-MM-DD (Formato SQL)
                        String dataFormatada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        editText.setText(dataFormatada);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }
}