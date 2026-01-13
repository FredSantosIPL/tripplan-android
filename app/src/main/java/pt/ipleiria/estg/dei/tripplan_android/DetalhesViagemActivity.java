package pt.ipleiria.estg.dei.tripplan_android; // Mantém o teu package original

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import pt.ipleiria.estg.dei.tripplan_android.models.Destino;
import pt.ipleiria.estg.dei.tripplan_android.models.Estadia; // Importante
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarAtividadeActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarDestinoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarEstadiaActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarFotoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarTransporteActivity;

public class DetalhesViagemActivity extends AppCompatActivity implements SingletonGestor.DetalhesListener {

    private TextView tvTitulo, tvDatas;
    private LinearLayout llContainer; // A nossa "prateleira" de cartões
    private int idViagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_viagem);

        // 1. Receber o ID (ATENÇÃO: Usei "ID_VIAGEM" para ser igual ao MainActivity)
        idViagem = getIntent().getIntExtra("ID_VIAGEM", -1);

        // Se falhar o primeiro, tenta com o outro nome por segurança
        if (idViagem == -1) {
            idViagem = getIntent().getIntExtra("VIAGEM_ID", -1);
        }

        if (idViagem == -1) {
            Toast.makeText(this, "Erro: Viagem não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ligar Views
        tvTitulo = findViewById(R.id.txtDetalheTitulo);
        tvDatas = findViewById(R.id.txtDetalheDatas);

        // AQUI ESTÁ A MUDANÇA: Em vez de TextView, usamos o LinearLayout
        llContainer = findViewById(R.id.llContainerDados);

        // 3. Configurar Botões
        configurarBotoes();

        // 4. Pedir dados à API
        SingletonGestor.getInstance(this).setDetalhesListener(this);
        SingletonGestor.getInstance(this).getViagemDetalhesAPI(idViagem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recarregar dados quando voltamos de adicionar algo
        SingletonGestor.getInstance(this).getViagemDetalhesAPI(idViagem);
    }

    private void configurarBotoes() {
        findViewById(R.id.btnAdicionarTransporte).setOnClickListener(v -> abrirActivity(AdicionarTransporteActivity.class));
        findViewById(R.id.btnAdicionarDestino).setOnClickListener(v -> abrirActivity(AdicionarDestinoActivity.class));
        findViewById(R.id.btnAdicionarEstadia).setOnClickListener(v -> abrirActivity(AdicionarEstadiaActivity.class));
        findViewById(R.id.btnAdicionarAtividade).setOnClickListener(v -> abrirActivity(AdicionarAtividadeActivity.class));
        findViewById(R.id.btnAdicionarFoto).setOnClickListener(v -> abrirActivity(AdicionarFotoActivity.class));
    }

    private void abrirActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("ID_VIAGEM", idViagem);
        startActivity(intent);
    }

    @Override
    public void onViagemDetalhesCarregados(Viagem viagem) {
        if (viagem != null) {
            System.out.println("ZEZOCA_DEBUG: Viagem carregada: " + viagem.getNomeViagem());

            // 1. Preencher Cabeçalho
            tvTitulo.setText(viagem.getNomeViagem());
            tvDatas.setText(viagem.getDataInicio() + " até " + viagem.getDataFim());

            // 2. Limpar a lista antiga
            llContainer.removeAllViews();

            // 3. Preencher DESTINOS
            List<Destino> destinos = viagem.getDestinos();

            // --- O ESPIÃO ESTÁ AQUI ---
            if (destinos == null) {
                System.out.println("ZEZOCA_DEBUG: A lista de destinos é NULL");
            } else {
                System.out.println("ZEZOCA_DEBUG: A lista tem " + destinos.size() + " destinos");
            }
            // --------------------------

            if (destinos != null && !destinos.isEmpty()) {
                adicionarTituloSeccao("DESTINOS");
                for (Destino d : destinos) {
                    System.out.println("ZEZOCA_DEBUG: A criar card para " + d.getNomeCidade());
                    criarCartao(d.getNomeCidade(), d.getPais() + " | " + d.getDataChegada());
                }
            } else {
                adicionarAvisoVazio("Sem destinos adicionados.");
            }
        } else {
            System.out.println("ZEZOCA_DEBUG: A Viagem veio NULL");
        }
    }

    private void criarCartao(String titulo, String descricao) {
        // Usa o layout 'item_simples.xml' que criámos no passo anterior
        View cardView = LayoutInflater.from(this).inflate(R.layout.item_simples, llContainer, false);

        TextView tvTit = cardView.findViewById(R.id.tvTituloCard);
        TextView tvDesc = cardView.findViewById(R.id.tvDescricaoCard);

        tvTit.setText(titulo);
        tvDesc.setText(descricao);

        // Adiciona à lista
        llContainer.addView(cardView);
    }

    private void adicionarTituloSeccao(String titulo) {
        TextView tv = new TextView(this);
        tv.setText(titulo);
        tv.setTextSize(16);
        tv.setPadding(16, 32, 16, 8);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setTextColor(getResources().getColor(R.color.black)); // Ou a tua cor primária
        llContainer.addView(tv);
    }

    private void adicionarAvisoVazio(String mensagem) {
        TextView tv = new TextView(this);
        tv.setText(mensagem);
        tv.setPadding(16, 16, 16, 16);
        tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
        llContainer.addView(tv);
    }


}