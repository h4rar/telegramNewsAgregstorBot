package h4rar.telegram_news_bot.tgbot.bot.service;

import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.model.SourceEnum;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import h4rar.telegram_news_bot.tgbot.utils.Emojis;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
public class InlineKeyboardMySub {
    private final MainMenuService mainMenuService;
    private final BotConnect botConnect;

    public InlineKeyboardMySub(MainMenuService mainMenuService, @Lazy BotConnect botConnect) {
        this.mainMenuService = mainMenuService;
        this.botConnect = botConnect;
    }

    public InlineKeyboardMarkup getInlineKeyboardMySub(long chatId, ClientRepository clientRepository) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        Client client = clientRepository.findByChatId(chatId);
        List<SourceEnum> sourceEnumsClient = client.getSourceEnums();
        EnumSet<SourceEnum> enums = EnumSet.allOf(SourceEnum.class);
        Emojis indicator;
        for (SourceEnum se : enums) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            if (sourceEnumsClient.contains(se))
                indicator = Emojis.YES;
            else
                indicator = Emojis.NO;
            inlineKeyboardButton.setText(se + " " + indicator);
            inlineKeyboardButton.setCallbackData(String.valueOf(se));
            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);
            rowList.add(keyboardButtonsRow);
        }

        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        indicator = Emojis.LIKE;
        inlineKeyboardButton1.setText("Готово " + indicator);
        inlineKeyboardButton1.setCallbackData("Готово");
        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        keyboardButtonsRow1.add(inlineKeyboardButton1);
        rowList.add(keyboardButtonsRow1);

        inlineKeyboardMarkup.setKeyboard(rowList);
        return inlineKeyboardMarkup;
    }

    public void callbackQueryUpdateKeyboardMySub(CallbackQuery buttonQuery, ClientRepository clientRepository) {
        final long chatId = buttonQuery.getFrom().getId();
        Integer messageId = buttonQuery.getMessage().getMessageId();
        if (buttonQuery.getData().equals("Готово")) {
            DeleteMessage deleteMessage = new DeleteMessage(chatId, messageId);
            botConnect.sendMessage(deleteMessage);
            mainMenuService.getMainMenuMessage(chatId, "reply.main_menu");
        } else {
            Client client = clientRepository.findByChatId(chatId);
            List<SourceEnum> sourceEnumsClient = client.getSourceEnums();
            SourceEnum callbackData = SourceEnum.valueOf(buttonQuery.getData());
            if (sourceEnumsClient.contains(callbackData)) {
                client.getSourceEnums().remove(callbackData);
            } else {
                client.getSourceEnums().add(callbackData);
            }
            clientRepository.save(client);
            EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setChatId(chatId);
            editMessageReplyMarkup.setMessageId(messageId);
            editMessageReplyMarkup.setReplyMarkup(getInlineKeyboardMySub(chatId, clientRepository));
            botConnect.sendMessage(editMessageReplyMarkup);
        }
    }
}
