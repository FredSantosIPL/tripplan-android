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
    private Uri imagemSelecionadaUri = null;

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

        findViewById(R.id.btnGuardarFoto).setOnClickListener(v -> {
            String descricao = etDescricao.getText().toString();

            if (descricao.isEmpty() || imagemSelecionadaUri == null) {
                Toast.makeText(this, "Escolhe uma foto e escreve uma descrição!", Toast.LENGTH_SHORT).show();
                return;
            }

            String imagemString = converterImagemParaBase64(imagemSelecionadaUri);

            if (imagemString == null) {
                Toast.makeText(this, "Erro ao processar imagem. Tente outra.", Toast.LENGTH_SHORT).show();
                return;
            }

            FotoMemoria novaMemoria = new FotoMemoria(
                    idViagemAtual,
                    descricao,
                    imagemString
            );

            SingletonGestor.getInstance(this).adicionarFotoAPI(novaMemoria);
            finish();
        });
    }

    private String converterImagemParaBase64(Uri imageUri) {
        try {

            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            int maxDimension = 1000;
            if (bitmap.getWidth() > maxDimension || bitmap.getHeight() > maxDimension) {
                float scale = Math.min((float) maxDimension / bitmap.getWidth(), (float) maxDimension / bitmap.getHeight());
                int newWidth = Math.round(bitmap.getWidth() * scale);
                int newHeight = Math.round(bitmap.getHeight() * scale);
                bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream);
            byte[] imageBytes = outputStream.toByteArray();

            return Base64.encodeToString(imageBytes, Base64.NO_WRAP);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}