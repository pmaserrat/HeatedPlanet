package EarthSimTest;

import static org.junit.Assert.*;



import org.junit.Before;
import org.junit.Test;

import EarthSim.EarthGrid;
import EarthSim.EarthSurface;
import EarthSim.SimulationSettings;

public class EarthSurfaceTest {
	EarthSurface earth;
	
	@Before
	public void setup(){
		earth = new EarthSurface(new SimulationSettings());
	}
	
	@Test
	public void test01() {
		EarthGrid Grid = earth.getEarthGrid();
		double[][] grid = Grid.getTempGrid();
		assertTrue(grid.length == 12);
	}
	
	@Test
	public void test02() {
		EarthGrid Grid = earth.getEarthGrid();
		double[][] grid = Grid.getTempGrid();
		assertTrue(grid[0].length == 24);
	}
	
	@Test
	public void test03() {
		EarthGrid Grid = earth.getEarthGrid();
		double[][] grid = Grid.getTempGrid();
		assertEquals(288.0, grid[0][0],0.01);
	}
	
	@Test
	public void test04(){
		
		
		assertTrue(false);
	}

}
