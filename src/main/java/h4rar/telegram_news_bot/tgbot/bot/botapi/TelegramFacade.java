package h4rar.telegram_news_bot.tgbot.bot.botapi;

import h4rar.telegram_news_bot.tgbot.bot.service.ReplyMessagesService;
import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
public class TelegramFacade {
    private BotStateContext botStateContext;
    private final ClientRepository clientRepository;
    private final ReplyMessagesService messagesService;

    public TelegramFacade(BotStateContext botStateContext, ClientRepository clientRepository,
                          ReplyMessagesService messagesService) {
        this.botStateContext = botStateContext;
        this.clientRepository = clientRepository;
        this.messagesService = messagesService;
    }

    public void handleUpdate(Update update) {
        handleInputMessage(update);
    }

    private void handleInputMessage(Update update) {
        String inputMsg = "null";
        long userId = 0;
        if (update.hasCallbackQuery()) {
            userId = update.getCallbackQuery().getFrom().getId();
            inputMsg = "CallbackQuery";
        } else if (update.hasMessage()) {
            if (update.getMessage().getText() != null) {
                inputMsg = update.getMessage().getText();
            }
            userId = update.getMessage().getFrom().getId();
        }

        BotState botState;
        Client client = clientRepository.findByChatId(userId);
        switch (inputMsg) {
            case "/start":
                botState = BotState.START;
                break;
            case "Новости":
                botState = BotState.DAY1;
                break;
            case "За 3 дня":
                botState = BotState.DAY3;
                break;
            case "За 7 дней":
                botState = BotState.DAY7;
                break;
            case "Мои подписки":
                botState = BotState.LIST_MY_SUB;
                break;
            case "Статистика":
                botState = BotState.STATISTICS;
                break;
            case "Количество слов в описании":
                botState = BotState.WORDS_COUNT_IN_DESCRIPTION;
                break;
            case "Помощь":
                botState = BotState.HELP;
                break;
            case "Часы постинга":
                botState = BotState.TIME_POSTING;
                break;
            case "В главное меню":
                botState = BotState.MAIN_MENU;
                break;
//            case "Изменить язык":
//                botState = BotState.INIT_LOCALE;
//                break;
            default:
                botState = client.getBotState();
                break;
        }

        if (client != null) {
            client.setBotState(botState);
            clientRepository.save(client);
        }
        botStateContext.processInputMessage(botState, update);
    }
}
