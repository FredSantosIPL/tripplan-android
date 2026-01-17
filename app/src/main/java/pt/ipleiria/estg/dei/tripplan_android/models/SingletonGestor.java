package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import pt.ipleiria.estg.dei.tripplan_android.api.ServiceBuilder;
import pt.ipleiria.estg.dei.tripplan_android.api.TripplanAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SingletonGestor {
    private static SingletonGestor instance = null;

    private TripPlanBDHelper dbHelper;
    private SQLiteDatabase database = null;
    private Context context;

    // --- VARIÁVEIS ---
    private ArrayList<Viagem> viagens;
    private TripplanAPI apiService;

    // --- DEFINIÇÃO DAS INTERFACES (IMPORTANTE ESTAREM AQUI) ---
    public interface ViagensListener {
        void onRefreshLista(ArrayList<Viagem> listaViagens);
    }

    public interface DetalhesListener {
        void onViagemDetalhesCarregados(Viagem viagem);
    }

    public interface FavoritosListener {
        void onRefreshFavoritos(ArrayList<Favorito> listaFavoritos);
    }

    public interface LoginListener {
        void onLoginSuccess();
        void onLoginError(String error);
    }

    public interface GestaoViagemListener {
        void onViagemRemovida();
        void onErro(String mensagem);
    }

    // Variáveis dos Listeners
    private ViagensListener viagensListener;
    private DetalhesListener detalhesListener;
    private FavoritosListener favoritosListener;

    // Variáveis de Sessão
    private int userIdLogado = 0;
    private String token = null;

<<<<<<< Updated upstream
    // Listeners Específicos
    private DetalhesListener detalhesListener;
    private FavoritosListener favoritosListener;

    private SingletonGestor(Context context){
        this.context = context;
        this.viagens = new ArrayList<>();

        // 1. Inicializar Base de Dados Local
        bdHelper = new TripPlanBDHelper(context);

        // 2. Inicializar a API com o IP guardado
        // MUDANÇA 1: Chamamos este método em vez de criar direto
        lerIpDasPreferencias();
=======
    private SingletonGestor(Context context){
        this.context = context;
        this.viagens = new ArrayList<>();
        dbHelper = new TripPlanBDHelper(context);
        apiService = ServiceBuilder.buildService(TripplanAPI.class);
>>>>>>> Stashed changes
    }

    public static synchronized SingletonGestor getInstance(Context context){
        if(instance == null){
            instance = new SingletonGestor(context);
        }
        return instance;
    }

<<<<<<< Updated upstream
    // --- MUDANÇA 2: NOVO MÉTODO PARA REINICIAR A API ---
    public void lerIpDasPreferencias() {
        SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);

        // URL Default (Emulador)
        String ipDefault = "http://10.0.2.2:8888/TripPlan/tripplan/tripplan/backend/web/index.php/";
        String ipGuardado = prefs.getString("IP_API", ipDefault);

        // 1. Avisa o ServiceBuilder para atualizar o URL base
        ServiceBuilder.setUrlBase(ipGuardado);

        // 2. Reconstrói o serviço Retrofit com o novo IP
        apiService = ServiceBuilder.buildService(TripplanAPI.class);

        android.util.Log.d("ZECA_API", "API Reiniciada com IP: " + ipGuardado);
    }

    // --- INTERFACE LISTENER LISTA PRINCIPAL ---
    public interface ViagensListener {
        void onRefreshLista(ArrayList<Viagem> listaViagens);
    }

=======
    // --- SETTERS DOS LISTENERS ---
>>>>>>> Stashed changes
    public void setViagensListener(ViagensListener listener) {
        this.viagensListener = listener;
    }

    public void setDetalhesListener(DetalhesListener listener) {
        this.detalhesListener = listener;
    }

    public void setFavoritosListener(FavoritosListener listener) {
        this.favoritosListener = listener;
    }

    public ArrayList<Viagem> getViagensLocais() {
        return new ArrayList<>(viagens);
    }

    // --- SESSÃO ---
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
    public void fazerLogout() {
        SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.remove("TOKEN_API");
        editor.remove("ID_USER");
        editor.remove("EMAIL_USER");
        editor.apply();

        this.token = null;
        this.userIdLogado = 0;
        this.viagens.clear();
    }

    // =============================================================
    //       MÉTODOS PRINCIPAIS
    // =============================================================

    public void getAllViagensAPI() {
        if (!isConnectionInternet(context)) {
            viagens = dbHelper.getAllViagensBD(userIdLogado);
            if (viagensListener != null) {
                viagensListener.onRefreshLista(viagens);
            }
            return;
        }

        Call<List<Viagem>> call = apiService.getAllViagens(userIdLogado);
        call.enqueue(new Callback<List<Viagem>>() {
            @Override
            public void onResponse(Call<List<Viagem>> call, Response<List<Viagem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Viagem> todasAsViagens = (ArrayList<Viagem>) response.body();
                    viagens = new ArrayList<>();

                    // Filtro manual pelo ID do User
                    for (Viagem v : todasAsViagens) {
                        if (v.getUserId() == userIdLogado) {
                            viagens.add(v);
                        }
                    }

                    dbHelper.guardarViagensBD(viagens, userIdLogado);

                    if (viagensListener != null) {
                        viagensListener.onRefreshLista(viagens);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Viagem>> call, Throwable t) {
                viagens = dbHelper.getAllViagensBD(userIdLogado);
                if (viagensListener != null) {
                    viagensListener.onRefreshLista(viagens);
                }
            }
        });
    }

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

    public void getViagemDetalhesAPI(int idViagem) {
<<<<<<< Updated upstream
        if (!isInternetAvailable()) return;

        System.out.println("DEBUG: A pedir detalhes da viagem ID: " + idViagem);
=======
        if (!isConnectionInternet(context)) {
            for (Viagem v : viagens) {
                if (v.getId() == idViagem) {
                    if (detalhesListener != null) detalhesListener.onViagemDetalhesCarregados(v);
                    return;
                }
            }
            return;
        }
>>>>>>> Stashed changes

        String expand = "destinos,atividades,transportes,fotosMemorias";
        Call<Viagem> call = apiService.getDetalhesViagem(idViagem, expand);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Viagem v = response.body();
                    dbHelper.atualizarViagemBD(v);

                    for (int i = 0; i < viagens.size(); i++) {
                        if (viagens.get(i).getId() == v.getId()) {
                            viagens.set(i, v);
                        }
                    }
                    if (detalhesListener != null) detalhesListener.onViagemDetalhesCarregados(v);
                }
            }
            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
                for (Viagem v : viagens) {
                    if (v.getId() == idViagem) {
                        if (detalhesListener != null) detalhesListener.onViagemDetalhesCarregados(v);
                    }
                }
            }
        });
    }

    public void loginAPI(final String email, final String password, final Context context, final LoginListener loginListener) {
        if (!isConnectionInternet(context)) {
            if (realizarLoginOffline(email, password)) {
                loginListener.onLoginSuccess();
            } else {
                loginListener.onLoginError("Sem internet e sem dados guardados.");
            }
            return;
        }

        LoginRequest request = new LoginRequest(email, password);
        Call<LoginResponse> call = apiService.fazerLogin(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String tokenRecebido = response.body().getToken();
                    int idRecebido = response.body().getId();

                    setToken(tokenRecebido);
                    setUserIdLogado(idRecebido);

                    Utilizador user = new Utilizador();
                    user.setId(idRecebido);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setNome(email);

                    if (dbHelper != null) {
                        dbHelper.guardarUtilizadorBD(user);
                    }
                    loginListener.onLoginSuccess();
                } else {
                    loginListener.onLoginError("Dados inválidos (API)");
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                if (realizarLoginOffline(email, password)) {
                    loginListener.onLoginSuccess();
                } else {
                    loginListener.onLoginError("Erro de rede: " + t.getMessage());
                }
            }
        });
    }

    private boolean realizarLoginOffline(String email, String password) {
        Utilizador userOffline = dbHelper.loginOffline(email, password);
        if (userOffline != null) {
            setUserIdLogado((int) userOffline.getId());
            setToken("TOKEN_OFFLINE_DUMMY");
            return true;
        }
        return false;
    }

    // --- BASE DE DADOS LOCAL (UTILIZADOR SEM MORADA/TEL) ---
    private void openDatabase(){
        if(database == null || !database.isOpen()){
            database = dbHelper.getWritableDatabase();
        }
    }

    public boolean adicionarUtilizador(Utilizador user) {
        openDatabase();
        ContentValues values = new ContentValues();
        values.put(TripPlanBDHelper.NOME_USER, user.getNome());
        values.put(TripPlanBDHelper.EMAIL_USER, user.getEmail());
        values.put(TripPlanBDHelper.PASS_USER, user.getPassword());
        long id = database.insert(TripPlanBDHelper.TABLE_UTILIZADOR, null, values);
        return id != -1;
    }

    public Utilizador autenticarUtilizador(String email, String password) {
        openDatabase();
        String selection = TripPlanBDHelper.EMAIL_USER + " = ? AND " + TripPlanBDHelper.PASS_USER + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = database.query(TripPlanBDHelper.TABLE_UTILIZADOR, null, selection, selectionArgs, null, null, null);

        Utilizador user = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TripPlanBDHelper.ID_USER));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow(TripPlanBDHelper.NOME_USER));
            // AQUI ESTAVA O ERRO: AGORA USA O CONSTRUTOR DE 4 ARGUMENTOS
            user = new Utilizador((int)id, nome, email, password);
            cursor.close();
        }
        return user;
    }

    // --- MÉTODOS EXTRA API ---
    public void adicionarTransporteAPI(Transporte transporte) {
        if (!isConnectionInternet(context)) return;
        Call<Transporte> call = apiService.adicionarTransporte(transporte);
        call.enqueue(new Callback<Transporte>() {
            public void onResponse(Call<Transporte> call, Response<Transporte> response) { if(response.isSuccessful()) Toast.makeText(context, "Transporte!", Toast.LENGTH_SHORT).show(); }
            public void onFailure(Call<Transporte> call, Throwable t) {}
        });
    }

    public void adicionarDestinoAPI(int idViagem, Destino destino) {
        if (!isConnectionInternet(context)) return;
        Call<Destino> call = apiService.adicionarDestino(destino);
        call.enqueue(new Callback<Destino>() {
            public void onResponse(Call<Destino> call, Response<Destino> response) { if(response.isSuccessful()) Toast.makeText(context, "Destino!", Toast.LENGTH_SHORT).show(); }
            public void onFailure(Call<Destino> call, Throwable t) {}
        });
    }

    public void adicionarAtividadeAPI(Atividade atividade) {
        if (!isConnectionInternet(context)) return;
        Call<Atividade> call = apiService.adicionarAtividade(atividade);
        call.enqueue(new Callback<Atividade>() {
            public void onResponse(Call<Atividade> call, Response<Atividade> response) { if(response.isSuccessful()) Toast.makeText(context, "Atividade!", Toast.LENGTH_SHORT).show(); }
            public void onFailure(Call<Atividade> call, Throwable t) {}
        });
    }

    public void adicionarEstadiaAPI(Estadia estadia) {
        if (!isConnectionInternet(context)) return;
        Call<Estadia> call = apiService.adicionarEstadia(estadia);
        call.enqueue(new Callback<Estadia>() {
            public void onResponse(Call<Estadia> call, Response<Estadia> response) { if(response.isSuccessful()) Toast.makeText(context, "Estadia!", Toast.LENGTH_SHORT).show(); }
            public void onFailure(Call<Estadia> call, Throwable t) {}
        });
    }

    public void getFavoritosAPI() {
        if (!isConnectionInternet(context)) return;
        Call<List<Favorito>> call = apiService.getFavoritos(userIdLogado);
        call.enqueue(new Callback<List<Favorito>>() {
            public void onResponse(Call<List<Favorito>> call, Response<List<Favorito>> response) {
                if(response.isSuccessful() && favoritosListener!=null) favoritosListener.onRefreshFavoritos((ArrayList<Favorito>)response.body());
            }
            public void onFailure(Call<List<Favorito>> call, Throwable t) {}
        });
    }

