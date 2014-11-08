package SimulationEngine;

import util.InitializeEarth;


public class Demo {
	private final static String newline = System.getProperty("line.separator");


	public static void main(String[] args) {
		if (args.length != 6) {
			System.out
					.println("Input incorrect, please enter the following : -d # -t # "
							+ newline
							+ "enter a numerical value for each #, the arguments should not be changed"
							+ newline
							+ "-d represents the degrees of precision of the lattice"
							+ newline
							+ "-t represents the time elapsed between each iteration");
		} else {
			InitializeEarth earth = new InitializeEarth(args);




			if (earth.getErrorMessage().equals(""))
				{
				HeatedEarth thisEarth = new HeatedEarth(earth);
			double[][] temps = thisEarth.getTemps();
			System.out.print(temps[0][0]);
				//new HeatedEarth(earth).getTemps();




				}
			else {
				System.out.println("INPUT ERROR:");
				System.out.println(earth.getErrorMessage());
			}
		}
	}
}
