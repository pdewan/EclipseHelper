package analyzer.extension;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import bus.uigen.OEFrame;
import bus.uigen.ObjectEditor;
import analyzer.AParticipantTimeLine;
import analyzer.AnAnalyzer;
import analyzer.Analyzer;
import analyzer.ParticipantTimeLine;
import difficultyPrediction.DifficultyRobot;
import difficultyPrediction.extension.APrintingDifficultyPredictionListener;
import difficultyPrediction.featureExtraction.RatioFeatures;
import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.commands.PredictionCommand;

/**Class that generates Arff Files from the input ratios via difficulty listener event callbacks and
 * new predictions.
 * <p>
 * Instructions:<br>
 * 1. To ins
 * 
 * @author wangk1
 *
 */
public class AnArffGenerator extends AnAnalyzerProcessor implements ArffGenerator{
	public static final String DEFAULT_ARFF_PATH="data/userStudy";

	//name of relation to be printed as @relation tag
	public static final String RELATION="programmer-weka.filters.supervised.instance.SMOTE-C0-K5-P100.0-S1-weka.filters.supervised.instance.SMOTE-C0-K5-P100.0-S1-weka.filters.supervised.instance.SMOTE-C0-K5-P100.0-S1-weka.filters.supervised.instance.SMOTE-C0-K5-P100.0-S1";

	//Insert More features here
	public static final String[] FEATURES={
		"insertPercentage","numeric",
		"deletePercentage","numeric",
		"debugPercentage","numeric",
		"searchPercentage","numeric",
		"focusPercentage","numeric",
		"removePercentage","numeric",
		"webLinkTimes","numeric",
		"stuck","{YES,NO}"
	};
	

	//path to save the arff file to. Include the .arff extension
	private String path;
	//Buffered writer for writing out to the arff file
	private ArffWriter arffWriter;

	//Queue of ratios, when a new prediction is made. Print all the ratios from the queue.
	private Queue<RatioFeatures> ratios;


	//set to keep
	//Is the user currently stuck
	private boolean isStuck;

	private boolean all;

	private Analyzer analyzer;

	//set the path of the arff file
	public AnArffGenerator(Analyzer analyzer) {
		this.analyzer=analyzer;
		this.isStuck=false;
		this.ratios=new LinkedList<RatioFeatures>();

		arffWriter=new AnArffGenerator.ArffWriter();

		//register the event listeners
		DifficultyRobot.getInstance().addRatioFeaturesListener(this);
		DifficultyRobot.getInstance().addPluginEventEventListener(this);
	}


	/**METHODS SECTION*/
	@Override
	public void newParticipant(String anId, String aFolder) {
		System.out.println("Extension**New Participant:" + anId);
		participantTimeLine = new AParticipantTimeLine();
		participantToTimeLine.put(anId, participantTimeLine );

		//set the right path for writing
		//case I for all
		if(!all) {
			if(anId.equals("All") && aFolder==null ) {
				//set path
				path=AnAnalyzer.PARTICIPANT_OUTPUT_DIRECTORY+"/all.arff";
				this.all=true;

				//else it is individual filess
			} else {
				path=AnAnalyzer.PARTICIPANT_OUTPUT_DIRECTORY+"/"+aFolder+"/"+aFolder+".arff";

			}

			//prep the arfffile and generate headers. Start the writer
			prep();
		}

	}

	/***/
	@Override
	public void finishParticipant(String aId, String aFolder) {
		System.out.println("***EXTENSION Participant "+aId+"Completed");

		

		//set all to false. Only if it is current generating all files
		//and the stop signal aka aId of All and aFolder of null is recieved.
		if(all && aId.equals("All") && aFolder==null) {
			//write the ratios out to arfffile only at the end or else there will be duplicates
			writeToArff(all);
			
			this.all=false;
			//stop the writer
			arffWriter.stop();

			//if not all then just stop the writer.
		} else if(!all) {
			//write the ratios out to arfffile
			writeToArff(all);
			 
			arffWriter.stop();

		}
	}

