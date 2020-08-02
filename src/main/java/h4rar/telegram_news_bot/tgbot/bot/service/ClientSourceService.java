package h4rar.telegram_news_bot.tgbot.bot.service;



import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.model.SourceEnum;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;

import java.util.ArrayList;
import java.util.List;

public class ClientSourceService {
    public static List<SourceEnum> getClientSourceEnum(ClientRepository clientRepository, long chatId) {
        Client client = clientRepository.findByChatId(chatId);
        List<SourceEnum> sourceEnumsClient = client.getSourceEnums();
        return sourceEnumsClient;
    }

    public static List<String> getClientSourceName(ClientRepository clientRepository, long chatId) {
        List<SourceEnum> sourceEnumsClient = getClientSourceEnum(clientRepository, chatId);
        List<String> clientSource = new ArrayList<>();
        for (SourceEnum se : sourceEnumsClient) {
            clientSource.add(se.getName());
        }
        return clientSource;
    }
}
