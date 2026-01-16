package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ViagemAdapter adapter;
    private List<Viagem> listaDeViagens;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Certifica-te que criaste o ficheiro fragment_home.xml
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        listaDeViagens = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerViewViagens);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Contexto aqui deve ser getContext()
        adapter = new ViagemAdapter(getContext(), listaDeViagens);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = view.findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(v -> startActivity(new Intent(getContext(), CriarViagemActivity.class)));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        carregarViagensDaAPI();
    }

    private void carregarViagensDaAPI() {
        Context context = getContext();
        if (context == null) return; // Segurança extra

        TripplanAPI service = ServiceBuilder.buildService(TripplanAPI.class);

        // CORREÇÃO: Usar 'context' em vez de 'this'
        int userId = SingletonGestor.getInstance(context).getUserIdLogado();

        Call<List<Viagem>> call = service.getAllViagens(userId);

        call.enqueue(new Callback<List<Viagem>>() {
            @Override
            public void onResponse(Call<List<Viagem>> call, Response<List<Viagem>> response) {
                if (!isAdded()) return; // Se o fragmento já fechou, não faz nada

                if (response.isSuccessful() && response.body() != null) {
                    listaDeViagens.clear();

                    // Filtragem Client-side (redundante se a API filtrar, mas seguro manter)
                    for (Viagem v : response.body()) {
                        if (v.getUserId() == userId) {
                            listaDeViagens.add(v);
                        }
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Erro na API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Viagem>> call, Throwable t) {
                if (!isAdded()) return;
                Toast.makeText(getContext(), "Falha de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}