package AGRobocode;
import AGRobocode.Regla;

import java.util.Random;
import java.io.*;

import AGRobocode.Configuracion;

public class Individuo {
	
	
	/* ---------------- constructores ---------------- */
	
	
	/**
	 * Constructor
	 */
	public Individuo(int gen, int pos){
		Configuration = new Regla [Configuracion.getNReg()];/*
		for(int i = 0 ; i < Configuracion.getNReg() ;i++){
			Configuration[i] = new Regla();
		}   */
		newConfig();
		fitness = -1000;
		elite = false;
		generation = gen;
		position = pos;
	}
	
	
	/* ---------------- métodos get/set ---------------- */
	
	
	/**
	 * Establece un nuevo conjunto de reglas
	 */
	public void setConfiguration(Regla eConfiguration []){
		Configuration = eConfiguration;
		fitness = -1000;
		elite = false;
	}
	
	/**
	 * Devuelve el conjunto de reglas
	 */
	public Regla [] getConfiguration(){
		return Configuration;
	}
	
	/**
	 * Establece una nueva regla
	 */
	public void setGenotype(int position, boolean [] eGenotype){
		Configuration[position].setGenotype(eGenotype);
		fitness = -1000;
	}
	
	/**
	 * Devuelve una regla
	 */
	public Regla getGenotype(int position){
		return Configuration[position];
	}
	
	/**
	 * Establece un nuevo elemento en una regla
	 */
	public void setGenome(int position, int rule,  boolean eGenome){
		Configuration[position].setGenome(rule, eGenome);
		fitness = -1000;
		elite = false;
	}
	
	/**
	 * Devuelve un elemento en una regla
	 */
	public boolean getGenome(int position, int rule){
		return Configuration[position].getGenome(rule);
	}
	
	/**
	 * Establece un nuevo valor fitness
	 */
	public void setFitness(int eFitness){
		fitness = eFitness;
	}
	
	/**
	 * Devuelve el valor fitness
	 */
	public int getFitness(){
		return fitness;
	}
	
	/**
	 * Establece el valor elite
	 */
	public void setElite(boolean eElite){
		elite = eElite;
	}
	
	/**
	 * Devuelve el valor elite
	 */
	public boolean getElite(){
		return elite;
	}
	
	/**
	 * Devuelve la generacion
	 */
	public int getGeneration(){ return generation; }
	
	/**
	 * Establece la generacion
	 */
	public void setGeneration(int eGeneracion) { generation = eGeneracion; }
	
	/**
	 * Devuelve la posicion
	 */
	public int getPosition(){ return position; }

	/**
	 * Establece la posicion
	 */
	public void setPosition(int ePosicion) { position = ePosicion; }
	
	/* ---------------- métodos genéticos ---------------- */
	
	
	/**
	 * Indica si el individuo esta evaluado
	 */
	public boolean isEvaluated(){
		if(fitness > -1000)								//quiza esta no sea la mejor manera de comprobar el fitness
			return true;
		return false;
	}
	
	/**
	 * Genera una configuracion aleatoria de reglas, con las mismas generacion y posicion
	 */
	public void newConfig(){
		Regla [] nConfiguration = new Regla [Configuracion.getNReg()];
		for(int i = 0 ; i < Configuracion.getNReg() ;i++){
			nConfiguration[i] = new Regla();
		}
		Configuration = nConfiguration;
	}
	
	/**
	 * Combina de forma aleatoria los genomas de los genotipos de dos individuos
	 * 
	 * Genera una mayor diversidad de individuos
	 * 
	 */
	public Individuo [] crossDiv (Individuo Padre){
		Individuo Hijos [] = new Individuo [2];											// 2 es el numero de hijos que obtendremos
		Regla aux [] = new Regla [2];													// las posiciones de los hijos son temporales y necesitan posterior actualizacion
		Hijos[0] = new Individuo(generation+1, 0);
		Hijos[1] = new Individuo(generation+1, 1);
		for(int i = 0 ; i < Configuracion.getNReg() ; i++){								//En cada vuelta genera dos reglas
			/* invoca al metodo combine de una regla del individuo que invoca el metodo*/
			aux = Configuration[0].combine(Padre.getGenotype(i), Configuration[i]);
			Hijos[0].setGenotype(i, aux[0].getGenotype());								//Introduce las reglas en los hijos
			Hijos[1].setGenotype(i, aux[1].getGenotype());
		}
		return mutation(Hijos);															//Al generar un hijo se aplica el operador de mutacion
	}
	
