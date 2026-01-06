package pt.ipleiria.estg.dei.tripplan_android.api;

import java.util.List;

import pt.ipleiria.estg.dei.tripplan_android.models.LoginRequest;
import pt.ipleiria.estg.dei.tripplan_android.models.LoginResponse;
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
    // NOTA: No Yii2, a rota de login geralmente não tem .php
    // Se criaste um controlador "AuthController" com action "Login", a rota é "auth/login"
    // Se estás a usar o standard do Yii2, confirma a rota correta. Vou assumir "login" ou "users/login".
    @POST("users/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);

    /* --- VIAGENS (CRUD COMPLETO) --- */

    // 1. Ler todas (READ) - Rota automática do Yii2
    @GET("trip")
    Call<List<Viagem>> getAllViagens(@Query("user_id") int userId);

    // 2. REQUISITO SIS: Master/Detail
    // Chama a actionDetails($id) que definimos no teu TripController.php
    @GET("trip/{id}/details")
    Call<Viagem> getViagemDetalhes(@Path("id") int id);

    // 3. Criar (CREATE) - Rota automática do Yii2
    // ATENÇÃO: Mudámos de 'api/criar_viagem.php' para 'trips'
    @POST("trip")
    Call<Viagem> adicionarViagem(@Body Viagem novaViagem);

    // 4. Atualizar (UPDATE) - Requisito obrigatório
    @PUT("trip/{id}")
    Call<Viagem> atualizarViagem(@Path("id") int id, @Body Viagem viagem);

    // 5. Apagar (DELETE) - Requisito obrigatório
    @DELETE("trip/{id}")
    Call<Void> apagarViagem(@Path("id") int id);
}