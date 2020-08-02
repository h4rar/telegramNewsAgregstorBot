package h4rar.telegram_news_bot.tgbot.bot.statistics;

import lombok.Getter;
import lombok.Setter;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieChartBuilder;

import java.io.IOException;
import java.util.List;

@Getter
@Setter
public class PieChartPercent {
    private String name;
    private int width;
    private int height;
    private List<InfoAboutCountNews> infoAboutCountNews;

    public PieChartPercent(String name, int width, int height, List<InfoAboutCountNews> infoAboutCountNews) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.infoAboutCountNews = infoAboutCountNews;
    }

    public void createChart(String path) {
        PieChart chart = new PieChartBuilder().width(width).height(height).title(name).build();
        for (InfoAboutCountNews i : infoAboutCountNews) {
            chart.addSeries(i.getSource(), i.getCount());
        }
        try {
            BitmapEncoder.saveBitmap(chart, path, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
