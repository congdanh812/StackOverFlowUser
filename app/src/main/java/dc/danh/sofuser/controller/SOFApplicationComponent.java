package dc.danh.sofuser.controller;

import dagger.Component;
import dc.danh.sofuser.controller.network.SOFService;

@SOFApplicationScope
@Component(modules = {SOFServiceModule.class, ActivityModule.class})
public interface SOFApplicationComponent {
    SOFService getGithubService();
}
