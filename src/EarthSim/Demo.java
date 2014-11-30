package EarthSim;


import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import gui.widget.earth.EarthPanel;
import gui.widget.earth.TempEarthGrid;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ActiveObject.Proxy;
import ActiveObject.SimulationEngineServant;
import ActiveObject.SimulationPresenterServant;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.awt.Font;

/********************************************************************************************* 
 * @author Team 9
 * @version 1.0
 * @since 05-Oct-14
 *  <br> 
 *  <p>
 *  <p><B>Course      :</B> Software Architecture and Design
 *  <p><B>Section     :</B> CS-6300-O01 
 *  <p><B>Term        :</B> Fall 2014
 *  <p><B>FileName    :</B> EarthSimDemoGUI.java
 *  <p><B>Description:</B> Heated Earth 
*  <p> Graphical User Interface 
 *  <p>  This is a Presentation  GUI necessary to which will visualize the Simulation of the heating of the Earth.
 *  <p>  There is business logic for the Sim Engine embedded. Only the code required to  properly present the data
 *  <p>  within the GUI is included here. 
 *  <p>  
 *
 * @see EarthGridDisplay
 * @see EarthImage
 * @see EarthPanel
 * @see SunDisplay
 * @see TemperatureColorPicker
 * @see TemperatureGrid
 **********************************************************************************************/
public class Demo extends JFrame {
	
	
	/** 
	 * -- Group layout (more complex than is might initially seem ) 
	 * -- Event listener and proxy stubs added
	 * -- HiConstrast Image loaded
	 * -- EarthGrid Resizes correctly
	 * -- Sun moves manually for the time being through continuous click of run simulation
	 * -- Run/Stop working Pause /restart visible/hidden correctly
	 * 
	 * Several TODO's are left including:
	 *    pixel color overlay 
	 *    Timer operation for presentation Animation -Need Grid data Queue
	 *    Rotational Position finder - need to understand the Math better to display this 
	 *    Elapsed Sim. Time format and display
	 */
	
	
	private static final long serialVersionUID = -3912945251996815677L;
	String[] algCmdObjLst = {"Scheduler(Master) has Initiative","Simulation(Producer) has Initiative","Presentation(Consumer) has Initiative"};
	
	/********************************************GUI Controls ***************************************************************************/
	private EarthPanel visualPlate;
	
	// these controls are being made public so the GUI Robot can hook into the App
	public static javax.swing.JComboBox jComboBox1;                      // Initiative drop down combo
	public static JSlider               jGridSpcSlider1 ;                //  grid spacing slider selector
	public static JSlider               jSimTmStpSlider2;                //  Simulation Time Step Slider selector
	public static JSlider               jPresDisRateSlider ;                       //  Presentation Display Rate
	public static JSlider               jSlider4;                        //  Buffer slider
         
	public static  int                  curIteration  =0;
	
	

	
	private JLabel    jGridSpcLabel1       = new javax.swing.JLabel();        // Grid Spacing Label      
	private JLabel    jSimTmStpLabel       = new javax.swing.JLabel();        // Sim Time Step Label     
    private JPanel    jPanel1              = new javax.swing.JPanel();        // lower status panel  
    private JPanel    jPanel2              = new javax.swing.JPanel();        // lower status panel
    private JPanel    jPanel3              = new javax.swing.JPanel();        // lower status panel
    private JPanel    jPanel5              = new javax.swing.JPanel();
    private JScrollPane jScrollPane2       = new javax.swing.JScrollPane(); 
    private JLabel    jElapSimTmLabel4     = new javax.swing.JLabel();        // Elapsed Sim. Time display label 
    private JLabel    jRotPosLabel3        = new javax.swing.JLabel();        // Rotational Position display Label 
    
    private JToggleButton jToggleButton1   = new javax.swing.JToggleButton(); // pause/restart button  (not sure if really necessary)
    
