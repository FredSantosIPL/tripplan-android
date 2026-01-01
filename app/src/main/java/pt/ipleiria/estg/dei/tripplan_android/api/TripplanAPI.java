package pt.ipleiria.estg.dei.tripplan_android.api;

import pt.ipleiria.estg.dei.tripplan_android.models.LoginRequest;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginResponse;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TripplanAPI {

    @POST("api/criar_viagem.php")
    Call<Viagem> adicionarViagem(@Body Viagem novaViagem);

    // --- ADICIONA ESTE NOVO ---
    @POST("api/login.php")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);
}