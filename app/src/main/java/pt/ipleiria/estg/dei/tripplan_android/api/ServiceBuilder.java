    package pt.ipleiria.estg.dei.tripplan_android.api;

    import okhttp3.OkHttpClient;
    import okhttp3.logging.HttpLoggingInterceptor;
    import retrofit2.Retrofit;
    import retrofit2.converter.gson.GsonConverterFactory;

    public class ServiceBuilder {
        private static final String URL = "http://10.0.2.2:8888/tripplan/tripplan/tripplan/backend/web/index.php/";        private static Retrofit retrofit = null;

        public static <S> S buildService(Class<S> serviceType) {
            if (retrofit == null) {
                // Isto permite ver o JSON e os erros no Logcat do Android Studio
                HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                logging.setLevel(HttpLoggingInterceptor.Level.BODY);

                OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
                httpClient.addInterceptor(logging);

                retrofit = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .client(httpClient.build())
                        .build();
            }
            return retrofit.create(serviceType);
        }
    }