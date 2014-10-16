package analyzer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.BlockingQueue;

import javax.swing.JFileChooser;

import org.joda.time.DateTime;

import config.FactoriesSelector;
import difficultyPrediction.ADifficultyPredictionRunnable;
import difficultyPrediction.DifficultyPredictionRunnable;
import difficultyPrediction.DifficultyPredictionSettings;
import difficultyPrediction.featureExtraction.RatioBasedFeatureExtractor;
import difficultyPrediction.featureExtraction.RatioFeatures;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.util.LogReader;
import trace.plugin.PluginThreadCreated;
import util.annotations.Column;
import util.annotations.Row;
import util.annotations.Visible;
import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import bus.uigen.models.AFileSetterModel;
import bus.uigen.models.FileSetterModel;

public class AnAnalyzer {
	public static final String PARTICIPANT_INFORMATION_DIRECTORY = "data/ExperimentalData/";

	public static final String PARTICIPANT_INFORMATION_FILE = "Participant_Info.csv";
	public static final int SEGMENT_LENGTH = 50;
	public static final String ALL_PARTICIPANTS = "All";
	static Hashtable<String, String> participants = new Hashtable<String, String>();
	List<List<ICommand>> commandsList;

	FileSetterModel participantsFolder;
	AParametersSelector parameters;
	LogReader reader;
	protected Thread difficultyPredictionThread;	
	protected DifficultyPredictionRunnable difficultyPredictionRunnable;
	protected BlockingQueue<ICommand> pendingPredictionCommands;
	public AnAnalyzer() {
		DifficultyPredictionSettings.setReplayMode(true);
		DifficultyPredictionSettings.setSegmentLength(SEGMENT_LENGTH);

		reader = new LogReader();
		participantsFolder = new AFileSetterModel(JFileChooser.DIRECTORIES_ONLY);
		participantsFolder.setText(MainConsoleUI.PARTICIPANT_INFORMATION_DIRECTORY);
		parameters = new AParametersSelector(this);
		parameters.getParticipants().addChoice(ALL_PARTICIPANTS);
		parameters.getParticipants().setValue(ALL_PARTICIPANTS);
//		difficultyPredictionRunnable = new ADifficultyPredictionRunnable();
//		pendingPredictionCommands = difficultyPredictionRunnable.getPendingCommands();
//		difficultyPredictionThread = new Thread(difficultyPredictionRunnable);
//		difficultyPredictionThread.setName(DifficultyPredictionRunnable.DIFFICULTY_PREDICTION_THREAD_NAME);
//		difficultyPredictionThread.setPriority(Math.min(
//				Thread.currentThread().getPriority(),
//				DifficultyPredictionRunnable.DIFFICULTY_PREDICTION_THREAD_PRIORITY));
//		difficultyPredictionThread.start();
//		PluginThreadCreated.newCase(difficultyPredictionThread.getName(), this);
		 
		
	}
	@Row(0)
	public FileSetterModel getParticipantsFolder() {
		return participantsFolder;
	}
	@Visible(false)
	public String getParticipantsFolderName() {
		return participantsFolder.getText();
	}
	@Visible(false)
	public void setParticipantsFolderName(String aName) {
		 participantsFolder.setText(aName);
	}
	boolean directoryLoaded;
	public boolean preLoadDirectory() {
		return !directoryLoaded;
	}
	@Visible(false)
	public void loadDirectory() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(
					PARTICIPANT_INFORMATION_DIRECTORY
							+ PARTICIPANT_INFORMATION_FILE));
			String word = null;
			while ((word = br.readLine()) != null) {
				String[] userInfo = word.split(",");
				participants.put(userInfo[0].trim(), userInfo[1].trim());
				parameters.getParticipants().addChoice(userInfo[0]);
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		directoryLoaded = true;		
	}
	public boolean preLoadLogs() {
		return directoryLoaded && !logsLoaded;
	}
	boolean logsLoaded;
	@Visible(false)
    public void loadLogs() {
		FactoriesSelector.configureFactories();
		String participantId = parameters.getParticipants().getValue();
		String numberOfSegments = "" + parameters.getSegmentLength();

		
		if(participantId.equalsIgnoreCase(""))
			participantId = ALL_PARTICIPANTS;
		
		if(numberOfSegments.equalsIgnoreCase(""))
			numberOfSegments = "" + SEGMENT_LENGTH;
		
		//todo need to ask for discrete chunks or sliding window
		//may d for discrete and s for sliding window

//		scanIn.close();

		System.out.println("Processing logs for: " + participantId);
		List<String> participantIds = parameters.getParticipants().getChoices();
		List<List<ICommand>> commandsList;
		if (participantId.equals(ALL_PARTICIPANTS)) {
			for (String aParticipantId:participantIds) {
				if (aParticipantId.equals(ALL_PARTICIPANTS))
					continue;

				
//				String aParticipantId = participantIds
//						.nextElement();
//				String aParticipantFolder = participants.
				String aParticipanttFolder = participants.get(aParticipantId);
				commandsList = convertXMLLogToObjects(aParticipanttFolder);
				 MainConsoleUI.processCommands(participantsFolder.getText(), commandsList, numberOfSegments,aParticipanttFolder);
			}
		} else {
			String aParticipanttFolder = participants.get(participantId);

			commandsList =  convertXMLLogToObjects(aParticipanttFolder);
			DifficultyPredictionSettings.setRatiosFileName(aParticipanttFolder + "ratios.csv");
			 MainConsoleUI.processCommands(participantsFolder.getText(), commandsList, numberOfSegments, aParticipanttFolder);
		}

		logsLoaded = true;
	}
	
	public void processParticipant(String aParticipantId) {
		String aParticipantFolder = participants.get(aParticipantId);

		commandsList =  convertXMLLogToObjects(aParticipantFolder);
		DifficultyPredictionSettings.setRatiosFileName(aParticipantFolder + "ratios.csv");
		difficultyPredictionRunnable = new ADifficultyPredictionRunnable();
		pendingPredictionCommands = difficultyPredictionRunnable.getPendingCommands();
		difficultyPredictionThread = new Thread(difficultyPredictionRunnable);
		difficultyPredictionThread.setName(DifficultyPredictionRunnable.DIFFICULTY_PREDICTION_THREAD_NAME);
		difficultyPredictionThread.setPriority(Math.min(
				Thread.currentThread().getPriority(),
				DifficultyPredictionRunnable.DIFFICULTY_PREDICTION_THREAD_PRIORITY));
		difficultyPredictionThread.start();
		PluginThreadCreated.newCase(difficultyPredictionThread.getName(), this);
//		for (ICommand aCommand: commandsList) {
//			
//		}
		

	}
	public  List<List<ICommand>> convertXMLLogToObjects(
			String aFolderName) {
		
		List<List<ICommand>> listOfListOFcommands = new Vector<List<ICommand>>();
		String fullName = participantsFolder.getText()
				+ aFolderName + "/";
		File folder = new File(fullName);
		if (!folder.exists()) {
			System.out.println("folder does not exist:" + fullName);
			return listOfListOFcommands;
		}
		if (!folder.isDirectory()) {
			System.out.println("folder not a directory:" + fullName);
			return listOfListOFcommands;
		}
		List<String> participantFiles = MainConsoleUI.getFilesForFolder(folder);
		System.out.println("Particpant " + aFolderName + " has "
				+ participantFiles.size() + " file(s)");
		System.out.println();
		for (int i = 0; i < participantFiles.size(); i++) {
			String aFileName = fullName
					+ participantFiles.get(i);
//			List<ICommand> commands = reader.readAll(participantDirectory
//					+ participantFiles.get(i));
			System.out.println("Reading " + aFileName);
//			List<ICommand> commands;
			try {
			List<ICommand> commands = reader.readAll(aFileName);
			listOfListOFcommands.add(commands);

			} catch (Exception e) {
				System.out.println("Could not read file" + aFileName + e);
				
			}
			
//			listOfListOFcommands.add(commands);
		}

		return listOfListOFcommands;
	}
	@Row(1)
	public AParametersSelector getParameters() {
		return parameters;
	}
	//	int segmentLength = 50;