    private JSlider   jSlider6             = new javax.swing.JSlider();       // iteration count
    private JSlider   jSlider5             = new javax.swing.JSlider();       // simulation rate
    private JLabel    jPresDisRateLabel    = new javax.swing.JLabel();        // Presentation Display rate Label
    private JLabel    jEndLocLabel         = new javax.swing.JLabel();        // End latitude/longitude Label
    private JLabel    jSimRateLabel        = new javax.swing.JLabel();        // Simulation Rate Label
    private JLabel    jStartLocLabel       = new javax.swing.JLabel();        // Start latitude/longitude Label
    private JLabel    jEccentLabel         = new javax.swing.JLabel();        // Eccentricity Label
    private JLabel    jTiltLabel           = new javax.swing.JLabel();        // Obliguity Label   
    private JLabel    jDurationLabel       = new javax.swing.JLabel();        // Duration Label
    private JLabel    jOrbitalPosLabel     = new javax.swing.JLabel();        // Orbital Position Label
    
    private JTextArea  jTextArea2          = new javax.swing.JTextArea();  
    
    private JTextField jEccentText         = new javax.swing.JTextField();    // Eccentricity TextField
    private JTextField jTiltText           = new javax.swing.JTextField();    // Obliquity TextField
    private JTextField jStartLocText       = new javax.swing.JTextField();    // Start Latitude/Longitude TextField
    private JTextField jEndLocText         = new javax.swing.JTextField();    // End Latitude/Longitude TextField
    private JTextField jDurationText       = new javax.swing.JTextField();    // Simulation Duration TextField
    private JTextField jOrbitalPosText     = new javax.swing.JTextField();    // Orbital Position TextField
    
    
    
    private TempEarthGrid tempgrid;
    
