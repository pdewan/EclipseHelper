package analyzer.tracker;

import java.util.Arrays;

/**A outputter wrapper that will only output results with a certain <b>ACTUAL</b> class, not predicted class. Ie. Only output those that are not stuck or those that are stuck
 * <br>
 * It delegates outputting duty to the input outputter, it just filters
 * @author wangk1
 *
 */
public class ClassFilterOutputter extends AbstractOutputter implements PredictionOutputter{
	private String outputClass;
	
	public ClassFilterOutputter(String class_to_output,PredictionOutputter...outputter) {
		super(Arrays.asList(outputter));
		this.outputClass=class_to_output;
		
	}

	
	@Override
	public void output(PredictedInstance i) {
		
		//delegate
		if(i.getInstance().classAttribute().value((int)i.getInstance().classValue()).equalsIgnoreCase(this.outputClass)) {
			for(PredictionOutputter p:this.getChildren())
				p.output(i);
			
		}
		
		
	}
	
	
}
