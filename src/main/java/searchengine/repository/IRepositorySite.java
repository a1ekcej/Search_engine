package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.SiteModel;

import javax.transaction.Transactional;

@Repository
public interface IRepositorySite extends JpaRepository<SiteModel, Long> {
    SiteModel findByUrl(String url);
    //SiteModel findByUrl(long id);
    //SiteModel findByUrl(SiteModel site);
}
