package dc.danh.sofuser.controller;

import android.app.Activity;
import android.app.Application;

import dc.danh.sofuser.R;
import io.github.inflationx.calligraphy3.CalligraphyConfig;
import io.github.inflationx.calligraphy3.CalligraphyInterceptor;
import io.github.inflationx.viewpump.ViewPump;
import timber.log.Timber;

public class SOFApplication extends Application {

    private SOFApplicationComponent component;

    public static SOFApplication get(Activity activity) {
        return (SOFApplication) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Timber.plant(new Timber.DebugTree());

        component = DaggerSOFApplicationComponent.builder()
                .contextModule(new ContextModule(this))
                .build();
        ViewPump.init(ViewPump.builder()
                .addInterceptor(new CalligraphyInterceptor(
                        new CalligraphyConfig.Builder()
                                .setDefaultFontPath("fonts/SF-Pro-Text-Regular.ttf")
                                .setFontAttrId(R.attr.fontPath)
                                .build()))
                .build());
    }

    public SOFApplicationComponent component() {
        return component;
    }
}