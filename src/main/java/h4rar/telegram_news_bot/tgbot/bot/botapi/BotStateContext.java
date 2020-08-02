package h4rar.telegram_news_bot.tgbot.bot.botapi;


import h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.InputMessageHandler;
import h4rar.telegram_news_bot.tgbot.bot.service.ReplyMessagesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class BotStateContext {
    private final ReplyMessagesService messagesService;
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers, ReplyMessagesService messagesService) {
        this.messagesService = messagesService;
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));

//        // Получаем набор элементов
//        Set<Map.Entry<BotState, InputMessageHandler>> set = this.messageHandlers.entrySet();
//        // Отобразим набор
//        for (Map.Entry<BotState, InputMessageHandler> me : set) {
//            System.out.print(me.getKey() + ": ");
//            System.out.println(me.getValue().getHandlerName());
//        }
    }

    public void processInputMessage(BotState currentState, Update update) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        currentMessageHandler.handle(update);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isMainMenu(currentState)) {
            return messageHandlers.get(BotState.MAIN_MENU);
        }
        if (isLatestNews(currentState)) {
            return messageHandlers.get(BotState.LATEST_NEWS);
        }
        if (isMySub(currentState)) {
            return messageHandlers.get(BotState.MY_SUB);
        }
        if (isStatistics(currentState)) {
            return messageHandlers.get(BotState.STATISTICS);
        }
        return messageHandlers.get(currentState);
    }

    private boolean isMySub(BotState currentState) {
        switch (currentState) {
            case LIST_MY_SUB:
                return true;
            default:
                return false;
        }
    }

    private boolean isMainMenu(BotState currentState) {
        switch (currentState) {
            case MAIN_MENU:
            case HELP:
                return true;
            default:
                return false;
        }
    }

    private boolean isLatestNews(BotState currentState) {
        switch (currentState) {
            case LATEST_NEWS:
            case DAY1:
            case DAY3:
            case DAY7:
                return true;
            default:
                return false;
        }
    }

    private boolean isStatistics(BotState currentState) {
        switch (currentState) {
            case STATISTICS:
            case WORDS_COUNT_IN_DESCRIPTION:
            case COUNT_NEWS_PIE_CHART:
            case DAY15:
            case MONTH1:
            case TIME_POSTING:
                return true;
            default:
                return false;
        }
    }
}