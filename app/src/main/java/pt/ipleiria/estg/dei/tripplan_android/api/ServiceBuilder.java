package pt.ipleiria.estg.dei.tripplan_android.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceBuilder {
    // Se usares emulador: "http://10.0.2.2:3000/"
    // Se for telemóvel: IP do PC (ex: "http://192.168.1.66:3000/")
    private static final String URL = "http://10.0.2.2:3000/";

    private static Retrofit retrofit = null;

    public static <S> S buildService(Class<S> serviceType) {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder().build())
                    .build();
        }
        return retrofit.create(serviceType);
    }
}