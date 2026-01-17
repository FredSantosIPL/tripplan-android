package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.MainActivity;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import pt.ipleiria.estg.dei.tripplan_android.databinding.ActivityLoginBinding;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginRequest;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginResponse;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
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
        findViewById(R.id.btnConfig).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, ConfigActivity.class));
        });


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

        // Link para o ecrã de registo
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

                    if (loginResponse != null && loginResponse.getToken() != null) {

                        // --- CORREÇÃO AQUI ---
                        int userId = loginResponse.getId();
                        String token = loginResponse.getToken();

                        // 1. Instanciar o Gestor numa variável
                        SingletonGestor gestor = SingletonGestor.getInstance(LoginActivity.this);

                        // 2. Usar a variável para guardar os dados
                        gestor.setUserIdLogado(userId);
                        gestor.setToken(token);
                        gestor.setUsernameLogado(loginResponse.getUsername());
                        gestor.setEmailLogado(loginResponse.getEmail());

                        Toast.makeText(LoginActivity.this, "Bem-vindo!", Toast.LENGTH_SHORT).show();

                        // 3. Mudar de Activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Erro: Token não encontrado.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(LoginActivity.this, "Login falhou: Verifique email/password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Erro de Rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}