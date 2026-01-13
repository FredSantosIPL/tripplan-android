package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

// Imports necessários
import pt.ipleiria.estg.dei.tripplan_android.models.RegisterRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
// O LoginResponse já não é estritamente necessário se usarmos Void, mas podes manter o import
import pt.ipleiria.estg.dei.tripplan_android.models.LoginResponse;
import pt.ipleiria.estg.dei.tripplan_android.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegistarAction.setOnClickListener(v -> {
            String nome = binding.etNome.getText().toString();
            String email = binding.etEmailRegisto.getText().toString();
            String pass = binding.etPassRegisto.getText().toString();
            String passConfirm = binding.etPassConfirm.getText().toString();

            // --- VALIDAÇÕES ---
            if (nome.isEmpty() || email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty() ) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                Toast.makeText(this, "Email inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!pass.equals(passConfirm)){
                Toast.makeText(this, "As passwords não coincidem", Toast.LENGTH_SHORT).show();
                return;
            }

            // --- CHAMADA À API ---
            RegisterRequest dadosRegisto = new RegisterRequest(nome, email, pass);

            TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);

            // CORREÇÃO AQUI: Mudámos de <LoginResponse> para <Void>
            Call<Void> call = service.registarUtilizador(dadosRegisto);

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        try {
                            String erroReal = response.errorBody().string();

                            // Log para debug
                            android.util.Log.e("ERRO_REGISTO", "Código: " + response.code());
                            android.util.Log.e("ERRO_REGISTO", "Mensagem: " + erroReal);

                            Toast.makeText(RegisterActivity.this, "Erro " + response.code() + ": " + erroReal, Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this, "Erro desconhecido no registo.", Toast.LENGTH_SHORT).show();
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