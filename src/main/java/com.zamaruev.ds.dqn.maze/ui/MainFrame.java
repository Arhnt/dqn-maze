package com.zamaruev.ds.dqn.maze.ui;

import lombok.Getter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

@Getter
public class MainFrame extends JFrame {

    private JPanel panel = new JPanel();
    private JSlider slider = new JSlider(0, 500, 20);
    private JTable grid = new JTable(new Object[5][5], new Object[]{"", "", "", "", ""});
    private JTextArea console = new JTextArea();
    private XYSeries history = new XYSeries("History");
    private XYSeries avgHistory = new XYSeries("Avg History", true, false);

    private JButton pause = new JButton("Pause");
    private JButton save = new JButton("Save Model");
    private JButton load = new JButton("Load Model");
    private JTextField random = new JTextField(5);


    public MainFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 650);

        add(panel);
        add(console, BorderLayout.SOUTH);
        add(slider, BorderLayout.NORTH);

        addMazeGrid();

        JPanel managePanel = new JPanel();
        managePanel.setLayout(new BoxLayout(managePanel, BoxLayout.Y_AXIS));
        panel.add(managePanel);

        addHistoryChart();
        addAvgHistoryChart();


        managePanel.add(pause);
        managePanel.add(save);
        managePanel.add(load);
        managePanel.add(random);
    }

    private void addMazeGrid() {
        grid.setGridColor(Color.BLACK);
        IntStream.range(0, grid.getColumnCount())
                .boxed()
                .forEach(i -> grid.getColumnModel().getColumn(i).setPreferredWidth(15));
        panel.add(grid);
    }

    private void addHistoryChart() {
        history.setMaximumItemCount(500);
        JFreeChart lineChart = ChartFactory.createScatterPlot(
                "",
                "",
                "",
                ds(history),
                PlotOrientation.VERTICAL,
                false, false, false);
        lineChart.getXYPlot().getRenderer().setSeriesShape(0, ShapeUtilities.createDiagonalCross(1, 1));

        ChartPanel chartPanel = new ChartPanel(lineChart);
        chartPanel.setPreferredSize(new Dimension(950, 210));
        panel.add(chartPanel);
    }

    private void addAvgHistoryChart() {
        avgHistory.setMaximumItemCount(500);
        JFreeChart avgLineChart = ChartFactory.createScatterPlot(
                "",
                "",
                "",
                ds(avgHistory),
                PlotOrientation.VERTICAL,
                false, false, false);
        avgLineChart.getXYPlot().getRenderer().setSeriesShape(0, ShapeUtilities.createDiagonalCross(1, 1));

        ChartPanel avgChartPanel = new ChartPanel(avgLineChart);
        avgChartPanel.setPreferredSize(new Dimension(950, 210));
        panel.add(avgChartPanel);
    }

    private XYDataset ds(XYSeries history) {
        XYSeriesCollection series = new XYSeriesCollection();
        series.addSeries(history);
        return series;
    }

}