    private EarthSurface       es;
    private SimulationSettings simSet     = new SimulationSettings();
    private Proxy proxy = null;
    private SimulationBuffer buffer = null;
    private SimulationSettings settings = null;
    private MasterControl mc = null;
    private MasterProducer pc = null;
    private Thread nonGUIThread = null;
    private boolean RunSimOK              = false;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		boolean simulationThread = false, presentationInitiative = false, simulationInitiative = false
				, presentationThread = false;
		int bufferSize = 1;
		for (int i = 0; i< args.length; i++)
		{
			//these should all test to see if i+1 goes off the end of the array
			//bigger fish to fry at this moment
			if (args[i].equals("-b"))
				bufferSize = Integer.parseInt(args[i+1]);
			else if (args[i].equals("-s"))
				simulationThread = true;
			else if (args[i].equals("-p"))
				presentationThread = true;						
			else if (args[i].equals("-r"))
				presentationInitiative = true;						
			else if (args[i].equals("-t"))
				simulationInitiative = true;
		}
		if (bufferSize <= 0)
			printUsage();
		else if (presentationInitiative && simulationInitiative) //these options are mutually exclusive
		{
			printUsage();
		}
		else
		{
			final SimulationSettings settings = new SimulationSettings();
			settings.setBufferSize(bufferSize);
			//both -s and -p are not supplied
			settings.setMasterController(!simulationThread || !presentationThread);
			settings.setMasterConsumer(presentationInitiative);
			settings.setMasterProducer(simulationInitiative);
			settings.setConsumerThread(presentationThread);
			settings.setProducerThread(simulationThread);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						 Demo frame = new Demo(settings);
						 frame.setVisible(true);	
					    }
					catch (Exception e)
					    {
						 e.printStackTrace();
					   }
				}
			});
		}
	}

	/**
	 * Create the frame.
	 */
	public Demo() {
		initComponents();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setBounds(100, 100, 1250, 580);                  // Default main windows size to show map correctly
	}
	public Demo(SimulationSettings settings)
	{
		// super();     //toggle for builder
		this();    //  "     "  runtime
		simSet = settings;
		//jSlider4.setValue(simSet.getBufferSize());
	}
	
	public int getCurIteration()
	{
		 
		int temp = curIteration;
		return (temp);
	}
	
	
	
	/**
     * This method is called from within the constructor of the DemoGUI
     * and builds a simple wire frame User interface. No Business Logic has been implemented.
     */
    @SuppressWarnings("unchecked")
                             
    private void initComponents() {
    		
    	
    	String[] algCmdObjLst = {"M(Scheduler) has Initiative","A(Producer) has Initiative","B(Consumer) has Initiative"};
    	
       	//@SuppressWarnings("rawtypes")    	
    	
 
		
        
        /***************************** Set initial GUI Control Values*****************************************************/
    	jComboBox1              = new javax.swing.JComboBox(algCmdObjLst);
    	jGridSpcSlider1         = new javax.swing.JSlider();  // Grid Spacing Slider
    	jSimTmStpSlider2        = new javax.swing.JSlider();  // Sim. Time Step Slider
    	jPresDisRateSlider      = new javax.swing.JSlider();
        jSlider4                = new javax.swing.JSlider();
       
    	
        jGridSpcLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jGridSpcLabel1.setText("Grid Spacing (15)");
        
        //grid spacing slider
        jGridSpcSlider1.setMaximum(180);                // max grid spacing
        jGridSpcSlider1.setMinimum(1);                  // min grid spacing  
        jGridSpcSlider1.setValue(15);                   // default grid spacing set to 14
        jGridSpcSlider1.setMajorTickSpacing(30);
        
        jSimTmStpLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jSimTmStpLabel.setText("Simulation Time Step (50)");
        
        // simulation time step slider
        jSimTmStpSlider2.setMaximum(525600);
        jSimTmStpSlider2.setMinimum(1);
        jSimTmStpSlider2.setValue(1440);
        
        jElapSimTmLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jElapSimTmLabel4.setText("Elapsed Simulation Time (days, hours, minutes) 00:00:00");
        
        jRotPosLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRotPosLabel3.setText("Rotational Position: 00.0");
        
        jToggleButton1.setText("Pause");
        jToggleButton1.setVisible(false);

        jScrollPane1 = new javax.swing.JScrollPane();
        simulationButton = new javax.swing.JButton();
        jSlider5.setValue(60);
        
        jSlider5.setMaximum(60);
        jSlider5.setMinimum(1);
        jSlider5.setPaintLabels(true);
        jSlider5.setPaintTicks(true);
        
        jSlider6.setValue(100);
        
        jSlider6.setMaximum(10000);
        jSlider6.setMinimum(1);
        jSlider6.setPaintLabels(false);
        jSlider6.setPaintTicks(false);
        jSlider6.setValue(10);
        
        jPresDisRateSlider.setMaximum(60);
        jPresDisRateSlider.setMinimum(1);
        jPresDisRateSlider.setPaintLabels(false);
        jPresDisRateSlider.setPaintTicks(false);

        jPresDisRateLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jPresDisRateLabel.setText("Presentation Display Rate");


        jSimRateLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jSimRateLabel.setText("Simulation Rate(60)");
        
        //private JLabel    jPresDisRateLabel    = new javax.swing.JLabel();  // Presentation Display rate Label
        //private JLabel    jEndLocLabel         = new javax.swing.JLabel();  // End latitude/longitude Label
        //private JLabel    jSimRateLabel        = new javax.swing.JLabel();  // Simulation Rate Label
        //private JLabel    jStartLocLabel       = new javax.swing.JLabel();  // Start latitude/longitude Label
        //private JLabel    jQueryLabel          = new javax.swing.JLabel();  // Query box Label
        //private JLabel    jEccentLabel         = new javax.swing.JLabel();  // Eccentricity Label
        //private JLabel    jTiltLabel           = new javax.swing.JLabel();  // Obliguity Label   
        //private JLabel    jDurationLabel       = new javax.swing.JLabel();      // Duration Label
        //private JLabel    jOrbitalPosLabel     = new javax.swing.JLabel();      // Orbital Position Label
              
        
        /*************Register GUI Component Listeners *****************/
        
    	
        
        //change listener for Grid Space SLider
        jGridSpcSlider1.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){
        		jGridSpcLabel1.setText("Grid Spacing (" + jGridSpcSlider1.getValue() + ")" );
        		
        		visualPlate.drawGrid(jGridSpcSlider1.getValue());
        	}

        });
        
        //change listener for Simulation Time Step SLider
        jSimTmStpSlider2.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){
        		jSimTmStpLabel.setText("Simulation Time Step (" + jSimTmStpSlider2.getValue() + ")" );	
        	}

        });

        //change listener for Presentation 
        jSlider5.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){

        		jSimRateLabel.setText("Simulation Rate(" + jSlider5.getValue() + ")" );	//TODO: should probably rename this slider and label

        	}

        });
        //change listener for Presentation 
        jSlider6.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){

        		//jLabel8.setText("Max Iterations(" + jSlider6.getValue() + ")" );	//TODO: should probably rename this slider and label

        	}

        });

      //change listener for Presentation 
        jPresDisRateSlider.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){

        		jPresDisRateLabel.setText("Presentation Display Rate(" + jPresDisRateSlider.getValue() + ")" );	//TODO: should probably rename this slider and label
                
        	}

        });
        jSlider4.setMinorTickSpacing(5);
        //change listener for Presentation 
        jSlider4.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){

        	//	jEndLocLabel.setText("Buffer Size (" + jSlider4.getValue() + ")" );	//TODO: should probably rename this slider and label

        	}

        });
       
        //Run Simulation Button ActionListener 
        simulationButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		
        		if (simulationButton.getText().contains("Run"))
        		{
					//System.out.println("Starting");
					RunSimOK = true;	
					simulationButton.setText("Stop Simulation");
					jToggleButton1.setText("Pause");
					jToggleButton1.setVisible(true);
					  
					setupNewSimulation();
					
					if(settings.isMasterController()){
						 
					}
					setUIState(false);
        		}  
        		else
        		{ 	
        			//System.out.println("Reset!");
        			RunSimOK = false;	
        		    simulationButton.setText("Run Simulation");
        		    jToggleButton1.setVisible(false);
        		    mc.pause();
        		    proxy.pause();
        		    pc.pause();
        		    if(nonGUIThread != null && !nonGUIThread.interrupted())
        		    	nonGUIThread.interrupt();
        		    setUIState(true);
        		}  
        	}

			private void setUIState(boolean b) {
				jComboBox1.setEnabled(b);
				jGridSpcSlider1.setEnabled(b);
				jSimTmStpSlider2.setEnabled(b);
				jPresDisRateSlider.setEnabled(b);
				jSlider4.setEnabled(b);
				jSlider5.setEnabled(b);
				jSlider6.setEnabled(b);
			}

        });
        
        // Pause/Restart ActionListener
        jToggleButton1.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		
        		if (jToggleButton1.getText().contains("Pause"))
        		{
        	      mc.pause();
        		  proxy.pause();
        		  pc.pause();
        		  jToggleButton1.setText("Resume");
        		  RunSimOK = false;
        		}  
        		else
        		{ 
        		  pc.resume();
        		  mc.resume();
        		  proxy.resume();
        		  RunSimOK = true;	
        		  jToggleButton1.setText("Pause");
        		  
        		}  
        		
        	}
        });
         
 
        
        
        this.setTitle("CS6310 - Heated Planet Simulation GUI");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    	
        // Found a high contrast NASA image (128mb) so i gray scaled it, and shrink it to an appropriate size
        Image earthImg = new ImageIcon("EarthLoRes.jpg").getImage();                     
        Dimension size = new Dimension(earthImg.getWidth(null),earthImg.getHeight(null));
        
        visualPlate = new EarthPanel( size, size, size   );
        
        jScrollPane1.setViewportView(visualPlate);

        simulationButton.setText("Run Simulation");

        jToggleButton1.setText("jToggleButton1");

        jPresDisRateSlider.setMaximum(60);
        jPresDisRateSlider.setMinimum(1);
        jPresDisRateSlider.setPaintLabels(false);
        jPresDisRateSlider.setPaintTicks(false);
        jPresDisRateSlider.setValue(3);
        jSlider4.setMaximum(1000);
        jSlider4.setMinimum(1);
        //jSlider4.setMinorTickSpacing(10);
        //jSlider4.setMajorTickSpacing(100);
        jSlider4.setPaintLabels(true);
        jSlider4.setValue(1000);

        jPresDisRateLabel.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jPresDisRateLabel.setText("Presentation Display Rate(3)");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5Layout.setHorizontalGroup(
        	jPanel5Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel5Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel5Layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(jSlider5, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
        				.addComponent(jPresDisRateSlider, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
        				.addComponent(jGridSpcSlider1, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
        				.addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(jSimTmStpSlider2, GroupLayout.DEFAULT_SIZE, 326, Short.MAX_VALUE)
        				.addGroup(jPanel5Layout.createSequentialGroup()
        					.addComponent(jSimTmStpLabel, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)
        					.addGap(85))
        				.addGroup(jPanel5Layout.createSequentialGroup()
        					.addComponent(jSimRateLabel)
        					.addGap(212))
        				.addGroup(jPanel5Layout.createSequentialGroup()
        					.addComponent(jPresDisRateLabel)
        					.addGap(138)))
        			.addContainerGap())
        		.addGroup(jPanel5Layout.createSequentialGroup()
        			.addGap(7)
        			.addComponent(simulationButton, GroupLayout.PREFERRED_SIZE, 148, GroupLayout.PREFERRED_SIZE)
        			.addGap(67)
        			.addComponent(jToggleButton1, GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
        			.addGap(13))
        		.addGroup(jPanel5Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jGridSpcLabel1, GroupLayout.PREFERRED_SIZE, 241, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(95, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
        	jPanel5Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel5Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jGridSpcLabel1)
        			.addGap(1)
        			.addComponent(jGridSpcSlider1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jSimTmStpLabel, GroupLayout.PREFERRED_SIZE, 22, GroupLayout.PREFERRED_SIZE)
        			.addGap(4)
        			.addComponent(jSimTmStpSlider2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jSimRateLabel)
        			.addGap(1)
        			.addComponent(jSlider5, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(jPresDisRateLabel)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jPresDisRateSlider, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addGap(10)
        			.addGroup(jPanel5Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(simulationButton)
        				.addComponent(jToggleButton1))
        			.addGap(147)
        			.addComponent(jScrollPane2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addGap(149))
        );
        jPanel5.setLayout(jPanel5Layout);
        
        JPanel panel = new JPanel();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1Layout.setHorizontalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.TRAILING)
        				.addComponent(jPanel5, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        				.addComponent(panel, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
        			.addGap(18))
        );
        jPanel1Layout.setVerticalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addGap(52)
        			.addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, 251, GroupLayout.PREFERRED_SIZE)
        			.addGap(18)
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(219, Short.MAX_VALUE))
        );
        panel.setLayout(null);
        jEccentLabel.setBounds(10, 11, 66, 14);
        panel.add(jEccentLabel);
        
        jEccentLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jEccentLabel.setText("Eccentricity");
        jEccentLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        jEccentLabel.setText("Eccentricity");
        jEccentText.setBounds(10, 33, 108, 20);
        panel.add(jEccentText);
        jTiltLabel.setBounds(128, 11, 92, 14);
        panel.add(jTiltLabel);
        
        jTiltLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jTiltLabel.setText("Obliquity");
        jTiltLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
        jTiltLabel.setText("Obliquity");
        jTiltText.setBounds(128, 33, 92, 20);
        panel.add(jTiltText);
        jOrbitalPosText.setBounds(240, 33, 99, 20);
        panel.add(jOrbitalPosText);
               jOrbitalPosLabel.setBounds(240, 15, 86, 14);
               panel.add(jOrbitalPosLabel);
        
               jOrbitalPosLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
               jOrbitalPosLabel.setText("Orbital Position");
               jOrbitalPosLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
               jOrbitalPosLabel.setText("Orbital Position");
               jStartLocLabel.setBounds(10, 64, 93, 14);
               panel.add(jStartLocLabel);
               jStartLocLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
               jStartLocLabel.setText("Start(Lat, Long)");
               jStartLocLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
               jStartLocLabel.setText("Start (Lat, Long)");
               jStartLocText.setBounds(10, 84, 107, 20);
               panel.add(jStartLocText);
               jEndLocText.setBounds(126, 84, 91, 20);
               panel.add(jEndLocText);
                       jEndLocLabel.setBounds(126, 64, 84, 14);
                       panel.add(jEndLocLabel);
               
                       jEndLocLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
                       jEndLocLabel.setText("End (Lat, Long)");
                       jDurationLabel.setBounds(238, 64, 49, 14);
                       panel.add(jDurationLabel);
                       jDurationLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
                       jDurationLabel.setText("Duration");
                       jDurationText.setBounds(238, 84, 98, 20);
                       panel.add(jDurationText);
        jPanel1.setLayout(jPanel1Layout);

        jRotPosLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRotPosLabel3.setText("Rotational Position: ###");

        jElapSimTmLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jElapSimTmLabel4.setText("Elapsed Simulation Time  ##:##:##");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jRotPosLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 577, Short.MAX_VALUE)
                .addComponent(jElapSimTmLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRotPosLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jElapSimTmLabel4))
                .addGap(0, 20, Short.MAX_VALUE))
        );

        jScrollPane1.setViewportView(visualPlate);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(0, 23, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 452, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 387, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 0, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
        			.addGap(84))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap(18, Short.MAX_VALUE)
        			.addGroup(layout.createParallelGroup(Alignment.LEADING)
        				.addGroup(Alignment.TRAILING, layout.createSequentialGroup()
        					.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        					.addGap(21))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, 506, GroupLayout.PREFERRED_SIZE)
        					.addContainerGap())
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 679, GroupLayout.PREFERRED_SIZE)
        					.addContainerGap())))
        );
        getContentPane().setLayout(layout);

        pack();
    }   
    
    
    private void setupNewSimulation(){
    	  settings = new SimulationSettings();
//    	  Values in order in the combo box
//    	  Scheduler(Master) has Initiative
//    	  Simulation(Producer) has Initiative
//    	  Presentation(Consumer) has Initiative
		  settings.setMasterController(jComboBox1.getSelectedIndex() == 0);  //was true
		  settings.setMasterProducer(jComboBox1.getSelectedIndex() == 1);  //was false
		  settings.setMasterConsumer(jComboBox1.getSelectedIndex() == 2);  //was false
		  
		  //System.out.println(settings.isMasterConsumer()+" " + settings.isMasterProducer() + " " + settings.isMasterController());
		  settings.setGridSpacing(jGridSpcSlider1.getValue());
		  
		  settings.setConsumerThread(simSet.isConsumerThread());  //not available in the UI, pull from the settings object created from the cmd line
		  settings.setProducerThread(simSet.isProducerThread()); //not available in the UI, pull from the settings object created from the cmd line
		  settings.setTimeStep(jSimTmStpSlider2.getValue()); // was 1440/100
		  settings.setPresentationRate(jPresDisRateSlider.getValue());  //was 2
		  settings.setSimulationRate(jSlider5.getValue());
		  settings.setSimulationIterations(jSlider6.getValue());
		  //settings.setMasterProducer(true);
		  //settings.setMasterConsumer(false);
		  buffer = new SimulationBuffer(jSlider4.getValue());  //was 1
		  proxy = new Proxy(settings,buffer);
		  mc = new MasterControl();
		  pc = new MasterProducer();
		  days=0;
		  updateDisplay(new EarthSurface(settings).getEarthGrid());
		  if(settings.isMasterController() || (!settings.isConsumerThread() && !settings.isProducerThread())){
			  visualPlate.reset();
			  SimulationPresenterServant p = SimulationPresenterServant.getInstance(settings, buffer);
			  p.giveGUI(this);
			  
			  proxy.startScheduler();
			  mc.start();
		  } else {
			  SimulationPresenterServant p = SimulationPresenterServant.getInstance(settings, buffer);
			  p.giveGUI(this);
			  nonGUIThread = new Thread(){
				  public void run(){
					  proxy.startScheduler();
				  }
			  };
			  nonGUIThread.start();
			  //pc.giveGUI(this);
			  //pc.start();
		  }
    }

    private int days = 0, lastHour = 0;
    public void updateDisplay(EarthGrid grid){

    	  visualPlate.updateGrid(new TempEarthGrid(grid.getTempGrid()));
    	  visualPlate.moveSunPosition((float) grid.getSunLongitude());
    	  jRotPosLabel3.setText("Rotational Position: " + grid.getSunLongitude());
    	  //create hours and minutes from longitude
    	  //the 15 is the fact that every 15 degrees of longitude then 1 hour has elapsed.  so integer divide the longitude by 
    	  //15 and you have the elapsed hour.  The minutes though are the fraction of that degrees.  That is proportional to the 
    	  //modulus of the 15 divided by 15. that is the percent of the hour used.  then muliply that by 60 and you have the minutes.
    	  //the day count is based on if the last hour is less than the current then the sun is back to point 0 and we start
    	  //a new day and hour goes to 0.
    	  int in360 = (int)grid.getSunLongitude();
    	  if (in360 < 0)
    		  in360 = 180 + (180-Math.abs(in360));
    	  int hours = in360 / 15;
    	  if (hours < lastHour) days+=1;
    	  int minutes = Math.abs((int)(60.0 * (grid.getSunLongitude() % 15.0 / 15.0)));
    	  jElapSimTmLabel4.setText(String.format("Elapsed Simulation Time (days, hours, minutes)  %02d:%02d:%02d", days, hours, minutes));
          lastHour = hours;
          curIteration++;
          
          
       
    }
    
    
    class MasterControl implements Runnable{
    	
    	Thread t = null;
    	String threadName = "MasterControl Thread";
    	boolean isRunning = false;
    	
    	public void start(){
			if(t == null){
				t = new Thread(this, threadName);
				t.start();
			}
    	}
    	
    	public void run(){
    		isRunning = true;
    		while(true){
    			synchronized(this) {
    				while(!isRunning){
    					try {
    						wait();
    					} catch (InterruptedException e) {}
    				}
    			}
    			if(settings.isMasterController()){
    				proxy.produceEarthGrid();
    				proxy.consumeEarthGrid();
    			}
    			
 				try{
 					Thread.sleep(1000/settings.getPresentationRate());
 				} catch (InterruptedException e){}
    		}
    	}
    	
    	
    	public void pause(){
    		isRunning = false;
    	}
    	
    	public synchronized void resume(){
    		isRunning = true;
    		notify();
    	}
    	
    	public boolean isRunning(){
    		return this.isRunning;
    	}
    	
    }
    
    private double[][] reverse(double[][] fGrid)
    {
        int rows = fGrid.length;
        int cols = fGrid[0].length;
        double[][] rGrid = new double[rows][cols];
        for(int i = rows-1; i >= 0; i--) {
            for(int j = cols-1; j >= 0; j--) {
                rGrid[rows-1-i][cols-1-j] = fGrid[i][j];
            }
        }
        return rGrid;
    }
    		
    

    class MasterProducer implements Runnable{
    	
    	SimulationPresenterServant c = SimulationPresenterServant.getInstance(settings, buffer);
    	SimulationEngineServant p = SimulationEngineServant.getInstance(settings, buffer);
    	boolean isRunning = false;
    	
    	public void start(){
    		if(settings.isConsumerThread() && settings.isProducerThread()){
    			c.start();
    			p.start();
    		} else if (settings.isProducerThread() && !settings.isConsumerThread()){
    			p.start();
    			if(settings.isMasterProducer()){
    				c.runMasterProducer();
    			} else {
    				c.runMasterConsumer();
    			}
    		} else if(!settings.isProducerThread() && settings.isConsumerThread()) {
    			c.start();
    			if(settings.isMasterProducer()){
    				p.runMasterProducer();
    			} else {
    				p.runMasterConsumer();
    			}
    		} else {
    			
    		}
    	}
    	
    	public void giveGUI(Demo gui){
    		c.giveGUI(gui);
    	}
    	
    	public void run(){
    		isRunning = true;
    		while(true){
    			synchronized(this) {
    				while(!isRunning){
    					try {
    						wait();
    					} catch (InterruptedException e) {}
    				}
    			}
    		}
    	}
    	
    	
    	public void pause(){
    		c.pause();
    		p.pause();
    		isRunning = false;
    	}
    	
    	public synchronized void resume(){
    		c.resume();
    		p.resume();
    		isRunning = true;
    		notify();
    	}
    	
    	public boolean isRunning(){
    		return this.isRunning;
    	}
    	
    }
    
   
    
	private final static String usageMg = "Invocation:" +

