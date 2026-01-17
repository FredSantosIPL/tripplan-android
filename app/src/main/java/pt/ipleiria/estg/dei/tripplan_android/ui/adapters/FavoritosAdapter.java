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
        // Certifica-te que tens o layout 'item_favorito.xml' ou muda para 'item_linha_simples'
        View view = LayoutInflater.from(context).inflate(R.layout.item_favorito, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Favorito fav = favoritos.get(position);

        if (fav.getViagem() != null) {
            // Se no teu model Viagem.java o campo for getNomeViagem()
            // Se der erro, tenta fav.getViagem().getNome() ou o que tiveres lá
            String nome = fav.getViagem().getNomeViagem();

            if (nome != null && !nome.isEmpty()) {
                holder.tvDestino.setText(nome);
            } else {
                holder.tvDestino.setText("Viagem sem nome");
            }
        } else {
            // Se chegar aqui, o 'expand' falhou no PHP
            holder.tvDestino.setText("ID Viagem: #" + fav.getPlanoViagemId());
        }
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
            // Confirma se este ID existe no teu layout item_favorito.xml
            tvDestino = itemView.findViewById(R.id.tvFavoritoDestino);
        }
    }
}