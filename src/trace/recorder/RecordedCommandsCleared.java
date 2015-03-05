package trace.recorder;

import java.util.LinkedList;

import edu.cmu.scs.fluorite.commands.ICommand;
import edu.cmu.scs.fluorite.util.EventLoggerConsole;
import trace.plugin.PluginStopped;
import util.trace.TraceableInfo;
import util.trace.Tracer;

public class RecordedCommandsCleared extends TraceableInfo{
	LinkedList<ICommand> commands;
	public RecordedCommandsCleared(String aMessage, LinkedList<ICommand> aCommands,  Object aFinder) {
		 super(aMessage, aFinder);
		 commands = aCommands;
	}
	public LinkedList<ICommand> getCommands() {
		return commands;
	}
	
	
    public static String toString(LinkedList<ICommand> aCommands) {
    	return("(" + 
//    				aCommands.toString() + 
    			"commands" + // do not read the list as it may mutate
    				")");
    }
    public static RecordedCommandsCleared newCase (String aMessage, LinkedList<ICommand> aCommands,  Object aFinder) {
    	if (Tracer.isPrintInfoEnabled(aFinder) || Tracer.isPrintInfoEnabled(RecordedCommandsCleared.class))
      	  EventLoggerConsole.getConsole().getMessageConsoleStream().println(Tracer.infoPrintBody(RecordedCommandsCleared.class) + ") " + aMessage);
    	if (shouldInstantiate(RecordedCommandsCleared.class)) {
    	RecordedCommandsCleared retVal = new RecordedCommandsCleared(aMessage, aCommands, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);
		Tracer.info(RecordedCommandsCleared.class, aMessage);


    	return null;
    }
    public static RecordedCommandsCleared newCase (LinkedList<ICommand> aCommands,  Object aFinder) {
    	String aMessage = toString(aCommands);
    	return newCase(aMessage, aCommands, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aCommands, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
