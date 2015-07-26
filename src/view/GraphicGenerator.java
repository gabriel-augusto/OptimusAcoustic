package view;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import simulator.objects.SoundObject;
import simulator.objects.SoundSourceObject;

public class GraphicGenerator{
	
	private JFreeChart graphic;
	private XYItemRenderer renderer = null;
	private XYSeries sounds = new XYSeries("Sound");
    private XYSeries soundSources = new XYSeries("SoundSource");
    private XYPlot xyPlot = null;
    private static GraphicGenerator graphicGenerator = null;
    private int red;
    private int blue;
	
	private GraphicGenerator() {
		
	}
	
	public static GraphicGenerator getInstance(){
		if(graphicGenerator == null){
			graphicGenerator = new GraphicGenerator();
		}
		return graphicGenerator;
	}
	
	public ChartPanel createPanel() {
        graphic = ChartFactory.createScatterPlot("Ambiente", "Largura", "Comprimento", createSampleData(), PlotOrientation.VERTICAL, true, true, false);
        
        xyPlot = (XYPlot) graphic.getPlot();
        xyPlot.setDomainCrosshairVisible(true);
        xyPlot.setRangeCrosshairVisible(true);
        xyPlot.setBackgroundPaint(Color.white);
        
        renderer = xyPlot.getRenderer();
        renderer.setSeriesPaint(0, new Color(0,150,0));
        
        return new ChartPanel(graphic);
    }
	
	public void setBounds(int width, int length){
		NumberAxis domain = (NumberAxis) xyPlot.getDomainAxis();
        adjustAxis((NumberAxis) xyPlot.getDomainAxis(), width, true);
        adjustAxis((NumberAxis) xyPlot.getRangeAxis(), length, false);
        
        domain.setVerticalTickLabels(true);
	}
	
	private XYDataset createSampleData() {
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
        xySeriesCollection.addSeries(soundSources);
        xySeriesCollection.addSeries(sounds);
        return xySeriesCollection;
    }
	
	public void updateSounds(){
		double maxSoundPower = 0;
		int maxPower = 0;
		
		for(SoundSourceObject soundSource : SoundSourceObject.getSoundSources().values()){
			if(soundSource.getPower() > maxPower){
				maxPower = soundSource.getPower();
			}
		}		
		sounds.clear();	
		renderer.setSeriesPaint(1, new Color(red,0,blue));
		for(SoundObject sound : SoundObject.getSounds().values()){
			if(sound.getPower() > maxSoundPower){
				maxSoundPower = sound.getPower();
			}
            sounds.add(sound.getActualLocation().getX(), sound.getActualLocation().getY());
        }
		red = (int)(maxSoundPower/maxPower * 255);
		blue = (int)(255 - (maxSoundPower/maxPower * 255));
		
	}
	
	public void updateSoundSources(){
		soundSources.clear();
		for(SoundSourceObject soundSource : SoundSourceObject.getSoundSources().values()){
			soundSources.add(soundSource.getLocation().getX(), soundSource.getLocation().getY());
		}
	}
	
	public void clearGraphic(){
		sounds.clear();
		soundSources.clear();
	}
	
	private void adjustAxis(NumberAxis axis, int value, boolean vertical) {
        axis.setRange(0, value);
        axis.setTickUnit(new NumberTickUnit(1));
        axis.setVerticalTickLabels(vertical);
    }
	 
	public void save(OutputStream out) throws IOException {
		ChartUtilities.writeChartAsPNG(out, graphic, 500, 350);
	}
}
