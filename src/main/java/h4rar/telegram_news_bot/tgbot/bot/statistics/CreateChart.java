package h4rar.telegram_news_bot.tgbot.bot.statistics;

import h4rar.telegram_news_bot.tgbot.model.SourceEnum;
import org.knowm.xchart.*;
import org.knowm.xchart.style.BoxStyler;
import org.knowm.xchart.style.Styler;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CreateChart {
    private int width;
    private int height;
    private String path;
    private String nameGraph;
    private final String nameXTitle;
    private final String nameYTitle;

    public CreateChart(int width, int height, String path, String nameGraph, String nameXTitle, String nameYTitle) {
        this.width = width;
        this.height = height;
        this.path = path;
        this.nameGraph = nameGraph;
        this.nameXTitle = nameXTitle;
        this.nameYTitle = nameYTitle;
    }

    public void createLinearChart(List<DataForLineGraph> listDataGraph) {
//        XYChart chart = new XYChart(width, height);
        XYChart chart = new XYChart(1000, height);
        chart.setTitle(nameGraph);
        chart.setXAxisTitle(nameXTitle);
        chart.setYAxisTitle(nameYTitle);
        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        chart.getStyler().setDatePattern("dd MMM");

        setXandY(listDataGraph, chart);
    }

//    public XYChart createScatterChart(List<DataForLineGraph> listDataGraph) {
//        // Create Chart
//        XYChart chart = new XYChartBuilder().width(width).height(height).build();
//        // Customize Chart
//        chart.setXAxisTitle(nameXTitle);
//        chart.setYAxisTitle(nameYTitle);
//        chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
//        chart.getStyler().setChartTitleVisible(false);
//        chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideS);
//        chart.getStyler().setMarkerSize(16);
//        chart.getStyler().setDatePattern("dd MMM");
//
//        setXandY(listDataGraph, chart);
//
//        try {
//            BitmapEncoder.saveBitmap(chart, path, BitmapEncoder.BitmapFormat.PNG);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return chart;
//    }

    public BoxChart boxChart(List<DataForLineGraph> listDataGraph) {
        BoxChart chart = new BoxChartBuilder().title(nameGraph).build();

        chart.getStyler().setBoxplotCalCulationMethod(BoxStyler.BoxplotCalCulationMethod.N_LESS_1_PLUS_1);
        chart.getStyler().setToolTipsEnabled(true);

        for (DataForLineGraph dataForLineGraph : listDataGraph) {
            String nameLine = dataForLineGraph.getName();
//            List<?> xData = dataForLineGraph.getxData();
            List<? extends Number> yData = dataForLineGraph.getyData();
            chart.addSeries(SourceEnum.getSourceEnum(nameLine).toString(), yData);
        }

        try {
            BitmapEncoder.saveBitmap(chart, path, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return chart;
    }

    private void setXandY(List<DataForLineGraph> listDataGraph, XYChart chart) {
        for (DataForLineGraph dataForLineGraph : listDataGraph) {
            String nameLine = dataForLineGraph.getName();
            List<?> xData = dataForLineGraph.getxData();
            List<? extends Number> yData = dataForLineGraph.getyData();

            chart.addSeries(SourceEnum.getSourceEnum(nameLine).toString(), xData, yData);
        }
        try {
            BitmapEncoder.saveBitmap(chart, path, BitmapEncoder.BitmapFormat.PNG);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getNameGraph() {
        return nameGraph;
    }

    public void setNameGraph(String nameGraph) {
        this.nameGraph = nameGraph;
    }
}
