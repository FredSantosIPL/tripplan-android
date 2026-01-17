package pt.ipleiria.estg.dei.tripplan_android.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {

    // 1. MUDANÇA: Removemos o 'final' para a variável poder mudar de valor
    // Este é o valor por defeito (Emulador), mas vai ser sobreposto pelo Singleton
    private static String urlBase = "http://10.0.2.2:8888/tripplan/tripplan/tripplan/backend/web/index.php/";

    private static Retrofit retrofit = null;

    // 2. NOVO MÉTODO: O Singleton chama isto quando leres as Preferências
    public static void setUrlBase(String novaUrl) {
        urlBase = novaUrl;
        retrofit = null; // IMPORTANTE: Define como null para obrigar a reconstruir no próximo pedido
    }

    public static <S> S buildService(Class<S> serviceType) {
        // Se o retrofit for null (ou porque é a 1ª vez, ou porque mudámos o IP), ele cria de novo
        if (retrofit == null) {

            // O teu Logger (Mantive igual, é essencial para debugging)
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(urlBase) // <--- Agora usa a variável dinâmica 'urlBase'
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit.create(serviceType);
    }
}