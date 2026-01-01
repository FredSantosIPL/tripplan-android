package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import pt.ipleiria.estg.dei.tripplan_android.databinding.ActivityLoginBinding; // Confirma se o nome do teu XML é activity_login.xml
import pt.ipleiria.estg.dei.tripplan_android.models.LoginRequest;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.etEmail.getText().toString();
                String pass = binding.etPassword.getText().toString();

                if (!email.isEmpty() && !pass.isEmpty()) {
                    fazerLogin(email, pass);
                } else {
                    Toast.makeText(LoginActivity.this, "Preenche os campos!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Link para o ecrã de registo (se tiveres)
        binding.tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        });
    }

    private void fazerLogin(String email, String password) {
        LoginRequest request = new LoginRequest(email, password);
        TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);
        Call<LoginResponse> call = service.fazerLogin(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();

                    if (loginResponse != null && loginResponse.getStatus().equals("sucesso")) {
                        Toast.makeText(LoginActivity.this, "Bem-vindo " + loginResponse.getNome(), Toast.LENGTH_SHORT).show();

                        // MUDAR DE ECRÃ: Vai para o Menu Principal (ou Criar Viagem)
                        Intent intent = new Intent(LoginActivity.this, CriarViagemActivity.class); // Ou MainActivity
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Dados incorretos!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Erro de ligação", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}