package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

    private TripPlanBDHelper bdHelper;
    private SQLiteDatabase database = null;
    private Context context;

    // --- VARIÁVEIS DE DADOS ---
    private ArrayList<Viagem> viagens;
    private ArrayList<Favorito> listaFavoritos; // A Lista que faltava!

    // --- API ---
    private TripplanAPI apiService;

    // --- LISTENERS ---
    private ViagensListener viagensListener;
    private DetalhesListener detalhesListener;
    private FavoritosListener favoritosListener;

    // --- SESSÃO ---
    private int userIdLogado = 0;
    private String token = null;

    private String usernameLogado;
    private String emailLogado;

    private SingletonGestor(Context context){
        this.context = context;
        this.viagens = new ArrayList<>();
        this.listaFavoritos = new ArrayList<>();

        bdHelper = new TripPlanBDHelper(context);
        lerIpDasPreferencias();
    }

    public static synchronized SingletonGestor getInstance(Context context){
        if(instance == null){
            instance = new SingletonGestor(context);
        }
        return instance;
    }

    public void lerIpDasPreferencias() {
        SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);

        // URL Default (Emulador)
        String ipDefault = "http://10.0.2.2:8888/TripPlan/tripplan/tripplan/backend/web/index.php/";
        String ipGuardado = prefs.getString("IP_API", ipDefault);
        ServiceBuilder.setUrlBase(ipGuardado);

        apiService = ServiceBuilder.buildService(TripplanAPI.class);

        android.util.Log.d("ZECA_API", "API Reiniciada com IP: " + ipGuardado);
    }

    //       INTERFACES (LISTENERS)
    public interface ViagensListener {
        void onRefreshLista(ArrayList<Viagem> listaViagens);
    }

    public interface DetalhesListener {
        void onViagemDetalhesCarregados(Viagem viagem);
    }

    public interface FavoritosListener {
        void onRefreshFavoritos(ArrayList<Favorito> listaFavoritos);
        void onFavoritoAlterado();
    }
    public interface LoginListener {
        void onLoginSuccess();
        void onLoginError(String error);
    }

    public interface GestaoViagemListener {
        void onViagemRemovida();
        void onErro(String mensagem);
    }

    // --- SETTERS DOS LISTENERS ---
    public void setViagensListener(ViagensListener listener) { this.viagensListener = listener; }
    public void setDetalhesListener(DetalhesListener listener) { this.detalhesListener = listener; }
    public void setFavoritosListener(FavoritosListener listener) { this.favoritosListener = listener; }

    public ArrayList<Viagem> getViagensLocais() {
        return new ArrayList<>(viagens);
    }

    public void setUserIdLogado(int id) { this.userIdLogado = id; }
    public int getUserIdLogado() { return userIdLogado; }

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
        this.listaFavoritos.clear();
    }
    public void getAllViagensAPI() {
        if (!isConnectionInternet(context)) {
            // Se não houver net, carrega da BD local
            viagens = bdHelper.getAllViagensBD(userIdLogado);
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

                    // Filtro manual pelo ID do User (caso a API mande todas)
                    for (Viagem v : todasAsViagens) {
                        if (v.getUserId() == userIdLogado) {
                            viagens.add(v);
                        }
                    }

                    // Atualiza BD Local
                    bdHelper.guardarViagensBD(viagens, userIdLogado);

                    if (viagensListener != null) {
                        viagensListener.onRefreshLista(viagens);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Viagem>> call, Throwable t) {
                // Fallback para BD Local
                viagens = bdHelper.getAllViagensBD(userIdLogado);
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

    //DETALHES DA VIAGEM
    public void getViagemDetalhesAPI(int idViagem) {
        if (!isConnectionInternet(context)) {
            // Tenta buscar na lista local
            for (Viagem v : viagens) {
                if (v.getId() == idViagem) {
                    if (detalhesListener != null) detalhesListener.onViagemDetalhesCarregados(v);
                    return;
                }
            }
            return;
        }

        System.out.println("DEBUG: A pedir detalhes da viagem ID: " + idViagem);

        String expand = "destinos,atividades,transportes,fotosMemorias,estadias";
        Call<Viagem> call = apiService.getDetalhesViagem(idViagem, expand);

        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Viagem v = response.body();

                    // Atualiza em memória e na BD
                    bdHelper.atualizarViagemBD(v);
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
                Toast.makeText(context, "Erro ao carregar detalhes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 4. LOGIN
    public void loginAPI(final String email, final String password, final LoginListener loginListener) {
        // Se não houver net, tenta login local
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
                    getFavoritosAPI();

                    // Guardar user localmente para futuro login offline
                    Utilizador user = new Utilizador();
                    user.setId(idRecebido);
                    user.setEmail(email);
                    user.setPassword(password); // Nota: Em app real, encriptar isto!
                    user.setNome(email);

                    if (bdHelper != null) {
                        bdHelper.guardarUtilizadorBD(user);
                    }
                    loginListener.onLoginSuccess();
                } else {
                    loginListener.onLoginError("Dados inválidos (API)");
                }
            }
            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Se falhar a API, tenta login offline
                if (realizarLoginOffline(email, password)) {
                    loginListener.onLoginSuccess();
                } else {
                    loginListener.onLoginError("Erro de rede: " + t.getMessage());
                }
            }
        });
    }

    private boolean realizarLoginOffline(String email, String password) {
        Utilizador userOffline = bdHelper.loginOffline(email, password);
        if (userOffline != null) {
            setUserIdLogado((int) userOffline.getId());
            setToken("TOKEN_OFFLINE_DUMMY");
            return true;
        }
        return false;
    }


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

        // Chamamos a API apenas com o ID.
        // O Retrofit já sabe que tem de adicionar "?expand=viagem" por causa do interface.
        apiService.getFavoritos(userIdLogado).enqueue(new Callback<List<Favorito>>() {
            @Override
            public void onResponse(Call<List<Favorito>> call, Response<List<Favorito>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaFavoritos = (ArrayList<Favorito>) response.body();

                    // DEBUG: Só para teres a certeza que os nomes estão a chegar
                    for (Favorito f : listaFavoritos) {
                        if (f.getViagem() != null) {
                            android.util.Log.d("ZECA_FAV", "Favorito encontrado: " + f.getViagem().getNomeViagem());
                        }
                    }

                    if (favoritosListener != null) {
                        favoritosListener.onRefreshFavoritos(listaFavoritos);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Favorito>> call, Throwable t) {
                android.util.Log.e("ZECA_API", "Erro ao carregar favoritos: " + t.getMessage());
            }
        });
    }

    public void adicionarFavoritoAPI(int idViagem) {
        // Atenção: O ID aqui é 0 porque é novo, o user e a viagem vão no corpo
        Favorito fav = new Favorito(0, userIdLogado, idViagem);

        Call<Favorito> call = apiService.adicionarFavorito(fav);
        call.enqueue(new Callback<Favorito>() {
            @Override
            public void onResponse(Call<Favorito> call, Response<Favorito> response) {
                if (response.isSuccessful()) {
                    // SUCESSO!
                    Favorito novoFavorito = response.body();
                    if (listaFavoritos == null) listaFavoritos = new ArrayList<>();

                    if (novoFavorito != null) {
                        if (novoFavorito.getPlanoViagemId() == 0) {
                            novoFavorito.setPlanoViagemId(idViagem);
                        }
                        listaFavoritos.add(novoFavorito);
                    }
                    Toast.makeText(context, "Guardado!", Toast.LENGTH_SHORT).show();
                    if (favoritosListener != null) favoritosListener.onFavoritoAlterado();
                } else {
                try {
                    // Isto vai imprimir algo como: {"field":"destino_id","message":"Destino ID is invalid"}
                    String erroDetalhado = response.errorBody().string();
                    System.out.println("ZECA_VALIDACAO_ERRO: " + erroDetalhado);
                    Toast.makeText(context, "Erro de Validação: " + erroDetalhado, Toast.LENGTH_LONG).show();
                } catch (Exception e) { e.printStackTrace(); }
            }

            }
            @Override
            public void onFailure(Call<Favorito> call, Throwable t) {
                System.out.println("ZECA_ERRO_REDE: " + t.getMessage());
                Toast.makeText(context, "Sem net: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void removerFavoritoAPI(int idFavorito) {
        Call<Void> call = apiService.removerFavorito(idFavorito);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {

                    // CORREÇÃO: Remover logo da lista local!
                    if (listaFavoritos != null) {
                        for (int i = 0; i < listaFavoritos.size(); i++) {
                            if (listaFavoritos.get(i).getId() == idFavorito) {
                                listaFavoritos.remove(i);
                                break;
                            }
                        }
                    }

                    Toast.makeText(context, "Removido dos Favoritos", Toast.LENGTH_SHORT).show();

                    // Avisar os ecrãs
                    if (favoritosListener != null) favoritosListener.onFavoritoAlterado();

                    getFavoritosAPI();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Erro ao remover.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int getFavoritoIdPorViagem(int idViagem) {
        if (listaFavoritos == null) {
            return -1;
        }

        for (Favorito f : listaFavoritos) {
            if (f.getPlanoViagemId() == idViagem) {
                return f.getId();
            }
        }
        return -1;
    }

    public void adicionarFotoAPI(FotoMemoria foto) {
        if (!isConnectionInternet(context)) {
            Toast.makeText(context, "Sem internet para enviar foto.", Toast.LENGTH_SHORT).show();
            return;
        }

        android.util.Log.d("ZECA_DEBUG", "A tentar enviar foto (Base64)...");

        Call<FotoMemoria> call = apiService.adicionarFoto(foto);
        call.enqueue(new Callback<FotoMemoria>() {
            @Override
            public void onResponse(Call<FotoMemoria> call, Response<FotoMemoria> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(context, "Memória guardada com sucesso! 📸", Toast.LENGTH_SHORT).show();
                } else {
                    android.util.Log.e("ZECA_DEBUG", "ERRO NO SERVIDOR! Código: " + response.code());
                    Toast.makeText(context, "Erro no envio da foto.", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<FotoMemoria> call, Throwable t) {
                Toast.makeText(context, "Erro de rede: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void removerViagemAPI(int idViagem, final GestaoViagemListener listener) {
        if (!isConnectionInternet(context)) {
            if (listener != null) listener.onErro("Sem ligação à internet.");
            return;
        }
        Call<Void> call = apiService.apagarViagem(idViagem);
        call.enqueue(new Callback<Void>() {
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    removerViagemLocal(idViagem);
                    if (listener != null) listener.onViagemRemovida();
                } else {
                    if (listener != null) listener.onErro("Erro API: " + response.code());
                }
            }
            public void onFailure(Call<Void> call, Throwable t) {
                if (listener != null) listener.onErro(t.getMessage());
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

    public static boolean isConnectionInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // Método auxiliar para compatibilidade com código antigo (se tiveres algum a chamar isInternetAvailable)
    private boolean isInternetAvailable() {
        return isConnectionInternet(context);
    }

    public String getUrlImagem(String nomeFotoNaBD) {
        if (nomeFotoNaBD == null) return "";

        SharedPreferences prefs = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE);

        // 1. Ler o URL da API configurado
        String urlApi = prefs.getString("IP_API", "http://10.0.2.2:8888/TripPlan/tripplan/tripplan/backend/web/index.php/");

        // 2. Transforma Backend em Frontend
        String urlImagens = urlApi.replace("backend/web/index.php/", "frontend/web/uploads/");

        if (urlImagens.equals(urlApi)) {
            urlImagens = urlApi.replace("backend/web/", "frontend/web/uploads/");
        }

        return urlImagens + nomeFotoNaBD;
    }

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
            // Assumindo que o construtor do Utilizador aceita 4 args (id, nome, email, pass)
            user = new Utilizador((int)id, nome, email, password);
            cursor.close();
        }
        return user;
    }
    
    public void editarViagemAPI(Viagem viagem, final GestaoViagemListener listener) {
        if (!isConnectionInternet(context)) {
            if (listener != null) listener.onErro("Sem ligação à internet.");
            return;
        }

        apiService.atualizarViagem(viagem.getId(), viagem).enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Atualizar a viagem na lista local em memória
                    for (int i = 0; i < viagens.size(); i++) {
                        if (viagens.get(i).getId() == viagem.getId()) {
                            viagens.set(i, response.body());
                            break;
                        }
                    }

                    if (listener != null) listener.onViagemRemovida();
                } else {
                    if (listener != null) listener.onErro("Erro ao atualizar: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Viagem> call, Throwable t) {
                if (listener != null) listener.onErro("Erro de rede: " + t.getMessage());
            }
        });
    }

    public void setUsernameLogado(String username) {
        this.usernameLogado = username;
        SharedPreferences.Editor editor = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE).edit();
        editor.putString("USERNAME_USER", username);
        editor.apply();
    }

    public void setEmailLogado(String email) {
        this.emailLogado = email;
        SharedPreferences.Editor editor = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE).edit();
        editor.putString("EMAIL_USER", email);
        editor.apply();
    }

    public String getUsernameLogado() {
        if (usernameLogado == null) {
            usernameLogado = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE).getString("USERNAME_USER", "Utilizador");
        }
        return usernameLogado;
    }

    public String getEmailLogado() {
        if (emailLogado == null) {
            emailLogado = context.getSharedPreferences("DADOS_TRIPPLAN", Context.MODE_PRIVATE).getString("EMAIL_USER", "email@exemplo.com");
        }
        return emailLogado;
    }
}