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
 * Class which describes attributes of a given display monitor/screen for a client. Each monitor
 * has a screen number and local min and max values corresponding to the display's border. This
 * enables us to calculate the maximum total screen real estate when there are multiple screens
 * present. Only one screen can have both localMinX of 0 and localMinY = 0;
 * @author Richard Warner.
 * @version 0.001.
 */

package client;

public class DisplayInfo {

  private int screenNumber;
  private int localMinX;
  private int localMaxX;
  private int localMinY;
  private int localMaxY;
  
  /**
   * Constructor for DisplayInfo objects.
   * @param screenNumber The id of the screen according to the OS.
   * @param minX The minimum possible x value for the current screen.
   * @param maxX The maximum possible x value for the current screen.
   * @param minY The minimum possible y value for the current screen.
   * @param maxY The maximum possible y value for the current screen.
   */
  public DisplayInfo(int screenNumber, int minX, int maxX, int minY, int maxY) {
    this.screenNumber = screenNumber;
    this.localMinX = minX;
    this.localMaxX = maxX;
    this.localMinY = minY;
    this.localMaxY = maxY;
  }
  
  public int getScreenNumber() {
    return screenNumber;
  }
  
  public int getLocalMinX() {
    return localMinX;
  }
  
  public int getLocalMaxX() {
    return localMaxX;
  }
  
  public int getLocalMinY() {
    return localMinY;
  }
  
  public int getLocalMaxY() {
    return localMaxY;
  }

}