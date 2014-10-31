package EarthSimTest;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import EarthSim.Demo;

public class AffectFromInititiveCon {

    EarthSim.Demo      UI          = new EarthSim.Demo();
	DemoGUIRobot       UITestBot          = new DemoGUIRobot();
	

	final int   INITITIVE                 = 0;
	final int   GRID_ANGLE                = 15;
	final int   PRESENTATION_RATE         = 60;
	final int   TIMESTEP                  = 1;
	final int   BUFFER_SIZE               = 100;
	final int   SIMULATION_RATE           = 100;
	final int   MAX_ITERATIONS            = 1000;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		
		
		
		
		 try {
	            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
	                if ("Nimbus".equals(info.getName())) {
	                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
	                    break;
	                }
	            }
	        } catch (ClassNotFoundException nfe) {
	        	nfe.printStackTrace();
	     
	        } catch (javax.swing.UnsupportedLookAndFeelException ulfe) {
	           ulfe.printStackTrace();
	        }
	        

	
	        
	        
	    }
		
		
	

	/**
	 * @throws java.lang.Exception
	 */
	@After
     public void tearDown() throws Exception {
		
		while(EarthSim.Demo.curIteration <1000)
		{
			System.out.println(EarthSim.Demo.curIteration);
		}
		
		
		EarthSim.Demo.curIteration =0;
	}
	
	

	/**
	 * Test method for {@link Demo#Demo()}.
	 */

	
	@Test
	public void testDemoGUI3() {
		
		   /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
              new  EarthSim.Demo().setVisible(true);
              EarthSim.Demo.curIteration =0;
            }
        });
        
        UITestBot.Pause(10000);
        
        EarthSim.Demo.jComboBox1.setSelectedIndex(INITITIVE +2);       
        
		UITestBot.clickComponement(EarthSim.Demo.jComboBox1, 50, .50);
		UITestBot.clickComponement(EarthSim.Demo.jComboBox1, 50, .50);
		EarthSim.Demo.jComboBox1.setSelectedIndex(INITITIVE +1); 
      
		UITestBot.clickComponement(EarthSim.Demo.jGridSpcSlider1, 50, .50);
		UITestBot.clickComponement(EarthSim.Demo.jGridSpcSlider1, 50, .50);
		EarthSim.Demo.jGridSpcSlider1.setValue(GRID_ANGLE);
      
		
		UITestBot.clickComponement(EarthSim.Demo.jSimTmStpSlider2, 50, .50);
		UITestBot.clickComponement(EarthSim.Demo.jSimTmStpSlider2, 50, .50);
		EarthSim.Demo.jSimTmStpSlider2.setValue(TIMESTEP);
		
		UITestBot.clickComponement(EarthSim.Demo.jSlider3, 50, .50);
		UITestBot.clickComponement(EarthSim.Demo.jSlider3, 50, .50);
        EarthSim.Demo.jSlider3.setValue(PRESENTATION_RATE );
      
        UITestBot.clickComponement(EarthSim.Demo.jSlider4, 50, .50);
		UITestBot.clickComponement(EarthSim.Demo.jSlider4, 50, .50);
        EarthSim.Demo.jSlider4.setValue(BUFFER_SIZE);
        UITestBot.Pause(1000); 
      
      
      UITestBot.clickComponement(EarthSim.Demo.simulationButton, 100, .50);
      assertTrue(true);
       
        
		
	}

}
