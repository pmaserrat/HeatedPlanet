package gui.widget.earth;

public class TempEarthGrid implements TemperatureGrid {

	private double[][] tGrid;
	public TempEarthGrid(double[][] grid) {
		
		tGrid = grid;
		//printGrid();
	}

	@Override
	public double getTemperature(int x, int y) {
	  double i = -1;
		try{	
		// kelvin to celcius conversion
		i = (tGrid[y][x] - 273.15);
	  }
	  
	  
	 catch(Exception e)
	  {
		//System.out.println("X = "+x + " Y = " + y  + "tgrid size = "+tGrid.length  + "error thrown= " +e.getLocalizedMessage()); 
	  }
		
	
		return i;
	 
		
	}

	@Override
	public float getCellHeight(int x, int y) {
		
		return 27;

}
	
	 public void printGrid()
	    {

			for(int i = 0; i < tGrid.length; i++){
				System.out.print("\n");
				for(int j = 0; j < tGrid[0].length; j++){
					System.out.print("["+i+"]" +"[" +j +"]" + "=" + new Double(getTemperature(i,j)).intValue()  + " "); 
				}
				
			}
	    }	
}