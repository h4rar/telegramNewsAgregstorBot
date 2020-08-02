package h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.mainMenu;



import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import h4rar.telegram_news_bot.tgbot.bot.botapi.BotState;
import h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.InputMessageHandler;
import h4rar.telegram_news_bot.tgbot.bot.service.MainMenuService;
import h4rar.telegram_news_bot.tgbot.bot.service.NewsAggregatorMenu;
import h4rar.telegram_news_bot.tgbot.bot.service.ReplyMessagesService;
import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import h4rar.telegram_news_bot.tgbot.repository.NewsRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;

@Component
public class MainMenu implements InputMessageHandler {
    private final NewsRepository newsRepository;
    private final ClientRepository clientRepository;
    private final ReplyMessagesService replyMessagesService;
    private final NewsAggregatorMenu newsAggregatorMenu;
    private final MainMenuService mainMenuService;
    private final BotConnect botConnect;


    public MainMenu(NewsRepository newsRepository, ClientRepository clientRepository, ReplyMessagesService replyMessagesService, NewsAggregatorMenu newsAggregatorMenu, MainMenuService mainMenuService, @Lazy BotConnect botConnect) {
        this.newsRepository = newsRepository;
        this.clientRepository = clientRepository;
        this.replyMessagesService = replyMessagesService;
        this.newsAggregatorMenu = newsAggregatorMenu;
        this.mainMenuService = mainMenuService;
        this.botConnect = botConnect;
    }

    @Override
    public void handle(Update update) {
        long chatId = update.getMessage().getChatId();
        Client client = clientRepository.findByChatId(chatId);
        BotState botState = client.getBotState();

        if (botState.equals(BotState.MAIN_MENU)) {
            mainMenuService.getMainMenuMessage(chatId, "reply.main_menu");
        }
        else if (botState.equals(BotState.HELP)) {
            mainMenuService.getMainMenuMessage(chatId, "reply.help");
        }
        else {
            botConnect.sendMessage(chatId,"reply.error");
        }
        client.setBotState(BotState.MAIN_MENU);
        clientRepository.save(client);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.MAIN_MENU;
    }

}
