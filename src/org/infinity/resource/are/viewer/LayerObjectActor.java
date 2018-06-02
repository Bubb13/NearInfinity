// Near Infinity - An Infinity Engine Browser and Editor
// Copyright (C) 2001 - 2005 Jon Olav Hauglid
// See LICENSE.txt for license information

package org.infinity.resource.are.viewer;

import java.awt.Point;
import java.util.HashMap;

import org.infinity.gui.layeritem.AbstractLayerItem;
import org.infinity.gui.layeritem.IconLayerItem;
import org.infinity.resource.AbstractStruct;
import org.infinity.resource.Viewable;
import org.infinity.resource.AbstractStruct.StructChangedListener;
import org.infinity.resource.are.Actor;

/**
 * Base class for layer type: Actor
 */
public abstract class LayerObjectActor extends LayerObject
{
  protected final Point location = new Point();

  protected IconLayerItem item;


  protected LayerObjectActor(Class<? extends AbstractStruct> classType, AbstractStruct parent)
  {
    super(ViewerConstants.RESOURCE_ARE, "Actor", classType, parent);
  }

  @Override
  public void close()
  {
    // TODO: implement method
    super.close();
    Viewable viewable = getViewable();
    if (viewable instanceof Actor) { // Sanity checks probably aren't needed, but why not. 
      Actor actor = (Actor)viewable;
      HashMap<Object, StructChangedListener> structChangeListeners = actor.getStructChangedListeners();
      if (structChangeListeners.containsKey(this)) {
        structChangeListeners.remove(this);
      }
    }
  }

  @Override
  public AbstractLayerItem getLayerItem()
  {
    return item;
  }

  @Override
  public AbstractLayerItem getLayerItem(int type)
  {
    return (type == 0) ? item : null;
  }

  @Override
  public AbstractLayerItem[] getLayerItems()
  {
    return new AbstractLayerItem[]{item};
  }

  @Override
  public void update(double zoomFactor)
  {
    if (item != null) {
      item.setItemLocation((int)(location.x*zoomFactor + (zoomFactor / 2.0)),
                           (int)(location.y*zoomFactor + (zoomFactor / 2.0)));
    }
  }

  @Override
  public Point getMapLocation()
  {
    return location;
  }

  @Override
  public Point[] getMapLocations()
  {
    return new Point[]{location};
  }
}
