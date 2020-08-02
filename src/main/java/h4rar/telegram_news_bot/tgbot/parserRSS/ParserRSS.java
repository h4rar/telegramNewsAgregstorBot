package h4rar.telegram_news_bot.tgbot.parserRSS;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.List;


@Setter
@Getter
public class ParserRSS {
    private List<String> listURL;

    public ParserRSS() {
    }

    public SyndFeed getSyndFeedForUrl(String url) {
        SyndFeed feed = null;
        try {
            URL feedUrl = new URL(url);
            SyndFeedInput input = new SyndFeedInput();
            feed = input.build(new XmlReader(feedUrl));
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("ERROR: " + ex.getMessage());
        }
        return feed;
    }
}
