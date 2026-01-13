package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences; // [NOVO] Importante para guardar dados
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

    // [NOVO] Variáveis de Sessão (ID e Token)
    private int userIdLogado = 0;
    private String token = null; // [NOVO] Variável para guardar o Token

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



    // --- INTERFACE LISTENER ---
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
       GESTÃO DE SESSÃO (Token e ID) - [ALTERADO AQUI]
       ====================================================== */

    // Guardar ID
    public void setUserIdLogado(int id) {
        this.userIdLogado = id;
    }

    public int getUserIdLogado() {
        return userIdLogado;
    }

    // [NOVO] Guardar Token (Memória + Telemóvel)
    public void setToken(String token) {
        this.token = token;

        // Guardar nas preferências do telemóvel para não perder se fechar a app
        SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("TOKEN_API", token);
        editor.apply();
    }

    // [NOVO] Ler Token
    public String getToken() {
        // Se a variável estiver vazia, tenta ir buscar à memória do telemóvel
        if (this.token == null) {
            SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);
            this.token = prefs.getString("TOKEN_API", null);
        }
        return this.token;
    }

    /* ======================================================
       MÉTODOS DA API
       ====================================================== */

    // 1. READ
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

    // 2. CREATE
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

    // 3. MASTER/DETAIL
    public void getViagemDetalhesAPI(int idViagem) {
        if (!isInternetAvailable()) return;

        System.out.println("ZEZOCA_DEBUG: A pedir detalhes da viagem ID: " + idViagem);

        Call<Viagem> call = apiService.getViagemDetalhes(idViagem);
        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful()) {
                    Viagem v = response.body();

                    // --- ESPIÃO DE DADOS ---
                    if (v != null) {
                        System.out.println("ZEZOCA_DEBUG: Viagem recebida: " + v.getNomeViagem());
                        if (v.getDestinos() == null) {
                            System.out.println("ZEZOCA_DEBUG: ATENÇÃO! A lista de destinos veio NULL (vazia)!");
                        } else {
                            System.out.println("ZEZOCA_DEBUG: A lista traz " + v.getDestinos().size() + " destinos.");
                        }
                    } else {
                        System.out.println("ZEZOCA_DEBUG: O corpo da resposta veio vazio (null).");
                    }
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

                    // [NOVO] Guardar automaticamente o token ao fazer login por aqui
                    String tokenRecebido = response.body().getToken();
                    setToken(tokenRecebido); // Usa o método novo que criámos
                    setUserIdLogado(response.body().getId()); // Guarda o ID também

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
        return true;
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

    public void adicionarTransporteAPI(Transporte transporte) {
        if (!isInternetAvailable()) return;

        Call<Transporte> call = apiService.adicionarTransporte(transporte);
        call.enqueue(new Callback<Transporte>() {
            @Override
            public void onResponse(Call<Transporte> call, Response<Transporte> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Transporte adicionado!", Toast.LENGTH_SHORT).show();
                    // Aqui podes disparar um listener para atualizar a lista de detalhes
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

        // NOTA: O backend precisa de saber que este destino pertence à viagem X.
        // Se o modelo Destino não tiver campo "plano_viagem_id", o backend deve tratar disso
        // ou terás de usar uma rota específica tipo "api/trips/{id}/destinos".
        // Vou assumir o envio direto:

        Call<Destino> call = apiService.adicionarDestino(destino);
        call.enqueue(new Callback<Destino>() {
            @Override
            public void onResponse(Call<Destino> call, Response<Destino> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Destino criado!", Toast.LENGTH_SHORT).show();
                    // Aqui podes disparar um listener se quiseres atualizar a lista
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

        // 1. ESPREITAR O QUE VAMOS ENVIAR
        System.out.println("--> ZECA_DEBUG: A enviar Atividade...");
        System.out.println("--> ZECA_DEBUG: Dados: " + atividade.toString());

        Call<Atividade> call = apiService.adicionarAtividade(atividade);
        call.enqueue(new Callback<Atividade>() {
            @Override
            public void onResponse(Call<Atividade> call, Response<Atividade> response) {
                if (response.isSuccessful()) {
                    Atividade resposta = response.body();
                    // 2. ESPREITAR O QUE O SERVIDOR RESPONDEU
                    System.out.println("--> ZECA_DEBUG: Sucesso! O servidor gravou isto:");
                    if (resposta != null) {
                        System.out.println("--> ZECA_DEBUG: Resposta: " + resposta.toString());
                    } else {
                        System.out.println("--> ZECA_DEBUG: Resposta veio vazia (null)!");
                    }

                    Toast.makeText(context, "Atividade adicionada!", Toast.LENGTH_SHORT).show();
                } else {
                    // 3. ESPREITAR SE HOUVE ERRO ESCONDIDO
                    try {
                        System.out.println("--> ZECA_DEBUG: Erro API " + response.code() + ": " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    Toast.makeText(context, "Estadia reservada com sucesso!", Toast.LENGTH_SHORT).show();
                    System.out.println("--> SUCESSO: Estadia criada com ID: " + response.body().getId());
                } else {
                    // AQUI ESTÁ O SEGREDO: Ler o corpo do erro
                    try {
                        String erroApi = response.errorBody().string();
                        System.out.println("--> ERRO API (Código " + response.code() + "): " + erroApi);
                        Toast.makeText(context, "Erro: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<Estadia> call, Throwable t) {
                Toast.makeText(context, "Falha de Rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                t.printStackTrace(); // Imprime o erro completo no Logcat
            }
        });
    }

    // --- FAVORITOS (LISTAGEM) ---
    // Precisamos de um listener específico para receber a lista na Activity
    private FavoritosListener favoritosListener;

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
    // --- FOTOS (COM LOGS DETALHADOS) ---
    public void uploadFotoAPI(int idViagem, String comentarioTexto, File ficheiroImagem) {
        if (!isInternetAvailable()) return;

        // 1. Preparar os dados
        int idUser = getUserIdLogado();

        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idViagem));
        RequestBody comentarioBody = RequestBody.create(MediaType.parse("text/plain"), comentarioTexto);

        // Novo RequestBody para o ID do User
        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idUser));

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), ficheiroImagem);
        MultipartBody.Part bodyFoto = MultipartBody.Part.createFormData("foto", ficheiroImagem.getName(), requestFile);

        System.out.println("--> A ENVIAR FOTO... User: " + idUser + " | Viagem: " + idViagem);

        // 2. Enviar tudo (incluindo o userBody)
        Call<FotoMemoria> call = apiService.uploadFoto(idBody, comentarioBody, userBody, bodyFoto);

        call.enqueue(new Callback<FotoMemoria>() {
            @Override
            public void onResponse(Call<FotoMemoria> call, Response<FotoMemoria> response) {
                if (response.isSuccessful()) {
                    // O servidor aceitou (Código 200-299)
                    if (response.body() != null) {
                        System.out.println("--> SUCESSO! Resposta do Servidor: " + response.body().toString());
                        // Se o modelo FotoMemoria tiver getters, podes imprimir:
                        // System.out.println("--> ID Criado: " + response.body().getId());
                        Toast.makeText(context, "Foto guardada com sucesso!", Toast.LENGTH_SHORT).show();
                    } else {
                        System.out.println("--> SUCESSO ESTRANHO: O corpo da resposta veio vazio (null).");
                    }
                } else {
                    // O servidor recusou (Erro 400, 404, 500...)
                    try {
                        String erroApi = response.errorBody().string();
                        System.out.println("--> ERRO API (Código " + response.code() + "): " + erroApi);
                        Toast.makeText(context, "Erro no upload: " + response.code(), Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<FotoMemoria> call, Throwable t) {
                // Erro de rede ou conversão de dados
                System.out.println("--> FALHA FATAL: " + t.getMessage());
                t.printStackTrace(); // Isto vai mostrar o erro completo no Logcat
                Toast.makeText(context, "Falha no envio: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private DetalhesListener detalhesListener; // <--- NOVO

    public interface DetalhesListener {
        void onViagemDetalhesCarregados(Viagem viagem);
    }

    public void setDetalhesListener(DetalhesListener listener) {
        this.detalhesListener = listener;
    }
}
