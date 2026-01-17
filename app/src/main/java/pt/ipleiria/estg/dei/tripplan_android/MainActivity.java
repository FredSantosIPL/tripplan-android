package pt.ipleiria.estg.dei.tripplan_android;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import pt.ipleiria.estg.dei.tripplan_android.ui.FavoritosFragment;
import pt.ipleiria.estg.dei.tripplan_android.ui.HomeFragment;
import pt.ipleiria.estg.dei.tripplan_android.ui.PerfilActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);

        // Configurar o Listener do Menu
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            }
            else if (itemId == R.id.nav_favorites) {
                selectedFragment = new FavoritosFragment();
            }
            else if (itemId == R.id.nav_profile) {
                // O Perfil continua a ser uma Activity, por isso usamos Intent
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                return false; // Retorna false para não selecionar a aba, já que mudámos de ecrã
            }


            // Trocar o Fragmento no ecrã
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            return false;
        });

        // Carregar a HOME por defeito quando a app abre (se não houver estado salvo)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNav.setSelectedItemId(R.id.nav_home);
        }
    }
}