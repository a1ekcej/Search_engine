package searchengine.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.LemmaModel;
import searchengine.model.PageModel;
import searchengine.model.SiteModel;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Repository
public interface IRepositoryPage extends JpaRepository<PageModel, Long> {

    @Transactional
    long countBySiteId (SiteModel siteId);
    @Transactional
    PageModel getById(long pageId);
    @Transactional
    Iterable<PageModel> findBySiteId(SiteModel url);

    @Transactional
    @Query(value = "SELECT * FROM Words_index JOIN Page  ON Page.id = Words_index.page_id WHERE Words_index.lemma_id IN :lemmas", nativeQuery = true)
    List<PageModel> findbyLemmas(@Param("lemmas")Collection<LemmaModel> lemmas);
}
