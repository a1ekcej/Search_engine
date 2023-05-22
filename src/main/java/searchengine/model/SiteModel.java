package searchengine.model;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name = "site")
public class SiteModel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @Column(name = "status_time", nullable = false)
    private Date statusTime;
    @Column(name = "last_error")
    private String lastError;
    @Column(nullable = false)
    private String url;
    @Column(nullable = false)
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "siteId", cascade = CascadeType.ALL)
    protected List<PageModel> pageModelList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "siteModelId", cascade = CascadeType.ALL)
    protected List<LemmaModel> lemmaEntityList = new ArrayList<>();

    // add override hashcode, equals, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SiteModel siteModel = (SiteModel) obj;
        return id == siteModel.id && status == siteModel.status && statusTime.equals(siteModel.statusTime) &&
                Objects.equals(lastError, siteModel.lastError) && url.equals(siteModel.url) && name.equals(siteModel.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, status, statusTime, lastError, url, name);
    }

    @Override
    public String toString() {
        return "SiteModel[" + "id=" + id + ", status=" + status + ", statusTime=" + statusTime +
                ", lastError='" + lastError + '\'' + ", url='" + url + '\'' + ", name='" + name + '\'' + ']';
    }
}
