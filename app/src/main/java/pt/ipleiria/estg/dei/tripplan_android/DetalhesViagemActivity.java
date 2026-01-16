package pt.ipleiria.estg.dei.tripplan_android; // Mantém o teu package original

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import java.util.ArrayList;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Atividade;
import pt.ipleiria.estg.dei.tripplan_android.models.Destino;
import pt.ipleiria.estg.dei.tripplan_android.models.Estadia;
import pt.ipleiria.estg.dei.tripplan_android.models.FotoMemoria;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Transporte;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarAtividadeActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarDestinoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarFotoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarTransporteActivity;

public class DetalhesViagemActivity extends AppCompatActivity implements SingletonGestor.DetalhesListener {

    private int idViagem;
    private TextView tvTitulo, tvDatas;
    private LinearLayout llDestinos, llAtividades, llTransportes, llFotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_viagem);

        idViagem = getIntent().getIntExtra("ID_VIAGEM", -1);
        if (idViagem == -1) {
            finish();
            return;
        }

        // 1. Ligar as Views
        tvTitulo = findViewById(R.id.tvTituloViagem);
        tvDatas = findViewById(R.id.tvDatasViagem);
        llDestinos = findViewById(R.id.llListaDestinos);
        llAtividades = findViewById(R.id.llListaAtividades);
        llTransportes = findViewById(R.id.llListaTransportes);
        llFotos = findViewById(R.id.llListaFotos);

        // 2. Configurar Botões (Navegação)
        configurarBotoes();

        // 3. Registar o Listener e Pedir dados
        SingletonGestor.getInstance(this).setDetalhesListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sempre que voltamos a este ecrã, atualizamos os dados
        SingletonGestor.getInstance(this).getViagemDetalhesAPI(idViagem);
    }

    @Override
    public void onViagemDetalhesCarregados(Viagem viagem) {
        if (viagem == null) return;

        // Atualizar Cabeçalho
        tvTitulo.setText(viagem.getNomeViagem());
        tvDatas.setText(viagem.getDataInicio() + "  até  " + viagem.getDataFim());

        // Preencher as Listas
        preencherDestinos(viagem.getDestinos());
        preencherAtividades(viagem.getAtividades());
        preencherTransportes(viagem.getTransportes());
        preencherFotos(viagem.getListaFotos());
    }

    // --- MÉTODOS AUXILIARES PARA DESENHAR AS LISTAS ---

    private void preencherDestinos(ArrayList<Destino> lista) {
        llDestinos.removeAllViews(); // Limpar lista antiga
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
            tvSecundario.setText("Chegada: " + d.getDataChegada());
            icon.setImageResource(android.R.drawable.ic_dialog_map);

            llDestinos.addView(view);
        }
    }

    private void preencherAtividades(ArrayList<Atividade> lista) {
        llAtividades.removeAllViews();
        if (lista == null || lista.isEmpty()) {
            adicionarTextoVazio(llAtividades, "Sem atividades planeadas.");
            return;
        }

        for (Atividade a : lista) {
            View view = getLayoutInflater().inflate(R.layout.item_linha_simples, null);
            TextView tvPrincipal = view.findViewById(R.id.tvPrincipal);
            TextView tvSecundario = view.findViewById(R.id.tvSecundario);
            ImageView icon = view.findViewById(R.id.imgIcon);

            tvPrincipal.setText(a.getNomeAtividade());
            tvSecundario.setText(a.getTipo());
            icon.setImageResource(android.R.drawable.ic_menu_compass); // Ícone genérico

            llAtividades.addView(view);
        }
    }

    private void preencherTransportes(ArrayList<Transporte> lista) {
        llTransportes.removeAllViews();
        if (lista == null || lista.isEmpty()) {
            adicionarTextoVazio(llTransportes, "Sem transportes.");
            return;
        }

        for (Transporte t : lista) {
            View view = getLayoutInflater().inflate(R.layout.item_linha_simples, null);
            TextView tvPrincipal = view.findViewById(R.id.tvPrincipal);
            TextView tvSecundario = view.findViewById(R.id.tvSecundario);
            ImageView icon = view.findViewById(R.id.imgIcon);

            tvPrincipal.setText(t.getTipo() + ": " + t.getOrigem() + " -> " + t.getDestino());
            tvSecundario.setText(t.getDataPartida());
            icon.setImageResource(android.R.drawable.ic_menu_send);

            llTransportes.addView(view);
        }
    }

    // Para as fotos precisamos de uma biblioteca de imagem ou código manual.
    // Vou usar uma lógica simples com placeholders se não tiveres Glide/Picasso.
    private void preencherFotos(ArrayList<FotoMemoria> lista) {
        llFotos.removeAllViews();
        if (lista == null || lista.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Sem fotos.");
            tv.setPadding(0, 20, 0, 20);
            llFotos.addView(tv);
            return;
        }

        for (FotoMemoria f : lista) {
            // Cria uma ImageView programaticamente
            ImageView img = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300); // Tamanho fixo
            params.setMargins(0, 0, 16, 0);
            img.setLayoutParams(params);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setBackgroundColor(Color.LTGRAY);

            // NOTA: Para carregar a imagem real da URL, precisas do Glide ou Picasso.
            // Exemplo com Glide:
            // String urlCompleta = "http://10.0.2.2:8888/tripplan/frontend/web/uploads/" + f.getFoto();
            // Glide.with(this).load(urlCompleta).into(img);

            // Por agora, mostramos um placeholder
            img.setImageResource(android.R.drawable.ic_menu_camera);

            llFotos.addView(img);
        }
    }

    private void adicionarTextoVazio(LinearLayout container, String texto) {
        TextView tv = new TextView(this);
        tv.setText(texto);
        tv.setTextColor(Color.GRAY);
        tv.setPadding(0, 10, 0, 20);
        container.addView(tv);
    }

    private void configurarBotoes() {
        findViewById(R.id.btnAddDestino).setOnClickListener(v -> navegarPara(AdicionarDestinoActivity.class));
        findViewById(R.id.btnAddAtividade).setOnClickListener(v -> navegarPara(AdicionarAtividadeActivity.class));
        findViewById(R.id.btnAddTransporte).setOnClickListener(v -> navegarPara(AdicionarTransporteActivity.class));
        // findViewById(R.id.btnAddEstadia).setOnClickListener(v -> navegarPara(AdicionarEstadiaActivity.class)); // Se tiveres esta Activity
        findViewById(R.id.btnAddFoto).setOnClickListener(v -> navegarPara(AdicionarFotoActivity.class));
    }

    private void navegarPara(Class<?> activityDestino) {
        Intent intent = new Intent(this, activityDestino);
        intent.putExtra("ID_VIAGEM", idViagem);
        startActivity(intent);
    }
}