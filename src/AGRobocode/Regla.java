package AGRobocode;

import AGRobocode.Configuracion;

import java.util.Random;

/**
 * This class represents a generic rule.
 * @author Jes�s Bermell Guill�n
 *  
 */
public class Regla {
	
	/* ---------------- constructores ---------------- */
	
	/**
	 * Constructor vac�o.
	 */
	public Regla(){
		config = new boolean [Configuracion.getAcc() + Configuracion.getParam()];
		newConfig();
	}
	
	/**
	 * Constructor parametrizado.
	 * @param eConfig configuraci�n externa.
	 */
	public Regla(boolean eConfig[] ){
		config = new boolean [Configuracion.getParam() + Configuracion.getAcc()];
		for(int i = 0 ; i < (Configuracion.getParam()+Configuracion.getAcc()) ; i++)	//copia los datos del vector eConfig
			config[i] = eConfig[i];
	}
	
	
	/* ---------------- metodos get/set ---------------- */
	
	 /**
	 * Devuelve el genotipo de la regla.
	 * @return configuraci�n de la regla.
	 */
	public boolean [] getGenotype(){
		return config;
	}
	
	/**
	 * Establece un nuevo genotipo en la regla.
	 * @param configuraci�n nueva.
	 */
	public void setGenotype(boolean eConfig[]){
		boolean aux;
		for(int i = 0 ; i < Configuracion.getAcc()+Configuracion.getParam() ; i++){
			aux = eConfig[i];
			config[i] =aux;
		}
			
	}
	
	/**
	 * Devuelve un elemento del vector.
	 * @param posici�n del elemento a devolver.
	 * @return elemento de la configuracion.
	 */
	public boolean getGen (int position){
		return config[position];
	}

	/**
	 * Establece un elemento del vector.
	 * @param posicion del futuro elemento.
	 * @param elemento a establecer.
	 */
	public void setGen (int position, boolean eGenome){
		config[position] = eGenome;
	}
	
	/*  ---------------- m�todos de escritura de ficheros ---------------- */

	/**
	 * Devuelve la configuraci�n como un String.
	 * @return cadena con la configuraci�n de la regla sin codificar.
	 */
	public String getConfigStr(){
		String StrCfg = new String("\n[");
		for(int i = 0 ; i < Configuracion.getAcc()+Configuracion.getParam() ; i++){
			switch(i){
				case 0:
					StrCfg = StrCfg + "energia actual = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 1:
					StrCfg = StrCfg + ", temperatura de la torreta = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 2:
					StrCfg = StrCfg + ", numero de oponentes en juego = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 3:
					StrCfg = StrCfg + ", distancia al enemigo = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 4:
					StrCfg = StrCfg + ", energia del enemigo = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 5:
					StrCfg = StrCfg + "|| mover adelante = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 6:
					StrCfg = StrCfg + ", mover atras = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 7:
					StrCfg = StrCfg + ", disparar = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 8:
					StrCfg = StrCfg + ", rotar derecha = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				case 9:
					StrCfg = StrCfg + ", rotar izquierda = ";
					if(config[i] == true)
						StrCfg = StrCfg + "ACTIVADO";
					else
						StrCfg = StrCfg + "DESACTIVADO";
					break;
				default:
					System.out.printf("\n------------------------------------\nFallo al pasar la configuracion de la regla a String.\n------------------------------------\n");
					break;
			}
			StrCfg = StrCfg + " ]";
		}
		return StrCfg;
	}

