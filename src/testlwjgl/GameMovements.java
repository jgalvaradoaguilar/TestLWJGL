package testlwjgl;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Acer
 */
public class GameMovements implements Runnable{
    private int contador_cuadrado = 0;

    public GameMovements(){
    }

    public void run(){


        while(! Main.salir){
            // Se corrigen las posiciones para que los objetos (excepto naves y balas)
            // cada 2 segundos
            sleep(2000);

            contador_cuadrado ++;
            Class clase;
            String modelo_usado;
            ModelledObject model_object;
            float velX, velY, velZ;
            if (contador_cuadrado > 4){
                contador_cuadrado = 1;
            }
            synchronized(GameEngine.objects){
                for (GameObject mi_objeto : GameEngine.objects) {
                    clase = mi_objeto.getClass();
                    if (clase.getName().equalsIgnoreCase("testlwjgl.ModelledObject")){
                        model_object = (ModelledObject) mi_objeto;
                        modelo_usado = model_object.getModel().get_fileName();
                        // Por lo pronto solo tendre una nave y el resto rocas
                        if (! (modelo_usado.equalsIgnoreCase("Ship.glo")) ){
                            // Se recuperan las velocidades
                            velX = mi_objeto.getVX();
                            velY = mi_objeto.getVY();
                            velZ = mi_objeto.getVZ();
                            //System.out.println("Veloc. Z es :  " + velZ);
                            // Para el movimiento inicial, detenido
                            if (contador_cuadrado == 1){
                                // Se la redirige hacia la derecha
                                velX = 0.5f;
                                velY = 0;
                            }
                            // Va hacia la derecha
                            if (contador_cuadrado == 2){
                                // Se la redirige hacia arriba
                                velX = 0;
                                velY = 0.5f;
                            }
                            // Va hacia arriba
                            if (contador_cuadrado == 3){
                                // Se la redirige hacia la izquierda
                                velY = 0;
                                velX = -0.5f;
                            }
                            // Va hacia la izquierda
                            if (contador_cuadrado == 4){
                                // Se la redirige hacia abajo
                                velX = 0;
                                velY = -0.5f;
                            }
                            mi_objeto.setVelocity(velX, velY, velZ);
                        }
                    }
                }
            }
        }

    }


    public void sleep(int time_ms){
        try{
            Thread.sleep(time_ms);
        }catch (InterruptedException ex) {
            // No es necesario hacer nada
        }
    }


}