	/**
	 * Combina de forma aleatoria los genomas del individuo que invoca el metodo y otro individuo
	 * 
	 * Implica una mayor velocidad de convergencia
	 * 
	 * @param Padre: individuo con el se cruza el objeto que invoca el metodo
	 * @param pos: posicion del array Hijos
	 */
	public Individuo [] crossCon(Individuo Padre){
		Individuo Hijos[] = new Individuo [2];
		Random Generator = new Random();
		Hijos[0] = new Individuo(generation+1, 0);
		Hijos[1] = new Individuo(generation+1, 1);
		/* recorre los genomas combinandolos en los hijos de forma aleatoria */
		for(int i = 0 ; i < Configuracion.getNReg() ; i++){
			if(Generator.nextBoolean() == true){										// Hijo0 reglas de este Individuo ; Hijo1 reglas de Padre
				Hijos[0].setGenotype(i, getGenotype(i).getGenotype());
				Hijos[1].setGenotype(i, Padre.getGenotype(i).getGenotype());
			}
			else{																		// Hijo0 reglas de Padre ; Hijo1 reglas de este Individuo
				Hijos[0].setGenotype(i, Padre.getGenotype(i).getGenotype());
				Hijos[1].setGenotype(i, getGenotype(i).getGenotype());
			}
		}
		
		return mutation(Hijos);
	}
	
	/**
	 * Operador de mutacion
	 * 
	 * Se aplican dos operadores de mutacion implementados en la clase Regla
	 * 
	 */
	public Individuo [] mutation (Individuo Hijos[]){
		Random Generator = new Random();
		for(int i = 0 ; i < 2 ; i++){
			if(Math.abs(Generator.nextInt()%10) == Configuracion.getPMut()*10)								// 1 de cada 10 muta
				Hijos[i].getGenotype(Math.abs(Generator.nextInt()%Configuracion.getNReg())).mutation();		// Genera la mutacion
			if(Math.abs(Generator.nextInt()%10) == Configuracion.getPMiniMut()*10)							// 2 de cada 10 muta
				Hijos[i].getGenotype(Math.abs(Generator.nextInt()%Configuracion.getNReg())).miniMutation();	// Genera la mutacion
		}
		return Hijos;
	}
	
	
	/* ---------------- métodos de escritura de comportamiento ---------------- */
	
	
	/**
	 * Comprueba cuantas reglas dependientes tiene un individuo
	 */
	public int countDep(){
		int nDep = 0;
		for(int i = 0 ; i < Configuracion.getNReg(); i++){
			if(Configuration[i].isDep() == true)
				nDep ++;
		}
		return nDep;
	}
	
