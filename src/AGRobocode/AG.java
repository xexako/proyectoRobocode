package AGRobocode;
//elitismo y cruce por ruleta..................................................POR HACER
//cache de evaluados...........................................................PLANTEARSELO
//ejecutar robocode con linea de comandos......................................HECHO
//mirar si roocode usa pipes (tuberias)........................................NO NECESARIO

import AGRobocode.Individuo;

import java.util.Random;
import java.util.Scanner;
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
		Hijos = new Individuo [Configuracion.getTPob()];					// poblacion temporal con la nueva generacion
		Elite = new Individuo [Configuracion.getNElite()];					// poblacion elite, tamaño 10% poblacion
		generacion = 0;
		posicion = 0;
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			Poblacion[i] = new Individuo(generacion, posicion);
			incrementPos();
		}
	}
	
	/* ---------------- métodos genéticos ---------------- */
	
	/**
	 * Iniciador
	 */
	
	/**
	 * Funcion de Evaluacion
	 * Para evaluar a los individuos:
	 * 1-Toma el primer individuo. (genera fichero de batalla)
	 * 2-Lo enfrenta recursivamente con el resto de sus compañeros.
	 * 3-Discrimina el valor de la victoria del individuo en cuestion y lo acumula en una variable.
	 * 4-Hace la media del valor acumulado y la asigna a fitness
	 * 5-Toma el siguiente individuo.
	 * 6-Repetir desde 2
	 */
	public void evaluation(){
		float acumulador = 0;										// servira para ir incrementando los resultados del individuo a evaluar
		int nBatalla = 0;											// contador de batalla
		int contador = 0;
		FileGenerator Fg = new FileGenerator(Poblacion[0]);
		
		//primero genero ficheros de batalla
		for(int i = 0 ; i < Configuracion.getTPob() ; i ++){
			Fg.next(Poblacion[i]);
			for(int j = 0 ; j < Configuracion.getTPob() ; j++){
				if(i != j){
					System.out.printf("");
					Fg.genBattle(i, j, generacion, nBatalla);
					nBatalla++;										// nBatalla va dentro del bucle para que no ejecute robocode sin fichero de batalla correspondiente
				}
				// nBatalla ++; 		si esta orden fuera en este lugar, habria saltos en las enumeraciones de los ficheros
			}
			
		}
		System.out.printf("\n\tBATALLAS GENERADAS");
		//nBatalla = 0;
		//ahora invoco robocode y genero ficheros resultado
		for(int i = 0 ; i < nBatalla ; i++){
			//	nBatalla++;
			invokeRoboCode(i);
		}
		// por ultimo extraigo el valor y discrimino
		for(int i = 0 ; i < nBatalla ; i++){												// hay 99 batallas por robot
			acumulador = acumulador + Fg.getBattleResults(Configuracion.getRPath()+"resultados"+Integer.toString(nBatalla%99)+"txt", "G"+Integer.toString(generacion)+"N"+Integer.toString(contador));
			if(i%99 == 0)
				contador++; 
			///////////////////////////////////////////////////
/*				if(i != j){											// no se enfrenta consigo mismo
					nBatalla = 0;									// esta orden provoca error de funcionamiento
					acumulador = 0;
					/* genera fichero de batalla *
					Fg.genBattle(i, j, generacion, nBatalla);
					/* enfrentamiento *
					invokeRoboCode(nBatalla);
					try {
						Thread.sleep(1000);							// espera a que escriba el fichero
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					/* discrimina el valor de victoria *
					acumulador = acumulador + Fg.getBattleResults("resultados"+Integer.toString(nBatalla)+".txt", "G"+Integer.toString(generacion)+"N"+Integer.toString(i));
				}
				nBatalla++;
			}
			/////////////////////////////////////////////////////
			/* media del valor acumulado */
			if(acumulador != 0){
				acumulador = acumulador/nBatalla;
			}
			Poblacion[contador].setFitness((int) acumulador);				// SI ESTO DA FALLO INVOCAR AL METODO intValue DE LA CLASE Float***************************************
		}
		
	}
	
	/**
	 * Operador de mutacion
	 * INCLUIDO EN INDIVIDUO
	 * 
	 * NOTA : revisar métodos internos
	 * 
	 */
	
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
		Individuo [] Generados = null;
		if(generacion <= (int) (Configuracion.getCritParada()/2))
			Generados = Padres[0].crossDiv(Padres[1]);
		else
			Generados = Padres[0].crossCon(Padres[1]);
		/* actualiza posiciones e inserta en el array */
		Generados[0].setPosition(posicion);
		Hijos[posicion] = Generados[0];
		incrementPos();
		Generados[1].setPosition(posicion);
		Hijos[posicion] = Generados[1];
		incrementPos();
	}
	
	/**
	 * Estrategia de seleccion
	 * A cada individuo se le da una porción de ruleta directamente proporcional con el valor fitness.
	 * La suma de todas las porciones equivale a 1.
	 * Genera un numero aleatorio entre 0 y 1.
	 * Recorre el vector de valores proporcionales y se para cuando la suma excede el valor dado.
	 * 	
	 * NOTA : comprobar que un individuo no se enfrenta consigo mismo ---> servirse de los metodos de Regla
	 */
	public Individuo [] selection(){
		/* variables */
		double relacion, suma = 0, pesos [];
		pesos = new double [Configuracion.getTPob()];					//contendra la ruleta, es decir, si el primero vale 2 y el segundo vale 5, en pesos[segundo] = 7
		Random Generator = new Random();
		Individuo Padres[] = new Individuo [Configuracion.getNPad()];
		/* primero calcula la relacion de porciones */
		for(int i = 0 ; i < Configuracion.getTPob() ; i++)
			suma = suma + Poblacion[i].getFitness();
		relacion = 1/suma;
		suma = 0;
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){				//multiplica la relacion (regla de 3) para que la resultante salga 1
			pesos[i] = relacion*Poblacion[i].getFitness();
			suma = suma + pesos[i];
			pesos[i] = suma;											//en el vector se muestran los resultados de las sumas de los elementos anteriores
		}
		/* seleccion de padres */
		for(int i = 0; i < Configuracion.getNPad() ; i++){				//selecciono los padres necesarios
				relacion = Generator.nextInt()%100;						//seleccion aleatoria del individuo
				relacion = relacion/100;								//ajusto el resultado al tamaño de la ruleta
				for(int j = 0 ; j < Configuracion.getTPob() ; j++){		//recorro el vector pesos
					suma = 0;
					if(suma <= relacion)								//para cuando la suma de las porciones supera el limite
						suma = suma + pesos[j];
					else{												//se para justamente cuando sobrepasa
						relacion = j;
						break;
					}
				}
				Padres[i] = Poblacion[(int)relacion];					//rellena el vector de padres seleccionados
		}
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
	public void replacement(){
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
		deleteGenFiles();
		/* Borra los ficheros de resultado y batalla */
		Fg.removeBat(generacion);
		Fg.removeRes();
		generacion++;
		/* Genera ficheros de comportamiento de la nueva generacion */
		writeGenFiles();
		/* Reemplaza la generacion anterior */
		Poblacion = Hijos;
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
	 */
	public void elitism (){
		int requeridos = Configuracion.getNElite();	 			//el 10% de la poblacion
		int aux;
		Individuo CopyPop[] = new Individuo [(int) requeridos];
		FileGenerator Fg = null;
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			CopyPop[i] = Poblacion[i];								//copiamos la poblacion
			Poblacion[i].setElite(false);							//quitamos todo el conjunto elite hasta ahora
		}
		for(int i = 0 ; i < requeridos ; i++){				//en cada vuelta se marca un elite
			aux = max(CopyPop);
			Poblacion[aux].setElite(true);							//marcamos en la poblacion original
			CopyPop[aux].setFitness(-1);							//quitamos al mejor de la poblacion copia
			Elite[i] = Poblacion[aux];								//agregamos al elite en la poblacion elite
		}
		/* escribe el fichero de report de los individuos elite */
		if(generacion%10000 == 0){							//condicion de report
			Fg = new FileGenerator(Elite[0]);
			Fg.genLogElite(Elite);
		}
	}
	
	/* ---------------- metodos auxiliares ---------------- */
	
	/**
	 * Devuelve la posicion del individuo con mayor fitness de un grupo de Individuos
	 */
	public int max (Individuo Padres []){
		int max = -1000;
		int posicion = -1;
		for(int i = 0 ; i < Configuracion.getTTor(); i++){
			if(max < Padres[i].getFitness()){
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
	public void writeGenFiles(){
		FileGenerator Fg = new FileGenerator(Poblacion[0]);
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
	 */
	public void deleteGenFiles(){
		FileGenerator Fg = new FileGenerator(Poblacion[0]);
		Fg.deleteOld(generacion);
	}
	
	/**
	 * Actualiza la posicion
	 */
	public void incrementPos(){
		posicion++;
		if(posicion >=100)
			posicion = 10;							// los 10 primeros puestos del array siempre estaran ocupados por los elite de la generacion anterior
	}
	
	/**
	 * llama a robocode para que se enfrenten dos individuos cargando un fichero de batalla
	 * 
	 * NOTA : los ficheros deben estar compilados antes de llamar a esta funcion, y los de batalla de la generacion anterior destruidos
	 * 
	 * **************REVISAR*****************
	 * Configuracion.getInvokeCmd y su homonima necesitan que se les pase el nombre del fichero de batalla(elemento 8) y el de resultados (elemento 10)
	 * 
	 */
	public void invokeRoboCode(int batalla){
		String [] Cmd = null;					// cadena de comando 
		FileGenerator Fg = null;
		/* condicion de report : invoca robocode en primer plano y escribe los comportamientos */
	/*	if(generacion % 10000 == 0){
			Cmd = Configuracion.getInvokeCmdDisplayed();		//hay que introducir el nombre del fichero de batalla y el de resultados
			Cmd[7] = Cmd[7]+"G"+Integer.toString(generacion)+"B"+Integer.toString(batalla)+".battle";
			Cmd[9] = Cmd[9]+"R"+Integer.toString(batalla)+".txt";
			/* invoca robocode en primer plano */
/*			try {
				Runtime.getRuntime().exec(Cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			/* esccribe el fichero de report de toda la generacion */
/*			Fg = new FileGenerator(Poblacion[0]);
			Fg.genLogFile(Poblacion);
		}
		/* invoca robocode de forma normal */
//		else{
			/* obtiene el comando de ejecucion */
			///////////////////////////////////////////////////////
			System.out.printf("\n\tOBTENGO COMANDO DE EJECUCION BATALLA %d", batalla);
			///////////////////////////////////////////////////////
			Cmd = Configuracion.getInvokeCmd();					// hay que introducir el nombre del fichero de batalla y el de resultados
			Cmd[7] = Cmd[7]+"G"+Integer.toString(generacion)+"B"+Integer.toString(batalla)+".battle";
			Cmd[9] = Cmd[9]+"R"+Integer.toString(batalla)+".txt";
			///////////////////////////////////////////////////////
			System.out.printf("\n\tCOMANDO OBTENIDO:\n\t\t");
			for(int i = 0 ; i < 10 ; i++)
				System.out.printf(Cmd[i]+" ");
			///////////////////////////////////////////////////////
			
			
			/* invoca robocode en segundo plano */
			try {
				System.out.printf("\n\tINVOCANDO ROBOCODE.....................................");
				Runtime.getRuntime().exec(Cmd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
			//////////////////////////////////////////////////////////
				Cmd[7] = "";
				Cmd[9] = "";
			///////////////////////////////////////////////////////////
			
		}/*
		// reseteo de Cmd				
		Cmd[7] = "";
		Cmd[9] = "";

	}*/
	
	/**
	 * selecciona los individuos que van a enfrentarse, comprobando que no se han enfrentado previamente y que no se enfrentan los mismos individuos
	 */
	
	/**
	 * Devuelve la generacion
	 */
	public int getGeneration() { return generacion; }
	
	/**
	 * Devuelve la poblacion
	 */
	public Individuo[] getPob() { return Poblacion; }
	
	/* ---------------- variables ---------------- */
	
	/**
	 * Poblacion de robots
	 */
	private Individuo Poblacion [];
	
	/**
	 * Hijos generados
	 */
	private Individuo Hijos [];
	
	/**
	 * Poblacion elite
	 */
	private Individuo Elite[];
	
	/**
	 * Contador de posicion para la nueva generacion
	 * Indica la siguiente posicion libre
	 */
	private int posicion;
	
	/**
	 * Contador de generacion
	 * Indica la generacion mas vieja en funcionamiento
	 */
	private int generacion;
	
	
	/* funcion main de prueba */
	/* testea el formato de guardado del fichero generado por Robocode tras cada batalla */
	public static void main1 (String [] args){
		/* inicializacion */ 
		int contador = 0;
		BufferedReader Br = null;
		File F = null;
		FileReader Fr = null;
		String Buff = new String();
		String [] Info1 = null, Info2 = null;			// info1 = resultados primer robot, info2 = resultados segundo robot
		String [] Relleno = {"Puntuación: ", "Daño infligido: ", "Veces que ha quedado primero: ", "Veces que ha quedado segundo: ", "Veces que ha quedado tercero: "};
		String [] Cmd = {"java", "-Xmx512M", "-Dsun.io.useCanonCaches=false", "-cp", "libs/robocode.jar", "robocode.Robocode", "-battle", "battles/prueba1.battle", "-results", "resultados/resultados.txt", "-nodisplay" };
		Info1 = new String[11];
		Info2 = new String[11];
		for(int i = 0 ; i < 11 ; i++){
			Info1[i] = new String();
			Info2[i] = new String();
		}
		F = new File("/C:/robocode/resultados/resultados.txt");
		/* ejecuta robodode  */
		try {
			Runtime.getRuntime().exec(Cmd);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/* abre el fichero resultado */
		try {
			Fr = new FileReader (F);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Br = new BufferedReader(Fr);
		/* procesa la informacion */
		for(int i = 0 ; i < 4 ; i++){
			try {
				Buff = Br.readLine();					//lectura
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(i == 2){
				Info1 = Buff.split("\t");				//discriminacion
			}
			if(i == 3){
				Info2 = Buff.split("\t");				//discriminacion
			}
		}
		System.out.printf("Nombre: Fire ");				//procesamiento : en este caso solamente los imprime por terminal
		for(int i = 0 ; i < 10 ; i++){
			if(i == 1 || i == 4 || i>7){
				System.out.printf("%s%s\t", Relleno[contador], Info1[i] );
				contador++;
			}
		}
		contador = 0;
		System.out.printf("\nNombre: Corners ");
		for(int i = 0 ; i < 10 ; i++){
			if(i == 1 || i == 4 || i > 7){
				System.out.printf("%s%s\t", Relleno[contador], Info2[i] );
				contador++;
			}
		}
		/* cierra el fichero */ 
		try {
			Fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
		/* inicializacion */
		AG Ag = new AG();
		///////////////////////
		Ag.menu();
	}
		/*
		do{
			/* evaluacion *
			Ag.evaluation();
			/* generacion de hijos *
			for(int i = 10 ; i < Configuracion.getTPob(); i++){			// i empieza en 10 para empezar a rellenar desde esa posicion
				/* seleccion, cruce y mutacion*
				Ag.cross(Ag.selection());
			}
			/* elitismo 
			Ag.elitism();
		}while(Ag.getGeneration() < Configuracion.getCritParada());
	}
	
	/**
	 * Menu de prueba para verificar que una ejecucion sola no peta
	 * @param Ag
	 */
	public void menu (){
		int opcion = -1;
		Individuo [] Padres = null;
		Scanner Entrada = new Scanner(System.in);
		System.out.printf("Inicio del algoritmo");
		do{
			/* print opciones */
			System.out.printf("\n\t1.Evaluacion\n\t2.Seleccion\n\t3.Cruce+mutacion\n\t4.Elitismo\n\t0.Salir\n");
			opcion = Entrada.nextInt();
			switch (opcion){
				case 1:
					/* Evaluacion */
					System.out.printf("\n\n\n------------------------------------\nEVALUACION\n");
					evaluation();
					break;
				case 2:
					/* Seleccion */
					System.out.printf("\n\n\n------------------------------------\nSELECCION\n");
					Padres = selection();
					break;
				case 3:
					/* Cruce+mutacion */
					System.out.printf("\n\n\n------------------------------------\nCRUCE Y MUTACION\n");
					cross(Padres);
					break;
				case 4:
					/* Elitismo */
					System.out.printf("\n\n\n------------------------------------\nELITISMO\n");
					elitism();
					break;
				case 0:
					/* Salir */
					System.out.printf("\n\n\n------------------------------------\nSALIENDO DEL ALGORITMO\n");
					break;
				default:
					/* Opcion no considerada */
					System.out.printf("\n\n\n------------------------------------\nEVALUACION\n");
					break;
			}
		}while(opcion != 0);
	}
}
