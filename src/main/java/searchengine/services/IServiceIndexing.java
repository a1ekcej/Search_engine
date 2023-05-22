package searchengine.services;

import searchengine.dto.statistics.responce.IndexingResponse;

public interface IServiceIndexing {
    boolean indexingAllSites();
    boolean indexingOnePage(String url);
    boolean stopIndexing();
}
