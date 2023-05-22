package searchengine.model;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "page", indexes = {@Index(name = "path_site", columnList = "path")})
@Getter
@Setter
@NoArgsConstructor
public class PageModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false, referencedColumnName = "id")
    private SiteModel siteId;
    @Column(columnDefinition = "VARCHAR(255)", length = 256, nullable = false)
    private String path;
    @Column(nullable = false)
    private int code;
    @Column(columnDefinition = "MEDIUMTEXT")
    private String content;

    @OneToMany(mappedBy = "page", cascade = CascadeType.ALL)
    private List<IndexModel> index = new ArrayList<>();

    public PageModel(SiteModel siteId, String path, int code, String content) {
        this.siteId = siteId;
        this.path = path;
        this.code = code;
        this.content = content;
    }

    // add override hashcode, equals, toString
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PageModel pageModel = (PageModel) obj;
        return id == pageModel.id && code == pageModel.code &&
                siteId.equals(pageModel.siteId) &&
                path.equals(pageModel.path) &&
                content.equals(pageModel.content) &&
                index.equals(pageModel.index);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, siteId, path, code, content, index);
    }

    @Override
    public String toString() {
        return "PageModel[" + "id=" + id + ", siteId=" + siteId + ", path='" + path + '\'' +
                ", code=" + code + ", content='" + content + '\'' + ", index=" + index + ']';
    }
}

