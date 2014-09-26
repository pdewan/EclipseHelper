package edu.cmu.scs.fluorite.plugin;

import java.lang.reflect.Field;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.browser.LocationListener;
import org.eclipse.ui.IPageListener;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.browser.BrowserViewer;
import org.eclipse.ui.internal.browser.WebBrowserEditor;
import org.eclipse.ui.internal.browser.WebBrowserView;

public class EventLoggerStartup implements IStartup {

	public void earlyStartup() {
		final IPartListener partListener = new IPartListener() {
		    @Override
		    public void partOpened(IWorkbenchPart part) {
		        if (part instanceof WebBrowserEditor)
		        {
		            WebBrowserEditor editor = (WebBrowserEditor) part;

		            try {
		                Field webBrowser = editor.getClass().getDeclaredField("webBrowser");
		                webBrowser.setAccessible(true);
		                BrowserViewer viewer = (BrowserViewer)webBrowser.get(editor);

		                Field browser = viewer.getClass().getDeclaredField("browser");
		                browser.setAccessible(true);
		                Browser swtBrowser = (Browser) browser.get(viewer);

		                swtBrowser.addLocationListener(new LocationListener() {
		                    @Override
		                    public void changed(LocationEvent event) {
		                        System.out.println(event.location);
		                    }

							@Override
							public void changing(LocationEvent event) {
								// TODO Auto-generated method stub
								
							}
		                });
		            } catch (Exception e) {
		            }
		        }
		        else if (part instanceof WebBrowserView)
		        {
		            WebBrowserView view = (WebBrowserView) part;

		            try {
		                Field webBrowser = view.getClass().getDeclaredField("viewer");
//		                Field webBrowser = editor.getClass().getDeclaredField("viewer");

		                webBrowser.setAccessible(true);
		                BrowserViewer viewer = (BrowserViewer)webBrowser.get(view);

		                Field browser = viewer.getClass().getDeclaredField("browser");
		                browser.setAccessible(true);
		                Browser swtBrowser = (Browser) browser.get(viewer);

		                swtBrowser.addLocationListener(new LocationListener() {
		                    @Override
		                    public void changed(LocationEvent event) {
		                        System.out.println(event.location);
		                    }

							@Override
							public void changing(LocationEvent event) {
								// TODO Auto-generated method stub
								
							}
		                });
		            } catch (Exception e) {
		            }
		        }
		    }
//		    ...

			@Override
			public void partActivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void partBroughtToTop(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void partClosed(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void partDeactivated(IWorkbenchPart part) {
				// TODO Auto-generated method stub
				
			}
		};

		final IPageListener pageListener = new IPageListener() {
		    @Override
		    public void pageOpened(IWorkbenchPage page) {
		        page.addPartListener(partListener);
		    }
//		    ...

			@Override
			public void pageActivated(IWorkbenchPage page) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void pageClosed(IWorkbenchPage page) {
				// TODO Auto-generated method stub
				
			}
		};

		final IWindowListener windowListener = new IWindowListener() {
		    @Override
		    public void windowOpened(IWorkbenchWindow window) {
		        window.addPageListener(pageListener);
		    }
//		    ...

			@Override
			public void windowActivated(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeactivated(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(IWorkbenchWindow window) {
				// TODO Auto-generated method stub
				
			}
		};


		IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		if (activeWindow != null)
		{
		    IWorkbenchPage activePage = activeWindow.getActivePage();

		    if (activePage != null)
		    {
		        activePage.addPartListener(partListener);
		    }
		    else
		    {
		        activeWindow.addPageListener(pageListener);
		    }
		}
		else
		{
		    for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
		    {
		        for (IWorkbenchPage page : window.getPages()) {
		            page.addPartListener(partListener);
		        }
		        window.addPageListener(pageListener);
		    }

		    PlatformUI.getWorkbench().addWindowListener(windowListener);
		}       
		// TODO Auto-generated method stub
	}
}
