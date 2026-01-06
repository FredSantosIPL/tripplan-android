package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;

// O nome da classe deve ser igual ao nome do ficheiro (ViagensAdapter)
public class ViagemAdapter extends RecyclerView.Adapter<ViagemAdapter.ViagemViewHolder> {

    private List<Viagem> listaViagens;

    public ViagemAdapter(List<Viagem> listaViagens) {
        this.listaViagens = listaViagens;
    }

    @NonNull
    @Override
    public ViagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // CORREÇÃO AQUI: Usamos 'parent.getContext()' para garantir que temos o contexto correto
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViagemViewHolder holder, int position) {
        Viagem viagem = listaViagens.get(position);

        // Agora mostramos o Nome, Data Início e Data Fim
        String texto = viagem.getNomeViagem() + "\n" + viagem.getDataInicio() + " até " + viagem.getDataFim();
        holder.tituloViagem.setText(texto);
    }

    @Override
    public int getItemCount() {
        return listaViagens.size();
    }

    public static class ViagemViewHolder extends RecyclerView.ViewHolder {
        TextView tituloViagem;

        public ViagemViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloViagem = itemView.findViewById(android.R.id.text1);
        }
    }
}