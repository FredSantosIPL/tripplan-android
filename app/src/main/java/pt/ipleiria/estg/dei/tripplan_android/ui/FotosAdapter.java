package pt.ipleiria.estg.dei.tripplan_android.ui; // <-- Confirma o package

import android.content.Context;
import android.util.Log; // Import do Log
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.FotoMemoria;
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor;

public class FotosAdapter extends RecyclerView.Adapter<FotosAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FotoMemoria> listaFotos;

    // --- ATENÇÃO AQUI ZEZOCA ---
    // 1. IP 10.0.2.2 é para o Emulador (Aponta para o localhost do teu PC)
    // 2. Porta :8888 é o padrão do MAMP. Se usares XAMPP remove o ":8888"
    // 3. NÃO metas 'uploads/' no fim, porque a base de dados já traz isso!
// O SEGREDO FINAL 🗝️
    public FotosAdapter(Context context, ArrayList<FotoMemoria> listaFotos) {
        this.context = context;
        this.listaFotos = listaFotos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_foto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FotoMemoria foto = listaFotos.get(position);

        holder.tvComentario.setText(foto.getComentario());

        // Verifica se o nome da foto existe
        if (foto.getFoto() != null && !foto.getFoto().isEmpty()) {

            String urlCompleta = SingletonGestor.getInstance(context).getUrlImagem(foto.getFoto());

            // Agora o log vai mostrar o IP que tu configuraste no ConfigActivity!
            Log.d("ZECA_GLIDE", "Link da Imagem Dinâmico: " + urlCompleta);

            Glide.with(context)
                    .load(urlCompleta)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .error(android.R.drawable.stat_notify_error)
                    .into(holder.imgFoto);
        }
    }

    @Override
    public int getItemCount() {
        return listaFotos.size();
    }

    public void atualizarLista(ArrayList<FotoMemoria> novaLista) {
        this.listaFotos = novaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFoto;
        TextView tvComentario;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            tvComentario = itemView.findViewById(R.id.tvComentario);
        }
    }
}