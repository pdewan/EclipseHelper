package fluorite.recorders;

import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.source.ISourceViewerExtension3;
import org.eclipse.jface.text.source.ISourceViewerExtension4;
import org.eclipse.ui.IEditorPart;

import fluorite.commands.EHAssistCommand;
import fluorite.util.Utilities;

public class EHCompletionRecorder extends EHBaseRecorder implements
		ICompletionListener {

	private static EHCompletionRecorder instance = null;

	public static EHCompletionRecorder getInstance() {
		if (instance == null) {
			instance = new EHCompletionRecorder();
		}

		return instance;
	}

	private EHCompletionRecorder() {
		super();
	}

	@Override
	public void addListeners(IEditorPart editor) {
		ISourceViewerExtension3 sourceViewerExtension3 = Utilities
				.getSourceViewerExtension3(editor);
		if (sourceViewerExtension3 != null) {
			if (sourceViewerExtension3.getQuickAssistAssistant() != null) {
				sourceViewerExtension3.getQuickAssistAssistant()
						.addCompletionListener(this);
			}
		}

		ISourceViewerExtension4 sourceViewerExtension4 = Utilities
				.getSourceViewerExtension4(editor);
		if (sourceViewerExtension4 != null) {
			if (sourceViewerExtension4.getContentAssistantFacade() != null) {
				sourceViewerExtension4.getContentAssistantFacade()
						.addCompletionListener(this);
			}
		}
	}

	@Override
	public void removeListeners(IEditorPart editor) {
		try {
			ISourceViewerExtension3 sourceViewerExtension3 = Utilities
					.getSourceViewerExtension3(getRecorder().getEditor());
			if (sourceViewerExtension3 != null) {
				if (sourceViewerExtension3.getQuickAssistAssistant() != null) {
					sourceViewerExtension3.getQuickAssistAssistant()
							.removeCompletionListener(this);
				}
			}

			ISourceViewerExtension4 sourceViewerExtension4 = Utilities
					.getSourceViewerExtension4(getRecorder().getEditor());
			if (sourceViewerExtension4 != null) {
				if (sourceViewerExtension4.getContentAssistantFacade() != null) {
					sourceViewerExtension4.getContentAssistantFacade()
							.removeCompletionListener(this);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void assistSessionStarted(ContentAssistEvent event) {
		getRecorder().setAssistSession(true);

		if (event.assistant == null) {
			return;
		}

		EHAssistCommand.AssistType assistType = (event.assistant.getClass()
				.getCanonicalName().indexOf("QuickAssist") != -1) ? EHAssistCommand.AssistType.QUICK_ASSIST
				: EHAssistCommand.AssistType.CONTENT_ASSIST;

		getRecorder().recordCommand(
				new EHAssistCommand(assistType, EHAssistCommand.StartEndType.START,
						event.isAutoActivated, null));
	}

	public void assistSessionEnded(ContentAssistEvent event) {
		getRecorder().setAssistSession(false);

		if (event.assistant == null) {
			return;
		}

		EHAssistCommand.AssistType assistType = (event.assistant.getClass()
				.getCanonicalName().indexOf("QuickAssist") != -1) ? EHAssistCommand.AssistType.QUICK_ASSIST
				: EHAssistCommand.AssistType.CONTENT_ASSIST;

		getRecorder().recordCommand(
				new EHAssistCommand(assistType, EHAssistCommand.StartEndType.END,
						false, null));
	}

	public void selectionChanged(ICompletionProposal proposal,
			boolean smartToggle) {
		// TODO Auto-generated method stub

	}

}
