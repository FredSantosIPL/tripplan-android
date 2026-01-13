package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarFotoActivity extends AppCompatActivity {

    private int idViagemAtual;
    private ImageView imgPreview;
    private EditText etDescricao;
    private File ficheiroFotoSelecionada = null; // O ficheiro que vamos enviar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_foto);

        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);

        imgPreview = findViewById(R.id.imgPreview);
        etDescricao = findViewById(R.id.etDescricao);
        Button btnSelecionar = findViewById(R.id.btnSelecionarFoto);
        Button btnGuardar = findViewById(R.id.btnGuardarFoto);

        // 1. Configurar o Seletor de Fotos (Galeria)
        ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Mostra a imagem no ecrã
                        imgPreview.setImageURI(uri);
                        // Converte a imagem num Ficheiro real para enviar
                        ficheiroFotoSelecionada = getFileFromUri(uri);
                    }
                }
        );

        // Botão para abrir galeria
        btnSelecionar.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        // Botão para enviar
        btnGuardar.setOnClickListener(v -> {
            String descricao = etDescricao.getText().toString();

            if (ficheiroFotoSelecionada == null) {
                Toast.makeText(this, "Seleciona uma foto primeiro!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chama o método de upload no Singleton
            SingletonGestor.getInstance(this).uploadFotoAPI(idViagemAtual, descricao, ficheiroFotoSelecionada);
            finish();
        });
    }

    // --- MÉTODO AUXILIAR: Transforma o URI da Galeria num Ficheiro Temporário ---
    private File getFileFromUri(Uri uri) {
        try {
            // 1. Descobrir o nome do ficheiro
            String nomeFicheiro = "foto_temp.jpg";
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nomeIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nomeIndex != -1) nomeFicheiro = cursor.getString(nomeIndex);
                cursor.close();
            }

            // 2. Criar um ficheiro temporário na pasta de cache da app
            File file = new File(getCacheDir(), nomeFicheiro);

            // 3. Copiar os dados da imagem para esse ficheiro
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}