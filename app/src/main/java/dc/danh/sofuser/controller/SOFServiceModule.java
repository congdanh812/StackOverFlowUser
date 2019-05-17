package dc.danh.sofuser.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;

import dagger.Module;
import dagger.Provides;
import dc.danh.sofuser.controller.network.DateTimeConverter;
import dc.danh.sofuser.controller.network.SOFService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module(includes = NetworkModule.class)
public class SOFServiceModule {

    @Provides
    @SOFApplicationScope
    public SOFService githubService(Retrofit gitHubRetrofit) {
        return gitHubRetrofit.create(SOFService.class);
    }

    @Provides
    @SOFApplicationScope
    public Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(DateTime.class, new DateTimeConverter());
        return gsonBuilder.create();
    }

    @Provides
    @SOFApplicationScope
    public Retrofit retrofit(OkHttpClient okHttpClient, Gson gson) {
        return new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl("https://api.stackexchange.com/2.2/")
                .build();
    }

}
