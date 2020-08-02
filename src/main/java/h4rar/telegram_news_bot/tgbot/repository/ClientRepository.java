package h4rar.telegram_news_bot.tgbot.repository;



import h4rar.telegram_news_bot.tgbot.model.Client;
import org.springframework.data.repository.CrudRepository;

public interface ClientRepository extends CrudRepository<Client, Integer> {
    Client findByChatId(long chatId);
}
