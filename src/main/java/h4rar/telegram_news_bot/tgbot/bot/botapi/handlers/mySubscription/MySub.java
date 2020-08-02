package h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.mySubscription;


import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import h4rar.telegram_news_bot.tgbot.bot.botapi.BotState;
import h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.InputMessageHandler;
import h4rar.telegram_news_bot.tgbot.bot.service.InlineKeyboardMySub;
import h4rar.telegram_news_bot.tgbot.bot.service.MainMenuService;
import h4rar.telegram_news_bot.tgbot.bot.service.ReplyMessagesService;
import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;

import java.util.List;

@Component
public class MySub implements InputMessageHandler {
    private final MainMenuService mainMenuService;
    private final ReplyMessagesService messagesService;
    private final ClientRepository clientRepository;
    private final InlineKeyboardMySub inlineKeyboardMySub;
    private final BotConnect botConnect;

    public MySub(MainMenuService mainMenuService, ReplyMessagesService messagesService, ClientRepository clientRepository, InlineKeyboardMySub inlineKeyboardMySub, @Lazy BotConnect botConnect) {
        this.mainMenuService = mainMenuService;
        this.messagesService = messagesService;
        this.clientRepository = clientRepository;
        this.inlineKeyboardMySub = inlineKeyboardMySub;
        this.botConnect = botConnect;
    }


    @Override
    public void handle(Update update) {
        processUsersInput(update);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.MY_SUB;
    }

    private void processUsersInput(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            inlineKeyboardMySub.callbackQueryUpdateKeyboardMySub(callbackQuery, clientRepository);
        } else if (update.hasMessage()) {
            long chatId = update.getMessage().getChatId();
            Client client = clientRepository.findByChatId(chatId);
            BotState botState = client.getBotState();

            if (botState.equals(BotState.LIST_MY_SUB)) {
                SendMessage replyToUser = messagesService.getReplyMessage(chatId, "reply.list_my_sub");
//                botConnect.sendMessage(replyToUser);
                //удалить основную клаву
                SendMessage deleteKeyboard = new SendMessage(chatId, "Ваши подписки:");
                ReplyKeyboardRemove replyKeyboardRemove = new ReplyKeyboardRemove();
                deleteKeyboard.setReplyMarkup(replyKeyboardRemove);
                botConnect.sendMessage(deleteKeyboard);

                replyToUser.setReplyMarkup(inlineKeyboardMySub.getInlineKeyboardMySub(chatId, clientRepository));
                botConnect.sendMessage(replyToUser);
            }
            client.setBotState(BotState.LIST_MY_SUB);
            clientRepository.save(client);
        }
    }
}
