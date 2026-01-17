package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
// OU se usares ListView: import android.widget.ListView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.tripplan_android.databinding.FragmentHomeBinding;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;


public class HomeFragment extends Fragment implements SingletonGestor.ViagensListener {

    private FragmentHomeBinding binding;
    private ViagemAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new ViagemAdapter(getContext(), new ArrayList<>());
        binding.recyclerViewViagens.setAdapter(adapter);

        binding.fabAdicionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), CriarViagemActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        SingletonGestor.getInstance(getContext()).setViagensListener(this);
        SingletonGestor.getInstance(getContext()).getAllViagensAPI();
    }

    @Override
    public void onRefreshLista(ArrayList<Viagem> listaViagens) {
        if (listaViagens != null) {
            Toast.makeText(getContext(), "HOME: Carreguei " + listaViagens.size() + " viagens!", Toast.LENGTH_SHORT).show();

            adapter = new ViagemAdapter(getContext(), listaViagens);

            binding.recyclerViewViagens.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.recyclerViewViagens.setAdapter(adapter);
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}