package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
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
    private ViagensListener viagensListener; // Para avisar a Activity

    private SingletonGestor(Context context){
        this.context = context;
        this.viagens = new ArrayList<>();

        // 1. Inicializar Base de Dados Local (O teu código antigo)
        bdHelper = new TripPlanBDHelper(context);

        // 2. Inicializar a API (Retrofit)
        apiService = ServiceBuilder.buildService(TripplanAPI.class);
    }

    public static synchronized SingletonGestor getInstance(Context context){
        if(instance == null){
            instance = new SingletonGestor(context);
        }
        return instance;
    }

    // --- INTERFACE LISTENER (Para a Activity saber quando atualizar a lista) ---
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
       MÉTODOS DA API (SIS - CRUD + Master/Detail)
       ====================================================== */

    private int userIdLogado = 0;

    // Método para guardar o ID quando o login é feito com sucesso
    public void setUserIdLogado(int id) {
        this.userIdLogado = id;
    }

    public int getUserIdLogado() {
        return userIdLogado;
    }

    // 1. READ (Buscar todas as viagens)
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

                    // Avisa a Activity para atualizar o ecrã
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

    // 2. CREATE (Criar Viagem -> Dispara MQTT no servidor)
    public void adicionarViagemAPI(Viagem viagem) {
        Call<Viagem> call = apiService.adicionarViagem(viagem);
        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful()) {
                    Viagem nova = response.body();
                    viagens.add(nova); // Adiciona à lista local
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

    // 3. MASTER/DETAIL (Buscar detalhes + transportes)
    public void getViagemDetalhesAPI(int idViagem) {
        Call<Viagem> call = apiService.getViagemDetalhes(idViagem);
        call.enqueue(new Callback<Viagem>() {
            @Override
            public void onResponse(Call<Viagem> call, Response<Viagem> response) {
                if (response.isSuccessful()) {
                    Viagem v = response.body();
                    // Aqui podes abrir a Activity de Detalhes ou atualizar algo
                    Toast.makeText(context, "Detalhes carregados: " + v.getNomeViagem(), Toast.LENGTH_SHORT).show();
                    // Exemplo: mostrar quantos transportes tem
                    if (v   .getTransportes() != null) {
                        System.out.println("Transportes: " + v.getTransportes().size());
                    }
                }
            }
            @Override
            public void onFailure(Call<Viagem> call, Throwable t) { }
        });
    }

    // 4. LOGIN API (Autenticação Online)
    public void loginAPI(String email, String password, final LoginListener loginListener) {
        LoginRequest request = new LoginRequest(email, password); // Tens de ter este modelo criado
        Call<LoginResponse> call = apiService.fazerLogin(request);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Login Sucesso
                    String token = response.body().getToken();
                    // Podes guardar o token em SharedPreferences aqui se quiseres
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

    // Interface auxiliar para o Login
    public interface LoginListener {
        void onLoginSuccess();
        void onLoginError(String error);
    }

    // Auxiliar simples para checar net (podes melhorar depois)
    private boolean isInternetAvailable() {
        // Por agora retorna true, mas deves implementar o ConnectivityManager
        return true;
    }

    /* ======================================================
       MÉTODOS DA BASE DE DADOS LOCAL (SQLITE) - JÁ EXISTENTES
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
            user = new Utilizador((int)id, nome, email, password, tel, morada); // Ajustei o cast para int se necessário
            cursor.close();
        }
        return user;
    }
}