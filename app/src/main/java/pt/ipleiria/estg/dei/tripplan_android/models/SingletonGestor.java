package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingletonGestor {
    private static SingletonGestor instance = null;
    private TripPlanBDHelper bdHelper = null;
    private SQLiteDatabase database = null;
    private Context context;

    // --- VARIÁVEIS PARA A API (SIS) ---
    private ArrayList<Viagem> viagens;
    private TripplanAPI apiService;
    private ViagensListener viagensListener;

    // Variáveis de Sessão
    private int userIdLogado = 0;
    private String token = null;

    // Listeners Específicos
    private DetalhesListener detalhesListener; // Listener para os Detalhes
    private FavoritosListener favoritosListener; // Listener para os Favoritos

    private SingletonGestor(Context context){
        this.context = context;
        this.viagens = new ArrayList<>();

        // 1. Inicializar Base de Dados Local
        bdHelper = new TripPlanBDHelper(context);

        // 2. Inicializar a API
        apiService = ServiceBuilder.buildService(TripplanAPI.class);
    }

    public static synchronized SingletonGestor getInstance(Context context){
        if(instance == null){
            instance = new SingletonGestor(context);
        }
        return instance;
    }

    // --- INTERFACE LISTENER LISTA PRINCIPAL ---
    public interface ViagensListener {
        void onRefreshLista(ArrayList<Viagem> listaViagens);
    }

    public void setViagensListener(ViagensListener listener) {
        this.viagensListener = listener;
    }

    public ArrayList<Viagem> getViagensLocais() {
        return new ArrayList<>(viagens);
    }

    /* ======================================================
       GESTÃO DE SESSÃO
       ====================================================== */

    public void setUserIdLogado(int id) {
        this.userIdLogado = id;
    }

    public int getUserIdLogado() {
        return userIdLogado;
    }

    public void setToken(String token) {
        this.token = token;
        SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TOKEN_API", token);
        editor.apply();
    }

    public String getToken() {
        if (this.token == null) {
            SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);
            this.token = prefs.getString("TOKEN_API", null);
        }
        return this.token;
    }

    /* ======================================================
       MÉTODOS DA API
       ====================================================== */

    // 1. READ (LISTA)
    public void getAllViagensAPI() {
        if (!isInternetAvailable()) {
            Toast.makeText(context, "Sem internet", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<List<Viagem>> call = apiService.getAllViagens(userIdLogado);
        call.enqueue(new Callback<List<Viagem>>() {
            @Override
            public void onResponse(Call<List<Viagem>> call, Response<List<Viagem>> response) {
                if (response.isSuccessful()) {
                    viagens = (ArrayList<Viagem>) response.body();
                    if (viagensListener != null) {
                        viagensListener.onRefreshLista(viagens);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Viagem>> call, Throwable t) {
                Toast.makeText(context, "Erro API: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // 2. CREATE VIAGEM
    public void adicionarViagemAPI(Viagem viagem) {
        Call<Viagem> call = apiService.adicionarViagem(viagem);
        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful()) {
                    Viagem nova = response.body();
                    viagens.add(nova);
                    Toast.makeText(context, "Viagem criada!", Toast.LENGTH_SHORT).show();

                    if (viagensListener != null) {
                        viagensListener.onRefreshLista(viagens);
                    }
                }
            }
            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
                Toast.makeText(context, "Erro ao criar: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 3. MASTER/DETAIL (GET COMPLETO) - [ATUALIZADO AQUI]
    public interface DetalhesListener {
        void onViagemDetalhesCarregados(Viagem viagem);
    }

    public void setDetalhesListener(DetalhesListener listener) {
        this.detalhesListener = listener;
    }

    public void getViagemDetalhesAPI(int idViagem) {
        if (!isInternetAvailable()) return;

        System.out.println("ZEZOCA_DEBUG: A pedir detalhes da viagem ID: " + idViagem);

        // [IMPORTANTE] String mágica para o Yii2 trazer os dados das tabelas relacionadas
        String expand = "destinos,atividades,transportes,fotosMemorias";

        // [IMPORTANTE] Tens de ter criado o método getDetalhesViagem no TripplanAPI.java
        Call<Viagem> call = apiService.getDetalhesViagem(idViagem, expand);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Viagem v = response.body();

                    // --- ESPIÃO DE DADOS ---
                    System.out.println("ZEZOCA_DEBUG: Viagem recebida: " + v.getNomeViagem());
                    System.out.println("--> Destinos: " + (v.getDestinos() != null ? v.getDestinos().size() : "null"));
                    System.out.println("--> Atividades: " + (v.getAtividades() != null ? v.getAtividades().size() : "null"));
                    System.out.println("--> Transportes: " + (v.getTransportes() != null ? v.getTransportes().size() : "null"));
                    System.out.println("--> Fotos: " + (v.getListaFotos() != null ? v.getListaFotos().size() : "null"));
                    // -----------------------

                    if (detalhesListener != null) {
                        detalhesListener.onViagemDetalhesCarregados(v);
                    }
                } else {
                    System.out.println("ZEZOCA_DEBUG: Erro na API. Código: " + response.code());
                    Toast.makeText(context, "Erro API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
                System.out.println("ZEZOCA_DEBUG: Falha grave: " + t.getMessage());
                Toast.makeText(context, "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 4. LOGIN API
    public void loginAPI(String email, String password, final LoginListener loginListener) {
        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.fazerLogin(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String tokenRecebido = response.body().getToken();
                    setToken(tokenRecebido);
                    setUserIdLogado(response.body().getId());
                    loginListener.onLoginSuccess();
                } else {
                    loginListener.onLoginError("Dados inválidos");
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                loginListener.onLoginError(t.getMessage());
            }
        });
    }

    public interface LoginListener {
        void onLoginSuccess();
        void onLoginError(String error);
    }

    private boolean isInternetAvailable() {
        return true; // Assume true por agora (em produção deves verificar ConnectivityManager)
    }

    /* ======================================================
       MÉTODOS DA BASE DE DADOS LOCAL (SQLITE)
       ====================================================== */

    private void openDatabase(){
        if(database == null || !database.isOpen()){
            database = bdHelper.getWritableDatabase();
        }
    }

    public boolean adicionarUtilizador(Utilizador user) {
        openDatabase();
        ContentValues values = new ContentValues();
        values.put(TripPlanBDHelper.NOME_USER, user.getNome());
        values.put(TripPlanBDHelper.EMAIL_USER, user.getEmail());
        values.put(TripPlanBDHelper.PASS_USER, user.getPassword());
        values.put(TripPlanBDHelper.TELEFONE_USER, user.getTelefone());
        values.put(TripPlanBDHelper.MORADA_USER, user.getMorada());
        long id = database.insert(TripPlanBDHelper.TABLE_UTILIZADOR, null, values);
        return id != -1;
    }

    public Utilizador autenticarUtilizador(String email, String password) {
        openDatabase();
        String selection = TripPlanBDHelper.EMAIL_USER + " = ? AND " + TripPlanBDHelper.PASS_USER + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = database.query(
                TripPlanBDHelper.TABLE_UTILIZADOR,
                null, selection, selectionArgs, null, null, null
        );

        Utilizador user = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TripPlanBDHelper.ID_USER));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow(TripPlanBDHelper.NOME_USER));
            String tel = cursor.getString(cursor.getColumnIndexOrThrow(TripPlanBDHelper.TELEFONE_USER));
            String morada = cursor.getString(cursor.getColumnIndexOrThrow(TripPlanBDHelper.MORADA_USER));
            user = new Utilizador((int)id, nome, email, password, tel, morada);
            cursor.close();
        }
        return user;
    }

    // --- TRANSPORTES ---
    public void adicionarTransporteAPI(Transporte transporte) {
        if (!isInternetAvailable()) return;

        Call<Transporte> call = apiService.adicionarTransporte(transporte);
        call.enqueue(new Callback<Transporte>() {
            @Override
            public void onResponse(Call<Transporte> call, Response<Transporte> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Transporte adicionado!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Transporte> call, Throwable t) {
                Toast.makeText(context, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- DESTINOS ---
    public void adicionarDestinoAPI(int idViagem, Destino destino) {
        if (!isInternetAvailable()) return;

        Call<Destino> call = apiService.adicionarDestino(destino);
        call.enqueue(new Callback<Destino>() {
            @Override
            public void onResponse(Call<Destino> call, Response<Destino> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Destino criado!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Destino> call, Throwable t) {
                Toast.makeText(context, "Erro: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- ATIVIDADES ---
    public void adicionarAtividadeAPI(Atividade atividade) {
        if (!isInternetAvailable()) return;

        System.out.println("--> ZECA_DEBUG: A enviar Atividade: " + atividade.toString());

        Call<Atividade> call = apiService.adicionarAtividade(atividade);
        call.enqueue(new Callback<Atividade>() {
            @Override
            public void onResponse(Call<Atividade> call, Response<Atividade> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Atividade adicionada!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        System.out.println("--> ZECA_DEBUG: Erro API " + response.code() + ": " + response.errorBody().string());
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
            @Override
            public void onFailure(Call<Atividade> call, Throwable t) {
                System.out.println("--> ZECA_DEBUG: Falha Total: " + t.getMessage());
            }
        });
    }

    // --- ESTADIAS ---
    public void adicionarEstadiaAPI(Estadia estadia) {
        if (!isInternetAvailable()) return;

        Call<Estadia> call = apiService.adicionarEstadia(estadia);
        call.enqueue(new Callback<Estadia>() {
            @Override
            public void onResponse(Call<Estadia> call, Response<Estadia> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Estadia reservada!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Estadia> call, Throwable t) {
                Toast.makeText(context, "Falha de Rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- FAVORITOS (LISTAGEM) ---
    public interface FavoritosListener {
        void onRefreshFavoritos(ArrayList<Favorito> listaFavoritos);
    }

    public void setFavoritosListener(FavoritosListener listener) {
        this.favoritosListener = listener;
    }

    public void getFavoritosAPI() {
        if (!isInternetAvailable()) return;

        Call<List<Favorito>> call = apiService.getFavoritos(userIdLogado);
        call.enqueue(new Callback<List<Favorito>>() {
            @Override
            public void onResponse(Call<List<Favorito>> call, Response<List<Favorito>> response) {
                if (response.isSuccessful()) {
                    ArrayList<Favorito> favs = (ArrayList<Favorito>) response.body();
                    if (favoritosListener != null) {
                        favoritosListener.onRefreshFavoritos(favs);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Favorito>> call, Throwable t) {
                Toast.makeText(context, "Erro ao buscar favoritos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // --- FOTOS ---
    public void uploadFotoAPI(int idViagem, String comentarioTexto, File ficheiroImagem) {
        if (!isInternetAvailable()) return;

        int idUser = getUserIdLogado();
        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idViagem));
        RequestBody comentarioBody = RequestBody.create(MediaType.parse("text/plain"), comentarioTexto);
        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idUser));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), ficheiroImagem);
        MultipartBody.Part bodyFoto = MultipartBody.Part.createFormData("foto", ficheiroImagem.getName(), requestFile);

        System.out.println("--> A ENVIAR FOTO... User: " + idUser + " | Viagem: " + idViagem);

        Call<FotoMemoria> call = apiService.uploadFoto(idBody, comentarioBody, userBody, bodyFoto);
        call.enqueue(new Callback<FotoMemoria>() {
            @Override
            public void onResponse(Call<FotoMemoria> call, Response<FotoMemoria> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Foto guardada!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        System.out.println("--> ERRO API FOTO: " + response.errorBody().string());
                    } catch (Exception e) {}
                }
            }
            @Override
            public void onFailure(Call<FotoMemoria> call, Throwable t) {
                System.out.println("--> FALHA FOTO: " + t.getMessage());
            }
        });
    }
}