package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
// Confirma se o DetalhesViagemActivity está no package .ui
import pt.ipleiria.estg.dei.tripplan_android.DetalhesViagemActivity;

public class ViagemAdapter extends RecyclerView.Adapter<ViagemAdapter.ViagemViewHolder> {

    private Context context;
    private List<Viagem> listaViagens;

    public ViagemAdapter(Context context, List<Viagem> listaViagens) {
        this.context = context;
        this.listaViagens = listaViagens;
    }

    @NonNull
    @Override
    public ViagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_viagem, parent, false);
        return new ViagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViagemViewHolder holder, int position) {
        Viagem viagemAtual = listaViagens.get(position);

        holder.textTitulo.setText(viagemAtual.getNomeViagem());
        holder.textDatas.setText(viagemAtual.getDataInicio() + " -> " + viagemAtual.getDataFim());
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesViagemActivity.class);
            intent.putExtra("ID_VIAGEM", viagemAtual.getId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaViagens.size();
    }

    public void atualizarLista(List<Viagem> novaLista) {
        this.listaViagens = novaLista;
        notifyDataSetChanged();
    }

    public static class ViagemViewHolder extends RecyclerView.ViewHolder {

        TextView textTitulo;
        TextView textDatas;

        public ViagemViewHolder(@NonNull View itemView) {
            super(itemView);

            textTitulo = itemView.findViewById(R.id.textTituloViagem);
            textDatas = itemView.findViewById(R.id.textDatas);

        }
    }
}