	/**
	 * Devuelve un string con el codigo de comportamiento de un individuo 
	 */
	public void writeRoboBehav(){
		/** INICIALIZACION **/
		String temp = new String();
		String aux = new String();
		int dependientes [] = new int [Configuracion.getNReg()];
		int nDependientes = 0, contador = 0;
		File F = null;
		try{
			F = new File(Configuracion.getFilePath()+"G"+Integer.toString(generation)+"N"+Integer.toString(position)+".java");
		}
		catch(Exception e){
			System.out.printf("\n--------------------------------------\nError al generar el fichero.\n--------------------------------------\n");
		}
		FileWriter Fw = null;
		aux = "";
		temp = "";
		try {
			Fw = new FileWriter(F);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.printf("\n--------------------------------------\nError al abrir el fichero.\n--------------------------------------\n");
			e1.printStackTrace();
		}
		/** PARTE 1 : cabecera **/
		temp = "package myRobots;\nimport robocode.*;\n\npublic class ";
		temp = temp + "G" + Integer.toString(generation)+"N"+Integer.toString(position)+" extends Robot{\n\n\t";
		try {
			Fw.write(temp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/** PARTE 2 : eventos **/
		/* tab 1 */
		temp = "public void onHitByBullet(HitByBulletEvent e){\n\t\t";
		/* tab 2 */
		temp = temp + "scan();\n\t}\n\n";
		/* tab 1 */
		temp = temp + "\tpublic void onHitWall(HitWallEvent e){\n\t\t";
		/* tab 2 */
		temp = temp + "scan();\n\t}\n\n";
		try {
			Fw.write(temp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/** PARTE 3 : Reglas **/
		/* tab 1 */	
		for (int i = 0 ; i < Configuracion.getNReg() ; i++){
			aux = Configuration[i].getRoboRule();										// obtiene el codigo de la regla
			temp = "\tpublic void regla"+Integer.toString(i)+" ";			// cabecera de la regla
			if(Configuration[i].isDep() == true){										// determina si la regla es dependiente a eventos
				dependientes[nDependientes] = i;										// en caso de serlo la agrega a un vector para su tratamiento posterior
				nDependientes ++;
				temp = temp + "(ScannedRobotEvent e)";
			}
			else
				temp = temp + "()";
			temp = temp + aux;
			try {
				Fw.write(temp);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		/** PARTE 4 : Run **/
		/* tab 1 */
		temp ="\tpublic void run(){\n\t\t";
		/* tab 2 */
		temp = temp + "int contador = 0;\n\t\t";
		temp = temp + "while (true){\n\t\t\t";
		/* tab 3 */
		temp = temp + "contador = contador % nReglas;\n\t\t\t";
		temp = temp + "if(contador > nReglas)\n\t\t\t\tcontador = contador % nReglas;\n\t\t\t";
		temp = temp + "switch(contador){";
		/* tab 4 */
		for(int i = 0 ; i < Configuracion.getNReg() ; i++){
			if(Configuration[i].isDep() == false){
				contador++;
				temp = temp + "\n\t\t\t\tcase "+Integer.toString(contador)+":\n\t\t\t\t\tregla"+Integer.toString(i)+"();\n\t\t\t\t\tbreak;";
			}
		}
		temp = temp + "\n\t\t\t\tdefault:\n\t\t\t\t\tscan();\n\t\t\t\t\tbreak;";
		temp = temp + "\n\t\t\t}\n\t\t}\n\t}";
		try {
			Fw.write(temp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/** PARTE 5 : Evento scannedRobot **/
		/* tab 1 */
		temp = "\n\n\tpublic void onScannedRobot(ScannedRobotEvent e){";
		/* tab 2 */
		for(int i = 0 ; i < nDependientes ; i++){
			temp = temp + "\n\t\tregla" + Integer.toString(dependientes[i]) + "(e);";
		}
		temp = temp + "\n\t}";
		try {
			Fw.write(temp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		/** PARTE 6 : Variables de clase **/
		/* tab 1 */
		temp = "\n\tstatic public int nReglasTotales = "+ Integer.toString(Configuracion.getNReg()) +";\n\tstatic public int nReglasEvento = "+ Integer.toString(nDependientes) +";\n\tpublic int nReglas = nReglasTotales-nReglasEvento;\n";
		temp = temp + "\n\tpublic double curEnergy = 70;\n\tpublic double curHeat = 0;\n\tpublic double enemy = 1;\n\tpublic double dist = 70;\n\tpublic double enEnergy = 70;\n";
		temp = temp + "\n\t static public double DISTANCE = 500;\n\tstatic public double POWER = 2;\n\tstatic public double DEGREES = 90;\n}";
		try {
			Fw.write(temp);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			Fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Devuelve la configuracion de reglas como un String
	 */
	public String getConfigStr(){
		String Temp = new String("Individuo G"+Integer.toString(generation)+"N"+Integer.toString(position)+"\n");
		for(int i = 0 ; i < Configuracion.getNReg() ; i++){
			Temp = Temp + "\tRegla"+Integer.toString(i)+":\n";
			Temp = Temp + "\t\t" + Configuration[i].getConfigStr()+"\n";
		}
		return Temp;
	}
	
	/**
	 * Imprime por pantalla la configuracion de reglas del individuo
	 */
	public void printConfig(){
		System.out.printf("\nConfiguracion del individuo");
		for(int i = 0 ; i < Configuracion.getNReg() ; i++){
			System.out.printf("\n---------------------------\nRegla %d", i);
			getConfiguration()[i].printConfig();
		}
	}
	
	/**
	 * Indica si dos individuos tienen la misma configuracion de reglas
	 */
	public boolean compareThis(Individuo I){
		boolean temp = true;
		for(int i = 0 ; i < Configuracion.getNReg() ; i++){
			if(getConfiguration()[i].compareThis(I.getConfiguration()[i]) == false){
				temp = false;
//				System.out.printf("Regla %d\t--->\t"+Boolean.toString( getConfiguration()[i].compareThis(I.getConfiguration()[i]) ), i );
			}
		}
		return temp;
	}
	
	
	
	
	/* ---------------- variables de clase ---------------- */ 
	
	
	/**
	 * Valor de adaptacion
	 */
	private int fitness;
	
	/**
	 * Configuracion de reglas
	 */
	private Regla Configuration[];
	
	/**
	 * Etiqueta de elitismo
	 */
	private boolean elite;
	
	/**
	 * Generacion a la que pertenece
	 */
	private int generation;
	
	/**
	 * Posicion que ocupa en la generacion
	 */
	private int position;
	
	
	/* ---------------- funcion main de testeo ---------------- */
	
	
	/**
	 * Funcion main para probar la escritura de comportamientos
	 *
	public static void main(String[] args){
		Individuo I = new Individuo(1, 10);
		
		System.out.printf("\nComprobando detector de evaluados...");
		if(I.isEvaluated() == false)
			System.out.printf("\nEl detector funciona bien.");
		else
			System.out.printf("\nEl detector falla.");
		System.out.printf("\nComprobando detector de dependientes.");
		System.out.printf("\nEste individuo tiene %d reglas dependientes.", I.countDep());
		System.out.printf("\nEscribiendo comportamiento... ");
		I.writeRoboBehav();
		System.out.printf("\nNuevo fichero generado.");
	}
	*/
	
	/**
	 * Main de testeo metodos internos
	 * @param args
	 */
	public static void main(String [] args){
		Individuo I = new Individuo(100, 100);
		Individuo I2 = new Individuo(100, 101);
		Individuo [] Hijos = new Individuo[2];
		Hijos[0] = new Individuo(100, 102);
		Hijos[1] = new Individuo(100, 103);
		//System.out.printf("Este individuo esta evaluado\t--->\t"+Boolean.toString(I.isEvaluated()) );
		I.setFitness(9);
		//System.out.printf("Este individuo esta evaluado\t--->\t"+Boolean.toString(I.isEvaluated()) );
//		I.printConfig();
//		System.out.printf("\n\n"+Boolean.toString(I.compareThis(I2)));
		I.setConfiguration(I2.getConfiguration());
//		System.out.printf("\n\n"+Boolean.toString(I.compareThis(I2)));
		I.newConfig();
		I2.newConfig();
//		System.out.printf("\n\n"+Boolean.toString(I.compareThis(I2)));
//		I.printConfig();
//		I2.printConfig();
		System.out.printf("\n\n---------------------------------\nTesteo de operadores geneticos\nCruce divergente\n");
		Hijos[0].printConfig();
//		Hijos[1].printConfig();
		Hijos = I.crossDiv(I2);
		Hijos[0].printConfig();
//		Hijos[1].printConfig();
		System.out.printf("\n\nCruce convergente\n");
		Hijos = I.crossCon(I2);
		Hijos[0].printConfig();
//		Hijos[1].printConfig();
	}
}
