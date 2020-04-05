package AGRobocode;

import java.io.IOException;

/**
 * Esta clase se limita a proporcionar las variables globales necesarias para la ejecucion del AG.
 * @author Jesus
 *
 */
public class Configuracion {
	
	/* variables globales */
	final static private int parametros = 5;						//cantidad de parametros del antecedente
	final static private int acciones = 5;						//cantidad de acciones del consecuente
	final static private int nReglas = 7;							//cantidad de reglas por robot
	final static private double pMutacion = 0.1;					//probabilidad de mutacion
	final static private double pMiniMutacion = 0.2;				//probabilidad de minimutacion
	final static private int nPadres = 2; 						//numero de padres que se reproducen en cada cruce; SOLO VALORES PARES
	final static private int tamaPoblacion = 60;					//tamaño de poblacion; SOLO VALORES PARES
	
	/////////////////////////////////////////////////////////
	//CONFIGURACION DE TESTEO
	/////////////////////////////////////////////////////////
	//final static private int tamaPoblacion = 20;
	//final static private int criterioParada = 2;
	/////////////////////////////////////////////////////////
	//
	/////////////////////////////////////////////////////////
	
	final static private int tamaTorneo = 3;						//tamaño del torneo y numero de compañeros que se enfrentarán entre sí en batalla
	final static private int criterioParada = 10;				//numero de generaciones del AG
	final static private int nBattles = 5;						//numero de batallas en un enfrentamiento
	final static private int nElite = tamaPoblacion/10;			//tamaño de la poblacion elite
	final static private int totalBat = nBattles*tamaPoblacion;

	final static private int report = criterioParada/5;				//numero de informes que recibiremos de la poblacion

	final static private String Filepath = "/C:/robocode/robots/myRobots/"; 			//direccion donde se guardaran los ficheros de comportamiento
//	static private String Filepath = "/C:/robocode/robots/myRobots/pruebas/";			//direccion de testeo
	final static private String BattleFilePath = "/C:/robocode/battles/";
  	//comando que usa javac para compilar; FALTA AGREGARLE AL ULTIMO STRING EL NOMBRE DEL FICHERO A COMPILAR
	final static private String [] CompilationCmd = {"javac","-cp", "C:\\robocode\\libs\\robocode.jar","C:\\robocode\\robots\\myRobots\\"}; // String [4]
	final static private String CompilationFilepath = "C:\\robocode\\robots\\myRobots\\";
  	// HAY QUE PASARLE EL FICHERO DE BATALLA (elemento 8) Y EL DE RESULTADOS (elemento 10)
	//string [13]
	final static private String [] StartRobocode = {"java", "-Xmx512M", "-Dsun.io.useCanonCaches=false", "-cp", "C:/robocode/libs/robocode.jar", "robocode.Robocode", "-battle", "C:/robocode/battles/", "-results", "C:/robocode/resultados/", "-nodisplay", "", "" };
	final static private String ResultsFilepath = "C:/robocode/resultados/";
  	// HAY QUE PASARLE EL FICHERO DE BATALLA (elemento 8) Y EL DE RESULTADOS (elemento 10)
  	//string [10]
	final static private String [] StartRobocodeDisplayed = {"java", "-Xmx512M", "-Dsun.io.useCanonCaches=false", "-cp", "C:/robocode/libs/robocode.jar", "robocode.Robocode", "-battle", "C:/robocode/battles/", "-results", "C:/robocode/resultados/"};
	final static private String ReportPath = "C:/robocode/reports/";
	final static private String SavePath = "C:/robocode/PRUEBAS/saves/saveFile.txt";
  	
  	/* metodos */
	
	static public int getParam() { return parametros; }
	
	static public int getAcc() { return acciones; }
	
	static public int getNReg() { return nReglas; }
	
	static public double getPMut() { return pMutacion; }
	
	static public double getPMiniMut() { return pMiniMutacion; }
	
	static public int getNPad() { return nPadres; }
	
	public static int getTPob() { return tamaPoblacion; }
	
	static public int getTTor() { return tamaTorneo; }
	
	static public int  getNBat() { return nBattles; }

	static public int getCritParada() { return criterioParada; }

	static public int getNElite() { return nElite; }
	
	static public int getTotalBat() { return totalBat; }
	
	static public int getReport() { return report; }
	
