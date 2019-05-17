package dc.danh.sofuser.controller;

import android.content.Context;

import java.io.File;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import timber.log.Timber;

@Module(includes = ContextModule.class)
public class NetworkModule {

    @Provides
    @SOFApplicationScope
    public HttpLoggingInterceptor loggingInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Timber.i(message));
        interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        return interceptor;
    }

    @Provides
    @SOFApplicationScope
    public Cache cache(File cacheFile) {
        return new Cache(cacheFile, 10 * 1000 * 1000); //10MB Cache
    }

    @Provides
    @SOFApplicationScope
    public File cacheFile(@ApplicationContext Context context) {
        return new File(context.getCacheDir(), "okhttp_cache");
    }

    @Provides
    @SOFApplicationScope
    public OkHttpClient okHttpClient(HttpLoggingInterceptor loggingInterceptor, Cache cache) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .cache(cache)
                .build();
    }
}
