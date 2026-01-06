package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.MainActivity;
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
                // 1. Verificar se o servidor respondeu com SUCESSO (Código 200)
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();

                    // 2. Verificar se o corpo não veio vazio e se o token existe
                    if (loginResponse != null && loginResponse.getToken() != null) {

                        // SUCESSO! Guardar o token e mudar de ecrã
                        String token = loginResponse.getToken();

                        Toast.makeText(LoginActivity.this, "Login com sucesso!", Toast.LENGTH_SHORT).show();

                        // AQUI GUARDAS O TOKEN (SharedPreferences ou Singleton)
                        // SingletonGestor.getInstance(this).setToken(token);

                        // Mudar de Activity
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        // O servidor respondeu 200 OK, mas o JSON veio sem token (nomes errados?)
                        Toast.makeText(LoginActivity.this, "Erro: Token não encontrado na resposta.", Toast.LENGTH_LONG).show();
                    }

                } else {
                    // 3. O servidor respondeu ERRO (401 Unauthorized, 404, 500)
                    // Isto evita o NullPointerException quando erras a password!
                    Toast.makeText(LoginActivity.this, "Login falhou: Verifique email/password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Erro de rede (WAMP desligado, IP errado, etc.)
                Toast.makeText(LoginActivity.this, "Erro de Rede: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}