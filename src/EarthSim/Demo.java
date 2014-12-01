package EarthSim;


import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import gui.widget.earth.EarthPanel;
import gui.widget.earth.TempEarthGrid;

import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import ActiveObject.Proxy;
import ActiveObject.SimulationEngineServant;
import ActiveObject.SimulationPresenterServant;

import java.awt.Dimension;









import javax.swing.GroupLayout.Alignment;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle.ComponentPlacement;

import persistence.MainDB;

import java.awt.Font;
import java.util.Calendar;

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
	public static JComboBox             jComboBox1;                      // Initiative drop down combo
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
    private JSlider   jSlider5             = new javax.swing.JSlider();       // simulation Length
    private JLabel    jPresDisRateLabel    = new javax.swing.JLabel();        // Presentation Display rate Label
    private JLabel    jEndLocLabel         = new javax.swing.JLabel();        // End latitude/longitude Label
    private JLabel    jSimLenLabel        = new javax.swing.JLabel();        // Simulation Rate Label
    private JLabel    jStartLocLabel       = new javax.swing.JLabel();        // Start latitude/longitude Label
    private JLabel    jEccentLabel         = new javax.swing.JLabel();        // Eccentricity Label
    private JLabel    jTiltLabel           = new javax.swing.JLabel();        // Obliguity Label   
    private JLabel    jDurationLabel       = new javax.swing.JLabel();        // Duration Label
    private JLabel    jOrbitalPosLabel     = new javax.swing.JLabel();        // Orbital Position Label
    
        
    private JTextField jEccentText         = new javax.swing.JTextField();    // Eccentricity TextField
    private JTextField jTiltText           = new javax.swing.JTextField();    // Obliquity TextField
    private JTextField jStartLocText       = new javax.swing.JTextField();    // Start Latitude/Longitude TextField
    private JTextField jEndLocText         = new javax.swing.JTextField();    // End Latitude/Longitude TextField
    private JTextField jDurationText       = new javax.swing.JTextField();    // Simulation Duration TextField
    private JTextField jOrbitalPosText     = new javax.swing.JTextField();    // Orbital Position TextField
    
    private MainDB db;
    
    //private TempEarthGrid tempgrid;
    
   //private EarthSurface       es;
    private static SimulationSettings simSet     = new SimulationSettings();
    private Proxy proxy = null;
    private SimulationBuffer buffer = null;
    //private SimulationSettings settings = null;
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
			//final SimulationSettings settings = new SimulationSettings();
			simSet.setBufferSize(bufferSize);
			//both -s and -p are not supplied
			simSet.setMasterController(!simulationThread || !presentationThread);
			simSet.setMasterConsumer(presentationInitiative);
			simSet.setMasterProducer(simulationInitiative);
			simSet.setConsumerThread(presentationThread);
			simSet.setProducerThread(simulationThread);
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						 Demo frame = new Demo(simSet);
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
        jSimTmStpLabel.setText("Simulation Time Step (1440)");
        
        // simulation time step slider
        jSimTmStpSlider2.setMaximum(525600);
        jSimTmStpSlider2.setMinimum(1);
        jSimTmStpSlider2.setValue(1440);
        
        jElapSimTmLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jElapSimTmLabel4.setText("Elapsed Simulation Time: 00:00:00");
        
        jRotPosLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jRotPosLabel3.setText("Rotational Position: 00.0");
        
        jToggleButton1.setText("Pause");
        jToggleButton1.setVisible(false);

        jScrollPane1 = new javax.swing.JScrollPane();
        simulationButton = new javax.swing.JButton();
        jSlider5.setValue(12);
        
        jSlider5.setMaximum(1200);
        jSlider5.setMinimum(1);
        jSlider5.setPaintLabels(true);
        jSlider5.setPaintTicks(true);
        
        jSlider6.setValue(100);
        
        jSlider6.setMaximum(1000);
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


        jSimLenLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jSimLenLabel.setText("Simulation Length(12)");
        
        jEccentText.setText(Double.toString(simSet.getEccentricity()));
        jTiltText.setText(Double.toString(simSet.getObliquity()));
        
        
        
       
              
        
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

        		jSimLenLabel.setText("Simulation Length(" + jSlider5.getValue() + ")" );	//TODO: should probably rename this slider and label

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
        
        jEccentText.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae)
        	{
        	simSet.setEccentricity(Double.valueOf(jEccentText.getText()));
        	System.out.println("text field =  " +jEccentText.getText());
        	System.out.println("simSet     =  " +simSet.getEccentricity());
        	}
        	
         });
        
        jEccentText.addFocusListener(new FocusListener(){
        
       
                @Override
                public void focusLost(FocusEvent e) {
                    
                    if(!validEccInput()){
                    	
                    	JOptionPane.showMessageDialog(null,
                                "Error:  a valid eccentricity value must be between 0 and 1.0",
                                "Invalid Value Specified", JOptionPane.ERROR_MESSAGE);
                             jEccentText.setText("0.0617");  //dbl chk this value
                        
                    }
                }
                public boolean validEccInput() {
                    if ( (Double.valueOf(jEccentText.getText()) < 0) || (Double.valueOf(jEccentText.getText()) >1.0)) {
                      return false;
                    }
                    return true;
                 }
                
				@Override
				public void focusGained(FocusEvent arg0) {
					// TODO Auto-generated method stub
					
				} 
                
            });
        



         
       
        
        
        jTiltText.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent ae)
        	{
        	simSet.setObliquity(Double.valueOf(jTiltText.getText()));
        	System.out.println("text field =  " +jTiltText.getText());
        	System.out.println("simSet     =  " +simSet.getObliquity());
        	}
        	
         });
        
        jTiltText.addFocusListener(new FocusListener(){
            
            
            @Override
            public void focusLost(FocusEvent te) {
                
                if(!validTiltInput()){
                	
                	JOptionPane.showMessageDialog(null,
                            "Error: The valid Obliquity value must be between -180.00 and 180.00",
                            "Invalid Value Specified", JOptionPane.ERROR_MESSAGE);
                         jTiltText.setText("23.44");  //dbl chk this default value
                    
                }
            }
            public boolean validTiltInput() {
                if ( (Double.valueOf(jTiltText.getText()) >= -180.00) && (Double.valueOf(jTiltText.getText()) <= 180.00)) {
                  return true;
                }
                return false;
             }
            
			@Override
			public void focusGained(FocusEvent arg0) {
				// TODO Auto-generated method stub
				
			} 
            
        });
        
       
        //Run Simulation Button ActionListener 
        simulationButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent arg0) {
        		
        		if (simulationButton.getText().contains("Run"))
        		{
        			simSet.setEccentricity(Double.valueOf(jEccentText.getText()));
        			simSet.setObliquity(Double.valueOf(jTiltText.getText()));
        			
					//System.out.println("Starting");
					RunSimOK = true;	
					simulationButton.setText("Stop Simulation");
					jToggleButton1.setText("Pause");
					jToggleButton1.setVisible(true);
					  
					setupNewSimulation();
					
					//if(settings.isMasterController()){
					if(simSet.isMasterController()){	 
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
				jEccentText.setEnabled(b);
			    jTiltText.setEnabled(b);
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
        					.addComponent(jSimLenLabel)
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
        			.addComponent(jSimLenLabel)
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
        	jPanel1Layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jPanel5, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE)
        				.addComponent(panel, GroupLayout.DEFAULT_SIZE, 359, Short.MAX_VALUE))
        			.addGap(18))
        );
        jPanel1Layout.setVerticalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addComponent(jPanel5, GroupLayout.PREFERRED_SIZE, 252, GroupLayout.PREFERRED_SIZE)
        			.addGap(18)
        			.addComponent(panel, GroupLayout.PREFERRED_SIZE, 139, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(270, Short.MAX_VALUE))
        );
        panel.setLayout(null);
        jEccentLabel.setBounds(10, 11, 108, 14);
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
               jOrbitalPosLabel.setBounds(240, 15, 119, 14);
               panel.add(jOrbitalPosLabel);
        
               jOrbitalPosLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
               jOrbitalPosLabel.setText("Orbital Position");
               jOrbitalPosLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
               jOrbitalPosLabel.setText("Orbital Position");
               jStartLocLabel.setBounds(10, 64, 108, 14);
               panel.add(jStartLocLabel);
               jStartLocLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
               jStartLocLabel.setText("Start(Lat, Long)");
               jStartLocLabel.setFont(new Font("Tahoma", Font.BOLD, 11));
               jStartLocLabel.setText("Start (Lat, Long)");
               jStartLocText.setBounds(10, 84, 107, 20);
               panel.add(jStartLocText);
               jEndLocText.setBounds(126, 84, 94, 20);
               panel.add(jEndLocText);
                       jEndLocLabel.setBounds(126, 64, 99, 14);
                       panel.add(jEndLocLabel);
               
                       jEndLocLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
                       jEndLocLabel.setText("End (Lat, Long)");
                       jDurationLabel.setBounds(238, 64, 101, 14);
                       panel.add(jDurationLabel);
                       jDurationLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
                       jDurationLabel.setText("Duration");
                       jDurationText.setBounds(238, 84, 101, 20);
                       panel.add(jDurationText);
        jPanel1.setLayout(jPanel1Layout);

        jScrollPane1.setViewportView(visualPlate);
        
        
        
       
        
                jRotPosLabel3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
                jRotPosLabel3.setText("Rotational Position: ###");
                
                        jElapSimTmLabel4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
                        jElapSimTmLabel4.setText("Elapsed Simulation Time  ##:##:##");
                        
                                javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
                                jPanel2Layout.setHorizontalGroup(
                                	jPanel2Layout.createParallelGroup(Alignment.LEADING)
                                		.addGroup(Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                			.addComponent(jRotPosLabel3, GroupLayout.PREFERRED_SIZE, 179, GroupLayout.PREFERRED_SIZE)
                                			.addPreferredGap(ComponentPlacement.RELATED, 350, Short.MAX_VALUE)
                                			.addComponent(jElapSimTmLabel4, GroupLayout.PREFERRED_SIZE, 267, GroupLayout.PREFERRED_SIZE))
                                );
                                jPanel2Layout.setVerticalGroup(
                                	jPanel2Layout.createParallelGroup(Alignment.LEADING)
                                		.addGroup(jPanel2Layout.createSequentialGroup()
                                			.addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
                                				.addComponent(jRotPosLabel3, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
                                				.addComponent(jElapSimTmLabel4))
                                			.addGap(0, 20, Short.MAX_VALUE))
                                );
                                jPanel2.setLayout(jPanel2Layout);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3Layout.setHorizontalGroup(
        	jPanel3Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel3Layout.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(jPanel3Layout.createParallelGroup(Alignment.LEADING)
        				.addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
        				.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, 796, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
        	jPanel3Layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(Alignment.LEADING, jPanel3Layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 434, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(jPanel2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3.setLayout(jPanel3Layout);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        layout.setHorizontalGroup(
        	layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap()
        			.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 387, GroupLayout.PREFERRED_SIZE)
        			.addGap(12)
        			.addComponent(jPanel3, GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
        			.addGap(84))
        );
        layout.setVerticalGroup(
        	layout.createParallelGroup(Alignment.TRAILING)
        		.addGroup(layout.createSequentialGroup()
        			.addContainerGap(18, Short.MAX_VALUE)
        			.addGroup(layout.createParallelGroup(Alignment.TRAILING)
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(jPanel3, GroupLayout.PREFERRED_SIZE, 506, GroupLayout.PREFERRED_SIZE)
        					.addGap(184))
        				.addGroup(layout.createSequentialGroup()
        					.addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, 679, GroupLayout.PREFERRED_SIZE)
        					.addContainerGap())))
        );
        getContentPane().setLayout(layout);

        pack();
    }   
    
    
    private void setupNewSimulation(){
    	//  simSet= new SimulationSettings();  //reducing the "smell" from having  multiple simulation settings
//    	  Values in order in the combo box
//    	  Scheduler(Master) has Initiative
//    	  Simulation(Producer) has Initiative
//    	  Presentation(Consumer) has Initiative
    	  simSet.setMasterController(jComboBox1.getSelectedIndex() == 0);  //was true
    	  simSet.setMasterProducer(jComboBox1.getSelectedIndex() == 1);  //was false
    	  simSet.setMasterConsumer(jComboBox1.getSelectedIndex() == 2);  //was false
		  
		  //System.out.println(settings.isMasterConsumer()+" " + settings.isMasterProducer() + " " + settings.isMasterController());
    	  simSet.setGridSpacing(jGridSpcSlider1.getValue());
		  
    	  simSet.setConsumerThread(simSet.isConsumerThread());  //not available in the UI, pull from the settings object created from the cmd line
    	  simSet.setProducerThread(simSet.isProducerThread()); //not available in the UI, pull from the settings object created from the cmd line
    	  simSet.setTimeStep(jSimTmStpSlider2.getValue()); // was 1440/100
    	  simSet.setPresentationRate(jPresDisRateSlider.getValue());  //was 2
    	  simSet.setSimulationRate(60);
    	  simSet.setDuration(jSlider5.getValue());
    	  simSet.setSimulationIterations(jSlider6.getValue());
		  //settings.setMasterProducer(true);
		  //settings.setMasterConsumer(false);
		  buffer = new SimulationBuffer(jSlider4.getValue());  //was 1
		 
	  		
	  	  db = new MainDB();
	  	  simSet.setSimulationName();	
	  	  String dateString = simSet.getSimulationName();
	  	  db.addSimulation(dateString);
	  	  db.addSimSettings(dateString, simSet.getGridSpacing(), simSet.getTimeStep(), 
	  				simSet.getSimulationIterations());
	
	  	  db.addPhysical(dateString, (int)simSet.getObliquity(), simSet.getEccentricity());
	  	  int precision = 4;
	  	  int geographic = 100;
	  	  int temporal = 100;
	  	  db.addInvSettings(dateString, precision, geographic, temporal);
	  	 

		  proxy = new Proxy(simSet,buffer);
		  mc = new MasterControl();
		  pc = new MasterProducer();
		  days=0;
		  updateDisplay(new EarthSurface(simSet).getEarthGrid());
		  if(simSet.isMasterController() || (!simSet.isConsumerThread() && !simSet.isProducerThread())){
			  visualPlate.reset();
			  SimulationPresenterServant p = SimulationPresenterServant.getInstance(simSet, buffer);
			  p.giveGUI(this);
			  
			  proxy.startScheduler();
			  mc.start();
		  } else {
			  SimulationPresenterServant p = SimulationPresenterServant.getInstance(simSet, buffer);
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
    	  jOrbitalPosText.setText( "(" + Double.toString(Math.round(grid.getPosX()*100.00)/100.00 ) + " , " + Double.toString(Math.round(grid.getPosY()* 100.00)/100.00) + ")" ) ;
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
    	  jElapSimTmLabel4.setText(String.format("Elapsed Simulation Time: %02d:%02d:%02d", days, hours, minutes));
          lastHour = hours;
          curIteration++;
          System.out.println("Eccentricity_simSet =" + simSet.getEccentricity());
          System.out.println("Tilt_simSet  =" + simSet.getObliquity());
          System.out.println("gridSpacing_simSet  =" + simSet.getGridSpacing());
        
          //System.out.println("Eccentricity_settings =" + settings.getEccentricity());
          //System.out.println("Tilt_settings  =" + settings.getObliquity());
       
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
    			if(simSet.isMasterController()){
    				proxy.produceEarthGrid();
    				proxy.consumeEarthGrid();
    			}
    			
 				try{
 					Thread.sleep(1000/simSet.getPresentationRate());
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
    	
    	SimulationPresenterServant c = SimulationPresenterServant.getInstance(simSet, buffer);
    	SimulationEngineServant p = SimulationEngineServant.getInstance(simSet, buffer);
    	boolean isRunning = false;
    	
    	public void start(){
    		if(simSet.isConsumerThread() && simSet.isProducerThread()){
    			c.start();
    			p.start();
    		} else if (simSet.isProducerThread() && !simSet.isConsumerThread()){
    			p.start();
    			if(simSet.isMasterProducer()){
    				c.runMasterProducer();
    			} else {
    				c.runMasterConsumer();
    			}
    		} else if(!simSet.isProducerThread() && simSet.isConsumerThread()) {
    			c.start();
    			if(simSet.isMasterProducer()){
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