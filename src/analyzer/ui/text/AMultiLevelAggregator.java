package analyzer.ui.text;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import trace.difficultyPrediction.AggregatePredictionChanged;
import trace.difficultyPrediction.PredictionChanged;
import util.annotations.Column;
import util.annotations.ComponentHeight;
import util.annotations.PreferredWidgetClass;
import util.annotations.Row;
import util.annotations.Visible;
import util.misc.Common;
import util.models.ALabelBeanModel;
import util.models.AListenableVector;
import util.models.LabelBeanModel;
import analyzer.AWebLink;
import analyzer.ParticipantTimeLine;
import analyzer.RatioFilePlayerFactory;
import analyzer.WebLink;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.MultiLevelAggregator;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.metrics.RatioCalculator;
import difficultyPrediction.metrics.RatioCalculatorSelector;
import difficultyPrediction.predictionManagement.PredictionManagerStrategy;
import edu.cmu.scs.fluorite.commands.DifficulyStatusCommand;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.model.StatusConsts;

public class AMultiLevelAggregator implements MultiLevelAggregator{
	static OEFrame oeFrame;

//	protected List<ICommand> commands = new ArrayList();
	protected List<RatioFeatures> features = new ArrayList();
	protected List<String> predictions = new ArrayList();
	protected String aggregatedStatus = "";
	protected String manualStatus = "";
	protected String manualBarrier = "";
	
	static RatioCalculator ratioCalculator;
	static MultiLevelAggregator instance;
	protected StringBuffer commandsBuffer = new StringBuffer();
	protected StringBuffer ratiosBuffer = new StringBuffer();
	protected StringBuffer predictionsBuffer = new StringBuffer();
	protected PropertyChangeSupport propertyChangeSupport;
	protected List<LabelBeanModel> webLinks = new AListenableVector<>();
	protected int numWebLinks;
	
	
	


	public AMultiLevelAggregator() {
//		DifficultyRobot.getInstance().addRatioFeaturesListener(this);
		
		// need to remove the code duplication below by having a single object that gets bound to
		// either the robot or RatioFilePlayer
		
		// for live or replayed prediction events
		DifficultyRobot.getInstance().addStatusListener(this);
		DifficultyRobot.getInstance().addPluginEventEventListener(this);
		DifficultyRobot.getInstance().addRatioFeaturesListener(this);
		DifficultyRobot.getInstance().addWebLinkListener(this);
		DifficultyRobot.getInstance().addBarrierListener(this);
		
		// for rati file relay
		RatioFilePlayerFactory.getSingleton().addStatusListener(this);
		RatioFilePlayerFactory.getSingleton().addPluginEventEventListener(this);
		RatioFilePlayerFactory.getSingleton().addRatioFeaturesListener(this);
		RatioFilePlayerFactory.getSingleton().addWebLinkListener(this);
		RatioFilePlayerFactory.getSingleton().addBarrierListener(this);

		
//		ratioCalculator = APercentageCalculator.getInstance();
		ratioCalculator = RatioCalculatorSelector.getRatioFeatures();
//		webLinks.add(new ALabelBeanModel(Common.toBlueColoredUnderlinedHrefHTML("https://www.google.com", "google")));
		webLinks.add(new ALabelBeanModel(""));

		numWebLinks = 0;
		propertyChangeSupport = new PropertyChangeSupport(this);

	}
	
	
	@Override
    @Visible(false)
	public void reset() {
		aggregatedStatus = StatusConsts.INDETERMINATE;
		oldAggregateStatus = StatusConsts.INDETERMINATE;
		setBarrier("");
		features.clear();
		predictions.clear();
		commandsBuffer.setLength(0);
		ratiosBuffer.setLength(0);
		predictionsBuffer.setLength(0);	
		clearWebLinks();
	}
	public static String toString(DifficulyStatusCommand aCommand) {
		if (aCommand.getStatus() == null)
			return "";
		return aCommand.getStatus().toString();		
	}
	
