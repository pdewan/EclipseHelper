package analyzer.ui.template;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class AStatusBar extends JPanel implements StatusBar {

	private static final long serialVersionUID = 948158739933959785L;
	private List<Integer> predictedList = new ArrayList<Integer>();
	private List<Integer> actualList = new ArrayList<Integer>();
	private PlayAndRewindCounter counter;
	private RatioFileReader ratioFileReader;
	private static final int Y_BORDER_GAP = 10;
	private static final int X_BORDER_GAP = 65;
	private static final int SPACE_BETWEEN = 10;
	private static final Stroke GRAPH_STROKE = new BasicStroke(3f);

	public AStatusBar(PlayAndRewindCounter aCounter,
			RatioFileReader aRatioFileReader) {
		setBackground(Color.LIGHT_GRAY);
		addMouseListener(this);
		counter = aCounter;
		counter.addPropertyChangeListener(this);
		ratioFileReader = aRatioFileReader;
		ratioFileReader.addPropertyChangeListener(this);
	}

	public void paint(Graphics g) {
		super.paint(g); // clears the window
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		g2.drawString("Predicted", 5, getHeight() / 4);
		g2.drawString("Actual", 5, 2 * (getHeight() / 4));
		int xPos = 0;

		for (int i = counter.getStart(); i < counter.getEnd() - 1; i++) {
			if (i < predictedList.size() - 1 && i >= 0) {
				int x = (xPos * (getWidth() - X_BORDER_GAP * 2) / (10 - 1) + X_BORDER_GAP);
				int y = Y_BORDER_GAP;
				int width = (getWidth() - X_BORDER_GAP * 2) / 9;
				int height = getHeight() / 2 - Y_BORDER_GAP * 2 - SPACE_BETWEEN;
				if (predictedList.get(i) == 0) {
					// green
					g2.setColor(new Color(79, 191, 10));
				} else if (predictedList.get(i) == 1) {
					// light red
					g2.setColor(new Color(210, 149, 144));
				} else if (predictedList.get(i) == -1) {
					// orange
					g2.setColor(new Color(235, 172, 10));
				}
				g2.fillRect(x, y, width, height);
				xPos++;
			} else {
				break;
			}
		}
		xPos = 0;
		for (int i = counter.getStart(); i < counter.getEnd() - 1; i++) {
			if (i < actualList.size() - 1 && i >= 0) {
				int x = (xPos * (getWidth() - X_BORDER_GAP * 2) / (10 - 1) + X_BORDER_GAP);
				int y = Y_BORDER_GAP + (getHeight() / 2 - Y_BORDER_GAP * 2);
				int width = (getWidth() - X_BORDER_GAP * 2) / 9;
				int height = getHeight() / 2 - Y_BORDER_GAP * 2 - SPACE_BETWEEN;
				if (actualList.get(i) == -1) {
					// green
					g2.setColor(new Color(79, 191, 10));
				} else if (actualList.get(i) == 1) {
					// light red
					g2.setColor(new Color(210, 149, 144));
				} else if (actualList.get(i) == 2) {
					// dark red
					g2.setColor(new Color(201, 24, 10));
				} else {
					if (predictedList.get(i) == 0) {
						// green
						g2.setColor(new Color(79, 191, 10));
					} else if (predictedList.get(i) == 1) {
						// light red
						g2.setColor(new Color(210, 149, 144));
					} else if (predictedList.get(i) == -1) {
						// orange
						g2.setColor(new Color(235, 172, 10));
					}
				}
				g2.fillRect(x, y, width, height);
				xPos++;
			} else {
				break;
			}
		}

		// draw currentTimeLine
		g2.setStroke(GRAPH_STROKE);
		g2.setColor(Color.black);
		int x0 = (counter.getCurrentTime() - counter.getStart())
				* (getWidth() - X_BORDER_GAP * 2) / (10 - 1) + X_BORDER_GAP;
		int x1 = x0;
		int y0 = Y_BORDER_GAP;
		int y1 = Y_BORDER_GAP
				+ ((getHeight() / 2 - Y_BORDER_GAP * 2 - SPACE_BETWEEN) * 2)
				+ SPACE_BETWEEN;
		g2.drawLine(x0, y0, x1, y1);

	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equalsIgnoreCase("newRatioFeatures")) {
			newRatios((DuriRatioFeatures) evt.getNewValue());
			repaint();
		} else if (evt.getPropertyName().equalsIgnoreCase("start"))
			repaint();
	}

	public void mousePressed(MouseEvent event) {
		repaint();
	}

	public void mouseEntered(MouseEvent event) {
	}

	public void mouseExited(MouseEvent event) {
	}

	public void mouseReleased(MouseEvent event) {
	}

	public void keyPressed(KeyEvent event) {
	}

	public void keyReleased(KeyEvent event) {
	}

	public void mouseClicked(MouseEvent event) {
	}

	@Override
	public void newRatios(DuriRatioFeatures ratioFeatures) {
		predictedList.add(ratioFeatures.getPredictedStatus());
		actualList.add(ratioFeatures.getActualStatus());
	}

	public void setData(List<Integer> newPredictedList,
			List<Integer> newActualList) {
		predictedList = newPredictedList;
		actualList = newActualList;
		repaint();
	}
}
