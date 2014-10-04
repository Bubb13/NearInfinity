// Near Infinity - An Infinity Engine Browser and Editor
// Copyright (C) 2001 - 2005 Jon Olav Hauglid
// See LICENSE.txt for license information

package infinity.gui.converter;

import infinity.gui.ViewerUtil;
import infinity.resource.graphics.PseudoBamDecoder.PseudoBamFrameEntry;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

/**
 * Transform filter: mirrors BAM frames horizontally or vertically.
 * @author argent77
 */
public class BamFilterTransformMirror extends BamFilterBaseTransform implements ActionListener
{
  private static final String FilterName = "Mirror BAM frames";
  private static final String FilterDesc = "This filter allows you to mirror each BAM frame " +
                                            "horizontally, vertically or along both axes.";

  private JCheckBox cbHorizontal, cbVertical, cbAdjustCenter;

  public static String getFilterName() { return FilterName; }
  public static String getFilterDesc() { return FilterDesc; }

  public BamFilterTransformMirror(ConvertToBam parent)
  {
    super(parent, FilterName, FilterDesc);
  }

  @Override
  public PseudoBamFrameEntry process(PseudoBamFrameEntry entry) throws Exception
  {
    return applyEffect(entry);
  }

  @Override
  public PseudoBamFrameEntry updatePreview(PseudoBamFrameEntry entry)
  {
    return applyEffect(entry);
  }

  @Override
  protected JPanel loadControls()
  {
    GridBagConstraints c = new GridBagConstraints();

    cbHorizontal = new JCheckBox("Mirror horizontally");
    cbHorizontal.addActionListener(this);
    cbVertical = new JCheckBox("Mirror vertically");
    cbVertical.addActionListener(this);
    cbAdjustCenter = new JCheckBox("Adjust center position", true);
    cbAdjustCenter.addActionListener(this);

    JPanel p = new JPanel(new GridBagLayout());
    ViewerUtil.setGBC(c, 0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                      GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
    p.add(cbHorizontal, c);
    ViewerUtil.setGBC(c, 0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                      GridBagConstraints.NONE, new Insets(4, 0, 0, 0), 0, 0);
    p.add(cbVertical, c);
    ViewerUtil.setGBC(c, 0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.LINE_START,
                      GridBagConstraints.NONE, new Insets(8, 0, 0, 0), 0, 0);
    p.add(cbAdjustCenter, c);

    JPanel panel = new JPanel(new GridBagLayout());
    ViewerUtil.setGBC(c, 0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.CENTER,
                      GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0);
    panel.add(p, c);

    return panel;
  }

//--------------------- Begin Interface ActionListener ---------------------

  @Override
  public void actionPerformed(ActionEvent event)
  {
    if (event.getSource() == cbHorizontal || event.getSource() == cbVertical ||
        event.getSource() == cbAdjustCenter) {
      fireChangeListener();
    }
  }

//--------------------- Begin Interface ActionListener ---------------------


  private PseudoBamFrameEntry applyEffect(PseudoBamFrameEntry entry)
  {
    if (entry != null && entry.getFrame() != null) {
      int width = entry.getFrame().getWidth();
      int height = entry.getFrame().getHeight();
      byte[] pixelsB = null;
      int[] pixelsI = null;
      if (entry.getFrame().getRaster().getDataBuffer().getDataType() == DataBuffer.TYPE_BYTE) {
        pixelsB = ((DataBufferByte)entry.getFrame().getRaster().getDataBuffer()).getData();
      } else if (entry.getFrame().getRaster().getDataBuffer().getDataType() == DataBuffer.TYPE_INT) {
        pixelsI = ((DataBufferInt)entry.getFrame().getRaster().getDataBuffer()).getData();
      }

      if (cbHorizontal.isSelected()) {
        // mirror horizontally
        for (int y = 0; y < height; y++) {
          int left = y*width;
          int right = left + width - 1;
          while (left < right) {
            if (pixelsB != null) {
              byte v = pixelsB[left]; pixelsB[left] = pixelsB[right]; pixelsB[right] = v;
            }
            if (pixelsI != null) {
              int v = pixelsI[left]; pixelsI[left] = pixelsI[right]; pixelsI[right] = v;
            }
            left++;
            right--;
          }
        }

        // adjusting center
        if (cbAdjustCenter.isSelected()) {
          entry.setCenterX(width - entry.getCenterX() - 1);
        }
      }

      if (cbVertical.isSelected()) {
        // mirror vertically
        for (int x = 0; x < width; x++) {
          int top = x;
          int bottom = (height - 1)*width + x;
          while (top < bottom) {
            if (pixelsB != null) {
              byte v = pixelsB[top]; pixelsB[top] = pixelsB[bottom]; pixelsB[bottom] = v;
            }
            if (pixelsI != null) {
              int v = pixelsI[top]; pixelsI[top] = pixelsI[bottom]; pixelsI[bottom] = v;
            }
            top += width;
            bottom -= width;
          }
        }

        // adjusting center
        if (cbAdjustCenter.isSelected()) {
          entry.setCenterY(height - entry.getCenterY() - 1);
        }
      }
    }

    return entry;
  }
}
