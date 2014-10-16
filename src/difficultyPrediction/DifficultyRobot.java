package difficultyPrediction;


import java.sql.Date;
import java.util.Calendar;

//import main.Server;












import analyzer.AnAnalyzer;
import trace.difficultyPrediction.NewPredictionEvent;
import trace.difficultyPrediction.NewExtractedStatusInformation;
import trace.difficultyPrediction.NewPrediction;
import trace.difficultyPrediction.PredictionValueToStatus;
import trace.difficultyPrediction.StatusAggregationStarted;
import util.trace.Tracer;
//import database.Status;
import difficultyPrediction.eventAggregation.ADisjointDiscreteChunks;
import difficultyPrediction.eventAggregation.AnEventAggregator;
import difficultyPrediction.eventAggregation.AnEventAggregatorDetails;
import difficultyPrediction.eventAggregation.EventAggeragator;
import difficultyPrediction.featureExtraction.ExtractRatiosBasedOnNumberOfEvents;
import difficultyPrediction.featureExtraction.ARatioBasedFeatureExtractor;
import difficultyPrediction.featureExtraction.ARatioFeatures;
import difficultyPrediction.featureExtraction.RatioBasedFeatureExtractor;
import difficultyPrediction.featureExtraction.RatioFeatures;
import difficultyPrediction.predictionManagement.DecisionTreeModel;
import difficultyPrediction.predictionManagement.APredictionManager;
import difficultyPrediction.predictionManagement.APredictionManagerDetails;
import difficultyPrediction.predictionManagement.PredictionManager;
import difficultyPrediction.statusManager.StatusAggregationDiscreteChunks;
import difficultyPrediction.statusManager.StatusManager;
import difficultyPrediction.statusManager.StatusManagerDetails;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.commands.PredictionCommand;
import edu.cmu.scs.fluorite.commands.PredictionCommand.PredictionType;
import edu.cmu.scs.fluorite.model.EventRecorder;

public class DifficultyRobot implements Mediator {
	//Server server;
	String id = "";
	EventAggeragator eventAggregator;
	RatioBasedFeatureExtractor featureExtractor;
	PredictionManager predictionManager;
	StatusManager statusManager;
	private StatusInformation statusInformation;
	
	public DifficultyRobot(String id) {
		this.id = id;
		eventAggregator = new AnEventAggregator(this);
//		eventAggregator.eventAggregationStrategy = new ADisjointDiscreteChunks();
		eventAggregator.setEventAggregationStrategy(new ADisjointDiscreteChunks());

		
		featureExtractor = new ARatioBasedFeatureExtractor(this);
		featureExtractor.setFeatureExtractionStrategy(new ExtractRatiosBasedOnNumberOfEvents());
		
		predictionManager = new APredictionManager(this);
		predictionManager.setPredictionStrategy(new DecisionTreeModel(predictionManager));
		
		statusManager = new StatusManager(this);
		statusManager.strategy = new StatusAggregationDiscreteChunks(statusManager);
		
		//this.server=server;
	}
	
	
	//Aggregate events using aggregator class
	public void processEvent(ICommand e) {
		NewPredictionEvent.newCase(this);
//		Tracer.info(this, "difficultyRobot.processEvent");

//			eventAggregator.eventAggregationStrategy.performAggregation(e, eventAggregator);
			eventAggregator.getEventAggregationStrategy().performAggregation(e, eventAggregator);

	}
	
	//Takes aggregated events and uses a separate thread to compute features
	public void eventAggregator_HandOffEvents(AnEventAggregator aggregator,
			AnEventAggregatorDetails details) {

		Tracer.info(this,"difficultyRobot.handoffevents");
		this.featureExtractor.getFeatureExtractionStrategy().performFeatureExtraction(details.actions, featureExtractor);

		
	}


