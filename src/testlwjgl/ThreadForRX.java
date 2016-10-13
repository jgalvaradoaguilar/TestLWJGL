/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testlwjgl;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;


/**
 *
 * @author Acer
 */
public class ThreadForRX implements Runnable{

    private String mi_ip;
    private int mi_puerto;
    private ServerSocket mi_socketRx;
    //private ExecutorService executor = Executors.newCachedThreadPool();


    public ThreadForRX(String ip, int puerto){
        mi_ip = ip;
        mi_puerto = puerto;
    }

        public void run(){
            try{
                /*mi_socketRx = new ServerSocket(mi_puerto);*/
                mi_socketRx = new ServerSocket(mi_puerto, 0, InetAddress.getByName(mi_ip));
                mi_socketRx.setSoTimeout(250);
                System.out.println ("Peer escuchando en el puerto: " + mi_puerto);
                Socket connection;
                while (! Main.salir){
                    try{
                        connection = mi_socketRx.accept();
                    }catch(SocketTimeoutException ee){
                        continue; //va a la primera linea del bucle while
                    }
                    // Lanzo un objeto de la clase ThreadForResponse (mía) que tratará cada conexión entrante
                    //Runnable hilo_tratamiento = new ThreadForResponse(connection);
                    //executor.execute(hilo_tratamiento);
                    //System.out.println("Antes de lanzar hilo_tratamiento");
                    ThreadForResponse hilo_forResponse = new ThreadForResponse(connection);
                    Thread hilo_for_response = new Thread(hilo_forResponse);
                    hilo_for_response.setName("Hilo para ThreadForResponse, usuario: "+ Main.mi_nombre_usuario);
                    hilo_for_response.start();
                    //System.out.println("Despues de lanzar hilo tratamiento");
                }
                mi_socketRx.close();
            }catch(Exception e){
                System.out.println("Problemas en el servidor!!!");
            }
        }

}
