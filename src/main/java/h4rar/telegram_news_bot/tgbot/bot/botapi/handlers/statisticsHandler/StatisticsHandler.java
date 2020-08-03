package h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.statisticsHandler;


import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import h4rar.telegram_news_bot.tgbot.bot.botapi.BotState;
import h4rar.telegram_news_bot.tgbot.bot.botapi.handlers.InputMessageHandler;
import h4rar.telegram_news_bot.tgbot.bot.service.*;
import h4rar.telegram_news_bot.tgbot.bot.statistics.*;
import h4rar.telegram_news_bot.tgbot.model.Client;
import h4rar.telegram_news_bot.tgbot.model.News;
import h4rar.telegram_news_bot.tgbot.model.SourceEnum;
import h4rar.telegram_news_bot.tgbot.model.WordWeight;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import h4rar.telegram_news_bot.tgbot.repository.NewsRepository;
import h4rar.telegram_news_bot.tgbot.repository.WordWeightRepository;
import h4rar.telegram_news_bot.tgbot.utils.FileManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.FileNotFoundException;
import java.util.*;

@Component
public class StatisticsHandler implements InputMessageHandler {
    @Value("${picture.path}")
    private String pathDirectory;
    private final MainMenuService mainMenuService;
    private final NewsRepository newsRepository;
    private final WordWeightRepository wordWeightRepository;
    private final ClientRepository clientRepository;
    private final ReplyMessagesService replyMessagesService;
    private final StatisticsMenu statisticsMenu;
    private final LocaleMessageService localeMessageService;
    private final BotConnect botConnect;
    private final WordsCountInDescriptionMenu wordsCountInDescriptionMenu;


    public StatisticsHandler(MainMenuService mainMenuService, NewsRepository newsRepository, WordWeightRepository wordWeightRepository, ClientRepository clientRepository, ReplyMessagesService replyMessagesService, StatisticsMenu statisticsMenu, LocaleMessageService localeMessageService, @Lazy BotConnect botConnect, WordsCountInDescriptionMenu wordsCountInDescriptionMenu) {
        this.mainMenuService = mainMenuService;
        this.newsRepository = newsRepository;
        this.wordWeightRepository = wordWeightRepository;
        this.clientRepository = clientRepository;
        this.replyMessagesService = replyMessagesService;
        this.statisticsMenu = statisticsMenu;
        this.localeMessageService = localeMessageService;
        this.botConnect = botConnect;
        this.wordsCountInDescriptionMenu = wordsCountInDescriptionMenu;
    }

    @Override
    public BotState getHandlerName() {
        return BotState.STATISTICS;
    }

