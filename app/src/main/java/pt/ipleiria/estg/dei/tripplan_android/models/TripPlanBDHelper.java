package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TripPlanBDHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TripPlanDB";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_UTILIZADOR = "utilizador";
    public static final String ID_USER = "id";
    public static final String NOME_USER = "nome";
    public static final String EMAIL_USER = "email";
    public static final String PASS_USER = "password";
    public static final String TELEFONE_USER = "telefone";
    public static final String MORADA_USER = "morada";

    public TripPlanBDHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableUser = "CREATE TABLE " + TABLE_UTILIZADOR + " (" +
            ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            NOME_USER + " TEXT NOT NULL, " +
            EMAIL_USER + " TEXT NOT NULL, " +
            PASS_USER + " TEXT NOT NULL UNIQUE, " +
            TELEFONE_USER + " TEXT, " +
            MORADA_USER + " TEXT)";
        db.execSQL(createTableUser);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UTILIZADOR);
        onCreate(db);
    }
}
