package h4rar.telegram_news_bot.tgbot.repository;


import h4rar.telegram_news_bot.tgbot.bot.statistics.InfoAboutCountNews;
import h4rar.telegram_news_bot.tgbot.bot.statistics.InfoAboutCountWords;
import h4rar.telegram_news_bot.tgbot.bot.statistics.InfoAboutTimePosting;
import h4rar.telegram_news_bot.tgbot.model.News;
import h4rar.telegram_news_bot.tgbot.model.WordWeight;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface WordWeightRepository extends CrudRepository<WordWeight, Integer> {
    List<WordWeight> findByWordIn(List<String> word);
}