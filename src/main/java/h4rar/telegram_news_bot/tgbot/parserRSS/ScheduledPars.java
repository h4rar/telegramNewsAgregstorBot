package h4rar.telegram_news_bot.tgbot.parserRSS;

import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import h4rar.telegram_news_bot.tgbot.bot.BotConnect;
import h4rar.telegram_news_bot.tgbot.bot.service.MailingService;
import h4rar.telegram_news_bot.tgbot.bot.service.MainMenuService;
import h4rar.telegram_news_bot.tgbot.model.News;
import h4rar.telegram_news_bot.tgbot.model.WordWeight;
import h4rar.telegram_news_bot.tgbot.repository.ClientRepository;
import h4rar.telegram_news_bot.tgbot.repository.NewsRepository;
import h4rar.telegram_news_bot.tgbot.utils.TFIDFCalculator;
import opennlp.tools.tokenize.SimpleTokenizer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


import java.io.IOException;
import java.util.Map.Entry;

import java.util.*;

@Component
public class ScheduledPars {
    private final NewsRepository newsRepository;
    private final ClientRepository clientRepository;
    private final ParserRSS parserRSS;
    private final BotConnect botConnect;
    private final MailingService mailingService;
    private final MainMenuService mainMenuService;
    SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
    public static boolean DESC = false;

    public ScheduledPars(NewsRepository newsRepository, ClientRepository clientRepository, ParserRSS parserRSS, @Lazy BotConnect botConnect, MailingService mailingService, MainMenuService mainMenuService) {
        this.newsRepository = newsRepository;
        this.clientRepository = clientRepository;
        this.parserRSS = parserRSS;
        this.botConnect = botConnect;
        this.mailingService = mailingService;
        this.mainMenuService = mainMenuService;
    }

    @Scheduled(fixedDelayString = "${fixedRate.in.milliseconds}")
    public void refresh() {
//        String title1 = "title";
//        String source1 = "source";
//        String description1 = "description";
//        int wordsCountInDescription1 = 10;
//        String link1 = "link";
//        Date date1 = new Date();
//        String content1 = "content";
//
//        News news = new News(title1, source1, description1, wordsCountInDescription1, link1, date1, content1);
//        newsRepository.save(news);
//
//        String word = "5tgy5";
//        Double weight = 10.3;
//        WordWeight wordWeight = new WordWeight(word,weight);
//        WordWeight wordWeight1 = new WordWeight(word+"1",weight+1);
//        List<WordWeight> wordWeights = new ArrayList<>();
//        wordWeights.add(wordWeight);
//        wordWeights.add(wordWeight1);
//
//        news.setWordWeight(wordWeights);
//        newsRepository.save(news);
//
//        //
//        List<String> strings = Arrays.asList("source");
//        List<News> list =newsRepository.findBySourceIn(strings);
//        for (News n : list) {
//            System.out.println(n.getWordWeight().size());
//        }


        List<News> listNewNews = new ArrayList<>();
        List<SyndFeed> syndFeeds = new ArrayList<>();
        List<String> listURL = parserRSS.getListURL();

        for (String url : listURL) {
            syndFeeds.add(parserRSS.getSyndFeedForUrl(url));
        }
        for (SyndFeed feed : syndFeeds) {
            List<SyndEntryImpl> res = feed.getEntries();
            String sourceRSS = feed.getTitle();
            String titleDb;
            String title;
            if (newsRepository.findFirstBySourceContainingOrderByDateDesc(sourceRSS) != null) {
                titleDb = newsRepository.findFirstBySourceContainingOrderByDateDesc(sourceRSS).getTitle();
                for (SyndEntryImpl obj : res) {
                    title = ((!obj.getTitle().equals("")) ? obj.getTitle() : null);
                    if (titleDb.equals(title)) {
                        break;
                    }
                    String source = ((!feed.getTitle().equals("")) ? feed.getTitle() : "any");
                    String description = ((!obj.getDescription().getValue().equals("")) ? obj.getDescription().getValue() : null);
                    String link = ((!obj.getLink().equals("")) ? obj.getLink() : null);
                    String content = " ";
                    List<String> sources;
                    List<List<String>> allDescriptionList;
                    if(obj.getContents().size()>0){//TODO тут неявная логика получается
                        content=obj.getContents().get(0).toString().toLowerCase();
                        sources = Arrays.asList("Science & Technology – Harvard Gazette", "MIT Research News");
                        allDescriptionList = getContentList(sources);
                    }
                    else {
                        Document doc = getDoc(link);
                        Elements elements = null;
                        sources = Arrays.asList("Новости и события МФТИ", "НОВОСТИ САМАРСКОГО УНИВЕРСИТЕТА", "Новости МГУ");
                        allDescriptionList = getContentList(sources);
                        if(source.equals("Новости и события МФТИ")){
                            elements = doc.select("div.post-contents");
                        }
                        else if(source.equals("НОВОСТИ САМАРСКОГО УНИВЕРСИТЕТА")){
                            elements = doc.select("article.col.news-content.mb-2");
                        }
                        else if(source.equals("Новости МГУ")){
                            elements = doc.select("div.news-list-item-text");
                        }
                        content = elements.text().toLowerCase();
                    }
                    List<String> descriptionList = getThisDescriptionList(content);
                    allDescriptionList.add(descriptionList);

                    Date date = obj.getPublishedDate();
                    int wordsCountInDescription = description.split("\\s+").length;

                    Map<String, Double> stringWeight = new HashMap<>();

                    TFIDFCalculator calculator = new TFIDFCalculator();
                    for (String d :descriptionList) {
                        double tfidf = calculator.tfIdf(descriptionList, allDescriptionList, d);
                        stringWeight.put(d,tfidf);
                    }

                    Map<String, Double> sortedMapAsc = sortByComparator(stringWeight, DESC);
                    Map<String, Double> stringWeightSort = new LinkedHashMap<>();
                    List<WordWeight> wordWeights = new ArrayList<>();
                    int i =  0;
                    News news = new News(title, source, description, wordsCountInDescription, link, date, content);
                    for (Map.Entry<String, Double> entry : sortedMapAsc.entrySet()) {
                        if(i == 5)break;
                        WordWeight wordWeight = new WordWeight(news, entry.getKey(),entry.getValue());
                        wordWeights.add(wordWeight);
                        i++;
                    }

                    news.setWordWeight(wordWeights);
                    newsRepository.save(news);
                    listNewNews.add(news);
                }
            }

//                for (SyndEntryImpl obj : res) {
//                    title = ((!obj.getTitle().equals("")) ? obj.getTitle() : null);
//                    String source = ((!feed.getTitle().equals("")) ? feed.getTitle() : "any");
//                    String description = ((!obj.getDescription().getValue().equals("")) ? obj.getDescription().getValue() : null);
//                    String link = ((!obj.getLink().equals("")) ? obj.getLink() : null);
//                    String content = "null";
//                    if(obj.getContents().size()>0){
//                        content=obj.getContents().get(0).toString().toLowerCase();
//                    }
//                    else {
//                        Document doc = getDoc(link);
//                        Elements elements = null;
//                        if(source.equals("Новости и события МФТИ")){
//                            elements = doc.select("div.post-contents");
//                        }
//                        else if(source.equals("НОВОСТИ САМАРСКОГО УНИВЕРСИТЕТА")){
//                            elements = doc.select("article.col.news-content.mb-2");
//                        }
//                        else if(source.equals("Новости МГУ")){
//                            elements = doc.select("div.news-list-item-text");
//                        }
//                        content = elements.text().toLowerCase();
//                    }
//
//                    Date date = obj.getPublishedDate();
//                    int wordsCountInDescription = description.split("\\s+").length;
//
//                    Map<String, Double> stringWeight = new HashMap<>();
//                    News message = new News(title, source, description, wordsCountInDescription, link, date,stringWeight, content);

//                    newsRepository.save(message);
//                    listNewNews.add(message);
//                }

        }

//        //если есть отновления - сделать рассылку
//        if (listNewNews.size() > 0) {
//            mailingService.mailingAllClient(listNewNews);
//        }

        System.out.println("Конец");
    }


