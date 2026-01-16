package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.models.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import pt.ipleiria.estg.dei.tripplan_android.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 1. Botão de Voltar (Seta no topo)
        binding.imgBack.setOnClickListener(v -> finish());

        // 2. Botão Registar
        // MUDANÇA: O ID no XML novo é 'btnRegister', não 'btnRegistarAction'
        binding.btnRegister.setOnClickListener(v -> {

            // MUDANÇA: IDs atualizados conforme o activity_register.xml moderno
            String nome = binding.etNome.getText().toString();
            String email = binding.etEmailRegister.getText().toString();   // Antes: etEmailRegisto
            String pass = binding.etPasswordRegister.getText().toString(); // Antes: etPassRegisto

            // Nota: O campo Telefone existe no XML (etTelefone) mas o RegisterRequest
            // atual só pede (nome, email, pass). Se quiseres enviar o telefone, tens de alterar o modelo.

            // --- VALIDAÇÕES ---
            // Removi a validação do passConfirm porque removemos esse campo do layout visual
            if (nome.isEmpty() || email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- CHAMADA À API ---
            RegisterRequest dadosRegisto = new RegisterRequest(nome, email, pass);
            TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);
            Call<Void> call = service.registarUtilizador(dadosRegisto);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish(); // Fecha o registo e volta ao Login
                    } else {
                        try {
                            String erroReal = "";
                            if (response.errorBody() != null) {
                                erroReal = response.errorBody().string();
                            }
                            Toast.makeText(RegisterActivity.this, "Erro: " + response.code(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(RegisterActivity.this, "Falha de conexão: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}