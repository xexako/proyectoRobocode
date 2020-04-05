package AGRobocode;

import AGRobocode.Individuo;
import AGRobocode.Configuracion;

import java.io.*;
import java.util.Random;
//import java.util.Scanner;
/**
 * Se encarga de escribir los ficheros de configuracion de los individuos.
 */
public class FileGenerator {
	
	/* Métodos */
	
	/**
	 * Constructor
	 */
	public FileGenerator(Individuo I) {
		Sujeto = new Individuo(-1, -1);
		Sujeto.setNew(I);
		//nDep = Sujeto.countDep();
		//nReglas = Configuracion.getNReg();
	}
	
	/**
	 * Constructor vacío
	 */
	public FileGenerator(){
		Sujeto = new Individuo();
		//nDep = -1;
		//nReglas = 0;
	}
	
	
	/**
	 * Establece un nuevo sujeto y actualiza parámetros de clase
	 */
	public void next(Individuo I){
		Sujeto.setNew(I);
		//nDep = Sujeto.countDep();
		//nReglas = Configuracion.getNReg();
	}
	
	
	/**
	 * Indica a la clase Individuo que escriba los ficheros de comportamiento del sujeto actual.
	 * Todos los ficheros seguiran la misma estructura: 
	 * 	- Cabecera (constante)
	 * 	- Variables (cambiante)
	 *  - Metodo Run (cambiante)
	 *  - Eventos scanRobot (cambiante)
	 *  - Resto de eventos (constante)
	 *  - Reglas (cambiante)
	 *  - Variables (mixto)
	 *  
	 *  NOTA : los códigos de las reglas los escribe la clase Individuo sirviendose de la clase Regla.
	 */
	public void writeFile(){
		Sujeto.writeRoboBehav();
	}
	
