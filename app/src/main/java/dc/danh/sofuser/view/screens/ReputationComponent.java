package dc.danh.sofuser.view.screens;

import dagger.Component;
import dc.danh.sofuser.controller.SOFApplicationComponent;

@ReputationActivityScope
@Component(modules = ReputationActivityModule.class, dependencies = SOFApplicationComponent.class)
public interface ReputationComponent {
    void injectReputationActivity(ReputationActivity reputationActivity);
}
