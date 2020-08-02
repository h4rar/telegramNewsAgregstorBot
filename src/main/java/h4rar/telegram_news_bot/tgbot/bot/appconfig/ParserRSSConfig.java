package h4rar.telegram_news_bot.tgbot.bot.appconfig;

import h4rar.telegram_news_bot.tgbot.parserRSS.ParserRSS;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "scheduled")
public class ParserRSSConfig {
    @Bean
    public ParserRSS myParserRSS() {
        ParserRSS parserRSS = new ParserRSS();
        List<String> listURL = Arrays.asList(
                "http://news.mit.edu/rss/research",
                "https://news.harvard.edu/gazette/section/science-technology/feed/",
                "https://mipt.ru/rss",
                "https://ssau.ru/rss",
                "https://www.msu.ru/news/rss/"
                );
        parserRSS.setListURL(listURL);
        return parserRSS;
    }
}
