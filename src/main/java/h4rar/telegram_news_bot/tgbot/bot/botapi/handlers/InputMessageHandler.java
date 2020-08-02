package h4rar.telegram_news_bot.tgbot.bot.botapi.handlers;


import h4rar.telegram_news_bot.tgbot.bot.botapi.BotState;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface InputMessageHandler {
    void handle(Update update);
    BotState getHandlerName();
}
