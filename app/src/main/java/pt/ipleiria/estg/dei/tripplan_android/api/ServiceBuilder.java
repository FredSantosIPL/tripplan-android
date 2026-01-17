package pt.ipleiria.estg.dei.tripplan_android.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {

    private static String urlBase = "http://192.168.1.237/tripplan-web/tripplan/backend/web/index.php/";
    private static Retrofit retrofit = null;


    //NOVO MÉTODO: O Singleton chama isto quando leres as Preferências
    public static void setUrlBase(String novaUrl) {
        urlBase = novaUrl;
        retrofit = null;
    }

    public static <S> S buildService(Class<S> serviceType) {
        if (retrofit == null) {

            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);

            retrofit = new Retrofit.Builder()
                    .baseUrl(urlBase)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit.create(serviceType);
    }
}