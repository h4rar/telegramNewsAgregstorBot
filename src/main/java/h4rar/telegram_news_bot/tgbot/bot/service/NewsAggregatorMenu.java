package h4rar.telegram_news_bot.tgbot.bot.service;

import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

@Component
public class NewsAggregatorMenu implements MenuService {
    private LocaleMessageService localeMessageService;
    private BotConnect botConnect;

    public NewsAggregatorMenu(LocaleMessageService messageService, @Lazy BotConnect botConnect) {
        this.localeMessageService = messageService;
        this.botConnect = botConnect;
    }

    @Override
    public void getMainMenuMessage(long chatId, String textMessage) {
        final ReplyKeyboardMarkup replyKeyboardMarkup = getMenuKeyboard();
        final SendMessage mainMenuMessage =
                createMessageWithKeyboard(chatId, localeMessageService.getMessage(textMessage), replyKeyboardMarkup);
        botConnect.sendMessage(mainMenuMessage);
    }

    private ReplyKeyboardMarkup getMenuKeyboard() {
        final ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(false);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();

        row1.add(new KeyboardButton(localeMessageService.getMessage("reply.3day")));
        row1.add(new KeyboardButton(localeMessageService.getMessage("reply.7day")));
        row2.add(new KeyboardButton(localeMessageService.getMessage("reply.back")));
        keyboard.add(row1);
        keyboard.add(row2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    private SendMessage createMessageWithKeyboard(final long chatId,
                                                  String textMessage,
                                                  final ReplyKeyboardMarkup replyKeyboardMarkup) {
        final SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(textMessage);
        if (replyKeyboardMarkup != null) {
            sendMessage.setReplyMarkup(replyKeyboardMarkup);
        }
        return sendMessage;
    }
}

