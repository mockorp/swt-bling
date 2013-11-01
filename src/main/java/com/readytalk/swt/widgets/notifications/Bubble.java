package com.readytalk.swt.widgets.notifications;

import com.readytalk.swt.helpers.AncestryHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public class Bubble extends Widget {
  private static final RGB BACKGROUND_COLOR = new RGB(74, 74, 74);
  private static final RGB TEXT_COLOR = new RGB(204, 204, 204);
  private static final int TEXT_HEIGHT_PADDING = 5; //pixels
  private static final int TEXT_WIDTH_PADDING = 10; //pixels
  private static final int TRIANGLE_PADDING_FROM_COMPONENT = 10; //pixels
  private static final int TRIANGLE_SIDE = 10; //pixels
  private static final int TRIANGLE_HEIGHT = (int) Math.round(Math.sqrt(3) / 2 * TRIANGLE_SIDE); //pixels (equilateral triangle)

  private Listener listener;
  private String tooltipText;
  private Control parent;
  private Shell parentShell, tooltip;

  private Rectangle rectangle;
  private Color textColor;

  public Bubble(Control parent, String tooltipText) {
    super(parent, SWT.NONE);

    this.parent = parent;
    this.tooltipText = tooltipText;
    parentShell = AncestryHelper.getShellFromControl(parent);

    tooltip = new Shell(parentShell, SWT.ON_TOP | SWT.NO_TRIM);
    tooltip.setBackground(new Color(getDisplay(), BACKGROUND_COLOR)); // TODO: we need to manage our colors onDispose
    textColor = new Color(getDisplay(), TEXT_COLOR);

    listener = new Listener() {
      public void handleEvent(Event event) {
        switch (event.type) {
          case SWT.Dispose:
            onDispose(event);
            break;
          case SWT.Paint:
            onPaint(event);
            break;
          case SWT.MouseDown:
            onMouseDown(event);
            break;
        }
      }
    };
    addListener(SWT.Dispose, listener);
    tooltip.addListener(SWT.Paint, listener);
    tooltip.addListener(SWT.MouseDown, listener);
  }

  public void show() {
    Region toolTipRegion = new Region();
    Point location = parentShell.getDisplay().map(parentShell, null, parent.getLocation());
    Point textExtent = getTextSize(tooltipText);

    rectangle = calculateRectangleRegion(parent.getSize(), textExtent);
    int[] triangle = calculateTriangleRegion(rectangle);

    toolTipRegion.add(rectangle);
    toolTipRegion.add(triangle);

    tooltip.setRegion(toolTipRegion);
    tooltip.setLocation(location);
    tooltip.setVisible(true);
  }

  public void hide() {
    tooltip.setVisible(false);
  }

  private Rectangle calculateRectangleRegion(Point componentSize, Point textExtent) {
    return new Rectangle(componentSize.x / 2, componentSize.y,
            textExtent.x + TEXT_WIDTH_PADDING,
            textExtent.y + TEXT_HEIGHT_PADDING);
  }

  private int[] calculateTriangleRegion(Rectangle tooltipRectangle) {
    int[] triangle = {
      tooltipRectangle.x + TRIANGLE_PADDING_FROM_COMPONENT, tooltipRectangle.y,
      tooltipRectangle.x + TRIANGLE_PADDING_FROM_COMPONENT + (TRIANGLE_SIDE / 2), tooltipRectangle.y - TRIANGLE_HEIGHT,
      tooltipRectangle.x + TRIANGLE_PADDING_FROM_COMPONENT + TRIANGLE_SIDE, tooltipRectangle.y
    };

    return triangle;
  }

  public void checkSubclass() {
    //no-op
  }

  private void onDispose(Event event) {
    // TODO: dispose all the things here
  }

  private void onPaint(Event event) {
    GC gc = event.gc;

    gc.setForeground(textColor);
    gc.drawText(tooltipText, rectangle.x + (TEXT_WIDTH_PADDING / 2), rectangle.y + (TEXT_HEIGHT_PADDING / 2));
  }

  private void onMouseDown(Event event) {
    // TODO: dismiss the tooltip if they click it
  }

  private Point getTextSize(String text) {
    GC gc = new GC(getDisplay());
    Point textExtent = gc.textExtent(text);
    gc.dispose();
    return textExtent;
  }
}