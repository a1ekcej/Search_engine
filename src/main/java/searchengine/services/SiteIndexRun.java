package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import searchengine.config.Site;
import searchengine.config.SitesList;
import searchengine.dto.statistics.IndexDto;
import searchengine.dto.statistics.LemmaDto;
import searchengine.dto.statistics.PageDto;
import searchengine.model.*;
import searchengine.builder.SiteParser;
import searchengine.repository.IRepositoryIndex;
import searchengine.repository.IRepositoryLemma;
import searchengine.repository.IRepositoryPage;
import searchengine.repository.IRepositorySite;
import searchengine.services.index.PageIndex;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ForkJoinPool;

@Slf4j
@RequiredArgsConstructor
public class SiteIndexRun implements Callable<Boolean> {

    private final IRepositorySite repositorySite;
    private final IRepositoryPage repositoryPage;
    private final IRepositoryLemma repositoryLemma;
    private final IRepositoryIndex repositoryIndex;
    private final IndexLemma indexLemma;
    private final SiteParser siteParser;
    private final String url;
    private final SitesList configSite;
    @Override
    public Boolean call() throws InterruptedException{
        if (repositorySite.findByUrl(url) != null) {
            log.info("start site date delete from ".concat(url));
            SiteModel site = repositorySite.findByUrl(url);
            site.setStatus(Status.INDEXING);
            site.setName(getSiteName());
            site.setStatusTime(new Date());
            repositorySite.saveAndFlush(site);
            repositorySite.delete(site);
        }
        log.info("Site indexing start ".concat(url).concat(" ").concat(getSiteName()) );
        SiteModelIndexing siteModelIndexing = new SiteModelIndexing();
        SiteModel site = siteModelIndexing.getSiteModelRecord();
        try {
            if (!Thread.interrupted()) {
                List<PageDto> pageDtoList;
                if (!Thread.interrupted()) {
                    String urls = url.concat("/");
                    List<PageDto> pageDtosList = new CopyOnWriteArrayList<>();
                    List<String> urlList = new CopyOnWriteArrayList<>();
                    ForkJoinPool forkJoinPool = new ForkJoinPool(Runtime.getRuntime().availableProcessors());
                    List<PageDto> pages = forkJoinPool.invoke(new PageIndex(urls,urlList, pageDtosList, configSite));
                    pageDtoList = new CopyOnWriteArrayList<>(pages);
                } else throw new InterruptedException("Fork join exception!");
                List<PageModel> pageList = new CopyOnWriteArrayList<>();
                int start;
                String pagePath;
                for (PageDto page : pageDtoList) {
                    start = page.url().indexOf(url) + url.length();
                    pagePath = page.url().substring(start);
                    pageList.add(new PageModel(site, pagePath, page.code(), page.content()));
                }
                repositoryPage.saveAllAndFlush(pageList);
            } else {
                throw new InterruptedException("Local interrupted exception.");
            }
            new LemmaIndexing().saveLemmasInLemmaDTO();
            new AllSiteIndexing().allSiteIndexing(site);
        } catch (InterruptedException e) {
            log.error("WebParser stopped from ".concat(url).concat(". ").concat(e.getMessage()));
            new SiteModelIndexing().getErrorSiteModelRecord(site);
            new InterruptedException("Interrupted exception");
        }
        return true;
    }

    private String getSiteName() {
        return configSite.getSites().stream()
                .filter(site -> site.getUrl().equals(url))
                .findFirst()
                .map(Site::getName)
                .orElse("");
    }

    private class SiteModelIndexing {
        protected SiteModel getSiteModelRecord() {
            SiteModel site = new SiteModel();
            site.setUrl(url);
            site.setName(getSiteName());
            site.setStatus(Status.INDEXING);
            site.setStatusTime(new Date());
            repositorySite.saveAndFlush(site);
            return site;
        }

        protected void getErrorSiteModelRecord(SiteModel site) {
            SiteModel sites = new SiteModel();
            sites.setLastError("WebParser stopped");
            sites.setStatus(Status.FAILED);
            sites.setStatusTime(new Date());
            repositorySite.saveAndFlush(site);
        }
    }
    private class LemmaIndexing {

        protected void saveLemmasInLemmaDTO() throws InterruptedException {
            if (!Thread.interrupted()) {
                SiteModel siteModel = repositorySite.findByUrl(url);
                siteModel.setStatusTime(new Date());
                indexLemma.startLemmaIndexer();
                List<LemmaDto> lemmaDtoList = indexLemma.getLemmaDto();
                List<LemmaModel> lemmaList = new CopyOnWriteArrayList<>();

                for (LemmaDto lemmaDto : lemmaDtoList) {
                    lemmaList.add(new LemmaModel(lemmaDto.lemma(), lemmaDto.frequency(), siteModel));
                }
                repositoryLemma.saveAllAndFlush(lemmaList);
            } else {
                throw new InterruptedException("Error!!!");
            }
        }
    }

    private class AllSiteIndexing {
        protected void allSiteIndexing(SiteModel site) throws InterruptedException {
            if (!Thread.interrupted()) {
                siteParser.startParsing(site);
                List<IndexDto> indexDtoList = new CopyOnWriteArrayList<>(siteParser.getConfig());
                List<IndexModel> indexModels = new CopyOnWriteArrayList<>();
                site.setStatusTime(new Date());
                PageModel page;
                LemmaModel lemma;
                for (IndexDto indexDto : indexDtoList) {
                    page = repositoryPage.getById(indexDto.pageID());
                    lemma = repositoryLemma.getById(indexDto.lemmaID());
                    indexModels.add(new IndexModel(page, lemma, indexDto.rank()));
                }
                repositoryIndex.saveAllAndFlush(indexModels);
                log.info("WebParser stopping ".concat(url));
                site.setStatusTime(new Date());
                site.setStatus(Status.INDEXED);
                repositorySite.saveAndFlush(site);

            } else {
                throw new InterruptedException("Invalid getSiteAllIndexing");
            }
        }
    }



}
