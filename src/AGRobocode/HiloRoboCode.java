package AGRobocode;

import java.io.File;
import java.io.IOException;


public class HiloRoboCode extends Thread{
	
	static String [] Comando;
	static int generacion;
	static int batalla;
	static Object Padre = null;
	
	/**
	 * Constructor
	 * @param generacion
	 * @param batalla
	 */
	HiloRoboCode(int eGeneracion, int eBatalla, Object P){
		generacion = eGeneracion;
		batalla = eBatalla;
		Padre = P;
		Comando = new String [11];
		for(int i = 0 ; i < 11 ; i++){
			Comando[i] = new String(Configuracion.getInvokeCmd()[i]);
			System.out.printf(Configuracion.getInvokeCmd()[i]);
		}
		Comando[7] = Configuracion.getInvokeCmd()[7]+ "G" + Integer.toString(generacion)  + "B" + Integer.toString(batalla) + ".battle";
		Comando[9] = Configuracion.getInvokeCmd()[9] + "R" + Integer.toString(batalla) + ".txt";
	}
	
	/**
	 * Establece nueva generacion 
	 * @param generacion
	 */
	public void setGeneracion(int eGeneracion){
		generacion = eGeneracion;
	}
	
	/**
	 * Establece nueva batalla
	 * @param batalla
	 */
	public void setBatalla (int eBatalla){
		batalla = eBatalla;
	}
	
	/**
	 * Establece un nuevo array de comando
	 * @param Cmd
	 */
	public void setCmd(String [] eCmd){
		for(int i = 0; i < 10; i++)
			Comando[i] = eCmd[i];
	}
	
	/**
	 * Ejecuta robocode en un bloque sincronizado
	 * -----------------------------
	 * Requisitos
	 * 		- fichero de batalla generado
	 * 		- fichero de comportamiento compilado
	 * 		- contador de batalla actualizado
	 * 		- indicador de generacion actualizado
	 * 		- cadena de comandos actualizada
	 * -----------------------------
	 */
	@Override
	public void run(){
	/*	String [] Cmd = {"java","-Xmx512M","-DNOSECURITY=true","-Dsun.io.useCanonCaches=false","-cp","C:/robocode/libs/robocode.jar","robocode.Robocode","-battle","C:/robocode/battles/","-results","C:/robocode/resultados/","-nodisplay"};
		Cmd[7] = Configuracion.getInvokeCmd()[7]+ "G" + Integer.toString(generacion)  + "B" + Integer.toString(batalla) + ".battle";
		Cmd[9] = Configuracion.getInvokeCmd()[9] + "R" + Integer.toString(batalla) + ".txt";*/
		//////////////////////////////////////////////////////////////////////////
		System.out.printf("\nHIJO: EJECUTA ROBOCODE\n\tCmd:\t");
		for(int i = 0 ; i < 11 ; i++)
			System.out.printf(Comando[i]+ "##");
		////////////////////////////////////////////////////////////////////////
		
		synchronized(Padre){
			try {
				Runtime.getRuntime().exec(Comando);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Padre.notify();
		}

	}
	
	public static void main(String [] args){
		String [] cmd = {"java","-cp", "C:/robocode/bin/","AGRobocode.AGTestSinHilos"};
		File F = new File("C:/robocode/resultados/R1.txt");
		Process Robocode = null;

		Runtime Basurero = null;
		
		
		try {
			Robocode = new ProcessBuilder(cmd[0],cmd[1],cmd[2], cmd[3]).start();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
 		
		try {
			Robocode.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Robocode = null;
		
		Basurero = Runtime.getRuntime();
		Basurero.gc();
		
 		System.out.printf("\nEsperando...");
 		
 		while(F.exists() != true);
 		
 		System.out.printf("FIN");
	}

}
