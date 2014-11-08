package util;




public interface IHeatedEarth {


	//public final int MAX_ITERATIONS = 10000;
	public final int START_TEMP = 288;
	public final int SUN_IN = 278;
	public final int EARTH_RADIUS = 6371000;
	public final int EARTH_CIRCUMFERENCE = 40030140;
	public final long EARTH_SURFACEAREA= 510072000000000L;
	public final long EARTH_HEMISPHEREAREA=255036000000000L;
	public final int EARTH_DEGREES = 360;
	public final int MINUTES_IN_DAY = 1440;


	/*note this assumes we model earth as a 10cm deep plate to be heated. 
	volumetric heat capacities: water=4.1795 silica=1.547 both in J/cm^3/K 
	assume 2/1 ratio of water/silica for net of 330210J/m^2 at 10cm depth. Using watts=1366/m^2 per the domain model*/
	public final double HEAT_PER_MINUTE= 2.713;


















	public void displayResults();


}
