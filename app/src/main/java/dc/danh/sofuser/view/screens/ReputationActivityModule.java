package dc.danh.sofuser.view.screens;

import dagger.Module;
import dagger.Provides;

@Module
public class ReputationActivityModule {
    private final ReputationActivity reputationActivity;

    public ReputationActivityModule(ReputationActivity reputationActivity) {
        this.reputationActivity = reputationActivity;
    }

    @Provides
    @ReputationActivityScope
    public ReputationActivity reputationActivity(){
        return reputationActivity;
    }
}
