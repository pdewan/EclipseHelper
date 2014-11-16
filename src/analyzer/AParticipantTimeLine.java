package analyzer;

import java.util.ArrayList;
import java.util.List;

public class AParticipantTimeLine implements ParticipantTimeLine {
	private List<Double> insertionList = new ArrayList<Double>();
	private List<Double> deletionList = new ArrayList<Double>();
	private List<Double> debugList = new ArrayList<Double>();
	private List<Double> navigationList = new ArrayList<Double>();
	private List<Double> focusList = new ArrayList<Double>();
	private List<Double> removeList = new ArrayList<Double>();
	private List<Long> timeStampList = new ArrayList<Long>();
	protected List<Integer> predictionList = new ArrayList<Integer>();
	protected List<Integer> predictionCorrection = new ArrayList<Integer>();
	protected List<WebLink> webLinks = new ArrayList<WebLink>();
	public AParticipantTimeLine(List<Double> insertionList,
			List<Double> deletionList, List<Double> debugList,
			List<Double> navigationList, List<Double> focusList,
			List<Double> removeList, List<Long> timeStampList,
			List<Integer> predictionList, List<Integer> predictionCorrection,
			List<WebLink> webLinks) {
		super();
		this.insertionList = insertionList;
		this.deletionList = deletionList;
		this.debugList = debugList;
		this.navigationList = navigationList;
		this.focusList = focusList;
		this.removeList = removeList;
		this.timeStampList = timeStampList;
		this.predictionList = predictionList;
		this.predictionCorrection = predictionCorrection;
		this.webLinks = webLinks;
	}
	public AParticipantTimeLine() {
		this(new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList(), new ArrayList());
		
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getInsertionList()
	 */
	@Override
	public List<Double> getInsertionList() {
		return insertionList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setInsertionList(java.util.List)
	 */
	@Override
	public void setInsertionList(List<Double> insertionList) {
		this.insertionList = insertionList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getDeletionList()
	 */
	@Override
	public List<Double> getDeletionList() {
		return deletionList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setDeletionList(java.util.List)
	 */
	@Override
	public void setDeletionList(List<Double> deletionList) {
		this.deletionList = deletionList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getDebugList()
	 */
	@Override
	public List<Double> getDebugList() {
		return debugList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setDebugList(java.util.List)
	 */
	@Override
	public void setDebugList(List<Double> debugList) {
		this.debugList = debugList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getNavigationList()
	 */
	@Override
	public List<Double> getNavigationList() {
		return navigationList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setNavigationList(java.util.List)
	 */
	@Override
	public void setNavigationList(List<Double> navigationList) {
		this.navigationList = navigationList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getFocusList()
	 */
	@Override
	public List<Double> getFocusList() {
		return focusList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setFocusList(java.util.List)
	 */
	@Override
	public void setFocusList(List<Double> focusList) {
		this.focusList = focusList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getRemoveList()
	 */
	@Override
	public List<Double> getRemoveList() {
		return removeList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setRemoveList(java.util.List)
	 */
	@Override
	public void setRemoveList(List<Double> removeList) {
		this.removeList = removeList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getTimeStampList()
	 */
	@Override
	public List<Long> getTimeStampList() {
		return timeStampList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setTimeStampList(java.util.List)
	 */
	@Override
	public void setTimeStampList(List<Long> timeStampList) {
		this.timeStampList = timeStampList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getPredictionList()
	 */
	@Override
	public List<Integer> getPredictionList() {
		return predictionList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setPredictionList(java.util.List)
	 */
	@Override
	public void setPredictionList(List<Integer> predictionList) {
		this.predictionList = predictionList;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getPredictionCorrection()
	 */
	@Override
	public List<Integer> getPredictionCorrection() {
		return predictionCorrection;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setPredictionCorrection(java.util.List)
	 */
	@Override
	public void setPredictionCorrection(List<Integer> predictionCorrection) {
		this.predictionCorrection = predictionCorrection;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#getWebLinks()
	 */
	@Override
	public List<WebLink> getWebLinks() {
		return webLinks;
	}
	/* (non-Javadoc)
	 * @see analyzer.ParticipantTimeLine#setWebLinks(java.util.List)
	 */
	@Override
	public void setWebLinks(List<WebLink> webLinks) {
		this.webLinks = webLinks;
	}
	

	
	

}
