package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Context;
import android.util.Log;
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
import pt.ipleiria.estg.dei.tripplan_android.models.SingletonGestor; // <--- Importa o Singleton

public class FotosAdapter extends RecyclerView.Adapter<FotosAdapter.ViewHolder> {

    private Context context;
    private ArrayList<FotoMemoria> listaFotos;

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

        if (foto.getFoto() != null && !foto.getFoto().isEmpty()) {

            // --- AQUI ESTÁ A MÁGICA ---
            // Em vez de BASE_URL_IMAGENS, chamamos o método do Singleton
            // Ele já faz o replace de "backend" para "frontend" e adiciona o IP certo
            String urlCompleta = SingletonGestor.getInstance(context).getUrlImagem(foto.getFoto());

            Log.d("ZECA_GLIDE", "Link Gerado Dinamicamente: " + urlCompleta);

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

    // ... ViewHolder igual ...
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