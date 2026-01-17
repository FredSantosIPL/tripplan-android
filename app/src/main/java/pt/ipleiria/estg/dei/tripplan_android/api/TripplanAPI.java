package pt.ipleiria.estg.dei.tripplan_android.api;

import java.util.List;

import pt.ipleiria.estg.dei.tripplan_android.models.Atividade;
import pt.ipleiria.estg.dei.tripplan_android.models.Destino;
import pt.ipleiria.estg.dei.tripplan_android.models.Estadia;
import pt.ipleiria.estg.dei.tripplan_android.models.Favorito;
import pt.ipleiria.estg.dei.tripplan_android.models.FotoMemoria;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginRequest;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginResponse;
import pt.ipleiria.estg.dei.tripplan_android.models.RegisterRequest;
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

    @POST("api/auth/signup")
    Call<Void> registarUtilizador(@Body RegisterRequest request);

    /* --- VIAGENS (Trips) --- */
    @GET("api/trips")
    Call<List<Viagem>> getAllViagens(@Query("user_id") int userId);

    @GET("api/trips/{id}")
    Call<Viagem> getDetalhesViagem(@Path("id") int id, @Query("expand") String expand);

    @POST("api/trips")
    Call<Viagem> adicionarViagem(@Body Viagem novaViagem);

    @PUT("api/trips/{id}")
    Call<Viagem> atualizarViagem(@Path("id") int id, @Body Viagem viagem);

    @DELETE("api/trips/{id}")
    Call<Void> apagarViagem(@Path("id") int id);

    /* --- SUB-ENTIDADES --- */
    /* Nota: Se o servidor der erro 404 nestes, experimenta adicionar 's'
       (ex: api/transportes), porque o Yii2 gosta de plurais.
       Mas mantive como tinhas. */

    @POST("api/transporte")
    Call<Transporte> adicionarTransporte(@Body Transporte transporte);

    @POST("api/destino")
    Call<Destino> adicionarDestino(@Body Destino destino);

    @POST("api/atividade")
    Call<Atividade> adicionarAtividade(@Body Atividade atividade);

    @POST("api/estadia")
    Call<Estadia> adicionarEstadia(@Body Estadia estadia);

    /* --- FAVORITOS --- */
    @GET("api/favoritos")
    Call<List<Favorito>> getFavoritos(@Query("user_id") int userId);

    @POST("api/favoritos")
    Call<Favorito> adicionarFavorito(@Body Favorito favorito);

    @DELETE("api/favoritos/{id}")
    Call<Void> removerFavorito(@Path("id") int id);

    /* --- FOTOS / MEMÓRIAS --- */

    @POST("api/fotos-memorias")
    Call<FotoMemoria> adicionarFoto(@Body FotoMemoria foto);

}