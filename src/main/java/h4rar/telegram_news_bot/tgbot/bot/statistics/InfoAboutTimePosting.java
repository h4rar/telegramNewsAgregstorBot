package h4rar.telegram_news_bot.tgbot.bot.statistics;

import h4rar.telegram_news_bot.tgbot.model.News;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(types = {News.class})
public interface InfoAboutTimePosting {
    String getSource();

    Date getDate();
}