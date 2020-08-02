package h4rar.telegram_news_bot.tgbot.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Entity
@Setter
@Getter
public class News {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private String title;
    private String source;
    @Type(type = "text")
    private String description;
    @Type(type = "text")
    private String content;
    private String link;
    private Date date;
    private int wordsCountInDescription;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "news", cascade=CascadeType.ALL)
    private List<WordWeight> wordWeight;

    public News() {
    }

    public News(String title, String source, String description, int wordsCountInDescription, String link, Date date, String content) {
        this.title = title;
        this.source = source;
        this.description = description;
        this.link = link;
        this.date = date;
        this.wordsCountInDescription = wordsCountInDescription;
        this.content = content;
    }
}
