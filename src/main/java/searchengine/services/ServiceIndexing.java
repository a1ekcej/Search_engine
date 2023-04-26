package searchengine.services;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.config.Site;
import searchengine.config.SitesList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RecursiveTask;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceIndexing extends RecursiveTask implements IServiseIndexing {
    private ExecutorService executorService;

    @Override
    public boolean indexingAllSites() {
        List<Site> sites = new SitesList().getSites();
        return true;
    }

    @Override
    public boolean indexingOneSite() {
        return false;
    }

    @Override
    public boolean stopIndexing() {
        return false;
    }

    @Override
    protected Object compute() {
        return null;
    }
}
