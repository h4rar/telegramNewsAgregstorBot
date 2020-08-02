package h4rar.telegram_news_bot.tgbot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Comparator;

@Entity
@Setter
@Getter
public class WordWeight implements Comparable<WordWeight>{
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    private String word;
    private Double weight;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "news_id")
    private News news;

    public WordWeight() {}

    public WordWeight(News news, String word, Double weight) {
        this.news = news;
        this.word = word;
        this.weight = weight;
    }


    @Override
    public int compareTo(WordWeight compareWW) {
        double compareWeight =  ((WordWeight)compareWW).getWeight();
        int answer;
        double compareResult = compareWeight - this.weight;
        if(compareResult>0){
            answer = 1;
        }
        else if(compareResult<0){
            answer = -1;
        }
        else answer = 0;
        return answer;
    }

    public static Comparator<WordWeight> FruitNameComparator
            = new Comparator<WordWeight>() {

        public int compare(WordWeight fruit1, WordWeight fruit2) {

            String fruitName1 = fruit1.getWord().toUpperCase();
            String fruitName2 = fruit2.getWord().toUpperCase();

            //ascending order
            return fruitName1.compareTo(fruitName2);

            //descending order
            //return fruitName2.compareTo(fruitName1);
        }

    };
}
