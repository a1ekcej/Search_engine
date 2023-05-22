package searchengine.services.morphology;

import java.util.HashMap;
import java.util.List;

public interface IMorphology {
    HashMap<String, Integer> getLemmas(String content);
    List<Integer> getLemmma(String lemma);
    List<Integer> findLemma(String content, String lamma);
}
