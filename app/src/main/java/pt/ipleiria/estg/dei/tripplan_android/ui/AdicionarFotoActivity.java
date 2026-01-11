package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class AdicionarFotoActivity extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 101;

    private ImageView ivPreview;
    private EditText etComentario;
    private File ficheiroFotoAtual;
    private int idViagemAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_foto); // Cria este layout!

        idViagemAtual = getIntent().getIntExtra("ID_VIAGEM", -1);
        ivPreview = findViewById(R.id.ivPreviewFoto);
        etComentario = findViewById(R.id.etComentarioFoto);

        // Botão Tirar Foto
        findViewById(R.id.btnTirarFoto).setOnClickListener(v -> verificarPermissoes());

        // Botão Guardar
        findViewById(R.id.btnGuardarMemoria).setOnClickListener(v -> {
            String comentario = etComentario.getText().toString();
            if (ficheiroFotoAtual == null || !ficheiroFotoAtual.exists()) {
                Toast.makeText(this, "Tira uma foto primeiro!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Envia para o Singleton
            SingletonGestor.getInstance(this).uploadFotoAPI(idViagemAtual, comentario, ficheiroFotoAtual);
            finish();
        });
    }

    // 1. Verificar Permissões em tempo real
    private void verificarPermissoes() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        } else {
            abrirCamera();
        }
    }

    // 2. Resposta ao pedido de permissão
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamera();
            } else {
                Toast.makeText(this, "Precisamos da câmara para continuar.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 3. Lógica para criar ficheiro e abrir a app da Câmara
    private void abrirCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = criarFicheiroImagem();
            } catch (IOException ex) {
                Toast.makeText(this, "Erro ao criar ficheiro para foto.", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                // Usa o FileProvider que configurámos no Manifest
                Uri photoURI = FileProvider.getUriForFile(this,
                        "pt.ipleiria.estg.dei.tripplan_android.provider", // ATENÇÃO: Tem de bater certo com o Manifest
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // 4. Criar um ficheiro temporário vazio
    private File criarFicheiroImagem() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        ficheiroFotoAtual = image; // Guardamos a referência para o ficheiro
        return image;
    }

    // 5. Receber o resultado quando a câmara fecha
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            // Mostrar a imagem na ImageView
            Bitmap bitmap = BitmapFactory.decodeFile(ficheiroFotoAtual.getAbsolutePath());
            ivPreview.setImageBitmap(bitmap);
            // Nota: Se a imagem ficar rodada, é preciso ler o EXIF e rodar, mas para já serve.
        }
    }
}