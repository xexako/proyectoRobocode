package AGRobocode;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import AGRobocode.HiloRoboCode;
import AGRobocode.Configuracion;
import AGRobocode.Individuo;
import AGRobocode.Regla;
import AGRobocode.FileGenerator;

/**
 * Clase para probar el funcionamiento del AG
 * @author Jesus
 *
 */
public class AGTest {

	final static int TamaPob = 10;
	static int generacion, batalla;
	
	AGTest(){
		generacion = 0;
		batalla = 0;
	}
	
	public void compileThis(){
		/* declaracion de variables e inicializacion */
		Individuo I = new Individuo(1,1);
		FileGenerator Fg = new FileGenerator(I);
		File F = null;										// para obtener nuevas direcciones de fichero
		String Aux = new String();							// cadena de comparacion
		String [] Command = new String[5];					// al array hay que agregarle en ultimo lugar la direccion del fichero a compilar
		Aux = "G"+Integer.toString(generacion);
		
		/* compara los nombres y los compila si se requiere */
		for (int i = 0 ; i < TamaPob ; i++){
			F = Fg.getNewPath(generacion, i);					//obtiene una nueva dirección
			if(F.getName().indexOf(Aux) != -1){				//condicion de compilacion
				Command = Fg.getCompilationCmd(generacion, i);
				try {
					Runtime.getRuntime().exec(Command);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public void invocarRobocode(){
		HiloRoboCode Hijo = new HiloRoboCode(generacion, batalla, this);
		
		//////////////////////////////////////////////////////////////////////////
	//	System.out.printf("\nHIJO--->EMPEZANDO EJECUCION\n\t");
		/////////////////////////////////////////////////////////////////////////
		//Hijo.setCmd(Cmd);
		//////////////////////////////////////////////////////////////
	
		System.out.printf("\nrobocode........");
		
		//////////////////////////////////////////////////////////////
		Hijo.start();
		synchronized(Hijo){
			
			///////////////////////////////////
			System.out.printf("\nESPERANDO AL HIJO");
			///////////////////////////////////
			
			try {
				Hijo.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		
		
		//////////////////////////////////////////////////////////////////////////
		System.out.printf("\n");
		////////////////////////////////////////////////////////////////////////

	}
	
	
	
		/**
		 * TEST 1 :
		 * 		1- INICIALIZACION
		 * 		2- ESCRITURA DE COMPORTAMIENTOS
		 * 		3- COMPILACION
		 * 		4- EVALUACION
		 * 			4.0- Genera batallas
		 * 			4.1- Ejecuta Robocode y genera resultados para 1 unico robot (cada 10 ejecuciones se para 2 segundos para no sobrecargar el sistema)
		 * 			4.2- Lee y discrimina los ficheros, acumula el resultado discriminado
		 * 			4.3- Actualiza el fitness del robot
		 * 			4.4- Destruye los resultados
		 * @param args
		 */
	public static void main(String [] args){

			//////////////////////////////////////////////////////////////////////////
			System.out.printf("\nINICIALIZACION");
			////////////////////////////////////////////////////////////////////////

			AGTest Dis = new AGTest();
			Individuo [] Poblacion = new Individuo [TamaPob];
			int acumulador = 0;
			Dis.generacion = 0;
			Dis.batalla = 0;
			for(int i = 0 ; i < TamaPob ; i++){
				Poblacion[i] = new Individuo(generacion, i);
			}
			FileGenerator Fg = new FileGenerator (Poblacion[0]);
			
			Scanner entrada = new Scanner(System.in);
			
			/* ESCRITURA DE COMPORTAMIENTOS */

			//////////////////////////////////////////////////////////////////////////
			System.out.printf("\nESCRIBIENDO COMPORTAMIENTOS");
			////////////////////////////////////////////////////////////////////////

			for(int i = 0 ; i < TamaPob ; i++){
				Fg.next(Poblacion[i]);
				Fg.writeFile();
			}
			//////////////////////////////////////////////////////////////////////////
			System.out.printf("\n*");
			////////////////////////////////////////////////////////////////////////
	//entrada.next();
			
			/* COMPILACION */

			//////////////////////////////////////////////////////////////////////////
			System.out.printf("\nCOMPILANDO");
			////////////////////////////////////////////////////////////////////////

			
			Fg.next(Poblacion[0]);
			Dis.compileThis();
			

			//////////////////////////////////////////////////////////////////////////
			System.out.printf("\n*");
			////////////////////////////////////////////////////////////////////////
	//entrada.next();
			
			/* EVALUACION */

			//////////////////////////////////////////////////////////////////////////
			System.out.printf("\nEVALUACION");
			////////////////////////////////////////////////////////////////////////

			for(int i = 0 ; i < TamaPob ; i++){
				Fg.next(Poblacion[i]);
				Dis.batalla = 0;
				acumulador = 0;

				//////////////////////////////////////////////////////////////////////////
				System.out.printf("\n\tRobocode");
				////////////////////////////////////////////////////////////////////////

				// 1- llama a robocode para evaluar un solo robot			
				for(int j = 0 ; j < TamaPob ; j++){
					Fg.genBattle(i, j, Dis.generacion, Dis.batalla);
					// cada 10 ejecuciones de robocode espera 2 segundos
					if(j%10 == 0)

						//////////////////////////////////////////////////////////////////////////
						System.out.printf("\n.");
						////////////////////////////////////////////////////////////////////////
					
					if(i != j){
						Dis.invocarRobocode();
					}
					
					// actualiza batalla
					Dis.batalla++;
				}

				//////////////////////////////////////////////////////////////////////////
				System.out.printf("\n\tlectura");
				////////////////////////////////////////////////////////////////////////

				// 2- lee y discrimina los resultados
				// espera 3 segundos
		
				for(int j = 0 ; j < Dis.batalla ; j++){
					if(i != j)
						acumulador = acumulador +Fg.getBattleResults("R"+j+".txt", "G"+generacion+"N"+i+".java");
				}
				// 3- actualiza el fitness
				Poblacion[i].setFitness(acumulador);
				
				// 4- destruye resultados
				Fg.removeRes();

				//////////////////////////////////////////////////////////////////////////
				System.out.printf("\nFIN");
				////////////////////////////////////////////////////////////////////////

			}
	}
	
	public static void main2(String [] args){
		try {
			Runtime.getRuntime().exec("java -Xmx512M -Dsun.io.useCanonCaches=false -cp libs/robocode.jar robocode.Robocode -battle battles/ -results resultados/");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
