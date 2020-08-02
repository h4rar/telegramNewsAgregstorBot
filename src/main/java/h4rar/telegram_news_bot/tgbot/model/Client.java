package h4rar.telegram_news_bot.tgbot.model;



import h4rar.telegram_news_bot.tgbot.bot.botapi.BotState;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
public class Client {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private Long chatId;
    private String locale;
    private BotState botState;

    @ElementCollection(targetClass = SourceEnum.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_sourse_enum", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private List<SourceEnum> sourceEnums;

    public Client() {
    }

    public Client(Long chatId, List<SourceEnum> sourceEnums) {
        this.chatId = chatId;
        this.sourceEnums = sourceEnums;
    }
}
