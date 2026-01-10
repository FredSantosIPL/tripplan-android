package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences; // [NOVO] Importante para guardar dados
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
        Call<Viagem> call = apiService.getViagemDetalhes(idViagem);
        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful()) {
                    Viagem v = response.body();
                    Toast.makeText(context, "Detalhes carregados: " + v.getNomeViagem(), Toast.LENGTH_SHORT).show();
                    if (v.getTransportes() != null) {
                        System.out.println("Transportes: " + v.getTransportes().size());
                    }
                }
            }
            @Override
            public void onFailure(Call<Viagem> call, Throwable t) { }
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
}