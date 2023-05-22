package searchengine.services;

import lombok.AllArgsConstructor;
import searchengine.model.SiteModel;
import searchengine.model.Status;
import searchengine.repository.IRepositorySite;

import java.util.Date;

@AllArgsConstructor
public class IndexingSiteModel {
    private final String url;
    private final String siteName;
    private final IRepositorySite repositorySite;

    public SiteModel pushRepository() {
        SiteModel site = new SiteModel();
        site.setUrl(url);
        site.setName(siteName);
        site.setStatus(Status.INDEXING);
        site.setStatusTime(new Date());
        repositorySite.saveAndFlush(site);
        return site;
    }
}
