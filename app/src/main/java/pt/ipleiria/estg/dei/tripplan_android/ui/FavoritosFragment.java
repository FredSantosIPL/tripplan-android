package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class FavoritosFragment extends Fragment implements SingletonGestor.FavoritosListener {

    private RecyclerView rvFavoritos;
    private FavoritosAdapter adapter;

    // Mudou de TextView para View porque agora é um LinearLayout com ícone e texto
    private View layoutSemFavoritos;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Carrega o layout novo com cabeçalho azul
        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);

        // Ligar as Views pelos IDs novos do XML
        rvFavoritos = view.findViewById(R.id.rvFavoritos);
        layoutSemFavoritos = view.findViewById(R.id.layoutSemFavoritos);

        // Configurar RecyclerView
        if (getContext() != null) {
            rvFavoritos.setLayoutManager(new LinearLayoutManager(getContext()));
            // Inicializamos vazio, a API já vai preencher
            adapter = new FavoritosAdapter(getContext(), new ArrayList<>());
            rvFavoritos.setAdapter(adapter);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registar o listener e pedir dados sempre que o fragmento aparece
        if (getContext() != null) {
            SingletonGestor.getInstance(getContext()).setFavoritosListener(this);
            // Chama o método para buscar a lista
            SingletonGestor.getInstance(getContext()).getFavoritosAPI();
        }
    }

    // --- MÉTODOS DA INTERFACE (Obrigatórios) ---

    @Override
    public void onRefreshFavoritos(ArrayList<Favorito> listaFavoritos) {
        // Segurança: Verificar se o fragmento ainda existe
        if (!isAdded() || getContext() == null) return;

        if (listaFavoritos == null || listaFavoritos.isEmpty()) {
            // LISTA VAZIA: Mostra o layout do ícone e esconde a lista
            if (layoutSemFavoritos != null) layoutSemFavoritos.setVisibility(View.VISIBLE);
            if (rvFavoritos != null) rvFavoritos.setVisibility(View.GONE);
        } else {
            // TEM DADOS: Esconde o layout vazio e mostra a lista
            if (layoutSemFavoritos != null) layoutSemFavoritos.setVisibility(View.GONE);
            if (rvFavoritos != null) {
                rvFavoritos.setVisibility(View.VISIBLE);
                // Atualiza os dados no adapter
                adapter.updateList(listaFavoritos);
            }
        }
    }

    // Este era o método que faltava e dava erro!
    @Override
    public void onFavoritoAlterado() {
        // Se alguém clicou no coração noutro lado, recarregamos aqui
        if (getContext() != null) {
            SingletonGestor.getInstance(getContext()).getFavoritosAPI();
        }
    }
}