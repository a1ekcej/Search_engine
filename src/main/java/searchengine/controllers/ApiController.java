package searchengine.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.statistics.responce.IndexingResponse;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.IServiceIndexing;
import searchengine.services.StatisticsService;

@RestController
@RequestMapping("/api")
@Slf4j
public class ApiController {

    private final StatisticsService statisticsService;
    private final IServiceIndexing serviceIndexing;

    public ApiController(StatisticsService statisticsService, IServiceIndexing serviseIndexing) {
        this.statisticsService = statisticsService;
        this.serviceIndexing = serviseIndexing;
    }

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<Object> startIndexing() {
        if(serviceIndexing.indexingAllSites()){
            return new ResponseEntity<>(new IndexingResponse(true, "Идет инденксация сайтов..."), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new IndexingResponse(false, "Индексация не запущена!"), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Object> stopIndexing() {
        if(serviceIndexing.indexingAllSites()){
            return new ResponseEntity<>(new IndexingResponse(true, "Индексация остановлена"), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new IndexingResponse(false, "Индексация не остановлена"), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Object> indexingOnePage(@RequestParam(name = "url") String url) {
        if (url.isEmpty()) {
            log.info("Страница не указана");
            return new ResponseEntity<>(new IndexingResponse(false, "Страница не указана"), HttpStatus.BAD_REQUEST);
        } else {
            if (serviceIndexing.indexingOnePage(url)) {
                log.info("Страница - " + url + " - добавлена на переиндексацию");
                return new ResponseEntity<>(new IndexingResponse(true, ""), HttpStatus.OK);
            } else {
                log.info("Не верно введен url страницы");
                return new ResponseEntity<>(new IndexingResponse(false, "Не верно введен url страницы"), HttpStatus.BAD_REQUEST);
            }
        }
    }
}