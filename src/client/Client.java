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
 * Class which creates the client application and continuously runs until either local or remote
 * termination.
 * @author Richard Warner.
 */

package client;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.util.ArrayList;

public class Client {

  private boolean locationFound = false;
  private DisplayConfig displayConfig;
  private ArrayList<DisplayInfo> displays;
  private int numberOfScreens = 0;
  private int screenNo = 0;
  private int mousePosX = 0;
  private int mousePosY = 0;
  private Preview preview;

  private Robot robot;
  
  /**
   * Constructor for the Client object.
   * @throws AWTException Thrown if Abstract Window Toolkit exception occurs.
   */
  public Client() throws AWTException {
    displayConfig = new DisplayConfig();
    preview = new Preview();
    this.displays = displayConfig.getDisplays();
    this.numberOfScreens = displays.size();
    
    robot = new Robot();
  }

  /**
   * Method which updates the screen number if the mouse crosses a boundary.
   */
  public void locateCursor() {
    for (int i = 0; i < numberOfScreens; i++) {
      if (mousePosX >= displays.get(i).getLocalMinX() && mousePosX <= displays.get(i).getLocalMaxX() 
          && mousePosY >= displays.get(i).getLocalMinY() && mousePosY <= displays.get(i).getLocalMaxY()) {
        screenNo = i + 1;
//        System.out.println("Cursor is on screen: " + screenNo);
      }
    }
  }

