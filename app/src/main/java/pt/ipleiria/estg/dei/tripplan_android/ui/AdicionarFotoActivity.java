package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.FotoMemoria;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarFotoActivity extends AppCompatActivity {

    private int idViagemAtual;
    private ImageView imgPreview;
    private Uri imagemSelecionadaUri = null; // Guarda o caminho da foto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_foto);

        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);
        if (idViagemAtual == -1) {
            Toast.makeText(this, "Erro: Viagem inválida", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        imgPreview = findViewById(R.id.imgPreview);
        EditText etDescricao = findViewById(R.id.etDescricao);

        // 1. CONFIGURAR GALERIA
        ActivityResultLauncher<Intent> launcherGaleria = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imagemSelecionadaUri = result.getData().getData();
                        // Mostra na ImageView
                        imgPreview.setImageURI(imagemSelecionadaUri);
                        imgPreview.setPadding(0, 0, 0, 0);
                        imgPreview.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    }
                }
        );

        findViewById(R.id.btnSelecionarFoto).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            launcherGaleria.launch(intent);
        });

        // 2. BOTÃO GUARDAR
        findViewById(R.id.btnGuardarFoto).setOnClickListener(v -> {
            String descricao = etDescricao.getText().toString();

            if (descricao.isEmpty() || imagemSelecionadaUri == null) {
                Toast.makeText(this, "Escolhe uma foto e escreve uma descrição!", Toast.LENGTH_SHORT).show();
                return;
            }

            // CONVERTER A IMAGEM EM TEXTO (Base64)
            String imagemString = converterImagemParaBase64(imagemSelecionadaUri);

            if (imagemString == null) {
                Toast.makeText(this, "Erro ao processar imagem. Tente outra.", Toast.LENGTH_SHORT).show();
                return;
            }

            // CRIAR O OBJETO COMPLETO
            FotoMemoria novaMemoria = new FotoMemoria(
                    idViagemAtual,
                    descricao,
                    imagemString // Aqui vai a "tripa" de código da imagem
            );

            // Enviar para a API
            SingletonGestor.getInstance(this).adicionarFotoAPI(novaMemoria);
            finish();
        });
    }

    // --- MÉTODO MÁGICO DO ZECA ---
    // Transforma URI -> Bitmap -> Resize -> Base64 String
    private String converterImagemParaBase64(Uri imageUri) {
        try {
            // 1. Ler a imagem da galeria
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            // 2. Redimensionar se for muito grande (para não entupir a rede/memória)
            // Se a largura for maior que 1000px, reduzimos
            int maxDimension = 1000;
            if (bitmap.getWidth() > maxDimension || bitmap.getHeight() > maxDimension) {
                float scale = Math.min((float) maxDimension / bitmap.getWidth(), (float) maxDimension / bitmap.getHeight());
                int newWidth = Math.round(bitmap.getWidth() * scale);
                int newHeight = Math.round(bitmap.getHeight() * scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            }

            // 3. Comprimir para JPEG e converter para Bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream); // Qualidade 70%
            byte[] imageBytes = outputStream.toByteArray();

            // 4. Converter Bytes para String Base64
            // O flag NO_WRAP evita quebras de linha que podem baralhar o servidor
            return Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return null; // Deu erro
        }
    }
}