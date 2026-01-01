package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
            String telefone = binding.etTelefone.getText().toString();
            String morada = binding.etMorada.getText().toString();

            //Validações

            if (nome.isEmpty() || email.isEmpty() || pass.isEmpty() || passConfirm.isEmpty() || telefone.isEmpty() || morada.isEmpty()) {
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

            Toast.makeText(this, "Conta criada com sucesso!", Toast.LENGTH_SHORT).show();
            finish();

        });
    }
}

