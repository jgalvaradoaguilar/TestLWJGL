package testlwjgl;

import java.io.*;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    //Creo que esto no hace falta, ni la pantalla ni el Logger, comprobar!!!!!!!!!!!
    public static final int DISPLAY_HEIGHT = 480;
    public static final int DISPLAY_WIDTH = 640;
    public static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    protected static String IP_Servidor_principal;
    protected static int puerto_Servidor_principal;
    protected static String mi_ip;
    protected static int mi_puerto;
    protected static String mi_nombre_usuario;
    // Para poder usar los modelos desde cualquier clase!!!
    protected static Model model1, model2, model3, model4, model5;
    // Para salir del juego (tanto la parte grafica como la de comunicaciones)
    public static boolean salir = false;
    public static boolean enviado_terminar = false;

    static {
        try {
            LOGGER.addHandler(new FileHandler("errors.log", true));
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.toString(), ex);
        }
    }

    public static ArrayList<Float> Random(int cantidad, int limite){
        ArrayList<Float> posiciones = new ArrayList<Float>();
        Random randomGenerator = new Random();
        for(int i=0; i<cantidad; i++){
            float posicion = ((randomGenerator.nextFloat())*limite) - (limite/2);
            //System.out.println("La posicion es: " + posicion);
            posiciones.add(posicion);
        }
        return posiciones;
    }

    public static void main(String[] args) {
        GameEngine gEngine = null;
        //Model model1, model2, model3, model4, model5;
        ModelledObject obj;
        GameCommunications gComm = null;

        
        try {
            System.out.println("Keys:");
            System.out.println("down  - move backwards");
            System.out.println("up    - move forwards");
            System.out.println("left  - strafe left");
            System.out.println("right - strafe right");
            System.out.println("esc   - Exit");

            IP_Servidor_principal = args[0];
            puerto_Servidor_principal = Integer.parseInt(args[1]);
            mi_ip = args[2];
            mi_puerto = Integer.parseInt(args[3]);
            mi_nombre_usuario = args[4];
            // Se inicia la parte de comunicaciones del juego
            gComm = new GameCommunications();
            // Se inicializan los valores de puertos, IP_otros, etc...
            gComm.set_ip_puerto_servidor_principal(IP_Servidor_principal, puerto_Servidor_principal);
            gComm.set_mi_ip_puerto(mi_ip, mi_puerto);
            gComm.set_mi_nombre_usuario(mi_nombre_usuario);
            // Se ponen en marcha las comunicaciones
            gComm.setName("Hilo para comunicaciones, usuario: " + Main.mi_nombre_usuario);
            //gComm.start();
          
            // Se inicia la parte estructural del juego
            gEngine = new GameEngine();
            model1 = gEngine.addModel("Square.glo");
            model2 = gEngine.addModel("Square_1.glo");
            model3 = gEngine.addModel("Bullet.glo");
            model5 = gEngine.addModel("Rock1.glo");
            model4 = gEngine.addModel("Ship.glo");
            
            gEngine.create();
            gEngine.loadModels();
            /*gEngine.addObject(obj = new ModelledObject(0.0f, 0.0f, -0.20f, model1));// es un cuadrado verde
            gEngine.addObject(obj = new ModelledObject(0.0f, 0.0f, 10.20f, model2));// es una piedra plana rara
            gEngine.addObject(obj = new ModelledObject(0.0f, 0.0f, 20.20f, model3)); // estrella de 6 puntas
            gEngine.addObject(obj = new ModelledObject(0.0f, 0.0f, 30.20f, model4));// es la nave
            gEngine.addObject(obj = new ModelledObject(0.0f, 0.0f, 50.20f, model5));*/ // es una roca
            /* En las rocas va Y=[-350..350]  X=[-400..400] */

            ArrayList<Float> x = Random(10, 520);
            ArrayList<Float> y = Random(10, 350);
            // Se crea la nave
            gEngine.addObject(obj = new ModelledObject(0.0f, 0.0f, -100f, mi_nombre_usuario, model4));
            // Se crean las rocas
            gEngine.addObject(obj = new ModelledObject(x.get(0), y.get(0), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(1), y.get(1), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(2), y.get(2), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(3), y.get(3), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(4), y.get(4), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(5), y.get(5), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(6), y.get(6), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(7), y.get(7), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(8), y.get(8), -700f, mi_nombre_usuario, model5));
            gEngine.addObject(obj = new ModelledObject(x.get(9), y.get(9), -700f, mi_nombre_usuario, model5));
            //gEngine.setName("Hilo game engine, usuario: " + Main.mi_nombre_usuario);
            gComm.start();
            gEngine.run(); //Ojo, que de aqui no sale nunca


        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.toString(), ex);
        } finally {
            if (gEngine != null) {
                gEngine.destroy();
            }
        }
    }

}
