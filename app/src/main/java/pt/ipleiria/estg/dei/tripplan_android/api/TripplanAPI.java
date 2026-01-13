package pt.ipleiria.estg.dei.tripplan_android.api;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.ipleiria.estg.dei.tripplan_android.models.Atividade;
import pt.ipleiria.estg.dei.tripplan_android.models.Destino;
import pt.ipleiria.estg.dei.tripplan_android.models.Estadia;
import pt.ipleiria.estg.dei.tripplan_android.models.Favorito;
import pt.ipleiria.estg.dei.tripplan_android.models.FotoMemoria;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginRequest;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginResponse;
import pt.ipleiria.estg.dei.tripplan_android.models.RegisterRequest;
import pt.ipleiria.estg.dei.tripplan_android.models.Transporte;
import pt.ipleiria.estg.dei.tripplan_android.models.Utilizador;
import pt.ipleiria.estg.dei.tripplan_android.models.Viagem;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TripplanAPI {

    /* --- AUTENTICAÇÃO --- */
    @POST("api/auth/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);

    @POST("api/auth/signup")
    Call<Void> registarUtilizador(@Body RegisterRequest request);

    /* --- VIAGENS --- */

    // Listar todas


    @GET("api/trips") // Geralmente o Yii2 usa plural para coleções
    Call<List<Viagem>> getAllViagens(@Query("user_id") int userId);

    // Detalhes (Master/Detail) - Adicionado o prefixo api/
    // O ?expand diz ao backend para incluir as listas associadas (confirma se no PHP se chamam 'destino' e 'transporte')
    @GET("api/trips/{id}?expand=destino")
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

    /* --- transporte --- */
    @POST("api/transporte")
    Call<Transporte> adicionarTransporte(@Body Transporte transporte);

    /* --- destino --- */
    @POST("api/destino")
    Call<Destino> adicionarDestino(@Body Destino destino);

    /* --- atividade --- */
    @POST("api/atividade")
    Call<Atividade> adicionarAtividade(@Body Atividade atividade);

    /* --- estadia --- */
    @POST("api/estadia")
    Call<Estadia> adicionarEstadia(@Body Estadia estadia);

    /* --- FAVORITOS --- */
    // 1. Obter lista de favoritos do user (Podes passar o user_id ou usar o token)
    @GET("api/favoritos")
    Call<List<Favorito>> getFavoritos(@Query("user_id") int userId);

    // 2. Adicionar um favorito
    @POST("api/favoritos")
    Call<Favorito> adicionarFavorito(@Body Favorito favorito);

    // 3. Remover favorito (Opcional, mas útil)
    @DELETE("api/favoritos/{id}")
    Call<Void> removerFavorito(@Path("id") int id);

    @Multipart
    @POST("api/fotos-memorias")
    Call<FotoMemoria> uploadFoto(
            @Part("plano_viagem_id") RequestBody idViagem,
            @Part("comentario") RequestBody comentario,
            @Part("user_id") RequestBody userId,
            @Part MultipartBody.Part foto
    );
}