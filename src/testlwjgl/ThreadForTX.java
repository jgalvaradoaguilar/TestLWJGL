/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testlwjgl;
import java.io.*;
import java.net.Socket;

/**
 *
 * @author Acer
 */
public class ThreadForTX implements Runnable{
    //protected static final ArrayList<String> otros_peers = new ArrayList<String>();
    private String IP_peer, nombre_peer;
    private int puerto_peer;

    public ThreadForTX(String IP, int puerto, String nombre_usuario){
        IP_peer = IP;
        puerto_peer = puerto;
        nombre_peer = nombre_usuario;
        /*System.out.println("La IP_peer es: " + IP_peer);
        System.out.println("El puerto del peer es: " + puerto_peer);
        System.out.println("El nombre del peer es: " + nombre_peer);*/
    }

    public void run(){
        // Le envio mis asteroides al peer cada 30 ms
        DataOutputStream DOS = null;
        DataInputStream DIS = null;
        String mensaje;
        int n_objetos;
        Socket socket = null;
        Class clase;
        String modelo_usado, propietario_objeto;
        ModelledObject model_object;
        boolean enviado_terminar = false;

        // Se contacta con el jugador para enviar mis datos
        try{
            // Se crea el socket, una sola vez
            socket = new Socket(IP_peer, puerto_peer);
            // System.out.println ("Conexión establecida con el peer: "+nombre_peer+ " previamente conocido");
            // Se obtienen los streams para enviar/recibir datos
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new DataInputStream(socket.getInputStream());
            // 5 es para decirle al cliente conocido que se le envian mis objetos del juego
            DOS.write(5);
            DOS.flush();
            // Recibo la respuesta
            mensaje = DIS.readUTF();

            // Compruebo que el mensaje recibido sea "OK"
            if (mensaje.equalsIgnoreCase("OK")){
                // Aqui el destinatario me dice un puerto en el que puedo mandarle los datos
                
                // se envía el usuario al que pertenecen los datos
                DOS.writeUTF(Main.mi_nombre_usuario);

                //while(! Main.salir){
                while(! enviado_terminar){
                    // cada 30 ms se envian los datos
                    sleep(30);
                    // Se cuenta el numero de datos (objetos) a transmitir
                    n_objetos = 0;
                    if (Main.salir){
                        enviado_terminar = true;
                        GameCommunications.actualiza_peers_del(IP_peer, puerto_peer);
                    }

                    if (! enviado_terminar){
                        synchronized(GameEngine.objects){
                            //Envío los objetos (de los que tengo la propiedad!!!)
                            for (GameObject mi_objeto : GameEngine.objects) {
                                clase = mi_objeto.getClass();
                                if (clase.getName().equalsIgnoreCase("testlwjgl.ModelledObject")){
                                    model_object = (ModelledObject) mi_objeto;
                                    propietario_objeto = model_object.getUsuario();
                                    modelo_usado = model_object.getModel().get_fileName();
                                    //Si soy el propietario...
                                    if (propietario_objeto.equalsIgnoreCase(Main.mi_nombre_usuario)){
                                        //Si es un objeto que se deba enviar...
                                        if (modelo_usado.equalsIgnoreCase("Rock1.glo"))
                                            n_objetos ++;
                                    }
                                }
                            }

                            DOS.writeInt(n_objetos);
                         
                            // Envio los datos de mis objetos
                            for (GameObject mi_objeto : GameEngine.objects) {
                                clase = mi_objeto.getClass();
                                // Se envian los datos para cada objeto, si es necesario
                                if (clase.getName().equalsIgnoreCase("testlwjgl.ModelledObject")){
                                    model_object = (ModelledObject) mi_objeto;
                                    propietario_objeto = model_object.getUsuario();
                                    modelo_usado = model_object.getModel().get_fileName();
                                    // Si soy el propietario
                                    if (propietario_objeto.equalsIgnoreCase(Main.mi_nombre_usuario)){
                                        // Se envian los datos, si es un objeto pertinente
                                        if (modelo_usado.equalsIgnoreCase("Rock1.glo")){
                                            // Se envian las posiciones
                                            DOS.writeFloat(mi_objeto.getX());
                                            DOS.writeFloat(mi_objeto.getY());
                                            DOS.writeFloat(mi_objeto.getZ());
                                            // Puede que sea necesario enviar las velocidades
                                            DOS.flush();
                                        }
                                    }
                                }
                            }// Fin de envio de mis objetos
                        }// Fin de sincronismo de objetos
                        DOS.writeUTF("END");
                        DOS.flush();
                        mensaje = DIS.readUTF();
                        if (mensaje.equalsIgnoreCase("END")){
                            // System.out.println("Protocolo de envío de datos de objetos concluído con éxito");
                        }else{
                            System.out.println("Hubo un error al transmitir mis datos de objetos");
                        }
                    }// if
                        
                } // Fin del while
                
            }// Fin del OK
        }catch(Exception e){
            System.err.println(e);
            System.out.println("Excepcion en la recepcion del END final (5)");
        }
        
        // Se cierra el socket
        try{
            socket.close();
        }catch(Exception e){
            System.out.println("Excepcion al cerrar el socket");
        }
    } // Fin del RUN!!!

        

    public void sleep(int time_ms){
        try{
            Thread.sleep(time_ms);
        }catch (InterruptedException ex) {
            // No es necesario hacer nada
        }
    }


    public void termina_todo(DataOutputStream DOS, DataInputStream DIS){
        try{
            // Cierro las conexiones
            if (DOS != null)
                DOS.close();
            if (DIS != null)
                DIS.close();
        }catch(Exception e){
            System.out.println("Error en la función termina_todo");
        }
    }


}
