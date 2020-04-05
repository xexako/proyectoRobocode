package AGRobocode;

import AGRobocode.Individuo;

import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import AGRobocode.Configuracion;


/**
 * Algoritmo genetico 
 * @author Jesus
 */
public class AG {
	
	/* ---------------- constructores ---------------- */
	
	/**
	 * Constructor
	 */
	public AG(){
		//poblacion inicial, los robots estan listos
		Poblacion = new Individuo [Configuracion.getTPob()];				// array de individuos, poblacion
		Hijos = null;														// poblacion temporal con la nueva generacion
		Elite = null;														// poblacion elite, tamaño 10% poblacion
		generacion = 0;
		posicion = 0;
		resetBattleCounter();
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			Poblacion[i] = new Individuo(generacion, posicion);
			incrementPos();
		}
		
		posicion = 0;
	}
	
	/* ---------------- métodos genéticos ---------------- */
	
	
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
	public void cross(Individuo Padres []){
		Individuo [] Generados = new Individuo[Configuracion.getNPad()];
		if(generacion <= (int) (Configuracion.getCritParada()/2))
			Generados = Padres[0].crossDiv(Padres[1]);
		else
			Generados = Padres[0].crossCon(Padres[1]);
		/* actualiza posiciones e inserta en el array */
		for(int i = 0 ; i < Configuracion.getNPad(); i++){
			Generados[i].setPosition(posicion);
			Hijos[posicion].setNew(Generados[i]);
			incrementPos();
		}
	}
	
	/**
	 * Estrategia de seleccion
	 * A cada individuo se le da una porción de ruleta directamente proporcional con el valor fitness.
	 * La suma de todas las porciones equivale a 1.
	 * Genera un numero aleatorio entre 0 y 1.
	 * Recorre el vector de valores proporcionales y se para cuando la suma excede el valor dado.
	 * 	
	 */
	public Individuo [] selection(){
		
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
//		System.out.printf("\n\tVector de pesos:");
		
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
	//		System.out.printf("\n\t\t%d = %f",i, pesos[i]);
			
		}
		
		
	//	System.out.printf("\n\tseleccion de padres");
		
		/* seleccion de padres */
		i = 0;
		do{				//selecciono los padres necesarios
			
			//////////////////////////////////////////////
		//	System.out.printf("\n\t\tTirando dado...");
			
			
			relacion = Math.abs(Generator.nextFloat());						//seleccion aleatoria del individuo
			
			
			//////////////////////////////////////////////
		//	System.out.printf("resultado = %f", relacion);
			
			
			for(int j = 0 ; j < Configuracion.getTPob() ; j++){					//recorro el vector pesos
				if(relacion <= pesos[j] && seleccionado < 0)								//para cuando la suma de las porciones supera el limite
					seleccionado = j;
			}
			
			
			//////////////////////////////////////////////
	//		System.out.printf("\n\t\tPadre seleccionado = %d",seleccionado);
			
			
			
			if(seleccionado < 0) { System.out.printf("\nERROR AL SELECCIONAR LOS PADRES"); }
			else{
				for(int j = 0 ; j < i ; j++){
					if(Padres[j].getName().compareTo(Poblacion[seleccionado].getName())==0){
						repetido = true;
						
/*						
						try {
							TimeUnit.SECONDS.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						System.out.printf("\n<<<<<<<IGUALES!>>>>>>>"+Padres[j].compareThis(Poblacion[seleccionado]));
*/					}
					else{
					
						
					}
				}
				
				if(repetido == false){
					Padres[i].setNew(Poblacion[seleccionado]);					//rellena el vector de padres seleccionados
					i++;
				}
				else
					repetido = false;
				seleccionado = -1;
			}
		}while(i < Configuracion.getNPad());
		
		
