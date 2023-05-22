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
@Getter
@Setter
@Table(name = "lemma", indexes = {@Index(name = "lemma_list", columnList = "lemma")})
@NoArgsConstructor
public class LemmaModel implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", referencedColumnName = "id")
    private SiteModel siteModelId;
    private String lemma;
    private int frequency;

    @OneToMany(mappedBy = "lemma", cascade = CascadeType.ALL)
    private List<IndexModel> index = new ArrayList<>();

    public LemmaModel(String lemma, int frequency, SiteModel siteEntityId) {
        this.lemma = lemma;
        this.frequency = frequency;
        this.siteModelId = siteEntityId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, siteModelId, lemma, frequency, index);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        LemmaModel lemmaModel = (LemmaModel) obj;
        return id == lemmaModel.id && frequency == lemmaModel.frequency &&
                siteModelId.equals(lemmaModel.siteModelId) &&
                lemma.equals(lemmaModel.lemma) &&
                index.equals(lemmaModel.index);
    }

    @Override
    public String toString() {
        return "LemmaModel[" +
                "id=" + id + ", siteModelId=" + siteModelId + ", lemma='" + lemma + '\'' +
                ", frequency=" + frequency + ", index=" + index + ']';
    }
}
