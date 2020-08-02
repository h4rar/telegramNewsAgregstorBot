package h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.startHandler;


import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import h4rar.telegram_news_bot.tgbot.bot.botapi.BotState;
import h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.InputMessageHandler;
import h4rar.telegram_news_bot.tgbot.bot.service.InlineKeyboardMySub;
import h4rar.telegram_news_bot.tgbot.bot.service.MainMenuService;
import h4rar.telegram_news_bot.tgbot.bot.service.ReplyMessagesService;
import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.model.SourceEnum;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import h4rar.telegram_news_bot.tgbot.repository.NewsRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.Arrays;

@Component
public class StartHandler implements InputMessageHandler {
    private final MainMenuService mainMenuService;
    private final ClientRepository clientRepository;
    private final ReplyMessagesService messagesService;
    private final InlineKeyboardMySub inlineKeyboardMySub;
    private final BotConnect botConnect;
    private final NewsRepository newsRepository;

    public StartHandler(MainMenuService mainMenuService, ClientRepository clientRepository, ReplyMessagesService messagesService, InlineKeyboardMySub inlineKeyboardMySub, @Lazy BotConnect botConnect, NewsRepository newsRepository) {
        this.mainMenuService = mainMenuService;
        this.clientRepository = clientRepository;
        this.messagesService = messagesService;
        this.inlineKeyboardMySub = inlineKeyboardMySub;
        this.botConnect = botConnect;
        this.newsRepository = newsRepository;
    }


    @Override
    public void handle(Update update) {
        processUsersInput(update);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }

    private void processUsersInput(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            inlineKeyboardMySub.callbackQueryUpdateKeyboardMySub(callbackQuery, clientRepository);
            return;
        }
        long chatId = update.getMessage().getChatId();
        Client client = clientRepository.findByChatId(chatId);
        if (client == null) {
            ArrayList<SourceEnum> sourceEnums = new ArrayList<>(Arrays.asList(SourceEnum.values()));
            client = new Client(chatId, sourceEnums);
            client.setChatId(chatId);
        }
        client.setBotState(BotState.START);
        clientRepository.save(client);

        SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.start");
        replyToUser.setReplyMarkup(inlineKeyboardMySub.getInlineKeyboardMySub(chatId, clientRepository));
        botConnect.sendMessage(replyToUser);
    }
}
