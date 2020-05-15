/*
Copyright (C) 2018 Richard Warner NahVNC, a system administration tool for controlling multiple
computers using the host's input.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
package client;
*/

/**
 * Class which describes the virtual display available to a client as a combination
 * of the client's connected displays.
 * @author Richard Warner.
 */

package client;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;

public class DisplayConfig {
  private int numberOfScreens; // number of real screens a client has
  private ArrayList<DisplayInfo> displays = new ArrayList<>();
  private ArrayList<Point> border = new ArrayList<>(); // perimeter of the combined screens
  private ArrayList<ArrayList<Point>> localBorders = new ArrayList<>();
  private ArrayList<ArrayList<HashMap<Integer, Point>>> localBorderMaps = new ArrayList<>();
  
  // global origin from which we calculate distances
  int globalMinX;
  int globalMaxX;
  int globalMinY;
  int globalMaxY;

  /**
   * Gets display configuration information, number of screens and current resolutions.
  */
  public DisplayConfig() {
    Point point = new Point();
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] screenDevices = graphicsEnvironment.getScreenDevices();
    for (GraphicsDevice currentGd: screenDevices) {
      GraphicsConfiguration[] graphicsConfigurations = currentGd.getConfigurations();
      Rectangle bounds;
      // store local border for each screen
      ArrayList<Point> localBorder = new ArrayList<>();
      ArrayList<HashMap<Integer,Point>> localBorderMap = new ArrayList<>();
      HashMap<Integer, Point> localNorthMap = new HashMap<>();
      HashMap<Integer, Point> localEastMap = new HashMap<>();
      HashMap<Integer, Point> localSouthMap = new HashMap<>();
      HashMap<Integer, Point> localWestMap = new HashMap<>();
      // find minimum and maximum xy coords
      int localMinX = 0; 
      int localMaxX = 0;
      int localMinY = 0;
      int localMaxY = 0;
      for (GraphicsConfiguration curGraConf : graphicsConfigurations) {
        bounds = curGraConf.getBounds();
        localMinX = (int) bounds.getX();
        localMaxX = localMinX + (int) (bounds.getWidth()) - 1;
        localMinY = (int) bounds.getY();
        localMaxY = localMinY + (int) (bounds.getHeight()) - 1;
        numberOfScreens++;
        if (localMinX < this.globalMinX) {
          this.globalMinX = localMinX;
        }
        if (localMaxX > this.globalMaxX) {
          this.globalMaxX = localMaxX;
        }
        if (localMinY < this.globalMinY) {
          this.globalMinY = localMinY;
        }
        if (localMaxY > this.globalMaxY) {
          this.globalMaxY = localMaxY;
        }
        // north local border
        for (int x = localMinX; x < localMaxX; x++) {
          point = new Point(x, localMinY);
          localBorder.add(point);
          localNorthMap.put(point.x, point);
        }
        // east local border
        for (int y = localMinY; y < localMaxY; y++) {
          point = new Point(localMaxX, y);
          localBorder.add(point);
          localEastMap.put(point.y, point);
        }
        // south local border
        for (int x = localMaxX; x > localMinX; x--) {
          point = new Point(x, localMaxY);
          localBorder.add(point);
          localSouthMap.put(point.x, point);
        }
        // east local border
        for (int y = localMaxY; y > localMinY; y--) {
          point = new Point(localMinX, y);
          localBorder.add(point);
          localWestMap.put(point.y, point);
        }
        localBorders.add(localBorder);
        localBorderMap.add(localNorthMap);
        localBorderMap.add(localEastMap);
        localBorderMap.add(localSouthMap);
        localBorderMap.add(localWestMap);
        localBorderMaps.add(localBorderMap);
        DisplayInfo di = new DisplayInfo(
            numberOfScreens, localMinX, localMaxX, localMinY, localMaxY);
        displays.add(di);
        System.out.println("Display: " + numberOfScreens + " x: " + localMinX + " " + localMaxX
            + " y: " + localMinY + " " + localMaxY);
      }

    }
    // current display to create virtual border from
    for (DisplayInfo display : displays) {
      boolean checkedBounds = false;
      for (Point currentDisplayPoint : localBorders.get(display.getScreenNumber() - 1)) {
        boolean collision = false;
        // other displays to check current display points against
        for (DisplayInfo otherDisplay : displays) {
          // don't check current display in other displays
          if (display.getScreenNumber() == otherDisplay.getScreenNumber() || checkedBounds == true
              || checkCollision(display, otherDisplay) == false) {
            continue;
          }
          // for each point in current display's local border check against other display's min/max
          // check north border of current display for collision
          if (((currentDisplayPoint.y - 1) == otherDisplay.getLocalMaxY())
              && ((currentDisplayPoint.x > otherDisplay.getLocalMinX())
              && (currentDisplayPoint.x < otherDisplay.getLocalMaxX()))) {
            collision = true;
            localBorderMaps.get(display.getScreenNumber() - 1).get(0).remove(currentDisplayPoint.x);
            // collision detected, don't add point
            // check east border of current display for collision
          } else if (((currentDisplayPoint.x + 1) == otherDisplay.getLocalMinX())
              && ((currentDisplayPoint.y > otherDisplay.getLocalMinY())
              && (currentDisplayPoint.y < otherDisplay.getLocalMaxY()))) {
            collision = true;
            localBorderMaps.get(display.getScreenNumber() - 1).get(1).remove(currentDisplayPoint.y);
            // collision detected, don't add point
            // check south border of current display for collision
          } else if (((currentDisplayPoint.y + 1) == otherDisplay.getLocalMinY())
              && ((currentDisplayPoint.x > otherDisplay.getLocalMinX())
              && (currentDisplayPoint.x < otherDisplay.getLocalMaxX()))) {
            collision = true;
            localBorderMaps.get(display.getScreenNumber() - 1).get(2).remove(currentDisplayPoint.x);
            // collision detected, don't add point
            // check west border of current display for collision
          } else if (((currentDisplayPoint.x - 1) == otherDisplay.getLocalMaxX())
              && ((currentDisplayPoint.y > otherDisplay.getLocalMinY())
              && (currentDisplayPoint.y < otherDisplay.getLocalMaxY()))) {
            collision = true;
            localBorderMaps.get(display.getScreenNumber() - 1).get(3).remove(currentDisplayPoint.y);
            // collision detected, don't add point
          } else {
            // do nothing
          }
        }
        if (collision == false) {
          border.add(currentDisplayPoint);
        }
      }
      checkedBounds = true;
    }
    System.out.println(border.size());
  }
  
  private boolean checkCollision(DisplayInfo display, DisplayInfo otherDisplay) {
    // check north border of current display
    if ((display.getLocalMinY() - 1 == otherDisplay.getLocalMaxY())
        && (display.getLocalMinX() < otherDisplay.getLocalMaxX())) {
      return true;
      // check east border of current display
    } else if ((display.getLocalMaxX() + 1 == otherDisplay.getLocalMinX())
        && (display.getLocalMinY() < otherDisplay.getLocalMaxY())) {
      return true;
      // check south border of current display
    } else if ((display.getLocalMaxY() + 1 == otherDisplay.getLocalMinY())
        && (display.getLocalMinX() < otherDisplay.getLocalMaxX())) {
      return true;
      // check west border of current display
    } else if ((display.getLocalMinX() - 1 == otherDisplay.getLocalMaxX())
        && (display.getLocalMinY() < otherDisplay.getLocalMaxY())) {
      return true;
    } else {
      return false;
    }
  }
    
  public ArrayList<Point> getBorder() {
    return border;
  }

  public ArrayList<DisplayInfo> getDisplays() {
    return displays;
  }

  public ArrayList<ArrayList<HashMap<Integer, Point>>> getLocalBorderMaps() {
    return localBorderMaps;
  }
  
  public int getNumberOfScreens() {
    return this.numberOfScreens;
  }

}