    @Override
    public void handle(Update update) {
        long chatId = update.getMessage().getChatId();
        int width = 800;
        int height = 600;
        Date dateNow = new Date();
        Date sevenDayAgo = new Date(dateNow.getTime() - 7 * 86400000);
        Date d20DayAgo = new Date(dateNow.getTime() - 20 * 86400000);
        Date halfMonthAgo = new Date(dateNow.getTime() - 15 * 86400000);
        Date oneMonthAgo = new Date(halfMonthAgo.getTime() - 15 * 86400000);
        Date twoAndAHalfMonthAgo = new Date(oneMonthAgo.getTime() - 15 * 86400000);
        Date twoMonthAgo = new Date(twoAndAHalfMonthAgo.getTime() - 15 * 86400000);
        //создаю папку если она не создана
        FileManager.createFolder(pathDirectory);
        String path = pathDirectory + chatId + ".png";

        Client client = clientRepository.findByChatId(chatId);
        BotState botState = client.getBotState();
        if (botState.equals(BotState.STATISTICS)) {
            if (update.getMessage().getText().equals("Назад")) {
                mainMenuService.getMainMenuMessage(chatId, "reply.main_menu");
                return;
            }
            if (update.getMessage().getText().equals("Топ тегов")) {
                List<String> sourcesRu = Arrays.asList("Новости и события МФТИ", "НОВОСТИ САМАРСКОГО УНИВЕРСИТЕТА", "Новости МГУ");
                List<String> sourcesEn = Arrays.asList("MIT News", "Harvard Gazette");

                String messageRu = getMessageTop5Tags(halfMonthAgo, sourcesRu);
                String messageEn = getMessageTop5Tags(halfMonthAgo, sourcesEn);
                String resultMessage = "*Топ 5 тегов за месяц*" + "\n\nАнглоязычные:" + messageEn + "\n\nРусскоязычные:" + messageRu;

                SendMessage sendMessage = new SendMessage();
                sendMessage.setChatId(chatId);
                sendMessage.disableWebPagePreview();
                sendMessage.enableMarkdownV2(true);
                sendMessage.setText(resultMessage);
                botConnect.sendMessage(sendMessage);
                return;
            }
            if (update.getMessage().getText().equals("Количество новостей")) {
                String name = "Количество новостей";
                List<InfoAboutCountNews> infoAboutCountNewsForThisClient = getListInfoAboutCountNewsForThisClient(sevenDayAgo, chatId);
                PieChartPercent pieChartPercent = new PieChartPercent(name, width, height, infoAboutCountNewsForThisClient);
                pieChartPercent.createChart(path);
                try {
                    botConnect.sendPhoto(chatId, name + " за неделю", path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return;
            }
            statisticsMenu.getMainMenuMessage(chatId, "reply.statistics");
        } else if (botState.equals(BotState.WORDS_COUNT_IN_DESCRIPTION)) {
            if (update.getMessage().getText().equals("Назад")) {
                statisticsMenu.getMainMenuMessage(chatId, "reply.statisticsBack");
                botState = BotState.STATISTICS;
                client.setBotState(botState);
                clientRepository.save(client);
                return;
            }

            String nameGraph = "Количество слов в описании новости";
            CreateChart linearChart = new CreateChart(width, height, path, nameGraph, "Дата", "Количество слов");
            List<DataForLineGraph> listDataGraph = null;
            String countDay = "";
            if (update.getMessage().getText().equals("За 1 месяц")) {
                listDataGraph = getListDataGraph(oneMonthAgo, chatId);
                countDay = "1 месяц";
            }
            else if (update.getMessage().getText().equals("За 2 месяца")) {
                listDataGraph = getListDataGraph(twoMonthAgo, chatId);
                countDay = "2 месяца";
            }
            else {
                listDataGraph = getListDataGraph(halfMonthAgo, chatId);

                //todo
                for (DataForLineGraph dfg : listDataGraph){
                    System.out.println(dfg.getName());
                    List<?> dfgList = dfg.getxData();
                    for (Object obj : dfgList){
                        System.out.println(obj);
                    }
                }

                //строю график
                linearChart.createLinearChart(listDataGraph);
                //посылаю рисунок
                try {
                    botConnect.sendPhoto(chatId, "Среднее за день количество слов в описании новости", path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                wordsCountInDescriptionMenu.getMainMenuMessage(chatId, "reply.wordsCountInDescriptionMenu");
                return;
            }
            //строю график
            linearChart.createLinearChart(listDataGraph);
            //посылаю рисунок
            try {
                botConnect.sendPhoto(chatId, "Среднее за день количество слов в описании новости за "+countDay, path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else if (botState.equals(BotState.TIME_POSTING)) {
            if (update.getMessage().getText().equals("Назад")) {
                statisticsMenu.getMainMenuMessage(chatId, "reply.statisticsBack");
                botState = BotState.STATISTICS;
                client.setBotState(botState);
                clientRepository.save(client);
                return;
            }
            String nameGraph = "Распределение новостей по времени";
            CreateChart linearChart = new CreateChart(width, height, path, nameGraph, "Дата", "Часы");
            List<DataForLineGraph> listDataGraph = null;
            String countDay = "";
            if (update.getMessage().getText().equals("За 1 месяц")) {
                listDataGraph = getListDataScatterChart(oneMonthAgo, chatId);
                countDay = "1 месяц";
            }
            else if (update.getMessage().getText().equals("За 2 месяца")) {
                listDataGraph = getListDataScatterChart(twoMonthAgo, chatId);
                countDay = "2 месяца";
            }
            else {
                listDataGraph = getListDataScatterChart(halfMonthAgo, chatId);
                //строю график
                linearChart.boxChart(listDataGraph);
                //посылаю рисунок
                try {
                    botConnect.sendPhoto(chatId, "Распределение новостей по времени", path);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                wordsCountInDescriptionMenu.getMainMenuMessage(chatId, "reply.wordsCountInDescriptionMenu");
                return;
            }
            //строю график
            linearChart.boxChart(listDataGraph);
            //посылаю рисунок
            try {
                botConnect.sendPhoto(chatId, "Распределение новостей по времени за "+countDay, path);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


    public List<DataForLineGraph> getListDataGraph(Date timeInterval, long chatId) {
        List<InfoAboutCountWords> infoAboutCountWords = newsRepository.getInfoAboutCountWords(timeInterval);
        List<String> clientSourceNameList = ClientSourceService.getClientSourceName(clientRepository, chatId);
        List<DataForLineGraph> listDataGraph = new ArrayList<>();
        for (String clientSourceName : clientSourceNameList) {
            List<Double> countWorld = new ArrayList<>();
            List<Date> day = new ArrayList<>();
//            List<Double> dayD = new ArrayList<>();//todo
            for (InfoAboutCountWords i : infoAboutCountWords) {
                String source = i.getSource();
                if (clientSourceName.equals(source)) {
                    Date date = i.getTo_date();
//                    SimpleDateFormat format1 = new SimpleDateFormat("dd.MM");
//                    String df = format1.format(date);
                    double avg = i.getAvg();
                    countWorld.add(avg);
                    day.add(date);
//                    dayD.add(Double.valueOf(df));
                }
            }
            DataForLineGraph dataForLineGraph = new DataForLineGraph(clientSourceName, day, countWorld);
            listDataGraph.add(dataForLineGraph);
        }
        return listDataGraph;
    }

    public List<DataForLineGraph> getListDataScatterChart(Date timeInterval, long chatId) {
        List<InfoAboutTimePosting> infoAboutTimePosting = newsRepository.getInfoAboutTimePosting(timeInterval);
        List<String> clientSourceNameList = ClientSourceService.getClientSourceName(clientRepository, chatId);
        List<DataForLineGraph> listDataGraph = new ArrayList<>();
        for (String clientSourceName : clientSourceNameList) {
            List<Integer> hoursList = new ArrayList<>();
            List<Date> day = new ArrayList<>();
            for (InfoAboutTimePosting i : infoAboutTimePosting) {
                String source = i.getSource();
                if (clientSourceName.equals(source)) {
                    Date date = i.getDate();
                    int hours = date.getHours();
                    hoursList.add(hours);
                    day.add(date);
                }
            }
            DataForLineGraph dataForLineGraph = new DataForLineGraph(clientSourceName, day, hoursList);
            listDataGraph.add(dataForLineGraph);
        }
        return listDataGraph;
    }

    public List<InfoAboutCountNews> getListInfoAboutCountNewsForThisClient(Date date, long chatId) {
        List<String> clientSourceName = ClientSourceService.getClientSourceName(clientRepository, chatId);
        List<InfoAboutCountNews> infoAboutCountNews = newsRepository.getInfoAboutCountNewsForDate(date);
        List<InfoAboutCountNews> infoAboutCountNewsForThisClient = new ArrayList<>();
        for (InfoAboutCountNews i : infoAboutCountNews) {
            if (clientSourceName.contains(i.getSource())) {
                infoAboutCountNewsForThisClient.add(i);
            }
        }
        return infoAboutCountNewsForThisClient;
    }

    public String getMessageTop5Tags(Date date, List<String> sources){
        List<News> news = newsRepository.findByDateGreaterThanAndSourceInOrderByDate(date, sources);
        List<WordWeight> wordWeights = new ArrayList<>();
        for(News n : news){
            wordWeights.addAll(n.getWordWeight());
        }
        Collections.sort(wordWeights);
        List<String> wordWeightsTop5 = new ArrayList<>();
        for (int i = 0; i< 5; i++){
            WordWeight wordWeight = wordWeights.get(i);
            wordWeightsTop5.add(wordWeight.getWord());
        }

        String message = "";
        List<WordWeight> wordWeights1 = wordWeightRepository.findByWordIn(wordWeightsTop5);
        Collections.sort(wordWeights1, WordWeight.FruitNameComparator);
        String wordWeightLast = "";
        int number = 1;
        for (WordWeight wordWeight : wordWeights1){
            String source = wordWeight.getNews().getSource();
            SourceEnum sourceEnum = SourceEnum.getSourceEnum(source);
            String link = "["+sourceEnum+"](" +wordWeight.getNews().getLink()+") ";
            if(wordWeightLast.equals(wordWeight.getWord())){
                message += link;
            }
            else {
                String tag = "_" +wordWeight.getWord() + "_";
                message += "\n" + number + "\\. "+tag + " " + link;
                number++;
            }
            wordWeightLast = wordWeight.getWord();
        }
        return message;
    }
}
