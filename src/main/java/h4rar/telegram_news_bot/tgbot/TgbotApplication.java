package h4rar.telegram_news_bot.tgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
@EnableScheduling
public class TgbotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(TgbotApplication.class, args);
    }

}
