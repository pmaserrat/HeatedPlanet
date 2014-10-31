package EarthSim;


import java.awt.EventQueue;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	public static JSlider               jSlider3 ;                       //  Presentation Display Rate
	public static JSlider               jSlider4;                        //  Buffer slider
         
	public static  int                  curIteration  =0;
	
	
	private JLabel    jGridSpcLabel1       = new javax.swing.JLabel();   // Grid Spacing Label      
	private JLabel    jSimTmStpLabel       = new javax.swing.JLabel();   // Sim Time Step Label     
   // private JSlider   jSimTmStpSlider2     = new javax.swing.JSlider();  // Sim. Time Step Slider    
    private JPanel    jPanel1              = new javax.swing.JPanel();   // lower status panel      
    private JLabel    jElapSimTmLabel4     = new javax.swing.JLabel();   // Elapsed Sim. Time display label 
    private JLabel    jRotPosLabel3        = new javax.swing.JLabel();   // Rotational Position display Label 
    
    private JToggleButton jToggleButton1   = new javax.swing.JToggleButton(); // pause/restart button  (not sure if really necessary)
    
    private JSlider   jSlider6             = new javax.swing.JSlider();  //iterationcount
    private JSlider   jSlider5             = new javax.swing.JSlider();  //simulation rate
    private JLabel    jLabel5              = new javax.swing.JLabel();
    private JLabel    jLabel6              = new javax.swing.JLabel();
    private JLabel    jLabel7              = new javax.swing.JLabel();

    private JLabel    jLabel8              = new javax.swing.JLabel();
    
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
					} catch (Exception e) {
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
		setBounds(100, 100, 1100, 500);                  // Default main windows size to show map correctly
	}
	public Demo(SimulationSettings settings)
	{
		this();
		simSet = settings;
		jSlider4.setValue(simSet.getBufferSize());
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
    	jComboBox1       = new javax.swing.JComboBox(algCmdObjLst);
    	jGridSpcSlider1  = new javax.swing.JSlider();  // Grid Spacing Slider
    	jSimTmStpSlider2 = new javax.swing.JSlider();  // Sim. Time Step Slider
        jSlider3         = new javax.swing.JSlider();
        jSlider4         = new javax.swing.JSlider();
       
    	
        jGridSpcLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jGridSpcLabel1.setText("Grid Spacing (15)");
        
        //grid spacing slider
        jGridSpcSlider1.setMaximum(180);                // max grid spacing
        jGridSpcSlider1.setMinimum(1);                  // min grid spacing  
        jGridSpcSlider1.setValue(15);                   // default grid spacing set to 14
        jGridSpcSlider1.setMajorTickSpacing(30);        // because we are starting at 1 vs. 0 the slider tick values are off by 1 
        jGridSpcSlider1.setPaintTicks(true);            // show the tick marks on the slider
        jGridSpcSlider1.setPaintLabels(true);           // displa the tick values
        
        jSimTmStpLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jSimTmStpLabel.setText("Simulation Time Step (1)");
        
        // simulation time step slider
        jSimTmStpSlider2.setMaximum(1440);
        jSimTmStpSlider2.setMinimum(1);
        jSimTmStpSlider2.setValue(1);
        
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
        
        jSlider3.setMaximum(60);
        jSlider3.setMinimum(1);
        jSlider3.setPaintLabels(true);
        jSlider3.setPaintTicks(true);

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel5.setText("Presentation Display Rate");

        jLabel6.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel6.setText("Buffer Size (1)");
        jLabel8.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel8.setText("Maximum Iterations");


        jLabel7.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel7.setText("Simulation Rate");

        
        
 
      
        
        
              
        
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

        		jLabel7.setText("Simulation Rate(" + jSlider5.getValue() + ")" );	//TODO: should probably rename this slider and label

        	}

        });
        //change listener for Presentation 
        jSlider6.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){

        		jLabel8.setText("Max Iterations(" + jSlider6.getValue() + ")" );	//TODO: should probably rename this slider and label

        	}

        });

      //change listener for Presentation 
        jSlider3.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){

        		jLabel5.setText("Presentation Display Rate(" + jSlider3.getValue() + ")" );	//TODO: should probably rename this slider and label
                
        	}

        });
        jSlider4.setMinorTickSpacing(5);
        //change listener for Presentation 
        jSlider4.addChangeListener(new ChangeListener() {
        	public void stateChanged(ChangeEvent event){

        		jLabel6.setText("Buffer Size (" + jSlider4.getValue() + ")" );	//TODO: should probably rename this slider and label

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
				jSlider3.setEnabled(b);
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
        

        //Group layout aligns visual components along their respective horizontal and vertical axis' for a cleaner looking UI
 
        
        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1Layout.setHorizontalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addComponent(jRotPosLabel3, GroupLayout.PREFERRED_SIZE, 171, GroupLayout.PREFERRED_SIZE)
        			.addPreferredGap(ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
        			.addComponent(jElapSimTmLabel4, GroupLayout.PREFERRED_SIZE, 340, GroupLayout.PREFERRED_SIZE))
        );
        jPanel1Layout.setVerticalGroup(
        	jPanel1Layout.createParallelGroup(Alignment.LEADING)
        		.addGroup(jPanel1Layout.createSequentialGroup()
        			.addGroup(jPanel1Layout.createParallelGroup(Alignment.BASELINE)
        				.addComponent(jElapSimTmLabel4)
        				.addComponent(jRotPosLabel3, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
        			.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1.setLayout(jPanel1Layout);

        jToggleButton1.setText("jToggleButton1");

        jSlider3.setMaximum(60);
        jSlider3.setMinimum(1);
        jSlider3.setPaintLabels(true);
        jSlider3.setPaintTicks(true);
        jSlider3.setValue(1);
        jSlider4.setMaximum(1000);
        jSlider4.setMinimum(1);
        //jSlider4.setMinorTickSpacing(10);
        //jSlider4.setMajorTickSpacing(100);
        jSlider4.setPaintLabels(true);
        jSlider4.setValue(1);

        jLabel5.setFont(new java.awt.Font("Verdana", 1, 11)); // NOI18N
        jLabel5.setText("Presentation Display Rate");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jGridSpcLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSimTmStpLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSimTmStpSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jGridSpcSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(jSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)
                    .addComponent(jSlider5, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jSlider6, javax.swing.GroupLayout.PREFERRED_SIZE, 231, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(simulationButton, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(jToggleButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    )
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jGridSpcLabel1)
                        .addGap(1, 1, 1)
                        .addComponent(jGridSpcSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jSimTmStpLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSimTmStpSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        //.addGap(18, 18, 18)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(simulationButton)
                            .addComponent(jToggleButton1)))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 364, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

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
		  settings.setPresentationRate(jSlider3.getValue());  //was 2
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