	@Override
	public void featureExtractor_HandOffFeatures(RatioBasedFeatureExtractor extractor,
			RatioFeatures details) {
		Tracer.info(this, "difficultyRobot.featureExtractor");
		statusInformation = new AStatusInformation();
		statusInformation.setEditRatio(details.getEditRatio());
		statusInformation.setDebugRatio(details.getDebugRatio());
		statusInformation.setNavigationRatio(details.getNavigationRatio());
		statusInformation.setRemoveRatio(details.getRemoveRatio());
		statusInformation.setFocusRatio(details.getFocusRatio());
		NewExtractedStatusInformation.newCase(statusInformation, this);
		AnAnalyzer.maybeRecordFeatures(details);

		this.predictionManager.getPredictionStrategy().predictSituation(details.getEditRatio(), details.getDebugRatio(), details.getNavigationRatio(), details.getFocusRatio(), details.getRemoveRatio());
//		NewPrediction.newCase(this);

	}


	@Override
	public void predictionManager_HandOffPrediction(PredictionManager manager,
			APredictionManagerDetails details) {
		StatusAggregationStarted.newCase(this);
		Tracer.info(this, "difficultyRobot.handOffPrediction");
		statusInformation.setPredictedClass("Prediction");
		statusInformation.setPrediction(details.predictionValue);
		statusInformation.setTimeStamp(new Date(Calendar.getInstance().getTimeInMillis()));
		statusInformation.setStatusKind(StatusKind.PREDICTION_MADE);
		statusInformation.setUserId(this.id);
		statusInformation.setUserName(this.id);
		this.statusManager.strategy.aggregateStatuses(details.predictionValue);
	}


	@Override
	public void statusManager_HandOffStatus(StatusManager manager,
			StatusManagerDetails details) {
		Tracer.info(this, "difficultyRobot.handOffStatus");
		 StatusPrediction statusPrediction = new StatusPrediction();
         statusPrediction.timeStamp = new Date(Calendar.getInstance().getTimeInMillis());
         statusPrediction.prediction = details.predictionValue;
         statusPrediction.userId = this.id;
         statusPrediction.userName = this.id;
         statusPrediction.statusKind = StatusKind.PREDICTION_MADE;
       
         saveToLog(details);
         
         
         //I'm not sure I need this
//         DifficultyPredictionEventArgs eventArgs = new DifficultyPredictionEventArgs();
//         eventArgs.predictionValue = details.predictionValue;
//         eventArgs.id = this.id;
//         server.robot_handOffResultsFromDifficultyRobot(this, eventArgs);
	}
	
	
	public void saveToLog(StatusManagerDetails prediction)
    {
		Tracer.info(this, "Saving to log:" + prediction);
		PredictionValueToStatus.newCase(this);
		 PredictionType predictionType = PredictionType.MakingProgress;
		 if(prediction.predictionValue.equals("NO"))
         {
        	 predictionType = PredictionType.MakingProgress;
         }
         if(prediction.predictionValue.equals("TIE"))
         {
        	 predictionType = PredictionType.Indeterminate;
         }
         if(prediction.predictionValue.equals("YES"))
         {
        	 predictionType = PredictionType.HavingDifficulty;
         }
    
         PredictionCommand predictionCommand = new PredictionCommand(predictionType);
         EventRecorder.getInstance().recordCommand(predictionCommand);
    }


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public EventAggeragator getEventAggregator() {
		return eventAggregator;
	}


	public void setEventAggregator(EventAggeragator eventAggregator) {
		this.eventAggregator = eventAggregator;
	}


	public RatioBasedFeatureExtractor getFeatureExtractor() {
		return featureExtractor;
	}


	public void setFeatureExtractor(RatioBasedFeatureExtractor featureExtractor) {
		this.featureExtractor = featureExtractor;
	}


	public PredictionManager getPredictionManager() {
		return predictionManager;
	}


	public void setPredictionManager(PredictionManager predictionManager) {
		this.predictionManager = predictionManager;
	}


	public StatusManager getStatusManager() {
		return statusManager;
	}


	public void setStatusManager(StatusManager statusManager) {
		this.statusManager = statusManager;
	}


	public StatusInformation getStatusInformation() {
		return statusInformation;
	}


	public void setStatusInformation(StatusInformation statusInformation) {
		this.statusInformation = statusInformation;
	}
	
}