	/**Prep arff file method, called by newParticipant Method*/
	private void prep() {
		//create a new bufferedwriter, with a different path


		//if no file name exist generate one that does not override other files
		if(path==null) {
			path=findRightFileName();

		}

		Path p=Paths.get(path);

		//create file if not exists
		if(!Files.exists(p) && Files.notExists(p)) {
			try {
				Files.createFile(p);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {


		}

		if(this.all) {
			arffWriter.start(path, true);
			//now write headers out to file
			generateArffHeader();
			//stop is important. Flushes header since the newUser is going to create a new writer
			//this forces the bytes out or the header is not going to be written
		} else {
			arffWriter.start(path,false);
			//now write headers out to file
			generateArffHeader();

		}

	}

	/**Find the right name for the file if no name is specified by {@link #setArffFilePath(String)}*/
	private String findRightFileName() {
		int i=0;

		//find the right file to write out to
		while(Files.exists(Paths.get(DEFAULT_ARFF_PATH+i+".arff")) && !Files.notExists(Paths.get(DEFAULT_ARFF_PATH+i+".arff"))){
			i++;

		}

		return DEFAULT_ARFF_PATH+i+".arff";
	}

	/**Generate the @relation, @attribute header for arff files*/
	private void generateArffHeader() {
		arffWriter.writeRelation(RELATION).writeNewLine();

		//write all features out
		for(int i=0;i<FEATURES.length-1;i=i+2) {
			arffWriter.writeAttribute(FEATURES[i], FEATURES[i+1]);

		}

		arffWriter.writeNewLine();

	}

	private void writeToArff(boolean all) {
		if(all) {
			//for each participant
			for(Map.Entry<String, ParticipantTimeLine> e:super.participantToTimeLine.entrySet()) {
				//for each participant's data points

				ParticipantTimeLine p=e.getValue();
				outputRatios(p);


			}

			//one person
		} else {
			ParticipantTimeLine l=super.participantTimeLine;
			outputRatios(l);

		}
	}

	private void outputRatios(ParticipantTimeLine p) {
		for(int i=0;i<p.getDebugList().size();i++) {
			//get the correct numerical representation of predicition
			long prediction=p.getPredictionCorrections().get(i)<0? p.getPredictions().get(i):p.getPredictionCorrections().get(i);

			arffWriter.writeData(
					prediction==0? "NO":"YES", 
							p.getInsertionList().get(i),
							p.getDeletionList().get(i),
							p.getDebugList().get(i),
							p.getNavigationList().get(i),
							p.getFocusList().get(i),
							p.getRemoveList().get(i),
							p.getWebLinks()==null?0:p.getWebLinks().get(i)==null?0:p.getWebLinks().get(i).size()
					);
			
			
		}
	}


	/**Inner static class that encapsulate a buffered stream<br>*/
	/**Allows chaining by returning this<br>
	 * 
	 * Swallows errors*/
	public static class ArffWriter{
		private BufferedWriter writer;
		//turns to true when output to the data section started
		boolean datastarted;
		private String path;

		public ArffWriter() {
			this.datastarted=false;

		}

		/**Must be called before any form of writing<br>
		 * Can be called repeatidly to set to a new path
		 * */
		public void start(String path, boolean append) {
			this.datastarted=false;
			this.path=path;

			try {
				//truncate first
				writer=Files.newBufferedWriter(Paths.get(path), Charset.defaultCharset(), StandardOpenOption.WRITE,StandardOpenOption.TRUNCATE_EXISTING);

				//if append, start in append mode
				if(append) {
					writer.close();

					writer=Files.newBufferedWriter(Paths.get(path), Charset.defaultCharset(), StandardOpenOption.APPEND);
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**Start the same writer with the same path**/
		public void restart() {
			try {
				writer=Files.newBufferedWriter(Paths.get(path), Charset.defaultCharset(), StandardOpenOption.APPEND);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**Set another file to write to. If append, write to end of the filef*/
		public void resetPath(String path,boolean append) {
			start(path,append);

		}

		/**Stop the stream*/
		public void stop() {
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		/**Write out a @Relation and its content. Auto append a newline at the end*/
		public ArffWriter writeRelation(String name) {
			this.writeToArffFile("@Relation ")
			.writeToArffFile(name).writeNewLine();


			return this;
		}

		/**Write out @attribute, its name, and its type*/
		public ArffWriter writeAttribute(String attrname,String type) {
			this.writeToArffFile("@Attribute ").writeToArffFile(attrname+" ")
			.writeToArffFile(type).writeNewLine();

			return this;
		}

		/**Writers the ratios in data along with the prediction*/
		public ArffWriter writeData(String prediction, double ... data) {
			if(!this.datastarted) {
				datastarted=true;
				writeToArffFile("@data");
				writeNewLine();

			}

			//write each ratio out
			for(double d:data) {
				writeToArffFile(d+",");

			}

			//now write the prediction
			writeToArffFile(prediction);
			writeNewLine();


			return this;
		}

		/**Output methods*/

		/**Writes the ratios in the current stack buffer out to the arff file*/
		public ArffWriter writeToArffFile(String output) {

			//try to write out
			try {
				writer.append(output);
			} catch (IOException e) {
				e.getCause();

			}

			return this;
		}

		/***Writer new line to arff file*/
		public ArffWriter writeNewLine() {
			try {
				writer.newLine();
			} catch (IOException e) {
				e.getCause();

			}

			return this;
		}



	}


	public static void main(String[] args) {
		Analyzer analyzer = new AnAnalyzer();
		AnAnalyzerProcessor.analyzer=analyzer;
		ArffGenerator arffGenerator = new AnArffGenerator(analyzer);
		analyzer.addAnalyzerListener(arffGenerator);
		OEFrame frame = ObjectEditor.edit(analyzer);
		frame.setSize(550, 200);

	}



}