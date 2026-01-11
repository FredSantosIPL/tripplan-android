package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.content.Context; // <--- Importante
import android.content.Intent; // <--- Importante
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // <--- Importante
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pt.ipleiria.estg.dei.tripplan_android.R;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import pt.ipleiria.estg.dei.tripplan_android.DetalhesViagemActivity; // Confirma se o nome está certo

public class ViagemAdapter extends RecyclerView.Adapter<ViagemAdapter.ViagemViewHolder> {

    private List<Viagem> listaViagens;
    private Context context; // <--- 1. Adicionado para abrir a nova Activity

    // 2. Atualizei o construtor para receber o Context
    public ViagemAdapter(Context context, List<Viagem> listaViagens) {
        this.context = context;
        this.listaViagens = listaViagens;
    }

    @NonNull
    @Override
    public ViagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_viagem, parent, false);
        return new ViagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViagemViewHolder holder, int position) {
        Viagem viagemAtual = listaViagens.get(position);

        // 1. CORREÇÃO AQUI: Usar getNomeViagem()
        holder.textNomeCidade.setText(viagemAtual.getNomeViagem());

        // (Opcional) Se quiseres mostrar a data também:
        // holder.textData.setText(viagemAtual.getDataInicio());

        // --- LÓGICA DO BOTÃO VER ---
        holder.btnVer.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalhesViagemActivity.class);

            // 2. Passar o ID (isto já estava correto porque tens o getId())
            intent.putExtra("VIAGEM_ID", viagemAtual.getId());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaViagens.size();
    }

    public static class ViagemViewHolder extends RecyclerView.ViewHolder {

        TextView textNomeCidade;
        TextView btnVer;

        public ViagemViewHolder(@NonNull View itemView) {
            super(itemView);

            // Confirma se estes IDs estão no teu 'item_viagem.xml'
            textNomeCidade = itemView.findViewById(R.id.textTituloViagem); // Mudei para txtDestino (comum no item_viagem)
            btnVer = itemView.findViewById(R.id.btnVer); // <--- 5. Ligação do botão
        }
    }
}