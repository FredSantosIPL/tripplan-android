package pt.ipleiria.estg.dei.tripplan_android;

import android.content.Intent;
import android.graphics.Color; // <--- IMPORTANTE PARA AS CORES
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarAtividadeActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarDestinoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarEstadiaActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarFotoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarTransporteActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.CriarViagemActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.FotosAdapter;
import pt.ipleiria.estg.dei.tripplan_android.models.Atividade;
import pt.ipleiria.estg.dei.tripplan_android.models.Destino;
import pt.ipleiria.estg.dei.tripplan_android.models.Estadia;
import pt.ipleiria.estg.dei.tripplan_android.models.Favorito;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Transporte;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;

public class DetalhesViagemActivity extends AppCompatActivity implements SingletonGestor.DetalhesListener, SingletonGestor.FavoritosListener {

    private int idViagem;
    private TextView tvTitulo, tvDatas;
    private ImageButton btnFavorito; // O botão do coração

    private LinearLayout llDestinos, llAtividades, llTransportes, llEstadias;
    private RecyclerView rvFotos;
    private FotosAdapter fotosAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_viagem);

        idViagem = getIntent().getIntExtra("ID_VIAGEM", -1);
        if (idViagem == -1) {
            finish();
            return;
        }

        // Ligar Views
        tvTitulo = findViewById(R.id.tvTituloViagem);
        tvDatas = findViewById(R.id.tvDatasViagem);
        btnFavorito = findViewById(R.id.btnFavorito);

        llDestinos = findViewById(R.id.llListaDestinos);
        llAtividades = findViewById(R.id.llListaAtividades);
        llTransportes = findViewById(R.id.llListaTransportes);
        llEstadias = findViewById(R.id.llListaEstadias);

        rvFotos = findViewById(R.id.rvFotos);
        rvFotos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Configurações
        configurarBotoesAcao();
        configurarBotoesAdicionar();
        configurarBotaoFavorito();

        // Registar Listeners
        SingletonGestor.getInstance(this).setDetalhesListener(this);
        SingletonGestor.getInstance(this).setFavoritosListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SingletonGestor gestor = SingletonGestor.getInstance(this);
        gestor.getViagemDetalhesAPI(idViagem);
        gestor.getFavoritosAPI();
    }

    // --- LÓGICA DO FAVORITO COM DEBUG E CORES ---
    private void configurarBotaoFavorito() {
        if (btnFavorito == null) return;

        btnFavorito.setOnClickListener(v -> {
            SingletonGestor gestor = SingletonGestor.getInstance(this);
            int idFav = gestor.getFavoritoIdPorViagem(idViagem);

            // LOGS PARA VERES NO LOGCAT
            System.out.println("ZECA_DEBUG: Clicou na Estrela!");
            System.out.println("ZECA_DEBUG: ID Viagem Atual: " + idViagem);
            System.out.println("ZECA_DEBUG: ID Favorito Encontrado: " + idFav);

            if (idFav != -1) {
                // Já existe -> REMOVER
                System.out.println("ZECA_DEBUG: Ação -> REMOVER");

                // Muda visualmente logo
                btnFavorito.setImageResource(android.R.drawable.btn_star_big_off);
                btnFavorito.setColorFilter(Color.GRAY);

                gestor.removerFavoritoAPI(idFav);
            } else {
                // Não existe -> ADICIONAR
                System.out.println("ZECA_DEBUG: Ação -> ADICIONAR");

                // Muda visualmente logo
                btnFavorito.setImageResource(android.R.drawable.btn_star_big_on);
                btnFavorito.setColorFilter(Color.parseColor("#FFC107")); // Amarelo Ouro

                gestor.adicionarFavoritoAPI(idViagem);
            }
        });
    }

    @Override
    public void onRefreshFavoritos(ArrayList<Favorito> listaFavoritos) {
        atualizarIconeFavorito();
    }

    @Override
    public void onFavoritoAlterado() {
        atualizarIconeFavorito();
    }

    private void atualizarIconeFavorito() {
        if (btnFavorito == null) return;

        int idFav = SingletonGestor.getInstance(this).getFavoritoIdPorViagem(idViagem);

        System.out.println("ZECA_DEBUG: Atualizar Ícone -> ID Fav: " + idFav);

        if (idFav != -1) {
            // É FAVORITO: Estrela Cheia e Amarela
            btnFavorito.setImageResource(android.R.drawable.btn_star_big_on);
            btnFavorito.setColorFilter(Color.parseColor("#FFC107"));
        } else {
            // NÃO É FAVORITO: Estrela Vazia e Cinzenta
            btnFavorito.setImageResource(android.R.drawable.btn_star_big_off);
            btnFavorito.setColorFilter(Color.GRAY);
        }
    }

    // --- RESTO DO CÓDIGO (IGUAL) ---

    @Override
    public void onViagemDetalhesCarregados(Viagem viagem) {
        if (viagem == null) return;

        tvTitulo.setText(viagem.getNomeViagem());
        tvDatas.setText(viagem.getDataInicio() + "  até  " + viagem.getDataFim());

        preencherDestinos(viagem.getDestinos());
        preencherAtividades(viagem.getAtividades());
        preencherTransportes(viagem.getTransportes());
        preencherEstadias(viagem.getEstadias());

        if (viagem.getListaFotos() != null && !viagem.getListaFotos().isEmpty()) {
            fotosAdapter = new FotosAdapter(this, viagem.getListaFotos());
            rvFotos.setAdapter(fotosAdapter);
            rvFotos.setVisibility(View.VISIBLE);
        } else {
            rvFotos.setVisibility(View.GONE);
        }
    }

    // ... (Copiar os métodos preencherDestinos, preencherAtividades, etc. que já tinhas) ...
    // Vou colocar aqui os básicos para não dar erro

    private void preencherDestinos(ArrayList<Destino> lista) {
        llDestinos.removeAllViews();
        if (lista == null || lista.isEmpty()) {
            adicionarTextoVazio(llDestinos, "Sem destinos definidos.");
            return;
        }
        for (Destino d : lista) {
            View view = getLayoutInflater().inflate(R.layout.item_linha_simples, null);
            TextView tvPrincipal = view.findViewById(R.id.tvPrincipal);
            TextView tvSecundario = view.findViewById(R.id.tvSecundario);
            ImageView icon = view.findViewById(R.id.imgIcon);
            tvPrincipal.setText(d.getNomeCidade() + ", " + d.getPais());
            tvSecundario.setText("Chegada: " + (d.getDataChegada() != null ? d.getDataChegada() : "--/--"));
            icon.setImageResource(android.R.drawable.ic_dialog_map);
            llDestinos.addView(view);
        }
    }

    private void preencherAtividades(ArrayList<Atividade> lista) {
        llAtividades.removeAllViews();
        if (lista == null || lista.isEmpty()) { adicionarTextoVazio(llAtividades, "Sem atividades."); return; }
        for (Atividade a : lista) {
            View view = getLayoutInflater().inflate(R.layout.item_linha_simples, null);
            TextView tvP = view.findViewById(R.id.tvPrincipal);
            TextView tvS = view.findViewById(R.id.tvSecundario);
            ImageView icon = view.findViewById(R.id.imgIcon);
            tvP.setText(a.getNomeAtividade());
            tvS.setText(a.getTipo());
            icon.setImageResource(android.R.drawable.ic_menu_compass);
            llAtividades.addView(view);
        }
    }

    private void preencherTransportes(ArrayList<Transporte> lista) {
        llTransportes.removeAllViews();
        if (lista == null || lista.isEmpty()) { adicionarTextoVazio(llTransportes, "Sem transportes."); return; }
        for (Transporte t : lista) {
            View view = getLayoutInflater().inflate(R.layout.item_linha_simples, null);
            TextView tvP = view.findViewById(R.id.tvPrincipal);
            TextView tvS = view.findViewById(R.id.tvSecundario);
            ImageView icon = view.findViewById(R.id.imgIcon);
            tvP.setText(t.getTipo() + ": " + t.getOrigem() + " ➔ " + t.getDestino());
            tvS.setText("Partida: " + t.getDataPartida());
            icon.setImageResource(android.R.drawable.ic_menu_send);
            llTransportes.addView(view);
        }
    }

    private void preencherEstadias(ArrayList<Estadia> lista) {
        llEstadias.removeAllViews();
        if (lista == null || lista.isEmpty()) { adicionarTextoVazio(llEstadias, "Sem estadias."); return; }
        for (Estadia e : lista) {
            View view = getLayoutInflater().inflate(R.layout.item_linha_simples, null);
            TextView tvP = view.findViewById(R.id.tvPrincipal);
            TextView tvS = view.findViewById(R.id.tvSecundario);
            ImageView icon = view.findViewById(R.id.imgIcon);
            tvP.setText(e.getNomeAlojamento());
            tvS.setText("Check-in: " + e.getDataCheckin());
            icon.setImageResource(android.R.drawable.ic_menu_my_calendar);
            llEstadias.addView(view);
        }
    }

    private void adicionarTextoVazio(LinearLayout container, String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(Color.GRAY);
        tv.setTypeface(null, Typeface.ITALIC);
        tv.setPadding(0, 10, 0, 20);
        container.addView(tv);
    }

    private void configurarBotoesAcao() {
        View btnApagar = findViewById(R.id.btnApagarViagem);
        if (btnApagar != null) {
            btnApagar.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Apagar Viagem")
                        .setMessage("Tens a certeza?")
                        .setPositiveButton("Apagar", (dialog, which) -> {
                            SingletonGestor.getInstance(DetalhesViagemActivity.this).removerViagemAPI(idViagem, new SingletonGestor.GestaoViagemListener() {
                                @Override
                                public void onViagemRemovida() {
                                    Toast.makeText(DetalhesViagemActivity.this, "Viagem removida!", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                @Override
                                public void onErro(String mensagem) {
                                    Toast.makeText(DetalhesViagemActivity.this, "Erro: " + mensagem, Toast.LENGTH_LONG).show();
                                }
                            });
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
            });
        }
        View btnEditar = findViewById(R.id.btnEditarViagem);
        if (btnEditar != null) {
            btnEditar.setOnClickListener(v -> {
                Intent i = new Intent(this, CriarViagemActivity.class);
                i.putExtra("ID_VIAGEM_EDITAR", idViagem);
                startActivity(i);
            });
        }
    }

    private void configurarBotoesAdicionar() {
        findViewById(R.id.btnAddDestino).setOnClickListener(v -> navegarPara(AdicionarDestinoActivity.class));
        findViewById(R.id.btnAddAtividade).setOnClickListener(v -> navegarPara(AdicionarAtividadeActivity.class));
        findViewById(R.id.btnAddTransporte).setOnClickListener(v -> navegarPara(AdicionarTransporteActivity.class));
        findViewById(R.id.btnAddEstadia).setOnClickListener(v -> navegarPara(AdicionarEstadiaActivity.class));
        findViewById(R.id.btnAddFoto).setOnClickListener(v -> navegarPara(AdicionarFotoActivity.class));
    }

    private void navegarPara(Class<?> activityDestino) {
        Intent intent = new Intent(this, activityDestino);
        intent.putExtra("ID_VIAGEM", idViagem);
        startActivity(intent);
    }
}