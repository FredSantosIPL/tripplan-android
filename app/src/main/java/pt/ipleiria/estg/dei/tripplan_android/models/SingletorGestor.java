package pt.ipleiria.estg.dei.tripplan_android.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SingletorGestor {
    private static SingletorGestor instance = null;
    private TripPlanBDHelper bdHelper = null;

    private SQLiteDatabase database = null;

    private SingletorGestor(Context context){
        bdHelper = new TripPlanBDHelper(context);
    }

    public static SingletorGestor getInstance(Context context){
        if(instance == null){
            instance = new SingletorGestor(context);
        }
        return instance;
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
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );


        Utilizador user = null;
        if (cursor != null && cursor.moveToFirst()) {
            long id = cursor.getLong(cursor.getColumnIndexOrThrow(TripPlanBDHelper.ID_USER));
            String nome = cursor.getString(cursor.getColumnIndexOrThrow(TripPlanBDHelper.NOME_USER));
            String tel = cursor.getString(cursor.getColumnIndexOrThrow(TripPlanBDHelper.TELEFONE_USER));
            String morada = cursor.getString(cursor.getColumnIndexOrThrow(TripPlanBDHelper.MORADA_USER));

            user = new Utilizador(id, nome, email, password, tel, morada);
            cursor.close();
            }
        return user;
    }
}
