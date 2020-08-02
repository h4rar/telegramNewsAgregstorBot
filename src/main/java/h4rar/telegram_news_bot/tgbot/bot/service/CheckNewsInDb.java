package h4rar.telegram_news_bot.tgbot.bot.service;


import h4rar.telegram_news_bot.tgbot.model.News;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import h4rar.telegram_news_bot.tgbot.repository.NewsRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Service
public class CheckNewsInDb {
    @Autowired
    final ClientRepository clientRepository;
    final @Autowired
    NewsRepository newsRepository;
    public CheckNewsInDb(ClientRepository clientRepository, NewsRepository newsRepository) {
        this.clientRepository = clientRepository;
        this.newsRepository = newsRepository;
    }

//    public List<News> getAllNewNews(NewsRepository newsRepository, long timeIntervalMillisecond) {
//        Date dateNow = new Date();
//        Date dateLast = new Date(dateNow.getTime() - timeIntervalMillisecond);
//        List<News> byDateGreaterThan = newsRepository.findByDateGreaterThan(dateLast);
//        return byDateGreaterThan;
//    }

    public List<News> getAllNewNewsMySub(long chatId, long timeIntervalMillisecond) {
        Date dateNow = new Date();
        Date dateLast = new Date(dateNow.getTime() - timeIntervalMillisecond);
        List<String> clientSourceName = ClientSourceService.getClientSourceName(clientRepository, chatId);
        List<News> bySourceIn = newsRepository.findByDateGreaterThanAndSourceInOrderByDate(dateLast, clientSourceName);
        return bySourceIn;
    }


}
