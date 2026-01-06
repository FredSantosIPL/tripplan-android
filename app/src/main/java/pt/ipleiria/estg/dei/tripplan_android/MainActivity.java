package pt.ipleiria.estg.dei.tripplan_android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import pt.ipleiria.estg.dei.tripplan_android.ui.CriarViagemActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.ViagemAdapter; // Confirmar se o teu ficheiro tem "s" no nome

// IMPORTS DO RETROFIT (Cura os erros vermelhos da imagem 9)
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ViagemAdapter adapter; // Se o teu ficheiro for ViagemAdapter, remove o 's'
    private List<Viagem> listaDeViagens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Configuração das margens para EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Inicializar a lista vazia (o onResume carregará os dados da API)
        listaDeViagens = new ArrayList<>();

        // 2. Configurar o RecyclerView
        recyclerView = findViewById(R.id.recyclerViewViagens);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ViagemAdapter(listaDeViagens);
        recyclerView.setAdapter(adapter);

        // 3. Configurar o Botão para abrir CriarViagemActivity
        Button btnCriar = findViewById(R.id.btnCriar);
        btnCriar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CriarViagemActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sempre que voltas a este ecrã, ele atualiza a lista vinda da BD
        carregarViagensDaAPI();
    }

    private void carregarViagensDaAPI() {
        TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);

        // Vai buscar o ID do utilizador que fez login no Singleton
        int userId = SingletonGestor.getInstance(this).getUserIdLogado();

        // Faz a chamada GET para obter as viagens
        Call<List<Viagem>> call = service.getAllViagens(userId);

        call.enqueue(new Callback<List<Viagem>>() {
            @Override
            public void onResponse(Call<List<Viagem>> call, Response<List<Viagem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Limpa a lista antiga e mete a nova que veio da API [cite: 13]
                    listaDeViagens.clear();
                    listaDeViagens.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MainActivity.this, "Erro na API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Viagem>> call, Throwable t) {
                // Erro de rede (ex: servidor desligado) [cite: 22]
                Toast.makeText(MainActivity.this, "Falha de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}