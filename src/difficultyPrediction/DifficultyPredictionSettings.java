package difficultyPrediction;

public class DifficultyPredictionSettings {
	static String ratiosFileName;
	static boolean ratioFileExists;
	static boolean replayMode;
	static boolean newRatioFiles;
	static int segmentLength = 50;

	public DifficultyPredictionSettings() {
	}

	public static boolean isReplayMode() {
		return replayMode;
	}

	public static void setReplayMode(boolean replayMode) {
		DifficultyPredictionSettings.replayMode = replayMode;
	}

	public static int getSegmentLength() {
		return segmentLength;
	}

	public static void setSegmentLength(int segmentLength) {
		DifficultyPredictionSettings.segmentLength = segmentLength;
	}

	public static String getRatiosFileName() {
		return ratiosFileName;
	}

	public static void setRatiosFileName(String ratiosFileName) {
		DifficultyPredictionSettings.ratiosFileName = ratiosFileName;
	}

	public static boolean isRatioFileExists() {
		return ratioFileExists;
	}

	public static void setRatioFileExists(boolean ratioFileExists) {
		DifficultyPredictionSettings.ratioFileExists = ratioFileExists;
	}

	public static boolean isNewRatioFiles() {
		return newRatioFiles;
	}

	public static void setNewRatioFiles(boolean newRatioFiles) {
		DifficultyPredictionSettings.newRatioFiles = newRatioFiles;
	}
	
	

}