<<<<<<< Updated upstream
    // --- FOTOS (MEMÓRIAS) ---
    public void adicionarFotoAPI(FotoMemoria foto) {
        if (!isInternetAvailable()) return;

        android.util.Log.d("ZECA_DEBUG", "A tentar enviar foto...");
        android.util.Log.d("ZECA_DEBUG", "ID Viagem: " + foto.getPlanoViagemId());
        android.util.Log.d("ZECA_DEBUG", "Comentário: " + foto.getComentario());
=======
    public void uploadFotoAPI(int idViagem, String comentarioTexto, File ficheiroImagem) {
        if (!isConnectionInternet(context)) return;
        RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(idViagem));
        RequestBody comentarioBody = RequestBody.create(MediaType.parse("text/plain"), comentarioTexto);
        RequestBody userBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(userIdLogado));
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), ficheiroImagem);
        MultipartBody.Part bodyFoto = MultipartBody.Part.createFormData("foto", ficheiroImagem.getName(), requestFile);
>>>>>>> Stashed changes

        if (foto.getImagemBase64() != null) {
            android.util.Log.d("ZECA_DEBUG", "Tamanho da Imagem (chars): " + foto.getImagemBase64().length());
        } else {
            android.util.Log.e("ZECA_DEBUG", "ERRO: A string Base64 está VAZIA!");
        }

        Call<FotoMemoria> call = apiService.adicionarFoto(foto);
        call.enqueue(new Callback<FotoMemoria>() {
<<<<<<< Updated upstream
            @Override
            public void onResponse(Call<FotoMemoria> call, Response<FotoMemoria> response) {
                if (response.isSuccessful()) {
                    android.util.Log.d("ZECA_DEBUG", "SUCESSO! Código: " + response.code());
                    Toast.makeText(context, "Memória guardada com sucesso! 📸", Toast.LENGTH_SHORT).show();
                } else {
                    android.util.Log.e("ZECA_DEBUG", "ERRO NO SERVIDOR! Código: " + response.code());
                    try {
                        String erroBody = response.errorBody().string();
                        android.util.Log.e("ZECA_DEBUG", "Detalhe do Erro: " + erroBody);
                        Toast.makeText(context, "Erro: " + erroBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<FotoMemoria> call, Throwable t) {
                android.util.Log.e("ZECA_DEBUG", "FALHA DE REDE: " + t.getMessage());
                Toast.makeText(context, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /* ======================================================
       MÉTODOS DE GESTÃO (EDITAR / REMOVER)
       ====================================================== */

    public interface GestaoViagemListener {
        void onViagemRemovida();
        void onErro(String mensagem);
    }

=======
            public void onResponse(Call<FotoMemoria> call, Response<FotoMemoria> r) { if(r.isSuccessful()) Toast.makeText(context, "Foto guardada!", Toast.LENGTH_SHORT).show(); }
            public void onFailure(Call<FotoMemoria> c, Throwable t) {}
        });
    }

>>>>>>> Stashed changes
    public void removerViagemAPI(int idViagem, final GestaoViagemListener listener) {
        if (!isConnectionInternet(context)) {
            if (listener != null) listener.onErro("Sem ligação à internet.");
            return;
        }
        Call<Void> call = apiService.apagarViagem(idViagem);
        call.enqueue(new Callback<Void>() {
            public void onResponse(Call<Void> call, Response<Void> response) {
<<<<<<< Updated upstream
                if (response.isSuccessful()) {
                    removerViagemLocal(idViagem);
                    if (listener != null) listener.onViagemRemovida();
                } else {
                    if (listener != null) listener.onErro("Erro API: " + response.code());
=======
                if(response.isSuccessful()){
                    removerViagemLocal(idViagem);
                    if(listener!=null) listener.onViagemRemovida();
>>>>>>> Stashed changes
                }
            }
            public void onFailure(Call<Void> call, Throwable t) {
                if(listener!=null) listener.onErro(t.getMessage());
            }
        });
    }

    private void removerViagemLocal(int id) {
        if (viagens != null) { for (int i = 0; i < viagens.size(); i++) { if (viagens.get(i).getId() == id) { viagens.remove(i); break; } } }
    }

    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    /* ======================================================
       CONFIGURAÇÃO DINÂMICA DE IMAGENS 📸
       ====================================================== */

    /**
     * Este método é chamado pelo Adapter para saber onde buscar a imagem.
     * Ele lê o IP configurado (Backend) e transforma no link do Frontend.
     */
    public String getUrlImagem(String nomeFotoNaBD) {
        if (nomeFotoNaBD == null) return "";

        SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);

        // 1. Ler o URL da API configurado (Default: Emulador)
        String urlApi = prefs.getString("IP_API", "http://10.0.2.2:8888/TripPlan/tripplan/tripplan/backend/web/index.php/");

        // 2. CIRURGIA: Trocar "backend/web/index.php/" por "frontend/web/"
        // NOTA IMPORTANTE: Retirou-se o 'uploads/' no replace, porque a BD já traz isso.
        // Assim ficamos com .../frontend/web/ + uploads/foto.jpg
        String urlImagens = urlApi.replace("backend/web/index.php/", "frontend/web/uploads/");

        // Caso o URL não tenha o index.php, tentamos outra substituição
        if (urlImagens.equals(urlApi)) {
            urlImagens = urlApi.replace("backend/web/", "frontend/web/uploads/");
        }

        // 3. Juntar o nome da foto (que já vem com "uploads/...")
        return urlImagens + nomeFotoNaBD;
    }
}