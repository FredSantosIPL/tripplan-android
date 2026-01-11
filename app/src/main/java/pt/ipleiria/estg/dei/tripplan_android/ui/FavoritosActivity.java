package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Favorito;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.ui.adapters.FavoritosAdapter;

public class FavoritosActivity extends AppCompatActivity implements SingletonGestor.FavoritosListener {

    private RecyclerView recyclerView;
    private FavoritosAdapter adapter;
    private TextView tvVazio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // TRUQUE: Podes reutilizar o layout da Main se tiveres um RecyclerView lá,
        // ou cria um activity_favoritos.xml igual ao da Main mas com título "Favoritos".

        // Se usares activity_favoritos.xml, ajusta aqui:
        // setContentView(R.layout.activity_favoritos);

        // Configurar Header (se tiveres acesso ao ID do header)
        // TextView titulo = findViewById(R.id.tvTituloHeader);
        // if(titulo != null) titulo.setText("Os Meus Favoritos");

        recyclerView = findViewById(R.id.recyclerViewViagens); // Reutilizando ID ou cria novo
        tvVazio = findViewById(R.id.textSemViagens); // Reutilizando ID "Sem Viagens"

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoritosAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // Registar Listener e pedir dados
        SingletonGestor.getInstance(this).setFavoritosListener(this);
        SingletonGestor.getInstance(this).getFavoritosAPI();
    }

    @Override
    public void onRefreshFavoritos(ArrayList<Favorito> listaFavoritos) {
        if (listaFavoritos == null || listaFavoritos.isEmpty()) {
            tvVazio.setVisibility(View.VISIBLE);
            tvVazio.setText("Ainda não tens favoritos.");
        } else {
            tvVazio.setVisibility(View.GONE);
            adapter.updateList(listaFavoritos);
        }
    }
}