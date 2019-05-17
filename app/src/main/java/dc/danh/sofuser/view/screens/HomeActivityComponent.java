package dc.danh.sofuser.view.screens;

import dagger.Component;
import dc.danh.sofuser.controller.SOFApplicationComponent;

@HomeActivityScope
@Component(modules = HomeActivityModule.class, dependencies = SOFApplicationComponent.class)
public interface HomeActivityComponent {

    void injectHomeActivity(HomeActivity homeActivity);
}