	protected void setManualStatus(String newValue) {
		String oldStatus = manualStatus;
		manualStatus = newValue;
		propertyChangeSupport.firePropertyChange("Corrected Status", oldStatus, manualStatus);
	}

	@Override
    @Visible(false)
	public void newCommand(ICommand newCommand) {
		if (newCommand instanceof DifficulyStatusCommand) {
			setManualStatus(toString((DifficulyStatusCommand) newCommand));
//			String oldStatus = manualStatus;
//			manualStatus = toString((DifficulyStatusCommand) newCommand);
//			propertyChangeSupport.firePropertyChange("Corrected Status", oldStatus, manualStatus);

		} else {
		maybeClearNonAggregatedStatus();
//		commands.add(newCommand);
		commandsBuffer.append(toClassifiedString(newCommand) + "\n");
		propertyChangeSupport.firePropertyChange("Segment", "", commandsBuffer.toString());
		}	
	}

	@Override
    @Visible(false)
	public void commandProcessingStarted() {
		reset();
		propertyChangeSupport.firePropertyChange("this", "", this);

		
	}

	@Override
    @Visible(false)
	public void commandProcessingStopped() {
//		reset();
//		propertyChangeSupport.firePropertyChange("this", "", this);

		
	}
	String oldStatus = "";
	@Override
    @Visible(false)
	public void newStatus(String aStatus) {
		if (!aStatus.equals(oldStatus)) {
			PredictionChanged.newCase(aStatus, getRatios(), this);
			oldStatus = aStatus;
		}
		predictions.add(aStatus);
		predictionsBuffer.append(aStatus + "\n");
		propertyChangeSupport.firePropertyChange("Predictions", "", predictionsBuffer.toString());
		
	}
	String oldAggregateStatus = "";
	boolean lastEventWasAggregatedStatus;
	@Override
    @Visible(false)
	public void newAggregatedStatus(String aStatus) {
		lastEventWasAggregatedStatus = true; // at next command clear data other than aggregated status
		if (!aStatus.equals(oldAggregateStatus)) {
			AggregatePredictionChanged.newCase(aStatus, getRatios(), this);
			aggregatedStatus = aStatus;
			propertyChangeSupport.firePropertyChange("AggregatedStatus", oldAggregateStatus, aStatus);
			oldAggregateStatus = aStatus;		

		}
//		predictions.clear();
//		predictionsBuffer.setLength(0);
//		ratiosBuffer.setLength(0);
//		commandsBuffer.setLength(0);
		
	}
    @Visible(false)
	public void maybeClearNonAggregatedStatus() {
		if (!lastEventWasAggregatedStatus) 
			return;
		lastEventWasAggregatedStatus = false;
		predictions.clear();
		predictionsBuffer.setLength(0);
		ratiosBuffer.setLength(0);
		commandsBuffer.setLength(0);
		setManualStatus("");
		setBarrier("");;
		clearWebLinks();
		
	}
    
    protected void clearWebLinks() {
    	for (LabelBeanModel aWebLink: webLinks) {
    		aWebLink.setText("");
//    		aWebLink.setSearchString("");
//    		aWebLink.setUrlString("");
    	}
    	numWebLinks=0;
    }

	@Override
    @Visible(false)
	public void newStatus(int aStatus) {
		// TODO Auto-generated method stub
		
	}

