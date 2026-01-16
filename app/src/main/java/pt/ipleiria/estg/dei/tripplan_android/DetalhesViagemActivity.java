package pt.ipleiria.estg.dei.tripplan_android;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import pt.ipleiria.estg.dei.tripplan_android.models.Atividade;
import pt.ipleiria.estg.dei.tripplan_android.models.Destino;
import pt.ipleiria.estg.dei.tripplan_android.models.FotoMemoria;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Transporte;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarAtividadeActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarDestinoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarFotoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarTransporteActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.CriarViagemActivity; // Import necessário para o Editar

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

        // 2. Configurar Botões de Ação (Editar / Apagar)
        configurarBotoesAcao();

        // 3. Configurar Botões de Adicionar (+ Destino, + Atividade...)
        configurarBotoesAdicionar();

        // 4. Registar o Listener e Pedir dados
        SingletonGestor.getInstance(this).setDetalhesListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Sempre que voltamos a este ecrã, atualizamos os dados
        SingletonGestor.getInstance(this).getViagemDetalhesAPI(idViagem);
    }

    private void configurarBotoesAcao() {
        // Botão APAGAR
        View btnApagar = findViewById(R.id.btnApagarViagem);
        if (btnApagar != null) {
            btnApagar.setOnClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Apagar Viagem")
                        .setMessage("Tens a certeza? Isto não pode ser desfeito.")
                        .setPositiveButton("Apagar", (dialog, which) -> {

                            // CORREÇÃO: Passar o Listener para saber quando terminou
                            SingletonGestor.getInstance(DetalhesViagemActivity.this).removerViagemAPI(idViagem, new SingletonGestor.GestaoViagemListener() {
                                @Override
                                public void onViagemRemovida() {
                                    Toast.makeText(DetalhesViagemActivity.this, "Viagem removida!", Toast.LENGTH_SHORT).show();
                                    finish(); // Só fecha o ecrã se a API confirmar sucesso
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

        // Botão EDITAR
        View btnEditar = findViewById(R.id.btnEditarViagem);
        if (btnEditar != null) {
            btnEditar.setOnClickListener(v -> {
                // Reutilizamos a Activity de Criar, mas enviamos o objeto Viagem (ou só o ID)
                // O ideal seria criar uma Intent para uma "EditarViagemActivity", mas por falta de tempo:
                // Podes adaptar a CriarViagemActivity para receber um ID e preencher os campos.
                // Por agora, fica o Toast ou o Intent simples:
                Intent intent = new Intent(DetalhesViagemActivity.this, CriarViagemActivity.class);
                intent.putExtra("ID_VIAGEM_EDITAR", idViagem); // O Dev A tem de tratar disto na outra Activity
                startActivity(intent);
            });
        }
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
            // Verifica se a data não é null para evitar crash
            tvSecundario.setText("Chegada: " + (d.getDataChegada() != null ? d.getDataChegada() : "N/A"));
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
            icon.setImageResource(android.R.drawable.ic_menu_compass);

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
            ImageView img = new ImageView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, 300);
            params.setMargins(0, 0, 16, 0);
            img.setLayoutParams(params);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            img.setBackgroundColor(Color.LTGRAY);
            img.setImageResource(android.R.drawable.ic_menu_camera); // Placeholder
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

    private void configurarBotoesAdicionar() {
        findViewById(R.id.btnAddDestino).setOnClickListener(v -> navegarPara(AdicionarDestinoActivity.class));
        findViewById(R.id.btnAddAtividade).setOnClickListener(v -> navegarPara(AdicionarAtividadeActivity.class));
        findViewById(R.id.btnAddTransporte).setOnClickListener(v -> navegarPara(AdicionarTransporteActivity.class));
        // findViewById(R.id.btnAddEstadia).setOnClickListener(v -> navegarPara(AdicionarEstadiaActivity.class));
        findViewById(R.id.btnAddFoto).setOnClickListener(v -> navegarPara(AdicionarFotoActivity.class));
    }

    private void navegarPara(Class<?> activityDestino) {
        Intent intent = new Intent(this, activityDestino);
        intent.putExtra("ID_VIAGEM", idViagem);
        startActivity(intent);
    }
}