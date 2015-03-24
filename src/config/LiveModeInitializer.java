package config;

import analyzer.ui.video.LocalScreenRecorderAndPlayerFactory;
import difficultyPrediction.ATestPredictionParametersSetter;
import difficultyPrediction.PredictionParametersSetterSelector;
import difficultyPrediction.featureExtraction.ARatioFeaturesFactory;
import difficultyPrediction.featureExtraction.RatioFeaturesFactorySelector;
import difficultyPrediction.metrics.ATestRatioCalculatorFactory;
import difficultyPrediction.metrics.RatioCalculatorSelector;

public class LiveModeInitializer {
	public static void configure() {
		RatioFeaturesFactorySelector.setFactory(new ARatioFeaturesFactory());
		RatioCalculatorSelector.setFactory(new ATestRatioCalculatorFactory());
//		RatioCalculatorSelector.setFactory(new ARatioCalculatorFactory());
//		PredictionParametersSetterSelector.setSingleton(new APredictionParametersSetter());
		PredictionParametersSetterSelector.setSingleton(new ATestPredictionParametersSetter());
//		RecorderFactory.createSingleton();
		LocalScreenRecorderAndPlayerFactory.createSingleton(); // subsumes RecorderFactory
//		SarosAccessorFactory.createSingleton();
		

	}

}
