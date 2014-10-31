package EarthSimTest;



import java.awt.AWTException;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.InputEvent;

/************************************************************************************************************** 
 * @author Team9
 * @version 1.0
 * @since 25-Oct-14
 *  <br> 
 *  <p>
 *  <p><B>Course      :</B> Software Architecture and Design
 *  <p><B>Section     :</B> CS-6310-O01 
 *  <p><B>Term        :</B> Fall  2014
 *  <p><B>FileName    :</B> DemoGUIRobot.java
 *  <p><B>Description:</B> 
 *  <p> <center><B> Project2 HeatedEarth Project 

 *  <p>  This is a Java Robot wrapper class. It simulates keyboard presses and mouse clicks of the DemoGUI
 *  <p> UI. It is used by the DemoTest junit Test case. wasn't exactly sure whether to place this in test 
 *  <p> or src, but ultimately decided it responsibility were more test not application. oriented.   
 *  <p>  
 *
 * @see test/DemoTest
 * @see src/ActiveObject/DemoGUI
 **************************************************************************************************************/
public class DemoGUIRobot {
	private static Robot robot;
	
    public DemoGUIRobot() {
       try
          {    
    	    robot = new Robot();
          }
      catch (AWTException e)
          {
    	    e.printStackTrace(); 
    	  }
    }
    
    //PRESS KEY Function
    public void PressKey(int key, int time)
       {
    	robot.keyPress(key);
		robot.delay(time);
		robot.keyRelease(key);
    	
       }
    
    //wait time Function
    public void  Pause ( int time)
       {
    	
		robot.delay(time);
		
    	
       }
    
    public void clickComponement(final Component comp,  int delay, double percent) {
        pointOnComp(comp, robot, percent);
        robot.delay(delay);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(delay);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
    }
    
/*    private void pointOnComp(final Component comp, final Robot robot)
        {
     
            Rectangle bounds = new Rectangle(comp.getLocationOnScreen(), comp.getSize());
    	    robot.mouseMove(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
    	 
         } */

    private void pointOnComp(final Component comp, final Robot robot, final double percent)
    {
 
        Rectangle bounds = new Rectangle(comp.getLocationOnScreen(), comp.getSize());
	    robot.mouseMove((int) Math.round(bounds.x + bounds.width * percent) , bounds.y + bounds.height / 2);
	 
     }  

}


