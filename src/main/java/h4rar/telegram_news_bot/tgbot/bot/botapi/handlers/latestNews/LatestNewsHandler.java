package h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.latestNews;


import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import h4rar.telegram_news_bot.tgbot.bot.botapi.BotState;
import h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.InputMessageHandler;
import h4rar.telegram_news_bot.tgbot.bot.service.*;
import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.model.News;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import h4rar.telegram_news_bot.tgbot.repository.NewsRepository;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class LatestNewsHandler implements InputMessageHandler {
    private final MainMenuService mainMenuService;
    private final NewsRepository newsRepository;
    private final ClientRepository clientRepository;
    private final ReplyMessagesService replyMessagesService;
    private final NewsAggregatorMenu newsAggregatorMenu;
    private final LocaleMessageService localeMessageService;
    private final BotConnect botConnect;

    public LatestNewsHandler(MainMenuService mainMenuService, NewsRepository newsRepository, ClientRepository clientRepository, ReplyMessagesService replyMessagesService, NewsAggregatorMenu newsAggregatorMenu, LocaleMessageService localeMessageService, @Lazy BotConnect botConnect) {
        this.mainMenuService = mainMenuService;
        this.newsRepository = newsRepository;
        this.clientRepository = clientRepository;
        this.replyMessagesService = replyMessagesService;
        this.newsAggregatorMenu = newsAggregatorMenu;
        this.localeMessageService = localeMessageService;
        this.botConnect = botConnect;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.LATEST_NEWS;
    }

    @Override
    public void handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Client client = clientRepository.findByChatId(chatId);
        BotState botState = client.getBotState();
        if (botState.equals(BotState.LATEST_NEWS)) {
            if (update.getMessage().getText().equals("Назад")) {
                mainMenuService.getMainMenuMessage(chatId, "reply.main_menu");
                botState = BotState.MAIN_MENU;
                client.setBotState(botState);
                clientRepository.save(client);
                return;
            }
//            newsAggregatorMenu.getMainMenuMessage(chatId, "reply.aggregator_menu");
        } else if (botState.equals(BotState.DAY1)) {
            latestNewHandler(chatId, 86400000);
            newsAggregatorMenu.getMainMenuMessage(chatId, "reply.aggregator_menu");
        } else if (botState.equals(BotState.DAY3)) {
            latestNewHandler(chatId, 3 * 86400000);
        } else if (botState.equals(BotState.DAY7)) {
            latestNewHandler(chatId, 7 * 86400000);
        }


        botState = BotState.LATEST_NEWS;
        client.setBotState(botState);
        clientRepository.save(client);
    }

    private void latestNewHandler(long chatId, long time) {
        CheckNewsInDb checkNewsInDb = new CheckNewsInDb(clientRepository, newsRepository);
        if (checkNewsInDb.getAllNewNewsMySub(chatId, time) != null) {
            List<News> listNewNews = checkNewsInDb.getAllNewNewsMySub(chatId, time);
            if (listNewNews.size() == 0) {
                SendMessage sendMessage = replyMessagesService.getReplyMessage(chatId, "reply.NoNewNews");
                botConnect.sendMessage(sendMessage);
            } else {
                for (News news : listNewNews) {
                    String link = news.getLink();
                    botConnect.sendMessage(chatId, link);
                }
            }
        }
    }
}
