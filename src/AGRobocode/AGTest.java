package AGRobocode;

import java.io.IOException;
import java.util.Random;

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
	
	
	/**
	 * Comprueba si un entero se encuentra dentro de un array de enteros
	 */
	public static boolean estaRepe(int i, int [] iii){
		for(int j = 0 ; j < 3 ; j++){
			if(i == iii[j])
				return true;
		}
		return false;
	}
	
	/**
	 * Invoca robocode
	 * @param batalla
	 */
	
	public static void invocarRobocode (int batalla){
		String [] Cmd = new String [10];
		for(int i = 0 ; i < 10 ; i++){	Cmd[i] = "";	}
		
		Cmd = Configuracion.getInvokeCmdDisplayed();
		
		Cmd[7] = Cmd[7] + "B" + Integer.toString(batalla) + ".battle";
		Cmd[9] = Cmd[9] + "R" + Integer.toString(batalla) + ".txt";
		
		//////////////////////////////////////////////////////////////////////////
		System.out.printf("\nINVOCANDO ROBOCODE\n");
		
		
		try {
			Runtime.getRuntime().exec(Cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * testeo del funcionamiento del algoritmo a pequeña escala
	 * 
		 *  1 - Inicializa
		 *  2 - Evaluacion
		 *  3 - Seleccion
		 *  4 - Cruce y mutacion
		 *  5 - Elitismo
		 *  6 - Reemplazamiento
		 *  7 - Repetir desde el paso 2 hasta que se cumpla el criterio de parada
		 *
	 */
	public static void main(String [] args){
		
		/* inicializacion */
		
		int tPob = 10;
		int nCombatientes = 3;
		int aux;
		int seleccionado;
		int contadorBatalla = 0;
		int [] seleccionados = new int[nCombatientes];
		Random Rg = new Random();
		Individuo [] Poblacion = new Individuo [tPob];
		for(int i = 0 ; i < tPob ; i++){ Poblacion[i] = new Individuo(1, i); }
		FileGenerator Fg = new FileGenerator(Poblacion[0]);
		Individuo Seleccionado = null;
		Individuo [] Combatientes = new Individuo [nCombatientes];
		
		
		/* escritura ficheros comportamiento *
		
		for(int i = 0 ; i < tPob ; i++){
			Fg.next(Poblacion[i]);
			Fg.writeFile();
		}
		
		/* compilacion ficheros comportamiento */
		Fg.compileThis(Poblacion[0].getGeneration());
		
		/* testeo del algoritmo genetico */
		
		/* evaluacion */
		
		for(int i = 0 ; i < tPob ; i++){				//recorre la poblacion
			aux = 0;
			// selecciono 3 individuos al azar
			do {
				seleccionado = Math.abs(Rg.nextInt()%tPob);
				seleccionados[aux] = seleccionado;			// almacena los indices
				aux++;
			} while(aux < nCombatientes);
			
			aux = 0;								// aux actua como acumulador
			
			// procedemos a la evaluacion
			for(int j = 0 ; j < nCombatientes ; j++){
				// escritura fichero de batalla
				Fg.next(Poblacion[i]);
				Fg.genBattle(i, seleccionados[j], Poblacion[i].getGeneration(), contadorBatalla);
				
				// invocamos robocode
				invocarRobocode(contadorBatalla);
				
				// obtenemos resultados y acumulamos
			//	aux = aux + Fg.getBattleResults(Configuracion.getRPath()+"R"+Integer.toString(contadorBatalla)+".txt", Configuracion.getFilePath()+"G"+Integer.toString(Poblacion[i].getGeneration())+"N"+Integer.toString(Poblacion[i].getPosition()));
				
				// destruimos fichero batalla
			//	Fg.removeBat(Poblacion[i].getGeneration());
				
				// actualizacion variables
				contadorBatalla++;
			}
			// eliminacion ficheros resultado
			
			/* seleccion */
			/* cruce */
			/* elitismo */
			/* reemplazamiento */
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
