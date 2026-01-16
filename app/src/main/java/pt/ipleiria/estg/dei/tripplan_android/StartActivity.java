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

        // CORREÇÃO: O ID no XML novo é btnLoginStart
        binding.btnLoginStart.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        // CORREÇÃO: O ID no XML novo é btnRegisterStart
        binding.btnRegisterStart.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}