package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
    }

    public static synchronized SingletonGestor getInstance(Context context){
        if(instance == null){
            instance = new SingletonGestor(context);
        }
        return instance;
    }

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

    // 3. MASTER/DETAIL (GET COMPLETO)
    public interface DetalhesListener {
        void onViagemDetalhesCarregados(Viagem viagem);
    }

    public void setDetalhesListener(DetalhesListener listener) {
        this.detalhesListener = listener;
    }

    public void getViagemDetalhesAPI(int idViagem) {
        if (!isInternetAvailable()) return;

        System.out.println("DEBUG: A pedir detalhes da viagem ID: " + idViagem);

        String expand = "destinos,atividades,transportes,fotosMemorias";

        Call<Viagem> call = apiService.getDetalhesViagem(idViagem, expand);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Viagem v = response.body();

                    if (detalhesListener != null) {
                        detalhesListener.onViagemDetalhesCarregados(v);
                    }
                } else {
                    Toast.makeText(context, "Erro API: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
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
        Call<Atividade> call = apiService.adicionarAtividade(atividade);
        call.enqueue(new Callback<Atividade>() {
            @Override
            public void onResponse(Call<Atividade> call, Response<Atividade> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Atividade adicionada!", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Atividade> call, Throwable t) {}
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
                }
            }
            @Override
            public void onFailure(Call<Estadia> call, Throwable t) {}
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

    // --- FOTOS (MEMÓRIAS) ---
    public void adicionarFotoAPI(FotoMemoria foto) {
        if (!isInternetAvailable()) return;

        android.util.Log.d("ZECA_DEBUG", "A tentar enviar foto...");
        android.util.Log.d("ZECA_DEBUG", "ID Viagem: " + foto.getPlanoViagemId());
        android.util.Log.d("ZECA_DEBUG", "Comentário: " + foto.getComentario());

        if (foto.getImagemBase64() != null) {
            android.util.Log.d("ZECA_DEBUG", "Tamanho da Imagem (chars): " + foto.getImagemBase64().length());
        } else {
            android.util.Log.e("ZECA_DEBUG", "ERRO: A string Base64 está VAZIA!");
        }

        Call<FotoMemoria> call = apiService.adicionarFoto(foto);
        call.enqueue(new Callback<FotoMemoria>() {
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

    public void removerViagemAPI(int idViagem, final GestaoViagemListener listener) {
        if (!isInternetAvailable()) {
            if (listener != null) listener.onErro("Sem ligação à internet.");
            return;
        }

        Call<Void> call = apiService.apagarViagem(idViagem);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removerViagemLocal(idViagem);
                    if (listener != null) listener.onViagemRemovida();
                } else {
                    if (listener != null) listener.onErro("Erro API: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                if (listener != null) listener.onErro("Falha: " + t.getMessage());
            }
        });
    }

    private void removerViagemLocal(int id) {
        if (viagens != null) {
            for (int i = 0; i < viagens.size(); i++) {
                if (viagens.get(i).getId() == id) {
                    viagens.remove(i);
                    break;
                }
            }
        }
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