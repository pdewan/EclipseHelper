package context.recording;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.widgets.Shell;

import util.pipe.ConsoleModel;
import util.remote.ProcessExecer;
import bus.uigen.misc.OEMisc;


public class ADisplayBoundsPiper extends AnAbstractDisplayBoundsOutputter implements  DisplayBoundsOutputter, PropertyChangeListener {
	// should really be specified in a config file
	public static final String RECORDER_JAVA_PATH = "D:/Program Files/Java/jdk1.7.0_51/bin/java";
	public static final String RECORDER_CLASS_PATH = "D:/dewan_backup/Java/eclipse/workspace/FileExample/bin";
	public static final String RECORDER_MAIN_CLASS = "InputAndOutput";
//	public static final String RECORDER_LAUNCHING_COMMAND = RECORDER_JAVA_PATH + 
//									" " + "-cp" + " " + RECORDER_CLASS_PATH +
//									" " + RECORDER_MAIN_CLASS;
	public static final String[] RECORDER_LAUNCHING_COMMAND = {RECORDER_JAVA_PATH, 
			"-cp" ,  RECORDER_CLASS_PATH,
			RECORDER_MAIN_CLASS};
//	Display display;
	ProcessExecer processExecer;
	ConsoleModel consoleModel;
//	public ADisplayBoundsPiper() {
//		display = Display.getCurrent();
//		display.addListener(SWT.RESIZE, this);
////		startRecorder(RECORDER_LAUNCHING_COMMAND);
////		listenToRecorderIOEvents();
//		
//	}	
//	@Override
//	public void connectToDisplayAndRecorder() {
//		listenToDisplayEvents();
//		connectToRecorder();
//	
//	}
//	@Override
	public void connectToRecorder() {
		startRecorder(RECORDER_LAUNCHING_COMMAND);		
		listenToRecorderIOEvents();
	}
//	@Override
//	public void listenToDisplayEvents() {
//		System.out.println("Shell " + display.getActiveShell());
////		display.getActiveShell().addListener(SWT.RESIZE, this);
//		Shell[] shells = display.getShells();
//		for (Shell shell:shells) {
//			
//			shell.addListener(SWT.RESIZE, this);
//			shell.addControlListener(this);
//			System.out.println("Shell " + shell);
//
//			System.out.println("Shell bounds " + shell.getBounds());
//		}
////		display.addListener(SWT.RESIZE, this);
//
//	}
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#startRecorder(java.lang.String)
	 */
	public void startRecorder() {
		startRecorder(RECORDER_LAUNCHING_COMMAND);

	}
//	@Override
	public void startRecorder(String[] aCommand) {		
		processExecer = OEMisc.runWithProcessExecer(aCommand);
		consoleModel = processExecer.getConsoleModel();
		
	}
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#listenToRecorderIOEvents()
	 */
//	@Override
	public void listenToRecorderIOEvents() {
		processExecer.consoleModel().addPropertyChangeListener(this);
	}
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#boundsToString()
	 */
//	@Override
//	public String boundsToString() {
//		if (display == null) return "";
//		Shell aShell = display.getActiveShell(); // can be null, dangerous!
//		if (aShell == null) return "";
//		return aShell.getBounds().toString();
//	}
//	
//	@Override
//	public String boundsToString(Shell aShell) {
//		
//		return aShell.getBounds().toString();
//	}
	
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#updateRecorder()
	 */
//	@Override
//	public void updateRecorder() {
//		System.out.println("Active shell:" + boundsToString());
//		if (processExecer != null)
//		processExecer.consoleModel().setInput(boundsToString());
//	}
	@Override
	public void updateRecorder(Shell aShell) {
		System.out.println("Updated shell:" + boundsToString(aShell));
		if (processExecer != null) {
		processExecer.consoleModel().setInput(boundsToString(aShell));
		}
	}
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#handleEvent(org.eclipse.swt.widgets.Event)
	 */
//	@Override
//	public void handleEvent(Event event) {
//	
//		updateRecorder((Shell) event.widget);
//		
//	}
	/* (non-Javadoc)
	 * @see context.recording.DisplayBoundsOutputter#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
	}
//	@Override
//	public void controlMoved(ControlEvent e) {
//		Shell aShell = (Shell)e.getSource();
////		System.out.println ("Changed shell " + boundsToString ((Shell)e.getSource()));
//		updateRecorder(aShell);
//		// TODO Auto-generated method stub
//		
//	}
//	@Override
//	public void controlResized(ControlEvent e) {
//		Shell aShell = (Shell)e.getSource();
//
//		System.out.println ("Changed shell " + aShell);
//		updateRecorder(aShell);
//
//
//		// TODO Auto-generated method stub
//		
//	}

	

}