"The Heated Earth simulation program should be invoked as follows:"
+ "\njava EarthSim.Demo [-s] [-p] [-r|-t] [-b #]"
+ "\nConcurrency: The Java Virtual Machine (JVM) starts each program in a "
+ "\nmain thread. If, in addition, Swing is used, there is a second, event-dispatch "
+ "\nthread that is responsible for handling user controls in the GUI. (Note that "
+ "\nin this section, the term GUI explicitly excludes the animated Presentation.) "
+ "\nThe following parameters control concurrency in terms of what additional threads"
+ "\nyour program should use."
+ "\n-s: Indicates that the Simulation should run in its own thread"
+ "\n-p: Indicates that the Presentation should run in its own thread"
+ "\nAll four subsets of {-s, -p} are allowed. For example, in the absence of both -s "
+ "\nand -p, all three components (Simulation, Presentation and GUI) should run in the "
+ "\nsame thread. Note that the threads used for the -s and -p options should be full "
+ "\npartners. By this is meant that they are not subordinate to the GUI thread or to each"
+ "\nother. In particular, you should not make use of the SwingWorker mechanism provided "
+ "\nby the Java libraries."
+ "\nInitiative: The following parameters control initiative."
+ "\n-t: Indicates that the Simulation, after producing an updated grid, should instruct "
+ "\nthe Presentation to consume it "
+ "\n-r: Indicates that the Presentation, after completing the display of a grid, should "
+ "\ninstruct the Simulation to produce another"
+ "\nIf neither -t nor -r are present, then a third party (presumably in the GUI thread) "
+ "\nshould be responsible for invoking both the Presentation and the Simulation and "
+ "\ncoordinating their interaction in a correct and efficient fashion."
+ "\nBuffering: The -b # parameter controls buffering, where # is a positive integer indicating "
+ "\nthe length of the buffer. In all combinations of parameters, data should be passed between"
+ "\nthe Simulation and the Presentation using a shared variable. If no explicit -b # parameter"
+ "\nis present, the shared variable can be thought of as a buffer of length one. That is, the "
+ "absense of the -b parameter is treated as if -b 1 appeared."; 
    
	
	
	private static void printUsage()
	{
		System.out.print(usageMg);
	}

	// hooks into UI for Robot test Automation                     
    public static javax.swing.JButton simulationButton;
    public static javax.swing.JScrollPane jScrollPane1;
}