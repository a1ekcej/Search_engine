package searchengine.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.config.SitesList;
import searchengine.dto.statistics.PageDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

@Slf4j
@RequiredArgsConstructor
public class IndexPage extends RecursiveTask<List<PageDto>> {
    private final String url;
    private final List<String> urlList;
    private final List<PageDto> pageDtoList;
    private final SitesList config;
    @Override
    protected List<PageDto> compute() {
        Document doc = null;
        try{
            Thread.sleep(100);
            doc = Jsoup.connect(url)
                    .userAgent(config.getUserAgent())
                    .referrer(config.getReferrer())
                    .get();

        } catch (Exception ex) {
            ex.getMessage();
        }
        assert doc != null;
        String html = doc.outerHtml();
        Connection.Response response = doc.connection().response();
        int status = response.statusCode();
        PageDto pageDto = new PageDto(url, html, status);
        pageDtoList.add(pageDto);
        Elements elements = doc.select("body")
                .select("a");
        List<IndexPage> taskList = new ArrayList<>();
        String link;
        IndexPage task;
        for (Element el : elements) {
            link = el.attr("abs:href");
            if (isSiteElementsType(link)
                    && link.startsWith(el.baseUri())
                    && !link.equals(el.baseUri())
                    && !link.contains("#")
                    && !urlList.contains(link)) {
                urlList.add(link);
                task = new IndexPage(link, urlList, pageDtoList, config);
                task.fork();
                taskList.add(task);
            }
        }
        taskList.forEach(ForkJoinTask::join);
        return pageDtoList;
}

    private boolean isSiteElementsType(String pathPage) {
        List<String> WRONG_TYPES = Arrays.asList(
                "JPG", "gif", "gz", "jar", "jpeg", "jpg", "pdf", "png", "ppt", "pptx", "svg", "svg", "tar", "zip");
        return !WRONG_TYPES.contains(pathPage.substring(pathPage.lastIndexOf(".") + 1));
    }
}