	static public String getFilePath() { return Filepath; } 

	static public String getBattlePath() { return BattleFilePath; }
	
	
//	static public String getCompilationPath() { return CompilationFilepath; }
	
	static public String [] getUncompletedCompilationCmd() { return CompilationCmd; }
	
	static public String getCompilationFilepath() { return CompilationFilepath; }
	
	static public String [] getInvokeCmd() { return StartRobocode; }
	
	static public String getRPath() { return ResultsFilepath; } 
	
	static public String [] getInvokeCmdDisplayed() { return StartRobocodeDisplayed; }
	
	static public String getReportPath(){ return ReportPath; }
	
	static public String getSavePath() { return SavePath; }
	
	/**
	 * Testeo de compilación desde consola.
	 * En primer lugar probaremos que el fichero de batalla funciona correctamente, cambiando el fichero prueba2.battle (en lugar de G100N100 intentaremos coger RoboPrueba)
	 * Una vez comprendido el funcionamiento del fichero de batalla, intentaremos compilar el fichero G100N100 alojado en la direccion C:/robocode/robots/myRobots/G100N100 
	 * @param args
	 */
	static public void main(String[] args){
		boolean firstTest = false;  // true = comprobar fichero de batalla, false = comprobar compilacion
		String [] Cmd = getInvokeCmdDisplayed();
	//	String [] Moverse = { "cd", "C:/robocode/"};
		String [] Compilacion9 = new String[4];
		String [] Compilacion8 = new String[4];
		for(int i = 0 ; i < 4 ; i++){
			Compilacion9[i] = getUncompletedCompilationCmd()[i];
			Compilacion8[i] = getUncompletedCompilationCmd()[i];
		}
	/*	String [] CompilationCmd = getUncompletedCompilationCmd();
		String [] CompilationCmd = new String [12];
		for(int i = 0 ; i < 11 ; i++)
			CompilationCmd[i] = getUncompletedCompilationCmd()[i];
		CompilationCmd[11] =  getCompilationFilepath()+"G100N100.java";  */
		
		Compilacion9[3] = Compilacion9[3] + "G1N9.java";
		Compilacion8[3] = Compilacion8[3] + "G1N8.java";
		
		Cmd[7] = Cmd[7] + "G1B0.battle";
		Cmd[9] = Cmd[9] + "R1.txt";
		
		///////////////////////////////////////////////////////////////////
		System.out.printf(Cmd[0]+" "+Cmd[1]+" "+Cmd[2]+" "+Cmd[3]+" "+Cmd[4]+" "+Cmd[5]+" "+Cmd[6]+" "+Cmd[7]+" "+Cmd[8]+" "+Cmd[9]);
		System.out.printf("\n"+Compilacion9[0]+" "+Compilacion9[1]+" "+Compilacion9[2]+" "+Compilacion9[3]);
		System.out.printf("\n"+Compilacion8[0]+" "+Compilacion8[1]+" "+Compilacion8[2]+" "+Compilacion8[3]);
		///////////////////////////////////////////////////////////////////
		
		/* primera prueba */ 
		for(int i = 0 ; i < 2 ; i ++){
			if(firstTest == true){
				///////////////////////////////////////////////////////////////////
		/**		System.out.printf("MOVIENDOSE\n");
				//////////////////////////////////////////////////////////////////////
				try {
					Runtime.getRuntime().exec(Moverse);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
*/
				///////////////////////////////////////////////////////////////////
				System.out.printf("INVOCANDO ROBOCODE\n");
				//////////////////////////////////////////////////////////////////////
				try {
					Runtime.getRuntime().exec(Cmd);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.printf("\n\n--------------------------------------------------\nERROR AL EJECUTAR ROBOCODE\n--------------------------------------------------\n");
					e.printStackTrace();
				}
			}
			else{//				COMPILACION
				try {
					Runtime.getRuntime().exec(Compilacion9);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.printf("\n\n--------------------------------------------------\nERROR AL COMPILAR\n--------------------------------------------------\n");
					e.printStackTrace();
				}
				
				try {
					Runtime.getRuntime().exec(Compilacion8);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.printf("\n\n--------------------------------------------------\nERROR AL COMPILAR\n--------------------------------------------------\n");
					e.printStackTrace();
				}
				
				firstTest = true;
			}
		}
	}
}
