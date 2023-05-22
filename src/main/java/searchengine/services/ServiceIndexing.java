package searchengine.services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.responce.IndexingResponse;
import searchengine.model.SiteModel;
import searchengine.builder.SiteParser;
import searchengine.repository.IRepositoryIndex;
import searchengine.repository.IRepositoryLemma;
import searchengine.repository.IRepositoryPage;
import searchengine.repository.IRepositorySite;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import static searchengine.model.Status.INDEXING;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceIndexing implements IServiceIndexing {
    private static final int processorCoreCount = Runtime.getRuntime().availableProcessors();
    private ExecutorService executorService;
    private final SitesList config;
    private final IRepositorySite repositorySite;
    private final IRepositoryPage repositoryPage;
    private final IRepositoryLemma repositoryLemma;
    private final IRepositoryIndex repositoryIndex;
    private final IndexLemma indexLemma;
    private final SiteParser siteParser;

    @Override
    public boolean indexingAllSites() {
        if (isIndexingActive()) {
            log.debug("Indexing started");
            new IndexingResponse(false, "Индексация запущена").getErrorMsg();
        } else {
            List<Site> siteList = config.getSites();
            executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            for (Site site : siteList) {
                String url = site.getUrl();
                SiteModel siteModel = new SiteModel();
                siteModel.setName(site.getName());
                log.info("Parsing web site ".concat(site.getName()));
                executorService.submit(new SiteIndexRun(repositorySite, repositoryPage, repositoryLemma,
                        repositoryIndex, indexLemma, siteParser, url, config));
            }
            executorService.shutdown();
        }
        return true;
    }

    @Override
    public boolean indexingOnePage(String url) {
        if (urlCheck(url)) {
            log.info("Reindexing site start - " + url);
            executorService = Executors.newFixedThreadPool(processorCoreCount);
            executorService.submit(new SiteIndexRun(repositorySite, repositoryPage, repositoryLemma,
                    repositoryIndex, indexLemma, siteParser, url, config));
            executorService.shutdown();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean stopIndexing() {
        if (isIndexingActive()){
            log.info("ндексация остановлена!");
            executorService.shutdown();
            return true;
        } else {
            log.info("ндексация не запущена!");
            return false;
        }
    }

    private boolean isIndexingActive() {
        repositorySite.flush();
        Iterable<SiteModel> siteList = repositorySite.findAll();
        for (SiteModel site : siteList) {
            if (site.getStatus() == INDEXING) {
                return true;
            }
        }
        return false;
    }

    private boolean urlCheck(String url) {
        List<Site> urlList = config.getSites();
        for (Site site : urlList) {
            if (site.getUrl().equals(url)) {
                return true;
            }
        }
        return false;
    }
}