	/**
	 * Escribe un fichero que almacena las codificaciones de los robots
	 * 
	 */
	public void writeLog(Individuo Poblacion []){
		//REPORT<generacion>.txt es el nombre del fichero donde se guardaran los sucesivos registros
		File F = new File(Configuracion.getReportPath()+"REPORT"+Integer.toString(Poblacion[0].getGeneration())+".txt");
		FileWriter Fw = null;						
		try {
			Fw = new FileWriter(F);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* recorre el array de poblacion */
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			try {
				Fw.write(Poblacion[i].getName()+"\n"+Poblacion[i].getConfigStr()+"\n");					//escribe en el fichero la configuracion de los robots
				Fw.write("Fitness = "+Poblacion[i].getFitness()+"\nElitismo = "+Poblacion[i].getElite()+"\n\n");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Elimina los ficheros de comportamiento de una generacion determinada
	 */
	public void deleteOld(int gen){
		File Temp = new File(Configuracion.getFilePath());
		for(int i = 0 ; i <= Configuracion.getTPob() ; i++){
			Temp = getNewPath(gen, i);
			if(Temp.exists() == true)
				Temp.delete();
		}
	}
	
	/**
	 * 
	 * Elimina los ficheros class de ejecucion generados con cada compilacion
	 */
	public void deleteClasses(int gen){
		File Aux = new File(Configuracion.getFilePath());			// aux existe para mantener una referencia al objeto de inicializacion
		File Temp = Aux;
		String FilePath ="";
				
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			FilePath = "";
			FilePath = Configuracion.getFilePath()+"G"+Integer.toString(gen)+"N"+Integer.toString(i)+".class";
			Temp = getNewPath(FilePath);
			if(Temp.exists() == true)
				Temp.delete();
			Temp = Aux;
		}
	}
	
	/**
	 * Proporciona un objeto File con filepath en funcion de la generacion y la posicion
	 */
	public File getNewPath(int generation, int position){
		File F = null;
		F = new File(Configuracion.getFilePath()+"G"+Integer.toString(generation)+"N"+Integer.toString(position)+".java");
		return F;
	}
	
	/**
	 * Proporciona un objeto File con filepath = FilePath
	 * @param FilePath
	 * @return
	 */
	public File getNewPath(String FilePath){
		File F = null; 
		F = new File(FilePath);
		return F;
	}
	
	/**
	 * Genera un fichero con los datos de los elite de la poblacion
	 * 
	 * El fichero se llamara ELITEGENx donde x es la generacion a la que pertenecen
	 * 
	 *
	public void genLogElite(Individuo [] Elite){
		File F = new File (Configuracion.getRPath()+"ELITEGEN"+Integer.toString(Elite[0].getGeneration()));
		FileWriter Fw = null;
		String Aux = null;
		try {
			Fw = new FileWriter(F);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* recorre el vector Elite y los imprime *
		for(int i = 0 ; i < Configuracion.getAcc() + Configuracion.getParam() ; i++){
			// copia la configuracion
			Aux = Elite[i].getConfigStr();
			try {
				Fw.write(Aux);							// escribe la configuracion
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			Fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Genera un fichero con los datos de una generacion entera
	 * 
	 * El fichero se llamara GENx donde x es la generacion 
	 * 
	 *
	public void genLogFile(Individuo [] Poblacion){
		File F = new File(Configuracion.getRPath()+"GEN"+Poblacion[0].getGeneration());
		FileWriter Fw = null;
		String Aux = null;
		
		try {
			Fw = new FileWriter(F);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* recorre la poblacion y la escribe *
		for(int i = 0 ; i < Configuracion.getParam() + Configuracion.getAcc() ; i++){
			/* obtiene la configuracion *
			Aux = Poblacion[i].getConfigStr();
			try {
				Fw.write(Aux);										//escritura
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			Fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * genera un fichero de batalla
	 * 
	 * los ficheros de batalla tienen por nombre
	 * 			G<generacion>B<batalla>
	 * 
	 */
	public void genBattle(int individuo, int enemigo, int generation, int nBat){
		/* inicializacion */
		File F = getNewBatPath(generation, nBat);
		FileWriter Fw = null;
		String aux = new String();						// en esta variable se almacena el contenido del fichero a escribir
		aux = "#Battle Properties\nrobocode.battleField.width=800\n	robocode.battleField.height=600\n";
		aux = aux + "robocode.battle.numRounds="+Integer.toString(Configuracion.getNBat())+"\nrobocode.battle.gunCoolingRate=0.1\nrobocode.battle.rules.inactivityTime=100\n";
		aux = aux + "robocode.battle.selectedRobots=myRobots.G" + Integer.toString(generation) + "N" + Integer.toString(individuo);
		aux = aux + "*,myRobots.G" + Integer.toString(generation) + "N" + Integer.toString(enemigo)+"*\n";
		
		/* creacion de fichero y escritura */
		try {
			Fw = new FileWriter(F);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Fw.write(aux);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/* cierre de fichero */
		try {
			Fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * elimina los ficheros de batalla de una generacion
	 */
	public void removeBat(int generation){
		File F = new File(Configuracion.getBattlePath());
		for(int i = 0 ; i < Configuracion.getTotalBat() ; i++){
			F = getNewBatPath(generation, i);
			if(F.exists() == true)
				F.delete();
		}
	}
	
	/**
	 * elimina todos los ficheros de resultado
	 */
	public void removeRes(){
		File F = null;
		
		System.out.printf("\n\nBatallas esperadas = "+Configuracion.getTotalBat());
		
		for(int i = 0; i < Configuracion.getTotalBat() ; i++){			// el numero de ficheros de resultados coincide con el de batalla
			F = getNewResPath(i);
			if(F.exists() == true)
				F.delete();
		}
	}
	
	/**
	 * devuelve un nuevo objeto File para ficheros de batalla
	 */
	public File getNewBatPath(int generation, int battle){
		File F = new File(Configuracion.getBattlePath()+"G"+Integer.toString(generation)+"B"+Integer.toString(battle)+".battle");
		return F;
	}
	
	/**
	 * devuelve un objeto File para ficheros de resultados
	 */
	public File getNewResPath(int nFile){
		File F = new File(Configuracion.getRPath()+"R"+Integer.toString(nFile)+".txt");
		return F;
	}
	/**
	 * escribe un nuevo fichero de batalla
	 *
	public void writeBattleFile(){
		
	}
	
	/**
	 * lee el fichero de resultados y devuelve un array con el numero de veces que ha quedado primero y segundo
	 * despues los pasa a la funcion discriminate y devuelve el resultado arrojado por este metodo
	 * 
	 * FileName es el nombre del fichero de resultado
	 * RoboName nombre del robot a evaluar
	 * ----------------------------------------------------------------------------------------------------------
	 * 
	 * REVISAR
	 * 
	 * -----------------------------------------------------------------------------------------------------------
	 */
	public int getBattleResults(String FileName, String RoboName){
		File F = null;							// fichero que abriremos
		FileReader Fr = null;					// clase de lectura
		BufferedReader Br = null;				// buffer de lectura
		String Buf = new String();				// buffer que almacenara los caracteres leidos
		String [] Robot1 = null;				// informacion relativa al primer robot
		String [] Robot2 = null;				// informacion relativa al segundo robot
		String Temp = null;
		
		
		/* inicializacion */
		Robot1 = new String[11];				// es el numero de strings arrojados por el metodo split de la clase lectora
		Robot2 = new String[11];
/*		for(int i = 0 ; i < 11 ; i++){
			Robot1[i] = new String();
			Robot2[i] = new String();
		}
	*/	
		Temp = new String(Configuracion.getRPath() + FileName);
		
		F = new File(Temp);
		
		
		
		//espera activa a que el proceso robocode termine
		
		try {
			Fr = new FileReader(F);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		
		Br = new BufferedReader(Fr);
		
		/* procesamiento de lectura */
		for(int i = 0 ; i < 4 ; i++){
			/* lectura */
			try {
				Buf = Br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(i == 2){	Robot1 = Buf.split("\t"); }		// almacena la informacion del primer robot
			if(i == 3){ Robot2 = Buf.split("\t"); }		// almacena la informacion del segundo robot
		}
		
		/* cierre de fichero */
		try {
			Fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return discriminate(Robot1, Robot2, RoboName);
		
	}
	
	/**
	 * ordena y discrimina el array de lectura recibido por getBattleResults y devuelve un array con la informacion discriminada
	 * 
	 * Recibe dos arrays de String
	 * Cada array de String consta de 11 cadenas:
	 * 		- nombre del robot (posicion: carpeta.nombre) ........................... usar para ordenar (solo el nombre)
	 * 		- puntuacion total (int (int%) ) ........................................ valor a discriminar (posicion 2)
	 * 		- sobrevive ............................................................. no interesa
	 * 		- bonus de supervivencia ................................................ no interesa
	 * 		- daño por disparo ...................................................... no interesa
	 * 		- bonus de disparo ...................................................... no interesa
	 * 		- daño por colision *2 .................................................. no interesa
	 * 		- bonus por colision .................................................... no interesa
	 * 		- veces que ha quedado primero (int) .................................... no discriminar : a veces hay fallo con este valor
	 * 		- veces que ha quedado segundo (int) .................................... no interesa
	 * 		- veces que ha quedado tercero (int) .................................... no interesa
	 * 
	 * Devuelve el numero de veces que el individuo ha quedado primero
	 * 
	 * 
	 * 
	 */
	public int  discriminate(String [] Resultado1, String [] Resultado2, String RoboName){
		String Buf = null;
		String [] Splitted = new String[2];								// splitted es el array de string que nos servira para separar la cadena
		int discriminados;												// almacenaremos la puntuacion
		if(Resultado1[0].indexOf(RoboName) != -1){						// Resultado1 contiene los resultados del robot a evaluar
			Buf = new String (Resultado1[1]);
			Splitted = Buf.split(" ");
			discriminados = Integer.parseInt(Splitted[0]);
		}
		else{															// Resultado2 contiene los resultados del robot a evaluar
			Buf = new String (Resultado2[1]);
			Splitted = Buf.split(" ");
			discriminados = Integer.parseInt(Splitted[0]);
		}
		
		return discriminados;
	}
	
	/**
	 * compila los ficheros de comportamiento de una generación
	 * 
	 */
	public void compileThis(int generation){
		/* declaracion de variables e inicializacion */
		File F = null;										// para obtener nuevas direcciones de fichero
		String Aux = new String();							// cadena de comparacion
		String [] Command = new String[5];					// al array hay que agregarle en ultimo lugar la direccion del fichero a compilar
		Aux = "G"+Integer.toString(generation);
		
		/* compara los nombres y los compila si se requiere */
		for (int i = 0 ; i < Configuracion.getTPob() ; i++){
			F = getNewPath(generation, i);					//obtiene una nueva dirección
			if(F.getName().indexOf(Aux) != -1){				//condicion de compilacion
				Command = getCompilationCmd(generation, i);
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
	 * devuelve un array de String con el comando de compilacion completo
	 * 
	 */
	public String [] getCompilationCmd(int generacion, int posicion){
		String [] Temp = new String [4]; //aqui se almacenara el comando a devolver
		for(int i = 0 ; i < 4 ; i++)
			Temp [i] = new String ("");
		String Filename = "G"+Integer.toString(generacion)+"N"+Integer.toString(posicion)+".java";
		for(int i = 0 ; i < 4 ; i++){
			Temp[i] = Configuracion.getUncompletedCompilationCmd()[i];
		}
		Temp[3] = Temp[3]+Filename;
		return Temp;
	}
	
	/**
	 * Almacena en un fichero la configuración de la población 
	 * @param Poblacion 
	 * @return Objeto File con la dirección de 
	 */
	public void saveThis(Individuo [] Poblacion){
		
		File F = new File (Configuracion.getSavePath());
		
		// escribe las codificaciones en un fichero

		FileWriter Fw = null;						
		try {
			Fw = new FileWriter(F);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* recorre el array de poblacion */
		/********
		 * ESCRITURA DE FICHEROS
		 * -generacion
		 * -posicion
		 * -7 veces : \ngen gen gen gen gen ... hasta 10 veces
		 */
		for(int i = 0 ; i < Configuracion.getTPob() ; i++){
			try {
				Fw.write("\n"+Poblacion[i].getGeneration()+"\n"+Poblacion[i].getPosition());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int j = 0 ; j < Configuracion.getNReg() ;j ++){
				try {
					Fw.write("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// escribe los genes
			
				for(int k = 0 ; k < Configuracion.getAcc()+Configuracion.getParam() ; k++){
					if(k == Configuracion.getAcc()+Configuracion.getParam()-1){
						if(Poblacion[i].getGenome(j, k) == true){
							try {
								Fw.write("true");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						else{
							try {
								Fw.write("false");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				
					else{
						if(Poblacion[i].getGenome(j, k) == true){
							try {
								Fw.write("true ");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else{
							try {
								Fw.write("false ");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}		
		try {
			Fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void loadData (Individuo [] Poblacion){
		FileReader Fr = null;					// clase de lectura
		BufferedReader Br = null;				// buffer de lectura
		String Buf = new String();				// buffer que almacenara los caracteres leidos
		String [] regla = new String [Configuracion.getNReg()];
		File F = new File(Configuracion.getSavePath());
		for(int i = 0 ; i < Configuracion.getNReg() ; i++)
			regla[i] = new String();
		
		
		try {
			Fr = new FileReader(F);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Br = new BufferedReader(Fr);
		
		/* lee el salto de linea inicial */
		try {
			Buf = Br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
		/* procesamiento de lectura */
		for(int i = 0 ; i < 3 ; i++){
			
			/* lee la generacion */
			try {
				Buf = Br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Poblacion[i].setGeneration(Integer.parseInt(Buf));
			
			/* lee la posicion */
			try {
				Buf = Br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Poblacion[i].setPosition(Integer.parseInt(Buf));
			
			for(int j = 0 ; j < Configuracion.getNReg() ; j++){
			
				/* lee una regla */
				try {
					Buf = Br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/* la divide */
				
				regla = Buf.split(" ");
				for(int k = 0 ; k < Configuracion.getAcc()+Configuracion.getParam() ; k++){
					if(regla[k].compareTo("true") == 0)
						Poblacion[i].setGenome(j, k, true);
					else{
						Poblacion[i].setGenome(j, k, false);
					}
			//		System.out.printf("\n\tEn la posicion %d del elemento %d se pone "+regla[k]+"--->"+Pob[i].getGenome(j, k), k,  j);
				}
			}
		}
		
		/* cierre de fichero */
		try {
			Fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	
	
	/* Variables */
	
	//private int nReglas;			// numero de reglas
	
	//private int nDep;			// numero de reglas dependientes de eventos
	
	private Individuo Sujeto;	// sujeto auxiliar
	
	/**
	 * funcion de prueba de los metodos de clase
	 * @param args
	 *
	public static void main(String[] args){
		Individuo Gen1 [] = new Individuo [3];
		for(int i = 0 ; i < 3 ; i++){
			Gen1[i] = new Individuo(1, i+1);
		}
		
		System.out.printf("\nPrueba de la escritura en masa. ");
		for(int i = 0 ; i < 3 ; i++){
			System.out.printf("%d", i);
			Gen1[i].writeRoboBehav();
		}
		System.out.printf("\nComportamientos generados");
	}
	
	/**
	 * funcion main de prueba.
	 * comprueba la creación, compilación y ejecución de los ficheros de comportamiento
	 */
	public static void main1(String[] args){
		/* inicializacion */
		Individuo Gen1 = new Individuo(100, 100);
	//	String [] StartRobocode = {"java", "-Xmx512M", "-Dsun.io.useCanonCaches=false", "-cp", "libs/robocode.jar", "robocode.Robocode", "-battle", "battles/prueba2.battle"};//, "-results", "resultados/resultados.txt", "-nodisplay" };
		String [] CompileBehav = new String [12];
		String [] UncompletedCmd = Configuracion.getUncompletedCompilationCmd();		//hay que agregar una última cadena con la direccion del fichero a compilar (desde C:\robocode\...)
		for(int i = 0 ; i < 11 ; i++){
			CompileBehav[i] = new String();
			CompileBehav[i] = UncompletedCmd[i];
			System.out.printf(CompileBehav[i]+" ");
		}
		CompileBehav[11] = new String();
		CompileBehav[11] = "C:\\robocode\\robots\\myRobots\\G100N100.java"; //NO ES DIRECCION DE TESTEO
		System.out.printf(CompileBehav[11]);
		/* escritura de fichero */
		System.out.printf("\nEscritura de fichero.");
		Gen1.writeRoboBehav();
		/* compilacion del fichero 
		try {
			Runtime.getRuntime().exec(CompileBehav);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			System.out.printf("\n\n----------------------\nERROR AL COMPILAR\n----------------------\n\n");
		}
		/* llamada a robocode 
		System.out.printf("\nLlamada a robocode.\n\tFichero de batalla: prueba2.battle");
		try {
			Runtime.getRuntime().exec(StartRobocode);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.printf("\n\n----------------------\nERROR AL LLAMAR A ROBOCODE\n----------------------\n\n");
		}				*/
	}
	
	/**
	 * comprueba si un Individuo puede copiarse en otro usando el operador "="
	 */
	public static void main2(String[] args){
		/* inicializacion */
		Individuo Original = new Individuo(100, 100);
		Individuo Copia = null;
		
		Copia = Original;
		/* imprimo el original */
		System.out.printf("\nOriginal:\n--------\nFitness\t=\t"+Integer.toString(Original.getFitness()));
		System.out.printf("\nElite\t=\t"+Boolean.toString(Original.getElite()));
		System.out.printf("\nGeneration\t=\t"+Integer.toString(Original.getGeneration())+"\nPosition\t=\t"+Integer.toString(Original.getPosition()));
		System.out.printf("\nGenome:");
		for(int i = 0 ; i < Configuracion.getNReg(); i++){			// se recorren las reglas
			System.out.printf("\n\tRegla "+Integer.toString(i)+"\t=\t");
			Original.getConfiguration()[i].printConfig();
		}
		
		/* imprimo la copia */
		System.out.printf("\nCopia:\n--------\nFitness\t=\t"+Integer.toString(Copia.getFitness()));
		System.out.printf("\nElite\t=\t"+Boolean.toString(Copia.getElite()));
		System.out.printf("\nGeneration\t=\t"+Integer.toString(Copia.getGeneration())+"\nPosition\t=\t"+Integer.toString(Copia.getPosition()));
		System.out.printf("\nGenome:");
		for(int i = 0 ; i < Configuracion.getNReg(); i++){			// se recorren las reglas
			System.out.printf("\n\tRegla "+Integer.toString(i)+"\t=\t");
			Copia.getConfiguration()[i].printConfig();
		}
		
		/* cambio el original */
		Original.setElite(true);
		/* imprimo el campo cambiado */
		System.out.printf("\n\nCampo variado en Original = Elite.\nOriginal:\telite\t=\t"+Boolean.toString(Original.getElite()));
		System.out.printf("\nCopia:\telite\t=\t"+Boolean.toString(Copia.getElite()));
		
		/* cambio la copia */
		Copia.setFitness(1234);
		/* imprimo el campo cambiado */
		System.out.printf("\n\nCampo variado en Copia = Fitness.\nOriginal:\tfitness\t=\t"+Integer.toString(Original.getFitness()));
		System.out.printf("\nCopia:\tfitness\t=\t"+Integer.toString(Copia.getFitness()));
		
	}
	
	/**
	 * comprueba la conversion de Strings a ints
	 */
	public static void main3 (String [] args){
		String Cadena = new String ("7");
		Integer numero = new Integer(0);
		numero = Integer.parseInt(Cadena);
		System.out.printf("\n\n%d\n\n", numero);
	}
	
	/**
	 * testeo del funcionamiento del algoritmo a pequeña escala
	 * 
		/* testeo del algoritmo genetico */
		/*
		1 - Inicializa
		 *  2 - Evaluacion
		 *  3 - Seleccion
		 *  4 - Cruce y mutacion
		 *  5 - Elitismo
		 *  6 - Reemplazamiento
		 *  7 - Repetir desde el paso 2 hasta que se cumpla el criterio de parada
		 *
	 */
	public static void xmain(String [] args){
		
		/* inicializacion */
		
		int tPob = 10;
		int nCombatientes = 3;
		int aux;
		int seleccionado;
		int [] seleccionados = new int[nCombatientes];
		Random Rg = new Random();
		Individuo [] Poblacion = new Individuo [tPob];
		FileGenerator Fg = new FileGenerator(Poblacion[0]);
		Individuo Seleccionado = null;
		Individuo [] Combatientes = new Individuo [nCombatientes];
		for(int i = 0 ; i < tPob ; i++){ Poblacion[i] = new Individuo(1, i); }
		
		/* testeo del algoritmo genetico */
		
		/* evaluacion */
		
		for(int i = 0 ; i < tPob ; i++){				//recorre la poblacion
			aux = 0;
			// selecciono 3 individuos al azar
			do {
				seleccionado = Math.abs(Rg.nextInt())%nCombatientes;
				
			} while(aux < 3);
			
			for(int j = 0 ; j < nCombatientes ; j++){}
			
		}
	}
	
	
	
	
	public static void main(String [] args){
		Individuo [] Pob = new Individuo [3];
		
		for(int i = 0 ; i < 3 ; i++)
			Pob[i] = new Individuo(200, i);
		
		File F = new File ("C:/robocode/PRUEBAS/saves/saveFile.txt");
		
		// escribe las codificaciones en un fichero

		FileWriter Fw = null;						
		try {
			Fw = new FileWriter(F);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/* recorre el array de poblacion */
		/********
		 * ESCRITURA DE FICHEROS
		 * -\n
		 * -generacion
		 * -\n
		 * -posicion
		 * -\n
		 * -7 veces : \ngen gen gen gen gen ... hasta 10 veces
		 * 
		 */
		for(int i = 0 ; i < 3 ; i++){
			try {
				Fw.write("\n"+Pob[i].getGeneration()+"\n"+Pob[i].getPosition());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for(int j = 0 ; j < Configuracion.getNReg() ;j ++){
				try {
					Fw.write("\n");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// escribe los genes
			
				for(int k = 0 ; k < Configuracion.getAcc()+Configuracion.getParam() ; k++){
					if(k == Configuracion.getAcc()+Configuracion.getParam()-1){
						if(Pob[i].getGenome(j, k) == true){
							try {
								Fw.write("true");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						else{
							try {
								Fw.write("false");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				
					else{
						if(Pob[i].getGenome(j, k) == true){
							try {
								Fw.write("true ");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						else{
							try {
								Fw.write("false ");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		
		
		try {
			Fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// altera el contenido de los individuos
		
		for(int i = 0 ; i < 3 ; i++){
			Pob[i].setPosition(80);
			for(int j = 0 ; j < Configuracion.getNReg() ; j++){
				for(int k = 0 ; k < Configuracion.getAcc() + Configuracion.getParam() ; k++){
					Pob[i].setGenome(j, k, false);
				}
			}
		}
		
		/********
		 * lectura de ficheros
		 */
		

		FileReader Fr = null;					// clase de lectura
		BufferedReader Br = null;				// buffer de lectura
		String Buf = new String();				// buffer que almacenara los caracteres leidos
		String [] regla = new String [Configuracion.getNReg()];
	//	File F = new File()
		for(int i = 0 ; i < Configuracion.getNReg() ; i++)
			regla[i] = new String();
		
		
		try {
			Fr = new FileReader(F);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Br = new BufferedReader(Fr);
		
		/* lee el salto de linea inicial */
		try {
			Buf = Br.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
		
		/* procesamiento de lectura */
		for(int i = 0 ; i < 3 ; i++){
			
			/* lee la generacion */
			try {
				Buf = Br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Pob[i].setGeneration(Integer.parseInt(Buf));
			
			/* lee la posicion */
			try {
				Buf = Br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Pob[i].setPosition(Integer.parseInt(Buf));
			
			for(int j = 0 ; j < Configuracion.getNReg() ; j++){
			
				/* lee una regla */
				try {
					Buf = Br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/* la divide */
				
				regla = Buf.split(" ");
				for(int k = 0 ; k < Configuracion.getAcc()+Configuracion.getParam() ; k++){
					if(regla[k].compareTo("true") == 0)
						Pob[i].setGenome(j, k, true);
					else{
						Pob[i].setGenome(j, k, false);
					}
			//		System.out.printf("\n\tEn la posicion %d del elemento %d se pone "+regla[k]+"--->"+Pob[i].getGenome(j, k), k,  j);
				}
			}
		}
		
		/* cierre de fichero */
		try {
			Fr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
