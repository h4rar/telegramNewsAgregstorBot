package h4rar.telegram_news_bot.tgbot.bot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.util.List;

public interface MenuService {
   void getMainMenuMessage(final long chatId, final String textMessage);
}
