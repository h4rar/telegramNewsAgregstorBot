package h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.initLocale;//package h4rar.example_bot.demo_bot.botapi.handlers.initLocale;
//
//import h4rar.example_bot.demo_bot.botapi.BotState;
//import h4rar.example_bot.demo_bot.botapi.InputMessageHandler;
//import h4rar.example_bot.demo_bot.model.Client;
//import h4rar.example_bot.demo_bot.repository.UserRepository;
//import h4rar.example_bot.demo_bot.service.ReplyMessagesService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
//import org.telegram.telegrambots.meta.api.objects.Message;
//
//@Component
//public class InitLocale implements InputMessageHandler {
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ReplyMessagesService messagesService;
//
//    @Value("${default.question_locale}")
//    private String questionInitLocale;
//
//    @Override
//    public SendMessage handle(Message message) {
//        return processUsersInput(message);
//    }
//
//    @Override
//    public BotState getHandlerName() {
//        return BotState.INIT_LOCALE;
//    }
//
//    private SendMessage processUsersInput(Message inputMsg) {
//        long chatId = inputMsg.getChatId();
//        String usersAnswer = inputMsg.getText();
//
//        Client client = userRepository.findByChatId(chatId);
//        client.setLocale(usersAnswer);
//        client.setBotState(BotState.MAIN_MENU);
//        userRepository.save(client);
//
//
//        SendMessage replyToUser = messagesService.getReplyMessage(chatId,"reply.finishInitLocale");
//        return replyToUser;
//    }
//}
