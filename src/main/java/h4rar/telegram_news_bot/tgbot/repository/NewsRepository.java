package h4rar.telegram_news_bot.tgbot.repository;


import h4rar.telegram_news_bot.tgbot.bot.statistics.InfoAboutCountNews;
import h4rar.telegram_news_bot.tgbot.bot.statistics.InfoAboutCountWords;
import h4rar.telegram_news_bot.tgbot.bot.statistics.InfoAboutTimePosting;
import h4rar.telegram_news_bot.tgbot.model.News;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface NewsRepository extends CrudRepository<News, Integer> {
    News findFirstBySourceContainingOrderByDateDesc(String str);
    List<News> findByDateGreaterThanAndSourceInOrderByDate(Date date, List<String> source);
    List<News> findBySourceIn(List<String> source);
    List<News> findByDateGreaterThanAndSourceOrderByDate (Date date, String source);

    @Query(value = "select count(*), source from News group by source", nativeQuery = true)
    List<InfoAboutCountNews> getInfoAboutCountNews();

    @Query(value = "select t1.to_date, t1.source, t1.avg from (select max(id), max(description), max(link), max(title), avg(words_count_in_description), source, to_date(cast(date as TEXT), 'YYYY.MM.DD') from news group by to_date(cast(date as TEXT), 'YYYY.MM.DD'),source order by source, to_date(cast(date as TEXT), 'YYYY.MM.DD')) as t1 where to_date > ?1", nativeQuery = true)
    List<InfoAboutCountWords> getInfoAboutCountWords(Date date);

    @Query(value = "select source, date from news where date > ?1 group by date,source order by source, date", nativeQuery = true)
    List<InfoAboutTimePosting> getInfoAboutTimePosting(Date date);
}