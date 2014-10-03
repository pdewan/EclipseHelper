package trace.logger;

import java.util.logging.FileHandler;

import util.trace.TraceableInfo;
import util.trace.Tracer;

public class LogHandlerBound extends TraceableInfo{
	FileHandler fileHandler;
	public LogHandlerBound(String aMessage, FileHandler aFileHandler,  Object aFinder) {
		 super(aMessage, aFinder);
		 fileHandler = aFileHandler;
	}
	public FileHandler getFileHandler() {
		return fileHandler;
	}
	
	
    public static String toString(FileHandler aFileHandler) {
    	return("(" + 
    				aFileHandler + 
    				")");
    }
    public static LogHandlerBound newCase (String aMessage, FileHandler aFileHandler,  Object aFinder) {
    	if (shouldInstantiate(LogHandlerBound.class)) {
    	LogHandlerBound retVal = new LogHandlerBound(aMessage, aFileHandler, aFinder);
    	retVal.announce();
    	return retVal;
    	}
		Tracer.info(aFinder, aMessage);

    	return null;
    }
    public static LogHandlerBound newCase (FileHandler aFileHandler,  Object aFinder) {
    	String aMessage = toString(aFileHandler);
    	return newCase(aMessage, aFileHandler, aFinder);
//    	ExcludedCommand retVal = new ExcludedCommand(aMessage, aFileHandler, aFinder);
//    	retVal.announce();
//    	return retVal;
    }
}