	/**
	 * Devuelve el c�digo de comportamiento de la regla.
	 * @return comportamiento de la regla codificado.
	 */
	public String getRoboRule(){
		String temp = new String();
		boolean firstCond = true;
		temp = "{\n\tif(";
		
		
		for(int i = 0 ; i < Configuracion.getParam() ; i++){		// bucle de los antecedentes
			if(config[i] == true){
				if(firstCond == false)								// agrega el operador AND
					temp = temp + "&&";
				switch (i) {										// cada caso se corresponde con un parametro a evaluar
				case 0:
					temp = temp + "(curEnergy <= getEnergy())";
					break;
				case 1:
					temp = temp + "(curHeat <= getGunHeat())";
					break;
				case 2:
					temp = temp + "(enemy <= getOthers())";
					break;
				case 3:
					temp = temp + "(dist <= e.getDistance())";
					break;
				case 4:
					temp = temp + "(enEnergy <= getEnergy())";
					break;
				default:
					System.out.println("ERROR AL ESCRIBIR LOS CONDICIONANTES");
					break;
				}
				firstCond = false;
			}
		}
		if(firstCond == true)			//no tiene condicionantes que comprobar
			temp = temp +"true";
		temp = temp + "){";
				
		for(int i = 0 ; i< Configuracion.getAcc() ; i++){		// bucle de los consecuentes
			if(config[i+Configuracion.getParam()] == true){
				switch (i) {									// cada caso se corresponde con una accion a ejecutar
				case 0:
					temp = temp + "\n\t\tahead(DISTANCE);";
					break;
				case 1:
					temp = temp + "\n\t\tback(DISTANCE);";
					break;
				case 2:
					temp = temp + "\n\t\tfire(POWER);";
					break;
				case 3:
					temp = temp + "\n\t\tturnRight(DEGREES);";
					break;
				case 4:
					temp = temp + "\n\t\tturnLeft(DEGREES);";
					break;
				default:
					System.out.println("ERROR AL ESCRIBIR LAS ACCIONES");
					break;
				}
			}
		}
		temp = temp + "}\n\t}\n\n";
		return temp;
	}
	

	/**
	 * Indica si la regla esta sujeta a eventos.
	 * @return true si la regla depende de eventos.
	 */
	public boolean isDep(){
		if((config[3] == true)||(config[4] == true))		//si se comprueban las condiciones sujetas a eventos, la regla es dependiente
			return true;
		else
			return false;
	}
	
	/**
	 * Compara dos reglas.
	 * @param R regla a comparar.
	 * @return true si son iguales, false si son diferentes.
	 * 
	 * 
	 * 														REVISAR
	 * 
	 */
	public boolean compareThis(Regla R){
		boolean temp [] = new boolean [(Configuracion.getParam()+Configuracion.getAcc())];
		temp = R.getGenotype();
		for(int i = 0 ; i < (Configuracion.getParam()+Configuracion.getAcc()) ; i++){ //compara las configuraciones de las reglas
			if(temp[i]!=config[i])
				return false;
		}
			return true;
	}
	

	/* ---------------- m�todos gen�ticos ---------------- */
	
	/**
	 * Combina dos genotipos de dos Reglas.
	 * @param regla a combinar.
	 * @param regla a combinar.
	 * @return array con dos reglas resultantes de combinar los par�metros de entrada.
	 */
	public Regla [] combine (Regla R1, Regla R2){
		/* inicializaci�n */
		Regla Combined [] = new Regla [2];
		Random Generator = new Random ();
		Combined[0] = new Regla();
		Combined[1] = new Regla();
		/* Introduce los genomas de forma aleatoria */
		for(int i = 0 ; i < Configuracion.getParam()+Configuracion.getAcc() ; i++){
			if(Generator.nextBoolean() == true){						// se quedan como est�n
				Combined[0].setGen(i, R1.getGen(i));
				Combined[1].setGen(i, R2.getGen(i));
			}
			else{														// intercambia valores
				Combined[1].setGen(i, R2.getGen(i));
				Combined[0].setGen(i, R1.getGen(i));
			}
		}
		return Combined;
	}
	
	/**
	 * Generador de configuraci�n.
	 * Crea una configuraci�n de regla aleatoria.
	 */
	public void newConfig(){
		boolean temp [] = new boolean [Configuracion.getParam()+Configuracion.getAcc()];
		Random ran = new Random(); 
		for(int i = 0 ; i < (Configuracion.getParam()+Configuracion.getAcc()); i++){//va generando los valores para el array
			temp[i]= ran.nextBoolean();
		}
		config = temp;
	}

