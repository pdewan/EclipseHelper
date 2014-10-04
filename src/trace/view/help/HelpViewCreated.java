package trace.view.help;

import org.eclipse.swt.widgets.Composite;

import trace.view.ViewCreated;
import util.trace.Traceable;
import util.trace.TraceableInfo;
import util.trace.Tracer;


public class HelpViewCreated extends ViewCreated {
	Composite composite;
	public HelpViewCreated(String aMessage, Composite aComposite, Object aFinder) {
		super(aMessage, aComposite, aFinder);
	}
	public static HelpViewCreated newCase(String aMessage, Composite aComposite, Object aFinder) {
		if (shouldInstantiate(HelpViewCreated.class)) {
		HelpViewCreated retVal = new HelpViewCreated(aMessage, aComposite, aFinder);
		retVal.announce();
		return retVal;
		}
		Tracer.info(aFinder, aMessage);
		return null;
	}

	public static HelpViewCreated newCase(Composite aComposite, Object aFinder) {
		String aMessage = aComposite.toString();
		return newCase(aMessage, aComposite, aFinder);
	}

	
}
