package h4rar.telegram_news_bot.tgbot.bot.service;

import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.model.News;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

import java.util.List;

@Service
public class MailingService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private MainMenuService mainMenuService;
    @Autowired
    @Lazy
    private BotConnect botConnect;

    public void mailingAllClient(List<News> list){
        ReplyKeyboardMarkup replyKeyboardMarkup = mainMenuService.getMainMenuKeyboard();
        Iterable<Client> allClient = clientRepository.findAll();
        for (Client client : allClient) {
            long chatId = client.getChatId();
            List<String> clientSourceNameList = ClientSourceService.getClientSourceName(clientRepository, chatId);
            for (News news : list) {
                if(clientSourceNameList.contains(news.getSource())){
                    SendMessage sendMessage = new SendMessage(chatId,news.getLink());
                    sendMessage.setReplyMarkup(replyKeyboardMarkup);
                    botConnect.sendMessage(sendMessage);
                }
            }
        }
    }
}
