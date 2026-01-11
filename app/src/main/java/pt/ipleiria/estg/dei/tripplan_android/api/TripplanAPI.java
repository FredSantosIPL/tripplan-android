package pt.ipleiria.estg.dei.tripplan_android.api;

import java.util.List;

import pt.ipleiria.estg.dei.tripplan_android.models.LoginRequest;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginResponse;
import pt.ipleiria.estg.dei.tripplan_android.models.Transporte;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TripplanAPI {

    /* --- AUTENTICAÇÃO --- */
    @POST("api/auth/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);

    /* --- VIAGENS --- */

    // Listar todas
    @GET("api/trips") // Geralmente o Yii2 usa plural para coleções
    Call<List<Viagem>> getAllViagens(@Query("user_id") int userId);

    // Detalhes (Master/Detail) - Adicionado o prefixo api/
    @GET("api/trips/{id}/details")
    Call<Viagem> getViagemDetalhes(@Path("id") int id);

    // Criar (No REST padrão do Yii2, é apenas POST no endpoint base)
    @POST("api/trips")
    Call<Viagem> adicionarViagem(@Body Viagem novaViagem);

    // Atualizar
    @PUT("api/trips/{id}")
    Call<Viagem> atualizarViagem(@Path("id") int id, @Body Viagem viagem);

    // Apagar
    @DELETE("api/trips/{id}")
    Call<Void> apagarViagem(@Path("id") int id);

    /* --- TRANSPORTES --- */
    @POST("api/transportes")
    Call<Transporte> adicionarTransporte(@Body Transporte transporte);
}