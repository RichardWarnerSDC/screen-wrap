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
 * Class which creates a Preview of a client's virtual border with the borders 
 * removed between the client's adjacent screens. A Preview object also displays
 * the current cursor position.
 * @author Richard Warner.
 */


package client;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Preview extends JPanel{

  private static int WIDTH = 800;
  private static int HEIGHT = 600;
  
  private int xdimOffset = 0;
  private int ydimOffset = 0;
  
  private DisplayConfig config = new DisplayConfig();
  private ArrayList<Point> border = new ArrayList<>(config.getBorder());
  private ArrayList<Point> middlePoints = new ArrayList<>();
  
  private int mousePosX = MouseInfo.getPointerInfo().getLocation().x;
  private int mousePosY = MouseInfo.getPointerInfo().getLocation().y;
  
  /**
   * Constructor for Preview objects
   */
  public Preview() {
    this.setLayout(null);
    JFrame frame = new JFrame("NahVNC - Richard Warner");
    JButton quitButton = new JButton("Quit");
    quitButton.addActionListener(new ActionListener() { 
      public void actionPerformed(ActionEvent e) { 
        System.exit(1);
      } 
    });
    quitButton.setBounds(340, 500, 80, 40);
    this.add(quitButton);
    frame.setSize(WIDTH, HEIGHT);
    frame.setLocationRelativeTo(null);
    frame.add(this);
    frame.setVisible(true);
    setPreviewScale();
  }
  
  public void setPreviewScale() {
    DisplayInfo currentDisplay;
    int minX = config.globalMinX / 10;
    int maxX = config.globalMaxX / 10;
    int minY = config.globalMinY / 10;
    int maxY = config.globalMaxY / 10;
    
    xdimOffset = (WIDTH / 2) - ((minX + maxX) / 2);
    ydimOffset = (HEIGHT / 2) - ((minY + maxY) / 2);
    
    for (int j = 0; j < config.getNumberOfScreens(); j++) {
      currentDisplay = config.getDisplays().get(j);
      middlePoints.add(new Point((currentDisplay.getLocalMaxX() + currentDisplay.getLocalMinX()) / 2,
         (currentDisplay.getLocalMaxY() + currentDisplay.getLocalMinY()) / 2));
    }
     
    for (Point point : border) {
      point.x /= 10;
      point.y /= 10;
      point.x += xdimOffset;
      point.y += ydimOffset;
    }
    
    for (Point point : middlePoints) {
      point.x /= 10;
      point.y /= 10;
      point.x += xdimOffset;
      point.y += ydimOffset;
    }
  }
  
  /** 
   * Override JPanel's paintComponent method to draw the perimeter of the virtual screen.
   * config.getBorder() is altered each time.
   * @Override
   */
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    g.setColor(Color.BLACK);

    for (int i = 0; i < border.size() - 1; i++) {
      // calls to border.get() can be replaced
      // int borderNo = border.get(i);
      if (((border.get(i + 1).x - border.get(i).x < 0)
          || (border.get(i + 1).x - border.get(i).x > 1))
          || ((border.get(i + 1).y - border.get(i).y < 0)
          || (border.get(i + 1).y - border.get(i).y > 1))) {
        continue;
      }

      g.drawLine(border.get(i).x, border.get(i).y,
            border.get(i+1).x, border.get(i+1).y);
    }
    
    for (int j = 0; j < middlePoints.size(); j++) {
      g.drawString(Integer.toString(config.getDisplays().get(j).getScreenNumber()), middlePoints.get(j).x, middlePoints.get(j).y);
    }
    
    g.drawRect(mousePosX - 1, mousePosY - 1, 3, 3);
  }
  
  public void setMousePos(int newMousePosX, int newMousePosY) {
    this.mousePosX = (newMousePosX / 10) + xdimOffset;
    this.mousePosY = (newMousePosY / 10) + ydimOffset;
  }

}
