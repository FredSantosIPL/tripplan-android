package pt.ipleiria.estg.dei.tripplan_android;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.databinding.ActivityStartBinding;
import pt.ipleiria.estg.dei.tripplan_android.ui.LoginActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.RegisterActivity;

public class StartActivity extends AppCompatActivity {

    private ActivityStartBinding binding; // ViewBinding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Clique no botão Entrar -> Vai para LoginActivity
        binding.btnEntrar.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // Clique no botão Registar
        binding.btnRegistar.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}