//	@Row(1)
//	public int getSegmentLength() {
//		return segmentLength;
//	}
//	public void setSegmentLength(int newVal) {
//		this.segmentLength = newVal;
//	}
	public static void main (String[] args) {
		OEFrame frame = ObjectEditor.edit(new AnAnalyzer());
		frame.setSize(550, 200);
		
	}
	
	public static void maybeRecordFeatures(RatioFeatures details) {
		if (!DifficultyPredictionSettings.isReplayMode()) 
			return;
		String aFileName = DifficultyPredictionSettings.getRatiosFileName();

		System.out.println("Insertion ratio:" + details.getInsertionRatio());
		System.out.println("Deletion ratio:" + details.getDeletionRatio());
		System.out.println("Debug ratio:" + details.getDebugRatio());
		System.out.println("Navigation ratio:" + details.getNavigationRatio());
		System.out.println("Focus ratio:" + details.getFocusRatio());
		System.out.println("Remove ratio:" + details.getRemoveRatio());
		System.out.println("features have been computed");
		
		java.util.Date time=new java.util.Date((long)details.getSavedTimeStamp());
		Calendar mydate = Calendar.getInstance();
		mydate.setTimeInMillis(details.getSavedTimeStamp());
		
		//mydate.get(Calendar.HOUR)
		//mydate.get(Calendar.MINUTE)
		//mydate.get(Calendar.SECOND)
		DateTime timestamp = new DateTime(details.getSavedTimeStamp());
		//timestamp.get(timestamp)
		
		System.out.println(timestamp.toString("MM-dd-yyyy H:mm:ss"));
		try
		{
//		    String filename= "/users/jasoncarter/filename.csv";
//		    String filename = dataFolder + "ratios.csv";
		    FileWriter fw = new FileWriter(aFileName,true); //the true will append the new data
		   
		    fw.write(""+ details.getInsertionRatio());
		    fw.write(",");
		    fw.write("" + details.getDeletionRatio());
		    fw.write(",");
		    fw.write("" + details.getDebugRatio());
		    fw.write(",");
		    fw.write("" + details.getNavigationRatio());
		    fw.write(",");
		    fw.write("" + details.getFocusRatio());
		    fw.write(",");
			fw.write("" + details.getRemoveRatio());
			fw.write(",");
			fw.write("" + timestamp.toString("MM-dd-yyyy H:mm:ss"));
			fw.write("\n");
		    fw.close();
		}
		catch(IOException ioe)
		{
		    System.err.println("IOException: " + ioe.getMessage());
		}
		
		

	}

}
