package difficultyPrediction.predictionManagement;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPluginDescriptor;

import config.HelperConfigurationManagerFactory;
import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import difficultyPrediction.APredictionParameters;
import difficultyPrediction.DifficultyPredictionSettings;
import fluorite.plugin.Activator;

public class DecisionTreeModel implements PredictionManagerStrategy {
	public PredictionManager predictionManager;

//	private J48 j48Model = new J48();

//	private boolean isClassifierModelBuilt = false;
	protected Map<Classifier, Boolean> classifierBuilt = new HashMap();

	private String wekaDataFileLocation = "data/userStudy2010.arff";


	public DecisionTreeModel(PredictionManager predictionManager) {
		this.predictionManager = predictionManager;
	}
	
	protected String wekaDataFileLocation() {
		String aSpecifiedLocation = HelperConfigurationManagerFactory.getSingleton().getARFFFileName();
//		return aSpecifiedLocation == null?wekaDataFileLocation:aSpecifiedLocation;
//		return APredictionParameters.getInstance().getARFFFileName().getText();
		return APredictionParameters.getInstance().getClassificationParameters().getARFFFileName();

	}
	
	protected Classifier getClassifier() {
		return APredictionParameters.getInstance().getClassificationParameters().getClassifierSpecification().toClassifier();
//		return DifficultyPredictionSettings.getClassifierSpecification().toClassifier();
//		return j48Model;
	}

	private void buildClassifierModel() {
		weka.core.Instances trainingSet;
		URL url;

		try {
			//platform:/plugin/
			InputStream inputStream;
			if (DifficultyPredictionSettings.isReplayMode()) {
				inputStream = new FileInputStream( wekaDataFileLocation());
			} else {
//				Activator anActivator = fluorite.plugin.Activator.getDefault();
////				URL anInstallURL = null;
////				IPluginDescriptor aDescriptor = anActivator.getDescriptor();
////				if (aDescriptor != null) {
////					anInstallURL = aDescriptor.getInstallURL();
////				} else {
////					anInstallURL = anActivator.getBundle().getEntry("/");
////				}
//			URL anInstallURL = anActivator.getBundle().getEntry("/");	
//			url = new URL(anInstallURL, wekaDataFileLocation());
//				
////			url = new URL(fluorite.plugin.Activator.getDefault()
////					.getDescriptor().getInstallURL(), wekaDataFileLocation());
////			
//
////			InputStream inputStream = url.openConnection().getInputStream();
			url = new URL(Activator.getInstallURL(), wekaDataFileLocation());

			inputStream = url.openConnection().getInputStream();
			}


			trainingSet = new weka.core.Instances(new BufferedReader(
					new InputStreamReader(inputStream)));
			trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
			getClassifier().buildClassifier(trainingSet);
			classifierBuilt.put(getClassifier(), true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	protected boolean isClassifierBuilt() {
		Boolean isBuilt = classifierBuilt.get(getClassifier());
		return isBuilt != null && isBuilt;
//		return true == isBuilt; // can be null
	}

	public void predictSituation(double editOrInsertRatio, double debugRatio,
			double navigationRatio, double focusRatio, double deleteRatio) {
//		String predictedValue = "NO";
		String predictedValue = PROGRESS_PREDICTION;
		try {
			// Declare five numeric attributes
			weka.core.Attribute searchPercentageAttribute = new weka.core.Attribute(
					"searchPercentage");
			weka.core.Attribute debugPercentageAttribute = new weka.core.Attribute(
					"debugPercentage");
			weka.core.Attribute focusPercentageAttribute = new weka.core.Attribute(
					"focusPercentage");
			weka.core.Attribute editPercentageAttribute = new weka.core.Attribute(
					"editPercentage");
			weka.core.Attribute removePercentageAttribute = new weka.core.Attribute(
					"removePercentage");

			// Declare the class attribute along with its values
			weka.core.FastVector fvClassVal = new weka.core.FastVector(2);
//			fvClassVal.addElement("YES");
//			fvClassVal.addElement("NO");
			fvClassVal.addElement(DIFFICULTY_PREDICTION);
			fvClassVal.addElement(PROGRESS_PREDICTION);
			weka.core.Attribute ClassAttribute = new weka.core.Attribute(
					"STUCK", fvClassVal);

			// Declare the feature vector
			// should be 6
			weka.core.FastVector fvWekaAttributes = new weka.core.FastVector(4);
			fvWekaAttributes.addElement(searchPercentageAttribute);
			fvWekaAttributes.addElement(debugPercentageAttribute);
			fvWekaAttributes.addElement(focusPercentageAttribute);
			fvWekaAttributes.addElement(editPercentageAttribute);
			fvWekaAttributes.addElement(removePercentageAttribute);
			fvWekaAttributes.addElement(ClassAttribute);

			// Create an empty training set
			weka.core.Instances testingSet = new weka.core.Instances("Rel",
					fvWekaAttributes, 10);

			// Set class index
			testingSet.setClassIndex(5);

			// Create the instance
			weka.core.Instance iExample = new weka.core.Instance(5);

			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(0),
					navigationRatio);
			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(1),
					debugRatio);
			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(2),
					focusRatio);
			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(3),
					editOrInsertRatio);
			iExample.setValue(
					(weka.core.Attribute) fvWekaAttributes.elementAt(4),
					deleteRatio);

			// add the instance
			testingSet.add(iExample);

			if (!isClassifierBuilt()) {
				long startTime = System.currentTimeMillis();
				buildClassifierModel();
//				isClassifierModelBuilt = true;
				System.out.println(" Built  model in m:" + (System.currentTimeMillis() - startTime));

				
			}

			double predictedClass = getClassifier().classifyInstance(testingSet
					.instance(0));
			predictedValue = testingSet.classAttribute().value(
					(int) predictedClass);
//			System.out.println("Predicted Value: " + predictedValue);
		} catch (Exception e) {
			e.printStackTrace();
		}

		predictionManager.onPredictionHandOff(predictedValue);
	}
}
