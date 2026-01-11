package pt.ipleiria.estg.dei.tripplan_android; // ou .ui se mudaste a pasta

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarAtividadeActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarDestinoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarEstadiaActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarFotoActivity;
import pt.ipleiria.estg.dei.tripplan_android.ui.AdicionarTransporteActivity;

public class DetalhesViagemActivity extends AppCompatActivity implements SingletonGestor.DetalhesListener {

    private TextView tvTitulo, tvDatas, tvResumo;
    private int idViagem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_viagem); // Confirma se o nome do layout é este!

        // 1. Receber o ID
        idViagem = getIntent().getIntExtra("VIAGEM_ID", -1); // Nota: Na MainActivity usaste "VIAGEM_ID" ou "ID_VIAGEM"? Tem de ser igual!
        if (idViagem == -1) {
            Toast.makeText(this, "Erro: Viagem não encontrada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 2. Ligar Views
        tvTitulo = findViewById(R.id.txtDetalheTitulo); // Confirma os IDs no teu XML
        tvDatas = findViewById(R.id.txtDetalheDatas);
        tvResumo = findViewById(R.id.txtListaDestinos);

        // 3. Configurar Botões de Ação (Adicionar coisas)
        configurarBotoes();

        // 4. Pedir dados à API
        SingletonGestor.getInstance(this).setDetalhesListener(this);
        SingletonGestor.getInstance(this).getViagemDetalhesAPI(idViagem);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Atualizar quando voltamos de um formulário de adição
        SingletonGestor.getInstance(this).getViagemDetalhesAPI(idViagem);
    }

    private void configurarBotoes() {
        // Exemplo: Botão para Adicionar Transporte
        // Tens de ter estes botões no teu layout XML ou criar um Floating Action Menu

        findViewById(R.id.btnAdicionarTransporte).setOnClickListener(v -> abrirActivity(AdicionarTransporteActivity.class));
        findViewById(R.id.btnAdicionarDestino).setOnClickListener(v -> abrirActivity(AdicionarDestinoActivity.class));
        findViewById(R.id.btnAdicionarEstadia).setOnClickListener(v -> abrirActivity(AdicionarEstadiaActivity.class));
        findViewById(R.id.btnAdicionarAtividade).setOnClickListener(v -> abrirActivity(AdicionarAtividadeActivity.class));
        findViewById(R.id.btnAdicionarFoto).setOnClickListener(v -> abrirActivity(AdicionarFotoActivity.class));
    }

    private void abrirActivity(Class<?> activityClass) {
        Intent intent = new Intent(this, activityClass);
        intent.putExtra("ID_VIAGEM", idViagem); // Manda o ID para a próxima tela saber a quem adicionar
        startActivity(intent);
    }

    @Override
    public void onViagemDetalhesCarregados(Viagem viagem) {
        if (viagem != null) {
            tvTitulo.setText(viagem.getNomeViagem());
            tvDatas.setText(viagem.getDataInicio() + " - " + viagem.getDataFim());

            // Resumo rápido (Idealmente usarias RecyclerViews para listar tudo)
            String resumo = "Transportes: " + (viagem.getTransportes() != null ? viagem.getTransportes().size() : 0) + "\n" +
                    "Destinos: " + (viagem.getDestinos() != null ? viagem.getDestinos().size() : 0);

            tvResumo.setText(resumo);
        }
    }
}