package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Favorito;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.ui.adapters.FavoritosAdapter;

// 1. Implementar a interface do Listener
public class FavoritosFragment extends Fragment implements SingletonGestor.FavoritosListener {

    private RecyclerView recyclerView;
    private FavoritosAdapter adapter;
    private TextView tvVazio;

    // 2. Usar onCreateView em vez de onCreate
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Carrega o layout (tens de criar este XML, vê abaixo)
        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        recyclerView = view.findViewById(R.id.rvFavoritos);
        tvVazio = view.findViewById(R.id.tvSemFavoritos);

        // Usar getContext() em vez de 'this'
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FavoritosAdapter(getContext(), new ArrayList<>());
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registar o listener quando o fragmento aparece
        if (getContext() != null) {
            SingletonGestor.getInstance(getContext()).setFavoritosListener(this);
            SingletonGestor.getInstance(getContext()).getFavoritosAPI();
        }
    }

    @Override
    public void onRefreshFavoritos(ArrayList<Favorito> listaFavoritos) {
        // Verificar se o fragmento ainda está vivo para evitar crashes
        if (!isAdded()) return;

        if (listaFavoritos == null || listaFavoritos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvVazio.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter.updateList(listaFavoritos);
        }
    }
}