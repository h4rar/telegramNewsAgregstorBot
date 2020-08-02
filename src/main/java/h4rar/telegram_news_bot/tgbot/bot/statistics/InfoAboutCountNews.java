package h4rar.telegram_news_bot.tgbot.bot.statistics;

import h4rar.telegram_news_bot.tgbot.model.News;
import org.springframework.data.rest.core.config.Projection;

@Projection(types = {News.class})
public interface InfoAboutCountNews {
    String getSource();

    int getCount();
}