	/**
	 * Cambia al azar la estructura de la regla.
	 */
	public void mutation(){
		Random Generator = new Random ();
		for(int i = 0 ; i < Configuracion.getAcc()+Configuracion.getParam(); i ++){
			config[i] = Generator.nextBoolean();
		}
		
	}
	
	/**
	 * Intercambia los valores de dos posiciones de una misma regla.
	 */
	public void miniMutation(){
		Random Generator = new Random();
		int p1 = -1, p2 = -1;
		boolean aux;
		/* genero las posiciones a intercambiar */
		do{
			p1 = Generator.nextInt()%(Configuracion.getAcc()+Configuracion.getParam());
		}while(p1 < 0);
		do{ 
			p2 = Generator.nextInt()%(Configuracion.getAcc()+Configuracion.getParam()); 
			}while(p1 == p2 || p2 < 0);
		
		/* intercambio */
		aux = config[p1];
		config[p1] = config[p2];
		config[p2] = aux;
	}
	
	/* ---------------- m�todos auxiliares ---------------- */
	
	/**
	 * Muestra por pantalla la configuracion actual.
	 * 
	 * 					REVISAR
	 * 
	 */
	public void printConfig(){
		int temp = Configuracion.getAcc()+Configuracion.getParam();
		System.out.printf("\n{");
		for(int i = 0 ; i < temp ; i++){
			if(config[i] == true)
				System.out.printf("1");
			else
				System.out.printf("0");
			if(i == 4)
				System.out.printf("|");
			if( i != temp-1 )
				System.out.printf(", ");
		}
		System.out.printf("}");
	}
	
	/**
	 * Vector de booleanos, refleja la configuraci�n de la regla.
	 * Su tama�o vendr� determinado por la cantidad de par�metros (Configuracion.getParam())
	 * y la cantidad de acciones por ejecutar (Configuracion.getAcc()).
	 */
	private boolean config [];
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Funcion main para testear todos los m�todos internos
	 * @param args
	 *
	public static void main(String[] args){
		Regla R = new Regla();
		R.printConfig();/*
		if(R.isDep() == true)
			System.out.printf("\nEsta regla est� sujeta a eventos");
		else
			System.out.printf("\nEsta regla no esta sujeta a eventos");
		Regla R1 = new Regla(R.getGenotype());
		R.compareThis(R1);
		String codigo = R.getRoboRule();
		System.out.printf("\nCodigo de la regla:\n"+codigo);*
		R.mutation();
		R.printConfig();
		R.miniMutation();
		System.out.printf("\n");
		R.printConfig();
	}*/
	
	/**
	 * main de testeo de todos los m�todos internos
	 * @param args
	 */
	public static void main(String [] args){
		Regla R = new Regla();
		Regla R1 = new Regla(R.getGenotype());
		Regla [] Reglas = null;
		R.printConfig();
		System.out.printf("\nEsta regla es dependiente de eventos?\t--->\t"+Boolean.toString(R.isDep()));			//isDep
		System.out.printf("\nR y R1 son iguales?\t--->\t"+Boolean.toString(R.compareThis(R1)));						//compareThis
		R1.newConfig();															//newConfig (testeado tambien en el constructor)
		R1.printConfig();
		System.out.printf("\nR y R1 son iguales?\t--->\t"+Boolean.toString(R.compareThis(R1)));
		System.out.printf("\n-------------------------\n");
		for(int i = 0 ; i < 5 ; i++){
			Reglas = R1.combine(R1, R);											//combine
			Reglas[0].printConfig();
			Reglas[1].printConfig();
			System.out.printf("\n-------------------------\n");
		}
		System.out.printf("\n\nTesteo operadores mutagenos\n");
		System.out.printf("\n-------------------------\n");
		for(int i = 0 ; i < 5 ; i++){
			Reglas[0].mutation();												// mutation
			Reglas[0].printConfig();
			Reglas[1].miniMutation();											// miniMutation
			Reglas[1].printConfig();
			System.out.printf("\n-------------------------\n");
		}
	}
	
}
