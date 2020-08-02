package h4rar.telegram_news_bot.tgbot.bot;


import h4rar.telegram_news_bot.tgbot.bot.botapi.TelegramFacade;
import org.springframework.util.ResourceUtils;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.FileNotFoundException;

public class BotConnect extends TelegramWebhookBot {
    private String webHookPath;
    private String botUserName;
    private String botToken;

    private final TelegramFacade telegramFacade;

    public BotConnect(DefaultBotOptions botOptions, TelegramFacade telegramFacade) {
        super(botOptions);
        this.telegramFacade = telegramFacade;
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return webHookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if(update.hasCallbackQuery() || update.hasMessage()){
            telegramFacade.handleUpdate(update);
        }
        return null;
    }

    public void sendPhoto(long chatId, String imageCaption, String imagePath) throws FileNotFoundException {
        File image = ResourceUtils.getFile(imagePath);
        SendPhoto sendPhoto = new SendPhoto().setPhoto(image);
        sendPhoto.setChatId(chatId);
        sendPhoto.setCaption(imageCaption);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(long chatId, String text){
        SendMessage sendMessage = new SendMessage(chatId, text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(SendMessage sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(BotApiMethod<?> sendMessage){
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void setWebHookPath(String webHookPath) {
        this.webHookPath = webHookPath;
    }

    public void setBotUserName(String botUserName) {
        this.botUserName = botUserName;
    }

    public void setBotToken(String botToken) {
        this.botToken = botToken;
    }
}