/*		for(i = 0 ; i < Configuracion.getNPad() ; i++){
			System.out.printf("\nPadre[%d] = "+Padres[i].getName(), i);
		}
	*/	
		return Padres;
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
	public void replacement(FileGenerator Fg){
		
		/* copia los elite */
		for(int i = 0 ; i < Configuracion.getNElite() ; i++){
			Hijos[i].setNew(Elite [i]);									// actualizamos en el array hijos
			Hijos[i].setGeneration(generacion+1);
			Hijos[i].setPosition(i);
			Hijos[i].setFitness(-1000);
		}
		
		
		
		//////////////////////////////////
		/*
		System.out.printf("\n******************************************************");
		System.out.printf("\nHijos + elite:\n");
		for(int i = 0 ; i < Configuracion.getTPob() ; i++)
			System.out.printf("\n\t%d = "+Hijos[i].getName(), i);
		System.out.printf("\n******************************************************");
		*/
		///////////////////////////////////
		
		
		/* Borra ficheros de comportamiento de la generacion anterior */
		Fg.deleteOld(generacion);
		/* Borra los ficheros de resultado y batalla */
		Fg.removeBat(generacion);
		Fg.removeRes();
		Fg.deleteClasses(generacion);
		generacion++;

		/* Reemplaza la generacion anterior */
		for(int i = 0 ; i < Configuracion.getTPob() ; i++)
			Poblacion[i].setNew(Hijos[i]);

		/* Genera ficheros de comportamiento de la nueva generacion */
		writeGenFiles(Fg);		
	}
	
	/**
	 * Criterio de parada
	 * 
	 * 
	 * 
	 */
	
	
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
	 * 
	 * 
	 * 
	 * REVISAR
	 * 
	 * 
	 * 
	 */
	public void elitism (){
		
		/*System.out.printf("\n---------------------------\nPoblacion de hijos antes del elitismo:");
		
		for(int i = 0 ; i < Configuracion.getTPob(); i++){
			System.out.printf("\n%d "+Hijos[i].getName(), i);
		}*/
		
		int aux;
		Individuo CopyPop[] = new Individuo [Configuracion.getTPob()];
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){	CopyPop[i] = new Individuo(-1, -1); }
		
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			CopyPop[i].setNew(Poblacion[i]);								// copiamos la poblacion
			Poblacion[i].setElite(false);									// quitamos todo el conjunto elite hasta ahora
		}
		for(int i = 0 ; i < Configuracion.getNElite() ; i++){				// en cada vuelta se marca un elite
			aux = max(CopyPop);
			Poblacion[aux].setElite(true);									// marcamos en la poblacion original
		//	CopyPop[aux].setFitness(-1);									// quitamos al mejor de la poblacion copia
			Elite[i].setNew(Poblacion[aux]);								// agregamos al elite en la poblacion elite
		}
	}
	
	/* ---------------- metodos auxiliares ---------------- */
	
	/**
	 * Devuelve la posicion del individuo con mayor fitness de un grupo de Individuos
	 * 
	 */
	public int max (Individuo Padres []){
		int max = -1000;
		int posicion = -1;
		for(int i = 0 ; i < Configuracion.getTPob(); i++){
			if(max < Padres[i].getFitness() && Padres[i].getElite() == false){
				max = max + Padres[i].getFitness();
				posicion = i;
			}
		}
		return posicion;
	}
	
	
	/**
	 * Crea los ficheros de comportamiento de los robots de una generacion
	 * 
	 * El contenido de los ficheros estara determinado por la configuracion de los robots
	 * 
	 * Cada robot de una generacion tiene una etiqueta del tipo:
	 * 			"G"numero de la generacion"N"posicion del robot dentro de la poblacion
	 * G1N10 sera de la generacion 1, posicion 10;
	 * G27N66 pertenecera a la generacion 27, posicion 66, etc.
	 * 
	 * Los ficheros se guardaran en la ruta de Robocode por defecto para los robots a ejecutar.
	 * 
	 */
	public void writeGenFiles(FileGenerator Fg){
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			Fg.next(Poblacion[i]);
			Fg.writeFile();
		}
	}
	
	/**
	 * Destruye los ficheros de comportamiento de la generacion anterior a la actual.
	 * 
	 * NOTA : esta funcion debe llamarse SIEMPRE DESPUES DE GENERAR UNA NUEVA POBLACION
	 * 
	 * 
	 * REVISAR
	 * 
	 * 
	 *
	public void deleteGenFiles(){
		FileGenerator Fg = new FileGenerator(Poblacion[0]);
		Fg.deleteOld(generacion);
	}
	
	/**
	 * Actualiza la posicion
	 * 
	 * 
	 * CONFIGURACION DE TESTEO
	 */
	public void incrementPos(){
		posicion++;
		if(posicion >=Configuracion.getTPob())
			posicion = Configuracion.getNElite();							// los 10 primeros puestos del array siempre estaran ocupados por los elite de la generacion anterior
	}
	
	/**
	 * Incrementa el contador de batalla
	 */
	public void incrementBattleCounter() { contadorBatalla++; }
	
	/**
	 * Resetea el contador de batalla
	 */
	public void resetBattleCounter(){ contadorBatalla = 0; }
	
	/**
	 * Resetea el indicador de posicion
	 */
	public void resetPosition(){ posicion = Configuracion.getNElite(); }
	
	/**
	 * llama a robocode para que se enfrenten dos individuos cargando un fichero de batalla
	 * 
	 * NOTA : los ficheros deben estar compilados antes de llamar a esta funcion, y los de batalla de la generacion anterior destruidos
	 * 
	 * **************REVISAR*****************
	 * 
	 * 
	 * Configuracion.getInvokeCmd y su homonima necesitan que se les pase el nombre del fichero de batalla(elemento 8) y el de resultados (elemento 10)
	 * 
	 */
	public void invokeRoboCode(boolean record){
		String [] Cmd = new String[13];					// cadena de comando 
		/* obtiene el comando para ejecutar Robocode */
		for(int i = 0 ; i < 13 ; i++){
			Cmd[i] = new String(Configuracion.getInvokeCmd()[i]);
		}
		Cmd[7] = Cmd[7]+"G"+Integer.toString(generacion)+"B"+Integer.toString(contadorBatalla)+".battle";
		Cmd[9] = Cmd[9]+"R"+Integer.toString(contadorBatalla)+".txt";
		if(record == true){
			// graba las batallas de la primera y de la ultima generacion
			Cmd[11] = Cmd[11] + "-record";
			Cmd[12] = Cmd[12] + "C:/robocode/reports/ReportGen"+generacion+"Bat"+contadorBatalla+".br";
		}
			
		/* invoca Robocode en segundo plano */
		try {
			Runtime.getRuntime().exec(Cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Devuelve un Individuo.
	 * @param posicion del Individuo en la población.
	 * @return Individuo seleccionado
	 */
	public Individuo getIndividuo(int individuo){ return Poblacion[individuo]; }
	
	
	
	/**
	 * selecciona los individuos que van a enfrentarse
	 * comprobando no se enfrentan los mismos individuos
	 * 
	 * 		1- Selecciona un grupo de Individuos.
	 * 			1.1- Comprueba que no se enfrenta un Individuo consigo mismo
	 * 		2- Genera ficheros de batalla
	 * 		3- Ejecuta Robocode
	 * 		(4- Espera que los ficheros de resultado se generen.)
	 */
	public void setFight(){
		Individuo [] Enemigos = new Individuo [Configuracion.getTTor()];
		for(int i = 0 ; i < Configuracion.getTTor(); i++){
			Enemigos[i] = new Individuo(-1,-1);				// establece este individuo temporalmente
		}
		Random Rg = new Random();
		FileGenerator Fg = new FileGenerator(Poblacion[0]);	// establece este individuo temporalmente
		int aux = -1;
		int enemigo, contador = 0;
		boolean seleccionado;
		
		/* genero ficheros de batalla */
		for(int j = 0; j < Configuracion.getTPob(); j++){
			enemigo = 0;
			/* selecciono enemigos */
			do{
				seleccionado = true;
				aux = Math.abs(Rg.nextInt()%Configuracion.getTPob());		// elige la posición del enemigo
				for(int i = 0 ; i < enemigo ; i++){
					if(aux == Enemigos[i].getPosition()||aux == posicion)	// comprueba si se repite 
						seleccionado = false;
				}
				if(seleccionado == true){									// si no se repite, se selecciona para el combate
					Enemigos[enemigo].setNew(Poblacion[aux]);
					enemigo++;
				}
			}while(enemigo < Configuracion.getTTor());
		
			/* generación ficheros de batalla */
			for(int i = 0 ; i < Configuracion.getTTor(); i++){
				Fg.next(Enemigos[i]);
				Fg.genBattle(posicion, Enemigos[i].getPosition(), generacion, contador);
				contador++;
			}
			aux = -1;
		}
	}
	
	/**
	 * Genera los ficheros de informe de los robots
	 */
	public void genLog(FileGenerator Fg){
		Fg.writeLog(Poblacion);
	}
	
	
	/**
	 * Actualiza el valor fitness para un Individuo
	 */
	public void setFitness(int individuo, int fitness) {
		Poblacion[individuo].setFitness(fitness);
	}
	
	/**
	 * Libera la memoria usada por la poblacion
	 */
	public void letsFreeThis(){
		for(int i = 0 ; i < Configuracion.getTPob() ; i++)
			Poblacion[i] = null;
		Poblacion = null;
	}
	
	/**
	 * Libera la memoria usada por los hijos
	 */
	public void freeChildren(){
		for(int i = 0 ; i < Configuracion.getTPob() ; i++)
			Hijos[i] = null;
		Hijos = null;
	}
	
	/**
	 * Libera la memoria usada por los elite
	 */
	public void freeElite(){
		for(int i = 0 ; i < Configuracion.getNElite() ; i++)
			Elite[i] = null;
		Elite = null;
	}
	
	/**
	 * inicicializa la poblacion
	 */
	public void reinitiatePob(){
		Poblacion = new Individuo [Configuracion.getTPob()];
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			Poblacion[i] = new Individuo(generacion, posicion);
		}
	}
	
	/**
	 * Inicializa el conjunto elite
	 */
	public void initiateElite(){
		Elite = new Individuo[Configuracion.getNElite()];
		for(int i = 0 ; i < Configuracion.getNElite(); i++)
			Elite[i] = new Individuo(-1, -1);
	}
	
	/**
	 * Inicializa la poblacion de hijos
	 */
	public void initiateChildren(){
		Hijos = new Individuo [Configuracion.getTPob()];
		for(int i = 0 ; i < Configuracion.getTPob() ; i++)
			Hijos[i] = new Individuo(-1, -1);
	}
	
	/**
	 * Devuelve la generacion
	 */
	public int getGeneration() { return generacion; }
	
	/**
	 * Devuelve la posicion
	 */
	public int getPosition() { return posicion; }
	
	/**
	 * Devuelve la poblacion
	 */
	public Individuo[] getPob() { return Poblacion; }
	

	/**
	 * Devuelve el contador de batalla.
	 * @return contador de batalla
	 */
	public int getBattleCounter() { return contadorBatalla; }

	
	/**
	 * Muestra por pantalla los nombres de los Individuos de esta generación.
	 */
	public void printGeneration(){
		for(int i = 0 ; i < Configuracion.getTPob() ; i++)
			System.out.printf("\nIndividuo %d = "+Poblacion[i].getName(), i);
	}
	
	/* ---------------- variables ---------------- */
	
	/**
	 * Poblacion de robots
	 */
	static private Individuo Poblacion [];
	
	/**
	 * Hijos generados
	 */
	static private Individuo Hijos [];
	
	/**
	 * Poblacion elite
	 */
	static private Individuo Elite[];
	
	/**
	 * Contador de posicion para la nueva generacion
	 * Indica la siguiente posicion libre en el array Hijos
	 */
	static private int posicion;
	
	/**
	 * Contador de generacion
	 * Indica la generacion mas vieja en funcionamiento
	 */
	static private int generacion;
	
	/**
	 * Contador de batalla
	 * Indica la siguiente batalla que escribir
	 */
	static private int contadorBatalla;
	


	/**
	 * Ejecucion del algoritmo genetico:
	 * 
	 * 	1 - Inicializa
	 *  2 - Evaluacion
	 *  3 - Seleccion
	 *  4 - Cruce y mutacion
	 *  5 - Elitismo
	 *  6 - Reemplazamiento
	 *  7 - Repetir desde el paso 2 hasta que se cumpla el criterio de parada
	 * 
	 * 
	 * 
	 * @param args
	 */
	public static void main (String [] args){
		
		/* INICIALIZACION */
		
		AG Ag = new AG();
		FileGenerator Fg = new FileGenerator(Ag.getIndividuo(0));
		Runtime Basurero = Runtime.getRuntime();
		File F = null;
		int aux = 0;
		int acumulador = 0;
		int contador ;
		
		boolean record = false;
		Individuo [] Padres = new Individuo[Configuracion.getNPad()];
		for(int i = 0 ; i < Configuracion.getNPad() ; i++){
			Padres[i] = new Individuo();
		}
		
		//System.out.printf("\n\nCONFIGURACION DE TESTEO ACTIVADA\n\n");
		
		
		/*System.out.printf("\nGENERACION DE INDIVIDUOS");
		Fg.next(Ag.getIndividuo(0));		
		*/
		// escritura de los ficheros de comportamiento
		Ag.writeGenFiles(Fg);

		 
		
		 do{
			/*****************************************************************************************************************/
				
			// compilacion de los ficheros de comportamiento
			Fg.compileThis(Ag.getGeneration());
			
			/* EVALUACION */
			
			System.out.printf("\nEVALUACION");
			


			// generacion ficheros de batalla
				//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
				Ag.setFight();
				//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

				// guarda la informacion en un fichero
				Fg.saveThis(Ag.getPob());
				// libera la memoria usada por la poblacion
				Ag.letsFreeThis();
				Basurero.gc();
				
			contador = 0;
			 do{
			/******************************************/
				System.out.printf("\n\tEvaluando individuo %d", contador);
				
				aux = Ag.getBattleCounter();
				
				// decide si graba las batallas
				if(Ag.getGeneration() == 0 || Ag.getGeneration() == Configuracion.getCritParada()-1){
						record = true;
				}
						
			    // invoca a Robocode
				for(int i = 0 ; i < Configuracion.getTTor() ; i++){
					Ag.invokeRoboCode(record);

					// incremento contador de batalla
					Ag.incrementBattleCounter();

				}
				
				// espera a que se obtengan los resultados
				
				for(int i = aux ; i < Ag.getBattleCounter() ; i++){
						F = Fg.getNewResPath(i);
						System.out.printf("\n***************\nFICHERO EN ESPERA = \"R"+i+".txt\n***************\n");
						while(F.exists() != true && F.canRead() != true){
							try {
								TimeUnit.SECONDS.sleep(30);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
				}
				// espera
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				contador++;
				record = false;
			 }while(contador<Configuracion.getTPob());
			 /******************************************/	
				System.out.printf("\n\tObteniendo resultados...");
				
				// reinicia la poblacion
				Ag.reinitiatePob();
				
				// carga la informacion previamente guardada
				Fg.loadData(Ag.getPob());
				
				// inicializa hijos
				Ag.initiateChildren();
				
				// inicializa elite
				Ag.initiateElite();
				
				// extrae resultados
				////////////////////////////////////////////////////
				////////////////////////////////////////////////////
			//	for(int i = 0 ; i < Configuracion.getTTor(); i++){ Ag.incrementBattleCounter(); }				
				///////////////////////////////////////////////////
				///////////////////////////////////////////////////
			/*******************************************************************/
			contador = 0;
			do{
				acumulador = 0;
				for(int i = aux ; i < Ag.getBattleCounter() ; i++){
					acumulador = acumulador + Fg.getBattleResults("R" + i + ".txt", "G" + Ag.getGeneration() + "N" + contador + ".java");
				}
		
				// actualiza fitness del individuo
				Ag.setFitness(contador, acumulador);
				Ag.incrementPos();
				contador++;
			
			 }while(contador<Configuracion.getTPob());
			  
			 /*******************************************************************/
		
			/* SELECCION */
			
				System.out.printf("\nSELECCION Y CRUCE");
			/********************************************/
			 contador = 0;
			 do{
			 
				
				 Padres = Ag.selection();
		
				/* CRUCE */
			
				 Ag.cross(Padres);
				
				contador++;
				
			 }while(contador<( Configuracion.getTPob() - Configuracion.getNElite() )/Configuracion.getNPad());
			/*********************************************/
			
			 /* ELITISMO */
			
			 System.out.printf("\nELITISMO");
			
			Ag.elitism();
			
			// genera periodicamente un informe de los robots
			if(Ag.getGeneration() == 0 || Ag.getGeneration() == Configuracion.getCritParada() || Ag.getGeneration()%Configuracion.getReport() == 0)
				Ag.genLog(Fg);
						
			
			/* REEMPLAZAMIENTO */
			System.out.printf("\n\nREEMPLAZAMIENTO");
		
			Ag.replacement(Fg);
		
		//	System.out.printf("\n\nPoblacion tras reemplazamiento : \n");
		//	Ag.printGeneration();
			
			// cada x generaciones crea un registro de todos los individuos
			// cada y generaciones crea un registro de todos los elite
			
			Ag.freeChildren();
			Ag.freeElite();
			Basurero.gc();
			Ag.resetBattleCounter();
			Ag.resetPosition();
			Fg.next(Ag.getIndividuo(0));
			
			
			System.out.printf("\nFIN DE LA GENERACION : %d", Ag.getGeneration()-1);

			
		} while(Ag.getGeneration() <= Configuracion.getCritParada());
		 /****************************************************************************************************/
		 //Ag.genLog(Fg);
	}
	
}
