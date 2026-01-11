package pt.ipleiria.estg.dei.tripplan_android.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Favorito;

public class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Favorito> favoritos;

    public FavoritosAdapter(Context context, ArrayList<Favorito> favoritos) {
        this.context = context;
        this.favoritos = favoritos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favorito fav = favoritos.get(position);

        // Se a API devolver o objeto Destino dentro do Favorito, podes fazer fav.getDestino().getNome()
        // Por agora, mostramos o ID para testar
        holder.tvDestino.setText("Destino Favorito #" + fav.getDestinoId());
    }

    @Override
    public int getItemCount() {
        return favoritos.size();
    }

    public void updateList(ArrayList<Favorito> novaLista) {
        this.favoritos = novaLista;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDestino;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDestino = itemView.findViewById(R.id.tvFavoritoDestino);
        }
    }
}