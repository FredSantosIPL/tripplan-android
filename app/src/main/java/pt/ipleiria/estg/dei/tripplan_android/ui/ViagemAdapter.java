package pt.ipleiria.estg.dei.tripplan_android.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;

// Se o "R" ficar vermelho, adiciona esta linha (troca pelo nome do TEU pacote se for diferente)
import pt.ipleiria.estg.dei.tripplan_android.R;

public class ViagemAdapter extends RecyclerView.Adapter<ViagemAdapter.ViagemViewHolder> {

    private List<Viagem> listaViagens;

    public ViagemAdapter(List<Viagem> listaViagens) {
        this.listaViagens = listaViagens;
    }

    @NonNull
    @Override
    public ViagemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Perfeito: a carregar o layout do cartão que criaste
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activitiy_mostrar_viagem, parent, false);
        return new ViagemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViagemViewHolder holder, int position) {
        Viagem viagemAtual = listaViagens.get(position);

        // Aqui juntas o Título com a Data. Ex: "Férias em Paris, 12/05/2024"
        holder.textNomeCidade.setText(viagemAtual.getNomeViagem() + ", " + viagemAtual.getDataInicio());

        // Nota: A imagem vai aparecer sempre a mesma (a que puseste no XML)
        // porque ainda não estamos a mudar a imagem aqui via código.
        // Para já serve perfeitamente para testar o visual!
    }

    @Override
    public int getItemCount() {
        return listaViagens.size();
    }

    public static class ViagemViewHolder extends RecyclerView.ViewHolder {

        TextView textNomeCidade;
        ImageView imageCidade;

        public ViagemViewHolder(@NonNull View itemView) {
            super(itemView);

            textNomeCidade = itemView.findViewById(R.id.textTituloViagem);
            //imageCidade = itemView.findViewById(R.id.imageCidade);

        }
    }
}