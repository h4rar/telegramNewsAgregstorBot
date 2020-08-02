package h4rar.telegram_news_bot.tgbot.bot.statistics;

import java.util.List;

public class DataForLineGraph {
    private String name;
    private List<?> xData;
    private List<? extends Number> yData;


    public DataForLineGraph(String name, List<?> xData, List<? extends Number> yData) {
        this.name = name;
        this.xData = xData;
        this.yData = yData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<?> getxData() {
        return xData;
    }

    public void setxData(List<?> xData) {
        this.xData = xData;
    }

    public List<? extends Number> getyData() {
        return yData;
    }

    public void setyData(List<? extends Number> yData) {
        this.yData = yData;
    }

}
