package lb.edu.aust.cce577.ife;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created on 7/17/2021
 *
 * @author Sara Jarekji, swj00001@students.aust.edu.lb
 */
public class InvoiceEditorPanel extends JPanel {

	private Dimension originalImageDimension;
	private Dimension scaledImageDimension;
	private double scaleFactorX, scaleFactorY;
	private double zoomLevel;

	private BufferedImage bufferedImage;
	private Image scaledImage;

	private Point mousePressedStartPoint;

	private final List<RealRectangle> rectangleList;
	private RealRectangle currentRectangle;

	private boolean button3Drag;
	private int offsetX;
	private int offsetY;
	private int moveX;
	private int moveY;

	private InvoiceEditorInteractionListener invoiceEditorInteractionListener;
	private final Point mousePoint;
	private File imageFile;

	public InvoiceEditorPanel() {

		this.mousePoint = new Point(0, 0);
		this.zoomLevel = 1;
		this.scaleFactorX = 1;
		this.scaleFactorY = 1;
		this.rectangleList = new ArrayList<>();


		this.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {

				{
					double x = scaleX(e.getX() - moveX);
					double y = scaleY(e.getY() - moveY);
					mousePoint.setLocation(x, y);
				}

				if (currentRectangle == null && button3Drag) {

					moveX -= offsetX - e.getX();
					moveY -= offsetY - e.getY();
					offsetX = e.getX();
					offsetY = e.getY();
				} else if (currentRectangle != null) {
					int width = (int) Math.abs((e.getX()) - (mousePressedStartPoint.getX()));
					int height = (int) Math.abs((e.getY()) - (mousePressedStartPoint.getY()));


					int x = (int) Math.min(e.getX(), mousePressedStartPoint.getX());
					int y = (int) Math.min(e.getY(), mousePressedStartPoint.getY());
					currentRectangle.setBounds(x, y, width, height);
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {

				double x = scaleX(e.getX() - moveX);
				double y = scaleY(e.getY() - moveY);
				mousePoint.setLocation(x, y);
			}
		});

		this.addMouseWheelListener(e -> {

			if (mousePressedStartPoint != null) {
				return;
			}

			zoomLevel += -1 * e.getWheelRotation() / 3.0;

			if (zoomLevel <= 0.3) {
				zoomLevel = 1;
			}

			System.out.println(zoomLevel);
		});

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON3) {
					moveX = 0;
					moveY = 0;
				} else if (e.getButton() == MouseEvent.BUTTON2) {
					zoomLevel = 1;
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON1) {

					if (!isPointValid(e.getPoint())) {
						return;
					}

					mousePressedStartPoint = e.getPoint();
					currentRectangle = new RealRectangle(e.getX(), e.getY(), 0, 0);
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					offsetX = e.getX();
					offsetY = e.getY();
					button3Drag = true;
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {

				if (e.getButton() == MouseEvent.BUTTON1) {

					if (mousePressedStartPoint == null) {
						return;
					}

					if (!isPointValid(e.getPoint())) {
						currentRectangle = null;
						mousePressedStartPoint = null;
						return;
					}

					int x = (int) (currentRectangle.getX());
					int y = (int) (currentRectangle.getY());
					int width = (int) currentRectangle.getWidth();
					int height = (int) currentRectangle.getHeight();

					final RealRectangle realRectangle = new RealRectangle(x, y, width, height);
					rectangleList.add(realRectangle);
					System.out.println(realRectangle);

					if (invoiceEditorInteractionListener != null) {

						invoiceEditorInteractionListener.onBoundingBoxDrawn(toBoundingBox(realRectangle));
					}

					mousePressedStartPoint = null;
					currentRectangle = null;
				} else if (e.getButton() == MouseEvent.BUTTON3) {
					button3Drag = false;

					offsetX = 0;
					offsetY = 0;
				}
			}
		});
	}

	private Feature.BoundingBox toBoundingBox(RealRectangle realRectangle) {

		final Feature.BoundingBox boundingBox = new Feature.BoundingBox(realRectangle.id);
		boundingBox.setX(realRectangle.getActualX());
		boundingBox.setY(realRectangle.getActualY());
		boundingBox.setWidth(realRectangle.getActualWidth());
		boundingBox.setHeight(realRectangle.getActualHeight());
		return boundingBox;
	}

	public void removeBoundingBox(Feature.BoundingBox boundingBox) {

		rectangleList.removeIf(realRectangle -> realRectangle.id.equals(boundingBox.getId()));

	}

	public void load(File file) throws Exception {

		if (!file.exists() || !file.isFile()) {
			throw new InvalidParameterException(file.getAbsolutePath() + " is not valid or doesn't exist");
		}

		this.zoomLevel = 1;
		this.scaleFactorX = 1;
		this.scaleFactorY = 1;
		this.button3Drag = false;
		this.offsetX = 0;
		this.offsetY = 0;
		this.moveX = 0;
		this.moveY = 0;

		this.imageFile = file;
		this.rectangleList.clear();
		this.bufferedImage = ImageIO.read(file);
		this.originalImageDimension = new Dimension(bufferedImage.getWidth(), bufferedImage.getHeight());

		refreshImage(true);
	}

	public void reset() {

		this.zoomLevel = 1;
		this.scaleFactorX = 1;
		this.scaleFactorY = 1;
		this.button3Drag = false;
		this.offsetX = 0;
		this.offsetY = 0;
		this.moveX = 0;
		this.moveY = 0;
		this.rectangleList.clear();
		this.imageFile = null;
		this.bufferedImage = null;
		this.originalImageDimension = null;
		this.scaledImage = null;
		this.scaledImageDimension = null;

		refreshImage(true);
	}

	public boolean isImageLoaded() {

		return this.imageFile != null && this.bufferedImage != null;
	}

	public void save() throws Exception {

		final BufferedImage tempBufferedImage = ImageIO.read(this.imageFile);
		final Graphics2D graphics = (Graphics2D) tempBufferedImage.getGraphics();
		graphics.setStroke(new BasicStroke(2));
		graphics.setColor(Color.RED);

		List<Feature.BoundingBox> boundingBoxes = new ArrayList<>(rectangleList.size());

		for (RealRectangle realRectangle : rectangleList) {

			graphics.drawRect((int) realRectangle.getActualX(), (int) realRectangle.getActualY(),
					(int) realRectangle.getActualWidth(), (int) realRectangle.getActualHeight());

			boundingBoxes.add(toBoundingBox(realRectangle));

		}

		if (this.invoiceEditorInteractionListener != null) {
			this.invoiceEditorInteractionListener.onSave(this.imageFile, tempBufferedImage, boundingBoxes);
		}
	}

	public boolean testLoad(File file) {

		if (file.exists() && file.isFile()) {
			try {
				return ImageIO.read(file) != null;
			} catch (IOException e) {
				return false;
			}
		}

		return false;
	}

	private double scaleX(double x) {

		return scaleFactorX * x;
	}

	private double scaleY(double y) {

		return scaleFactorY * y;
	}

	private double unscaleX(double scaledX) {

		return scaledX / scaleFactorX;
	}

	private double unscaleY(double scaledY) {

		return scaledY / scaleFactorY;
	}

	private boolean isPointValid(Point e) {

		return scaledImage != null && !(e.getX() < moveX || e.getY() < moveY
				|| e.getX() > moveX + scaledImage.getWidth(InvoiceEditorPanel.this)
				|| e.getY() > moveY + scaledImage.getHeight(InvoiceEditorPanel.this));
	}

	private void refreshImage(boolean force) {

		if (bufferedImage == null || originalImageDimension == null) {
			return;
		}

		final Container parent = this.getParent();

		final Dimension newScaledImageDimension = getScaledDimension(originalImageDimension, new Dimension((int) (parent.getWidth() * zoomLevel), (int) (parent.getHeight() * zoomLevel)));

		if (newScaledImageDimension.equals(scaledImageDimension) &&  !force) {
			return;
		}

		if (newScaledImageDimension.getWidth() <= 0 || newScaledImageDimension.getHeight() <= 0) {
			return;
		}

		scaledImageDimension = newScaledImageDimension;
		scaleFactorX = bufferedImage.getWidth() / scaledImageDimension.getWidth();
		scaleFactorY = bufferedImage.getHeight() / scaledImageDimension.getHeight();


		scaledImage = bufferedImage.getScaledInstance((int) scaledImageDimension.getWidth(), (int) scaledImageDimension.getHeight(), Image.SCALE_FAST);


		System.out.println("Scale: " + scaleFactorX + ", " + scaleFactorY);
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);

		Graphics2D graphics2D = ((Graphics2D) g);

		refreshImage(false);
		graphics2D.drawImage(scaledImage, moveX, moveY, this);


		graphics2D.setColor(Color.RED);
		graphics2D.setStroke(new BasicStroke(2));


		for (RealRectangle rectangle : rectangleList) {

			graphics2D.drawRect((int) rectangle.getRelativeX(), (int) rectangle.getRelativeY(), (int) rectangle.getRelativeWidth(), (int) rectangle.getRelativeHeight());
		}

		if (currentRectangle != null) {
			graphics2D.drawRect((int) currentRectangle.getRelativeX(), (int) currentRectangle.getRelativeY(), (int) currentRectangle.getRelativeWidth(), (int) currentRectangle.getRelativeHeight());
		}

		graphics2D.setColor(Color.GREEN);
		String mouseLocation = String.format("%.0f, %.0f", mousePoint.getX(), mousePoint.getY());

		graphics2D.drawString(mouseLocation, 0, 10);

		if (imageFile != null) {
			graphics2D.drawString(imageFile.getAbsolutePath(), 0, 30);
		}

		repaint();
	}

	public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {

		int original_width = imgSize.width;
		int original_height = imgSize.height;
		int bound_width = boundary.width;
		int bound_height = boundary.height;
		int new_width = original_width;
		int new_height = original_height;

		// first check if we need to scale width
		if (original_width > bound_width) {
			//scale width to fit
			new_width = bound_width;
			//scale height to maintain aspect ratio
			new_height = (new_width * original_height) / original_width;
		}

		// then check if we need to scale even with the new height
		if (new_height > bound_height) {
			//scale height to fit instead
			new_height = bound_height;
			//scale width to maintain aspect ratio
			new_width = (new_height * original_width) / original_height;
		}

		return new Dimension(new_width, new_height);
	}

	public void setBoundingBoxChangeListener(InvoiceEditorInteractionListener invoiceEditorInteractionListener) {

		this.invoiceEditorInteractionListener = invoiceEditorInteractionListener;
	}

	public void removeAllBoundingBoxes() {

		synchronized (this.rectangleList) {
			this.rectangleList.clear();
		}
	}

	private class RealRectangle extends Rectangle {

		protected final UUID id;
		protected double actualX;
		protected double actualY;
		protected double actualWidth;
		protected double actualHeight;

		public RealRectangle(int x, int y, int width, int height) {

			super(x, y, width, height);

			this.id = UUID.randomUUID();
			this.actualX = scaleX(x - moveX);
			this.actualY = scaleY(y - moveY);
			this.actualWidth = scaleX(width);
			this.actualHeight = scaleY(height);
		}

		public double getActualX() {

			return actualX;
		}

		public void setActualX(double actualX) {

			this.actualX = actualX;
		}

		public double getActualY() {

			return actualY;
		}

		public void setActualY(double actualY) {

			this.actualY = actualY;
		}

		public double getActualWidth() {

			return actualWidth;
		}

		public void setActualWidth(double actualWidth) {

			this.actualWidth = actualWidth;
		}

		public double getActualHeight() {

			return actualHeight;
		}

		public void setActualHeight(double actualHeight) {

			this.actualHeight = actualHeight;
		}

		public double getRelativeX() {

			return unscaleX(actualX) + moveX;
		}

		public double getRelativeY() {

			return unscaleY(actualY) + moveY;
		}

		public double getRelativeWidth() {

			return unscaleX(actualWidth);
		}

		public double getRelativeHeight() {

			return unscaleY(actualHeight);
		}

		@Override
		public void setBounds(int x, int y, int width, int height) {

			super.setBounds(x, y, width, height);
			this.actualX = scaleX(x - moveX);
			this.actualY = scaleY(y - moveY);
			this.actualWidth = scaleX(width);
			this.actualHeight = scaleY(height);
		}

		@Override
		public String toString() {

			return "RealRectangle{" +
					"id=" + id +
					", actualX=" + actualX +
					", actualY=" + actualY +
					", actualWidth=" + actualWidth +
					", actualHeight=" + actualHeight +
					", x=" + x +
					", y=" + y +
					", width=" + width +
					", height=" + height +
					'}';
		}
	}
}