    public  List<List<String>> getContentList(List<String> sources){
        List<List<String>> allDocuments = new ArrayList<>();
        List<News> news =newsRepository.findBySourceIn(sources);
        for (News n: news) {
            List<String> doc = new ArrayList<String>();
            String[]tokens = tokenizer.tokenize( n.getContent());
            for (String s : tokens) {
                doc.add(s);
            }
            allDocuments.add(doc);
        }
        return allDocuments;
    }

    public  List<String> getThisDescriptionList(String description){
        List<String> thisDocuments = new ArrayList<>();
        String[]tokens = tokenizer.tokenize(description);
        List<String> stopString = Arrays.asList("the","article", ",", ">", "<", "p", ".", "of","and","it","/","in","that","w","a","-","i","" +
                "","from","to","for","for","br","\"","=","07","b","div","class","_",":","px","you","’","“","”","is","es","c","--",")","d","wintersession","" +
                "", "в","и","по","на","с","и","«","»","а","то",";","какая","или","что","какая","г","jpg","–");
        for (String s : tokens) {
            if (!stopString.contains(s)){
                thisDocuments.add(s);
            }
        }
        return thisDocuments;
    }

    private static Map<String, Double> sortByComparator(Map<String, Double> unsortMap, final boolean order)
    {

        List<Entry<String, Double>> list = new LinkedList<Entry<String, Double>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Double>>()
        {
            public int compare(Entry<String, Double> o1,
                               Entry<String, Double> o2)
            {
                if (order)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    public static void printMap(Map<String, Double> map)
    {
        for (Entry<String, Double> entry : map.entrySet())
        {
            System.out.println("Key : " + entry.getKey() + " Value : "+ entry.getValue());
        }
    }

    private static Document getDoc(String url){
        Document doc = null;
        try {
            doc = Jsoup.connect(url)
                    .userAgent("Chrome/83.0.4103.61 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
//            System.out.println(doc);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return doc;
    }
}
