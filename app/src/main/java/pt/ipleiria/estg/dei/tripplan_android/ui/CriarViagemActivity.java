package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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

    // Variável para saber se estamos a editar (vem do Intent)
    private int idViagemEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCriarViagemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 0. Verificar se é Edição (Opcional, mas já fica preparado)
        idViagemEditar = getIntent().getIntExtra("ID_VIAGEM_EDITAR", -1);
        if(idViagemEditar != -1) {
            binding.tvTituloPagina.setText("Editar Viagem");
            binding.btnGuardar.setText("ATUALIZAR");
            // Aqui poderias carregar os dados antigos para os campos...
        }

        // 1. Configurar os cliques nos campos de data (agora clica-se na caixa inteira)
        // Impedir o teclado de abrir nas datas
        binding.etDataInicio.setFocusable(false);
        binding.etDataInicio.setClickable(true);
        binding.etDataFim.setFocusable(false);
        binding.etDataFim.setClickable(true);

        configurarDatePicker(binding.etDataInicio);
        configurarDatePicker(binding.etDataFim);

        // 2. Configurar Botão Guardar (Novo ID: btnGuardar)
        binding.btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Novos IDs: etTitulo
                String nome = binding.etTitulo.getText().toString();
                String dataInicio = binding.etDataInicio.getText().toString();
                String dataFim = binding.etDataFim.getText().toString();

                if (nome.isEmpty() || dataInicio.isEmpty() || dataFim.isEmpty()) {
                    Toast.makeText(CriarViagemActivity.this, "Preenche os dados todos!", Toast.LENGTH_SHORT).show();
                } else {
                    if(idViagemEditar == -1) {
                        enviarViagemParaAPI(nome, dataInicio, dataFim);
                    } else {
                        // TODO: Implementar lógica de editar se quiseres
                        Toast.makeText(CriarViagemActivity.this, "Modo edição não implementado totalmente.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Se tiveres botão de voltar no layout (no moderno não puseste, usa-se o do telemóvel)
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

    private void configurarDatePicker(EditText editText) {
        editText.setOnClickListener(v -> {
            int ano = calendar.get(Calendar.YEAR);
            int mes = calendar.get(Calendar.MONTH);
            int dia = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePicker = new DatePickerDialog(CriarViagemActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        // Formato SQL: AAAA-MM-DD
                        String dataFormatada = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
                        editText.setText(dataFormatada);
                    }, ano, mes, dia);
            datePicker.show();
        });
    }
}