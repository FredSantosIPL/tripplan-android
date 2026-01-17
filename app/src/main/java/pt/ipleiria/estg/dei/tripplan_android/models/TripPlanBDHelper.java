package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class TripPlanBDHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TripPlanDB";
    private static final int DATABASE_VERSION = 1;

    // --- TABELA UTILIZADOR ---
    public static final String TABLE_UTILIZADOR= "user";
    public static final String ID_USER = "id";
    public static final String NOME_USER = "nome";
    public static final String EMAIL_USER = "email";
    public static final String PASS_USER = "password";

    // --- TABELA VIAGEM ---
    private static final String TABLE_VIAGEM = "viagem";
    private static final String ID_VIAGEM = "id";
    private static final String USER_ID_VIAGEM = "user_id";
    private static final String TITULO = "titulo";
    private static final String DESTINO = "destino";
    private static final String DATA_INICIO = "data_inicio";
    private static final String DATA_FIM = "data_fim";

    // --- TABELA ATIVIDADE ---
    private static final String TABLE_ATIVIDADE = "atividade";
    private static final String FK_VIAGEM = "viagem_id";
    private static final String TITULO_ATV = "titulo";
    private static final String TIPO_ATV = "tipo";

    // --- TABELA DESTINO ---
    private static final String TABLE_DESTINO = "destino_lista";
    private static final String NOME_DEST = "nome";
    private static final String PAIS_DEST = "pais";

    public TripPlanBDHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 1. User
        db.execSQL("CREATE TABLE " + TABLE_UTILIZADOR + " (" + ID_USER + " INTEGER PRIMARY KEY, " + NOME_USER + " TEXT, " + EMAIL_USER + " TEXT, " + PASS_USER + " TEXT, telefone TEXT, morada TEXT)");

        // 2. Viagem
        db.execSQL("CREATE TABLE " + TABLE_VIAGEM + "(" + ID_VIAGEM + " INTEGER PRIMARY KEY, " + USER_ID_VIAGEM + " INTEGER, " + TITULO + " TEXT, " + DESTINO + " TEXT, " + DATA_INICIO + " TEXT, " + DATA_FIM + " TEXT)");

        // 3. Atividades e Destinos
        db.execSQL("CREATE TABLE " + TABLE_ATIVIDADE + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + FK_VIAGEM + " INTEGER, " + TITULO_ATV + " TEXT, " + TIPO_ATV + " TEXT)");
        db.execSQL("CREATE TABLE " + TABLE_DESTINO + " (id INTEGER PRIMARY KEY AUTOINCREMENT, " + FK_VIAGEM + " INTEGER, " + NOME_DEST + " TEXT, " + PAIS_DEST + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTILIZADOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIAGEM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATIVIDADE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DESTINO);
        onCreate(db);
    }

    public void guardarViagensBD(ArrayList<Viagem> viagens, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Apagamos as viagens deste user para inserir as novas
        // NOTA: As atividades ficam "orfs" por milisegundos mas não são apagadas!
        db.delete(TABLE_VIAGEM, USER_ID_VIAGEM + " = ?", new String[]{String.valueOf(userId)});

        if (viagens == null) return;

        for (Viagem v : viagens) {
            ContentValues values = new ContentValues();
            values.put(ID_VIAGEM, v.getId());
            values.put(USER_ID_VIAGEM, userId);
            values.put(TITULO, v.getNomeViagem());
            values.put(DESTINO, v.getDestino());
            values.put(DATA_INICIO, v.getDataInicio());
            values.put(DATA_FIM, v.getDataFim());
            db.insert(TABLE_VIAGEM, null, values);
        }
    }

    //guardar os detalhes
    public void atualizarViagemBD(Viagem v) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Atualiza a capa
        ContentValues values = new ContentValues();
        values.put(TITULO, v.getNomeViagem());
        values.put(DESTINO, v.getDestino());
        values.put(DATA_INICIO, v.getDataInicio());
        values.put(DATA_FIM, v.getDataFim());
        db.update(TABLE_VIAGEM, values, ID_VIAGEM + " = ?", new String[]{String.valueOf(v.getId())});

        // Atualiza o recheio (Aqui sim!)
        guardarExtras(db, v.getId(), v.getAtividades(), v.getDestinos());
    }

    private void guardarExtras(SQLiteDatabase db, int idViagem, ArrayList<Atividade> atvs, ArrayList<Destino> dests) {
        // Guardar Atividades
        db.delete(TABLE_ATIVIDADE, FK_VIAGEM + "=?", new String[]{String.valueOf(idViagem)});
        if (atvs != null) {
            for (Atividade a : atvs) {
                ContentValues v = new ContentValues();
                v.put(FK_VIAGEM, idViagem);
                v.put(TITULO_ATV, a.getTitulo());
                v.put(TIPO_ATV, a.getTipo());
                db.insert(TABLE_ATIVIDADE, null, v);
            }
        }
        // Guardar Destinos
        db.delete(TABLE_DESTINO, FK_VIAGEM + "=?", new String[]{String.valueOf(idViagem)});
        if (dests != null) {
            for (Destino d : dests) {
                ContentValues v = new ContentValues();
                v.put(FK_VIAGEM, idViagem);
                v.put(NOME_DEST, d.getCidade());
                v.put(PAIS_DEST, d.getPais());
                db.insert(TABLE_DESTINO, null, v);
            }
        }
    }

    public ArrayList<Viagem> getAllViagensBD(int userId) {
        ArrayList<Viagem> viagens = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_VIAGEM, null, USER_ID_VIAGEM + " = ?", new String[]{String.valueOf(userId)}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(ID_VIAGEM));
                String titulo = cursor.getString(cursor.getColumnIndexOrThrow(TITULO));
                String destino = cursor.getString(cursor.getColumnIndexOrThrow(DESTINO));
                String dataInicio = cursor.getString(cursor.getColumnIndexOrThrow(DATA_INICIO));
                String dataFim = cursor.getString(cursor.getColumnIndexOrThrow(DATA_FIM));

                Viagem v = new Viagem(id, userId, titulo, dataInicio, dataFim);
                v.setDestino(destino);
                v.setAtividades(lerAtividades(db, id));
                v.setDestinos(lerDestinos(db, id));
                viagens.add(v);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return viagens;
    }
    private ArrayList<Atividade> lerAtividades(SQLiteDatabase db, int idViagem) {
        ArrayList<Atividade> lista = new ArrayList<>();
        Cursor c = db.query(TABLE_ATIVIDADE, null, FK_VIAGEM + "=?", new String[]{String.valueOf(idViagem)}, null, null, null);
        while (c.moveToNext()) {
            Atividade a = new Atividade();
            a.setTitulo(c.getString(c.getColumnIndexOrThrow(TITULO_ATV)));
            a.setTipo(c.getString(c.getColumnIndexOrThrow(TIPO_ATV)));
            lista.add(a);
        }
        c.close();
        return lista;
    }

    private ArrayList<Destino> lerDestinos(SQLiteDatabase db, int idViagem) {
        ArrayList<Destino> lista = new ArrayList<>();
        Cursor c = db.query(TABLE_DESTINO, null, FK_VIAGEM + "=?", new String[]{String.valueOf(idViagem)}, null, null, null);
        while (c.moveToNext()) {
            Destino d = new Destino();
            d.setCidade(c.getString(c.getColumnIndexOrThrow(NOME_DEST)));
            d.setPais(c.getString(c.getColumnIndexOrThrow(PAIS_DEST)));
            lista.add(d);
        }
        c.close();
        return lista;
    }
    public void guardarUtilizadorBD(Utilizador user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_UTILIZADOR, null, null);
        ContentValues values = new ContentValues();
        values.put(ID_USER, user.getId());
        values.put(NOME_USER, user.getNome());
        values.put(EMAIL_USER, user.getEmail());
        values.put(PASS_USER, user.getPassword());
        db.insert(TABLE_UTILIZADOR, null, values);
    }

    public Utilizador loginOffline(String email, String password) {
        Utilizador user = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_UTILIZADOR, null, EMAIL_USER + "=? AND " + PASS_USER + "=?", new String[]{email, password}, null, null, null);
        if (cursor.moveToFirst()) {
            user = new Utilizador();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow(ID_USER)));
            user.setNome(cursor.getString(cursor.getColumnIndexOrThrow(NOME_USER)));
            user.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(EMAIL_USER)));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow(PASS_USER)));
        }
        cursor.close();
        return user;
    }
}