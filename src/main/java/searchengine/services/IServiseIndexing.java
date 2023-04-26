package searchengine.services;

import java.util.concurrent.RecursiveTask;

public interface IServiseIndexing {
    boolean indexingAllSites();
    boolean indexingOneSite();
    boolean stopIndexing();
}