  /**
   * Method which transports the mouse "seamlessly" between virtual screen borders along the x or
   * y dimensions. Uses the 
   * @throws InterruptedException Thrown if thread interrupt fails.
   */
  public void transportMouse() {
    
    Point currentCursorLocation = MouseInfo.getPointerInfo().getLocation();
    
    mousePosX = (int) currentCursorLocation.getX();
    mousePosY = (int) currentCursorLocation.getY();
    
    int lastMousePosX = 0;
    int lastMousePosY = 0;
    
    preview.setMousePos(mousePosX, mousePosY);
    
    locateCursor();
    
    try {
      while (true) {
        
        // if mousePos hasn;t changed, sleep and update mousePos again
        if (mousePosX == lastMousePosX && mousePosY == lastMousePosY) {
          Thread.sleep(20);
          currentCursorLocation = MouseInfo.getPointerInfo().getLocation();
          mousePosX = (int) currentCursorLocation.getX();
          mousePosY = (int) currentCursorLocation.getY();
          continue;
        }
        locationFound = false;

        
//        System.out.println(screenNo + " " + mousePosX + " : " + mousePosY);
        
        // reached LocalMinY
        // if mouse lies on local border check local border point is in virtual border.
        if (mousePosY == displayConfig.getDisplays().get(screenNo - 1).getLocalMinY()) {
          // check point is in matching border hashmap
          if (displayConfig.getLocalBorderMaps().get(screenNo - 1).get(0).containsKey(mousePosX)) {
            // if current mouse x == border.x
            if (mousePosX == displayConfig.getLocalBorderMaps().get(screenNo - 1).get(0).get(mousePosX).x) {
              // save all possible y points to transport to
              ArrayList<Integer> yPoints = new ArrayList<>();
              // check each display for the x point
              for (DisplayInfo display : displays) {
                // skip over current display
                if (display.getScreenNumber() == screenNo) {
                  continue;
                } else if (displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(2).get(mousePosX) != null) {
                  if (mousePosX == displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(2).get(mousePosX).x) {
                  // xPoint found on other display local display
                  yPoints.add(displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(2).get(mousePosX).y);
                   locationFound = true;
                  }
                }
              }
              if (locationFound == true) {
                if (yPoints.size() == 1) {
                  robot.mouseMove(mousePosX, yPoints.get(0) - 1);
                } else {
                  int previousPoint = yPoints.get(0);
                  for (int movePoint : yPoints) {
                    if (movePoint > previousPoint && movePoint < mousePosY) {
                      previousPoint = movePoint;
                    }
                  }
                  robot.mouseMove(mousePosX, previousPoint - 1);
                }
              } else {
                robot.mouseMove(mousePosX, displays.get(screenNo - 1).getLocalMaxY()- 1);
              }
            }
          } else {
            // point not on border, don't wrap
          }
        }
        
        // reached LocalMaxX
        // if mouse lies on local border check local border point is in virtual border.
        if (mousePosX == displayConfig.getDisplays().get(screenNo - 1).getLocalMaxX()) {
          // check point is in matching border hashmap
          if (displayConfig.getLocalBorderMaps().get(screenNo - 1).get(1).containsKey(mousePosY)) {
            // if current mouse y == border.y
            if (mousePosY == displayConfig.getLocalBorderMaps().get(screenNo - 1).get(1).get(mousePosY).y) {
              // save all possible x points to transport to
              ArrayList<Integer> xPoints = new ArrayList<>();
              // check each display for the y point
              for (DisplayInfo display : displays) {
                // skip over current display
                if (display.getScreenNumber() == screenNo) {
                  continue;
                } else if (displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(3).get(mousePosY) != null) {
                  if (mousePosY == displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(3).get(mousePosY).y) {
                  // yPoint found on other display local display
                  xPoints.add(displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(3).get(mousePosY).x);
                   locationFound = true;
                  }
                }
              }
              if (locationFound == true) {
                if (xPoints.size() == 1) {
                  robot.mouseMove(xPoints.get(0) + 1, mousePosY);
                } else {
                  int previousPoint = xPoints.get(0);
                  for (int movePoint : xPoints) {
                    if (movePoint > previousPoint && movePoint > mousePosX) {
                      previousPoint = movePoint;
                    }
                  }
                  robot.mouseMove(previousPoint + 1, mousePosY);
                }
              } else { //locationFound == false 
                robot.mouseMove(displays.get(screenNo - 1).getLocalMinX() + 1, mousePosY);
              }
            }
          } else {
            // point not on border, don't wrap
          }
        }
        
        // reached LocalMaxY
        // if mouse lies on local border check local border point is in virtual border.      
        if (mousePosY == displays.get(screenNo - 1).getLocalMaxY()) {
          // check point is in matching border hashmap
          if (displayConfig.getLocalBorderMaps().get(screenNo - 1).get(2).containsKey(mousePosX)) {
            // if current mouse x == border.x
            if (mousePosX == displayConfig.getLocalBorderMaps().get(screenNo - 1).get(2).get(mousePosX).x) {
              // save all possible y points to transport to
              ArrayList<Integer> yPoints = new ArrayList<>();
              // check each display for the y point
              for (DisplayInfo display : displays) {
                // skip over current display
                if (display.getScreenNumber() == screenNo) {
                  continue;
                } else if (displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(0).get(mousePosX) != null) {
                  if (mousePosX == displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(0).get(mousePosX).x) {
                  // xPoint found on other display local display
                  yPoints.add(displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(0).get(mousePosX).y);
                   locationFound = true;
                  }
                }
              }
              if (locationFound == true) {
                if (yPoints.size() == 1) {
                  robot.mouseMove(mousePosX, yPoints.get(0) + 1);
                } else {
                  int previousPoint = yPoints.get(0);
                  for (int movePoint : yPoints) {
                    if (movePoint < previousPoint && movePoint < mousePosY) {
                      previousPoint = movePoint;
                    }
                  }
                  robot.mouseMove(mousePosX, previousPoint + 1);
                }
              } else {
                robot.mouseMove(mousePosX, displays.get(screenNo - 1).getLocalMinY() + 1);
              }
            }
          } else {
            // point not on border, don't wrap
          }
        }
        
        // reached LocalMinX
        // if mouse lies on local border check local border point is in virtual border.
        if (mousePosX == displayConfig.getDisplays().get(screenNo - 1).getLocalMinX()) {
          // check point is in matching border hashmap
          if (displayConfig.getLocalBorderMaps().get(screenNo - 1).get(3).containsKey(mousePosY)) {
            // if current mouse y == border.y
            if (mousePosY == displayConfig.getLocalBorderMaps().get(screenNo - 1).get(3).get(mousePosY).y) {
              // save all possible x points to transport to
              ArrayList<Integer> xPoints = new ArrayList<>();
              // check each display for the y point
              for (DisplayInfo display : displays) {
                // skip over current display
                if (display.getScreenNumber() == screenNo) {
                  continue;
                } else if (displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(1).get(mousePosY) != null) {
                  if (mousePosY == displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(1).get(mousePosY).y) {
                  // xPoint found on other display local display
                  xPoints.add(displayConfig.getLocalBorderMaps().get(display.getScreenNumber() - 1).get(1).get(mousePosY).x);
                   locationFound = true;
                  }
                }
              }
              if (locationFound == true) {
                if (xPoints.size() == 1) {
                  robot.mouseMove(xPoints.get(0) - 1, mousePosY);
                } else {
                  int previousPoint = xPoints.get(0);
                  for (int movePoint : xPoints) {
                    if (movePoint > previousPoint && movePoint > mousePosY) {
                      previousPoint = movePoint;
                    }
                  }
                  robot.mouseMove(previousPoint - 1, mousePosY);
                }
              } else {
                robot.mouseMove(displays.get(screenNo - 1).getLocalMaxX() - 1, mousePosY);
              }
            }
          } else {
            // point not on border, don't wrap
          }
        }
        
        // prepare for next iteration
        
        lastMousePosX = mousePosX;
        lastMousePosY = mousePosY;
        
        Thread.sleep(20);
        
        currentCursorLocation = MouseInfo.getPointerInfo().getLocation();
        
        mousePosX = (int) currentCursorLocation.getX();
        mousePosY = (int) currentCursorLocation.getY();
        
        preview.setMousePos(mousePosX, mousePosY);
        preview.repaint();
        
        // update screen number if mousePos changes screen
        if ((mousePosX > displays.get(screenNo - 1).getLocalMaxX()) || (mousePosX < displays.get(screenNo - 1).getLocalMinX())
            || (mousePosY > displays.get(screenNo - 1).getLocalMaxY()) || (mousePosY < displays.get(screenNo - 1).getLocalMinY())) {
          locateCursor();
        }

      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static void main(String[] args) throws AWTException, InterruptedException {
    Client client = new Client();
    client.transportMouse();
  }
}