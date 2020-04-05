package AGRobocode;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import AGRobocode.Configuracion;
import AGRobocode.Individuo;
import AGRobocode.HiloRoboCode;
import AGRobocode.FileGenerator;


public class AGTestSinHilos {

	
	static final int TamaPob = 8;
	static int generacion, batalla;
	
	
	/**
	 * 
	 * @param generation
	 */
	public void compileThis(int generation){
		/* declaracion de variables e inicializacion */
		Individuo I = new Individuo(1,1);
		FileGenerator Fg = new FileGenerator(I);
		File F = null;										// para obtener nuevas direcciones de fichero
		String Aux = new String();							// cadena de comparacion
		String [] Command = new String[5];					// al array hay que agregarle en ultimo lugar la direccion del fichero a compilar
		Aux = "G"+Integer.toString(generation);
		
		/* compara los nombres y los compila si se requiere */
		for (int i = 0 ; i < TamaPob ; i++){
			F = Fg.getNewPath(generation, i);					//obtiene una nueva dirección
			if(F.getName().indexOf(Aux) != -1){				//condicion de compilacion
				Command = Fg.getCompilationCmd(generation, i);
				try {
					Runtime.getRuntime().exec(Command);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	/**
	 * 
	 */
	
	public void invocarRobocode(){
		String[] Comando = new String [11];
		for(int i = 0 ; i < 11 ; i++){
			Comando[i] = new String(Configuracion.getInvokeCmd()[i]);
			System.out.printf(Configuracion.getInvokeCmd()[i]);
		}
		Comando[7] = Configuracion.getInvokeCmd()[7]+ "G" + Integer.toString(generacion)  + "B" + Integer.toString(batalla) + ".battle";
		Comando[9] = Configuracion.getInvokeCmd()[9] + "R" + Integer.toString(batalla) + ".txt";
	
		//ejecuta robocode
		Process RoboCode = null;
		try {
			RoboCode = new ProcessBuilder(Comando[0], Comando[1], Comando[2], Comando[3], Comando[4], Comando[5], Comando[6], Comando[7], Comando[8], Comando[9], Comando[10]).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.printf("\n-------------------\nfin proceso\n-------------------\n");
	}
	
	
	
	/**
	 * 
	 *
	public Individuo [] selection(Individuo [] Poblacion){
		/* variables 
		int i, seleccionado = -1;
		boolean repetido = false;
		double relacion, suma = 0, pesos [];
		pesos = new double [TamaPob];					//contendra la ruleta, es decir, si el primero vale 2 y el segundo vale 5, en pesos[segundo] = 7
		Random Generator = new Random();
		Individuo Padres[] = new Individuo [Configuracion.getNPad()];
		for( i = 0 ; i < Configuracion.getNPad() ; i++){
			Padres[i] = new Individuo();
		}
		/* primero calcula la relacion de porciones 
		for( i = 0 ; i < TamaPob ; i++)
			suma = suma + Poblacion[i].getFitness();
		relacion = 1/suma;
		suma = 0;
		for( i = 0 ; i < TamaPob ; i++){				//multiplica la relacion (regla de 3) para que la resultante salga 1
			pesos[i] = relacion*Poblacion[i].getFitness();
			suma = suma + pesos[i];
			pesos[i] = suma;											//en el vector se muestran los resultados de las sumas de los elementos anteriores
		}
		/* seleccion de padres *
		i = 0;
		do{		//selecciono los padres necesarios
			relacion = Math.abs(Generator.nextFloat()%1);			//seleccion aleatoria del individuo
			for(int j = 0 ; j < TamaPob ; j++){						
				if(relacion <= pesos[j] && seleccionado < 0)		// si el valor de seleccionado es menor o igual que el peso del elemento y es el primero que cumple esta condicion 
					seleccionado = j;								// se selecciona
			}
			if(seleccionado < 0) { System.out.printf("\nERROR AL SELECCIONAR PADRES"); }	
			else{
				for(int j = 0 ; j < i ; j++){							// comprueba si se repite
					if(Padres[j].getPosition() == Poblacion[seleccionado].getPosition())
						repetido = true;
				}
				if(repetido == false){									// si no se repite se integra
					Padres[i].setNew(Poblacion[seleccionado]);
					seleccionado = -1;
					i++;
				}
				else
					repetido = false;									// si se repite no se actualiza la variable de control
			}
		}while(i<Configuracion.getNPad());
		return Padres;
	}
	
	/**
	 * Operador de cruce
	 * 
	 * 	En Individuo se invoca al operador de mutacion al generar los descendientes
	 * 
	 * Funcionamiento:
	 * 
	 * 0 - previamente a la generacion de hijos deben introducirse los elementos elite, ocupando el 10% de la capacidad del array.
	 * 1 - en las primeras generaciones se aplica un algoritmo de cruce que fomenta la diversidad; a partir de la mitad de las generaciones totales esperadas se cambia por otro algoritmo de cruce que provoca mayor convergencia.
	 * 2 - desde uno de los padres se invoca el metodo de cruce correspondiente.
	 * 3 - el resultado obtenido se introduce en el array de hijos.
	 * 
	 */
	public void cross(Individuo Padres [], Individuo [] Hijos, int posicion){
		Individuo [] Generados = null;
		if(generacion == 0)
			Generados = Padres[0].crossDiv(Padres[1]);
		else
			Generados = Padres[0].crossCon(Padres[1]);
		/* actualiza posiciones e inserta en el array */
		Generados[0].setPosition(posicion);
		Hijos[posicion] = Generados[0];
		//incrementPos();
		Generados[1].setPosition(posicion+1);
		Hijos[posicion+1] = Generados[1];
		//incrementPos();
	}
	
	
	/**
	 * Funcion elitismo
	 * El 10% de la poblacion con mayor fitness se etiqueta como elite
	 * Este grupo presenta las siguientes características:
	 * 		-Mayor probabilidad de reproduccion (implicito en la funcion de seleccion).
	 * 		-PROPUESTA: Susceptible a la mutacion (si muta se extrae de este grupo hasta que se evalue nuevamente) NO IMPLEMENTADA
	 * 		-Inmunidad al reemplazamiento.
	 * 
	 * Para extraer el 10% mas apto:
	 * 		-copiamos los fitness de la poblacion en un vector
	 * 		-averiguamos el mejor llamando a la funcion max
	 * 		-marcamos como elite a ese individuo en la poblacion original y lo quitamos de la copia
	 * 		-repetimos hasta haber marcado los individuos necesarios
	 */
	public void elitism (Individuo [] Poblacion, Individuo [] Elite){
		int requeridos = 2;
		int aux;
		Individuo CopyPop[] = new Individuo [TamaPob];
		
		for(int i = 0 ; i < TamaPob ; i++){
			CopyPop[i] = new Individuo(-1, -1);
		}
	//	FileGenerator Fg = null;
		
		System.out.printf("\n----------------------------\nELITISM ----> COPIANDO INDIVIDUOS\n");
		
		for(int i = 0 ; i < TamaPob ; i++){
			
			System.out.printf("\nCOPIANDO INDIVIDUO %d\nPoblacion[%d].fitness = "+Poblacion[i].getFitness()+"\n", i, i);
			CopyPop[i].setNew(Poblacion[i]);					//copiamos la poblacion
			Poblacion[i].setElite(false);							//quitamos todo el conjunto elite hasta ahora
			
			System.out.printf("\nCopyPop[%d].fitness = "+CopyPop[i].getFitness()+"\n", i);
		}
		for(int i = 0 ; i < requeridos ; i++){				//en cada vuelta se marca un elite
			aux = max(CopyPop, TamaPob);
			Poblacion[aux].setElite(true);							//marcamos en la poblacion original
			CopyPop[aux].setFitness(-1);							//quitamos al mejor de la poblacion copia
			Elite[i] = Poblacion[aux];								//agregamos al elite en la poblacion elite
		}
		/* escribe el fichero de report de los individuos elite *
		if(generacion%10000 == 0){							//condicion de report
			Fg = new FileGenerator(Elite[0]);
			Fg.genLogElite(Elite);
		}*/
	}
	
	

	/**
	 * Devuelve la posicion del elemento con mayor fitness
	 * @param Padres
	 * @param tama
	 * @return
	 */
	public int max (Individuo Poblacion [], int tama){
		int max = -1000;
		int posicion = -1;
		for(int i = 0 ; i < tama; i++){
			if(max < Poblacion[i].getFitness()){
				max = Poblacion[i].getFitness();
				posicion = i;
			}
		}
		return posicion;
	}
	
	

	/**
	 * Estrategia de reemplazamiento
	 * 
	 * Se reemplazaran todos los individuos no elite. Para ello se debe haber generado una cantidad de hijos igual al 90% de la poblacion.
	 * 
	 * 		0- REQUISITOS : tienen que haberse marcado los elite y producida la nueva generacion
	 * 		1- copia los elementos elite en el array de la nueva generacion, actualizando sus parametros (generacion, posicion y fitness)
	 * 		2- borra los ficheros de comportamiento de la generacion anterior.
	 * 		3- borra los ficheros de resultado y de batalla.
	 * 		4- genera ficheros de comportamiento de la nueva generacion.
	 * 		5- actualiza parametros. 
	 * 		6- reemplaza la generacion anterior.
	 * 
	 */
	public void replacement(Individuo [] Hijos, Individuo []Elite){
		/* inicializacion */
		FileGenerator Fg = new FileGenerator(Elite[0]);
		/* copia los elite */
		for(int i = 0 ; i < Configuracion.getNElite() ; i++){
			Hijos[i] = Elite [i];									// actualizamos en el array hijos
			Hijos[i].setGeneration(generacion+1);
			Hijos[i].setPosition(i);
			Hijos[i].setFitness(-1000);
		}
		/* Borra ficheros de comportamiento de la generacion anterior */
		//deleteGenFiles();
		/* Borra los ficheros de resultado y batalla */
		//Fg.removeBat(generacion);
		//Fg.removeRes();
		generacion++;
		/* Genera ficheros de comportamiento de la nueva generacion */
		//writeGenFiles();
		/* Reemplaza la generacion anterior */
		//Poblacion = Hijos;
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
	 * 		5- SELECCION
	 * 		6- CRUCE
	 * 		7- ELITISMO
	 * 		8- REEMPLAZAMIENTO
	 * 
	 * @param args
	 */
	public static void main1(String [] args){

		//////////////////////////////////////////////////////////////////////////
		System.out.printf("\nINICIALIZACION");
		////////////////////////////////////////////////////////////////////////

		AGTestSinHilos Dis = new AGTestSinHilos();
		Individuo [] Poblacion = new Individuo [TamaPob];
		Individuo [] Padres = null;
		Individuo [] NuevaGeneracion= new Individuo [TamaPob];
		int acumulador = 0;
		Dis.generacion = 0;
		Dis.batalla = 0;
		File [] F = new File[TamaPob];
		for(int i = 0 ; i < TamaPob ; i++){
			Poblacion[i] = new Individuo(generacion, i);
			F[i] = new File(Configuracion.getRPath()+"R"+i+".txt");
		}
		FileGenerator Fg = new FileGenerator (Poblacion[0]);
		
		Runtime Basurero = Runtime.getRuntime();
		
		
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
		Dis.compileThis(generacion);
		

		//////////////////////////////////////////////////////////////////////////
		System.out.printf("\n*");
		////////////////////////////////////////////////////////////////////////
//entrada.next();
		
		/* EVALUACION */

		//////////////////////////////////////////////////////////////////////////
		System.out.printf("\nEVALUACION");
		////////////////////////////////////////////////////////////////////////

		for(int i = 0 ; i < 1 ; i++){
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
	//			if(j%10 == 0)

					//////////////////////////////////////////////////////////////////////////
		//			System.out.printf("\n.");
					////////////////////////////////////////////////////////////////////////
				
				if(i != j){
					Dis.invocarRobocode();
				}
				
				// actualiza batalla
				//Dis.batalla++;
			}

			//////////////////////////////////////////////////////////////////////////
			System.out.printf("\n\tlectura");
			////////////////////////////////////////////////////////////////////////

			// 2- lee y discrimina los resultados
			
			
			// esperamos que todos los ficheros se escriban
			for(int j = 0 ; j < Dis.batalla ; j++){
				//if(i!=j)
				if(j!=0)
					while(F[j].exists()!=true){
						try {
							TimeUnit.SECONDS.sleep(2);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
			}
			
			
			Dis.batalla = TamaPob;
			
			for(int j = 0 ; j < Dis.batalla ; j++){
				//if(i != j){
				if(j!=0){
					System.out.printf("\nLeyendo archivo ---> R"+j+".txt");
		//			acumulador = acumulador +Fg.getBattleResults("R"+j+".txt", "G"+generacion+"N"+i+".java");
					acumulador = acumulador +Fg.getBattleResults("R"+j+".txt", "G"+generacion+"N"+0+".java");
					Basurero.gc();
				}	
			}		
			System.out.printf("\n\nacumulador = %d", acumulador);
			// 3- actualiza el fitness
			//Poblacion[i].setFitness(acumulador);
			Poblacion[0].setFitness(acumulador);
			// 4- destruye resultados
			Fg.removeRes();
			
			//////////////////////////////////////////////////////////////////////////
			//System.out.printf("\n\nFIN");
			////////////////////////////////////////////////////////////////////////
		}
		
		// actualizamos fitness del resto de la poblacion
		
		for(int i = 1 ; i < TamaPob ; i++)
			Poblacion[i].setFitness(Poblacion[0].getFitness()+4+i);
		
		/* SELECCION */
		
		System.out.printf("\n*");
	//	entrada.next();
		
		System.out.printf("\nSELECCION\nFitness = ");
		for(int i = 0 ; i < TamaPob ; i++ ){
			System.out.printf(Poblacion[i].getFitness()+" ");
		}
		
	//	Padres = Dis.selection(Poblacion);
		
		/* CRUCE */
		System.out.printf("\n*");
	//	entrada.next();
		
		System.out.printf("\nCRUCE\n");
		
		
		Dis.cross(Padres, NuevaGeneracion, 2); //LOS ELITE OCUPARAN LAS POSICIONES 0 Y 1 
		
		/* ELITISMO */
		System.out.printf("\n*");
		//entrada.next();
		
		System.out.printf("\nELITISMO\n");
		
		
		Dis.elitism(Poblacion, NuevaGeneracion);
		
//////////////////////////////////////////////////////////////////////////
System.out.printf("\n\nFIN");
////////////////////////////////////////////////////////////////////////

	}
	
	
	
public Individuo [] selection(Individuo [] Poblacion){
		
		System.out.printf("\nSelection");
		
		/* variables */
		int i, seleccionado = -1;
		boolean repetido = false;
		double relacion, suma = 0, pesos [];
		pesos = new double [Configuracion.getTPob()];//contendra la ruleta, es decir, si el primero vale 2 y el segundo vale 5, en pesos[segundo] = 7
		Random Generator = new Random();
		Individuo Padres[] = new Individuo [Configuracion.getNPad()];
		for(i = 0 ; i < Configuracion.getNPad(); i++)
			Padres[i] = new Individuo(-1, -1);
		/////////////////////////////////////////////////////////
		System.out.printf("\n\tVector de pesos:");
		
		/* primero calcula la relacion de porciones */
		for(i = 0 ; i < Configuracion.getTPob() ; i++)
			suma = suma + Poblacion[i].getFitness();
		relacion = 1/suma;
		suma = 0;
		for(i = 0 ; i < Configuracion.getTPob() ; i++){				//multiplica la relacion (regla de 3) para que la resultante salga 1
			pesos[i] = relacion*Poblacion[i].getFitness();
			suma = suma + pesos[i];
			pesos[i] = suma;											//en el vector se muestran los resultados de las sumas de los elementos anteriores
			
			/////////////////////////////////////////////////////
			//System.out.printf("\n\t\t%d = %f",i, pesos[i]);
			
		}
		
		
		System.out.printf("\n\tseleccion de padres");
		
		/* seleccion de padres */
		i = 0;
		do{				//selecciono los padres necesarios
			
			//////////////////////////////////////////////
			System.out.printf("\n\t\tTirando dado...");
			
			
			relacion = Math.abs(Generator.nextFloat());						//seleccion aleatoria del individuo
			
			
			//////////////////////////////////////////////
			System.out.printf("resultado = %f", relacion);
			
			
			for(int j = 0 ; j < Configuracion.getTPob() ; j++){					//recorro el vector pesos
				if(relacion <= pesos[j] && seleccionado < 0)								//para cuando la suma de las porciones supera el limite
					seleccionado = j;
			}
			
			
			//////////////////////////////////////////////
			System.out.printf("\n\t\tPadre seleccionado = %d",seleccionado);
			
			
			
			if(seleccionado < 0) { System.out.printf("\nERROR AL SELECCIONAR LOS PADRES"); }
			else{
				for(int j = 0 ; j < i ; j++){
					if(Padres[j].getPosition() == Poblacion[seleccionado].getPosition()){
						
						
						System.out.printf("\nj = %d\tseleccionado = %d", j, seleccionado);
						System.out.printf("\nPadres[j].getPosition() = "+Padres[j].getPosition()+"\tPoblacion[seleccionado].getPosition() = "+Poblacion[seleccionado].getPosition());
						
						repetido = true;
					}
				}
				
				System.out.printf("\t\t---->Repetido = "+repetido);
				
				if(repetido == false){
					Padres[i].setNew(Poblacion[seleccionado]);					//rellena el vector de padres seleccionados
					i++;
				}
				else
					repetido = false;
				seleccionado = -1;
			}
		}while(i < Configuracion.getNPad());
		
		System.out.printf("\nSale de selection");
		
		return Padres;
	}


	/**
	 * Testea el funcionamiento de selection
	 * @param args
	 */
	public static void main(String[] args){
	//	Scanner Entrada = new Scanner(System.in);
		AGTestSinHilos Ag = new AGTestSinHilos();
		Individuo [] Poblacion = new Individuo [Configuracion.getTPob()];
		for(int i = 0 ; i < Configuracion.getTPob(); i++){
			Poblacion[i] = new Individuo(1000, i);
			Poblacion[i].setFitness(i+100);
		}
		Individuo [] Padres = new Individuo[Configuracion.getNPad()];
		for(int i = 0 ; i < Configuracion.getNPad() ; i++){
			Padres[i] = new Individuo(1000, i+1);
		}
		int contador = 0;
		
		
		do{
		Padres = Ag.selection(Poblacion);
	//Entrada.next();
		System.out.printf("Padres Seleccionados = ");
		for(int i = 0 ; i < Configuracion.getNPad() ; i++){
			System.out.printf("\n\t"+Padres[i].getName());
		}
		contador++;
		}while(contador < 100);
	}
	
	
	
	
}