	@Override
	@Visible(false)
	public void newAggregatedStatus(int aStatus) {
		// TODO Auto-generated method stub
		
	}
	@Override
//	@Visible(false)
	public void newRatios(RatioFeatures newVal) {
		features.add(newVal);
//		commands.clear();	
//		commandsBuffer.setLength(0);
		commandsBuffer.append("\n");
		ratiosBuffer.append(newVal + "\n");
		propertyChangeSupport.firePropertyChange("Ratios", "", ratiosBuffer.toString());
//		setManualStatus("");
//		setBarrier("");

	}
	@Row(0)
	@Override
	public String getManualBarrier() {
		return manualBarrier;
	}
	@Row(1)
	@Override
	public String getManualStatus() {
		return manualStatus;
	}
//	@Override
//	public void setManualStatus(String newVal) {
//		this.manualStatus = newVal;
//	}
	
//	@Override
//	public void setBarrier(String newVal) {
//		this.barrier = newVal;
//	}
	@Row(2)
	@Override
	public String getAggregatedStatus() {
		return aggregatedStatus;
	}
    @Visible(false)
	protected  String toClassifiedString (ICommand aCommand) {
		String featureName = ratioCalculator.getFeatureName(aCommand);
		return featureName + " (" + aCommand + " )";
	}
	@Row(3)
	@PreferredWidgetClass(JTextArea.class)
	@ComponentHeight(100)
	@Override
	public String getPredictions() {
		return predictionsBuffer.toString();
	}
	@Row(4)
	@PreferredWidgetClass(JTextArea.class)
	@ComponentHeight(100)
	@Override
	public String getRatios() {
		return ratiosBuffer.toString();
	}
	@Row(5)	
	@Override
	public List<LabelBeanModel> getWebLinks() {
		return webLinks;
	}
//	
//	public void setWebLinks(List<LabelBeanModel> newVal) {
//		webLinks = newVal;
//	}


//	public void setWebLinks(List<WebLink> webLinks) {
//		this.webLinks = webLinks;
//	}
//	@Row(4)
//	@ComponentHeight(100)
//	public String getWebSearch() {
//		return webSearch;
//	}
//
//
//	public void setWebSearch(String webSearch) {
//		this.webSearch = webSearch;
//	}
//
//	@Row(5)
//	@ComponentHeight(100)
//	public String getWebURL() {
//		return webURL;
//	}
//
//
//	public void setWebURL(String webURL) {
//		this.webURL = webURL;
//	}
	@Row(6)
	@PreferredWidgetClass(JTextArea.class)
	@ComponentHeight(200)
	public String getSegment() {
		return commandsBuffer.toString();
	}
	
	
	
	@Visible(false)
	public static MultiLevelAggregator getInstance() {
		return AggregatorFactory.getSingleton();
		
//		if (instance == null) {
//			instance = new AMultiLevelAggregator();
//			
//		}
//		return instance;
	}
	
	@Override
	public void newWebLink(WebLink aWebLink) {		
		if (numWebLinks < webLinks.size()) {
//			webLinks.get(numWebLinks).setSearchString(aWebLink.getSearchString());
//			webLinks.get(numWebLinks).setUrlString(aWebLink.getUrlString());
			webLinks.get(numWebLinks).setText(aWebLink.getClickableLink().getText());
		} else {
			webLinks.add(new ALabelBeanModel(aWebLink.getClickableLink().getText()));
		}
		
	}
	
	public static void createUI() {
		if (oeFrame != null) {
			getInstance().reset();
			return;
		}
		 oeFrame = ObjectEditor.edit(getInstance());
		oeFrame.setSize(700, 700);
		oeFrame.getFrame().setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		oeFrame.setAutoExitEnabled(false);
	}

	

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
		
	}
	@Override
	public void newBarrier(String newValue) {
		setBarrier(newValue);
//		String oldValue = manualBarrier;
//		manualBarrier = newValue;
//		propertyChangeSupport.firePropertyChange("barrier", oldValue, newValue);		
	}
	
	 void setBarrier(String newValue) {
		String oldValue = manualBarrier;
		manualBarrier = newValue;
		propertyChangeSupport.firePropertyChange("barrier", oldValue, newValue);		
	}
	
	
	
//	@Override
//	public void reset() {
//		// TODO Auto-generated method stub
//		System.err.println("Reset not implemented");
//		
//	}
	
	public static void main (String[] args) {
//		ObjectEditor.edit(AMultiLevelAggregator.getInstance());
		createUI();
	}


	


	
	

}
