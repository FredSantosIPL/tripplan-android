package pt.ipleiria.estg.dei.tripplan_android;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray; // <--- NOVO
import org.json.JSONObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DetalhesViagemActivity extends AppCompatActivity {

    private TextView txtTitulo, txtDatas, txtListaDestinos; // <--- Adicionei txtListaDestinos

    // CONFIRMA O TEU IP
    private String URL_API = "http://192.168.1.237/api/detalhes_viagem.php?id=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_viagem);

        txtTitulo = findViewById(R.id.txtDetalheTitulo);
        txtDatas = findViewById(R.id.txtDetalheDatas);

        // Vamos ligar ao TextView onde diz "Adiciona as cidades..."
        txtListaDestinos = findViewById(R.id.txtListaDestinos);

        int viagemId = getIntent().getIntExtra("VIAGEM_ID", -1);

        if (viagemId != -1) {
            carregarDadosDaViagem(viagemId);
        } else {
            Toast.makeText(this, "Erro ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarDadosDaViagem(int id) {
        new Thread(() -> {
            try {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(URL_API + id).build();
                Response response = client.newCall(request).execute();

                if (response.isSuccessful()) {
                    String jsonResposta = response.body().string();
                    JSONObject jsonObject = new JSONObject(jsonResposta);

                    // 1. Dados Básicos
                    String nome = jsonObject.optString("nome_viagem", "Sem Nome");
                    String dataInicio = jsonObject.optString("data_inicio", "--");
                    String dataFim = jsonObject.optString("data_fim", "--");

                    // 2. Processar a Lista de Destinos
                    JSONArray arrayDestinos = jsonObject.optJSONArray("lista_destinos");
                    StringBuilder textoDestinos = new StringBuilder();

                    if (arrayDestinos != null && arrayDestinos.length() > 0) {
                        for (int i = 0; i < arrayDestinos.length(); i++) {
                            JSONObject destino = arrayDestinos.getJSONObject(i);
                            // Pega o nome da cidade (confirma se na BD é 'nome', 'cidade' ou 'local')
                            String nomeCidade = destino.optString("nome", "Cidade"); // <-- VERIFICA O NOME DA COLUNA NA BD
                            String pais = destino.optString("pais", ""); // Opcional

                            textoDestinos.append("📍 ").append(nomeCidade).append("\n");
                        }
                    } else {
                        textoDestinos.append("Sem destinos adicionados.");
                    }

                    // 3. Atualizar Ecrã
                    runOnUiThread(() -> {
                        txtTitulo.setText(nome);
                        txtDatas.setText("📅 " + dataInicio + " até " + dataFim);

                        // Aqui o texto "Adiciona cidades..." vai ser substituído por "Barcelona"
                        txtListaDestinos.setText(textoDestinos.toString());
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}