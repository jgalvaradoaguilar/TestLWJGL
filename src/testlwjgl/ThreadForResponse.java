/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testlwjgl;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Acer
 */
public class ThreadForResponse implements Runnable{
    //private InetAddress IP_cliente;
    //private int puerto_cliente;
    private DataInputStream DIS;
    private DataOutputStream DOS;
    private Socket connection;

    public ThreadForResponse(Socket connection_){
        try{
            connection = connection_;
            // Establezco la conexión privada con ese usuario
            DIS = new DataInputStream(connection.getInputStream());
            DOS = new DataOutputStream(connection.getOutputStream());
        }catch(Exception e){
            System.out.println("Error al recibir la conexión");
        }
    }


    public void run(){
        String mensaje;
        String nombre_peer;
        String IP_peer;
        int puerto_peer;
        int identificador;

        // Leo lo que me envía otro usuario
        try{
            identificador = DIS.read();
            System.out.println("He recibido un: "+identificador);
            switch(identificador){
                case 1:
                    // responder a HELLO (ésta opción sólo le llegará al servidor principal)
                    DOS.writeUTF("HI PEER");
                    DOS.flush();
                    nombre_peer = DIS.readUTF();
                    IP_peer = DIS.readUTF();
                    puerto_peer = DIS.readInt();
                    System.out.println("Datos recogidos (sayHello, 1): " + nombre_peer +" "+ IP_peer +" "+ puerto_peer);
                    DOS.writeUTF("END");
                    DOS.flush();
                    // Cierra el socket
                    connection.close();
                    // El servidor principal le enviará ese peer añadido al resto de jugadores
                    GameCommunications.actualiza_peers_add(IP_peer, puerto_peer, nombre_peer);
                    GameCommunications.add_peer_conocido(IP_peer, puerto_peer, nombre_peer);
                    // En cada add_peer_conocido se establecera una comunicacion unidireccional
                    break;

                case 2:
                    System.out.println("No implementado");
                    break;
                    // Dar de baja a un peer (esta opción sólo le llegará al servidor principal, al dar a Esc)
                    /*DOS.writeUTF("BYE PEER");
                    DOS.flush();
                    nombre_peer = DIS.readUTF();
                    IP_peer = DIS.readUTF();
                    puerto_peer = DIS.readInt();
                    DOS.writeUTF("END");
                    DOS.flush();
                    // Cierra el socket
                    connection.close();
                    // El servidor principal le enviará ese peer eliminado al resto de jugadores
                    GameCommunications.del_peer_conocido(IP_peer, puerto_peer, nombre_peer);
                    GameCommunications.actualiza_peers_del(IP_peer, puerto_peer, nombre_peer);
                    break;*/
                    
                case 3:
                    // Añade nuevo peer a la lista de peers de los clientes(esta opción sólo le llegará a clientes antiguos)
                    System.out.println("He recibido 3 ADD A KNOWN PEER");
                    DOS.writeUTF("OK");
                    DOS.flush();
                    nombre_peer = DIS.readUTF();
                    IP_peer = DIS.readUTF();
                    puerto_peer = DIS.readInt();
                    System.out.println("Datos recogidos en '3' : " + nombre_peer +" "+ IP_peer +" "+ puerto_peer);
                    DOS.writeUTF("END");
                    DOS.flush();
                    // Cierra el socket
                    connection.close();
                    // El cliente procede a añadir al peer a su lista de jugadores (peers) conocidos
                    GameCommunications.add_peer_conocido(IP_peer, puerto_peer, nombre_peer);
                    // Se envian los datos del jugador antiguo al nuevo jugador
                    GameCommunications.say_Hello2PeerInverse(IP_peer, puerto_peer, Main.mi_ip, Main.mi_puerto, Main.mi_nombre_usuario);
                    break;

                case 4:
                    // Elimina un peer de la lista de peers de los clientes(esta opción sólo le llegará a clientes antiguos)
                    DOS.writeUTF("OK");
                    DOS.flush();
                    nombre_peer = DIS.readUTF();
                    IP_peer = DIS.readUTF();
                    puerto_peer = DIS.readInt();
                    DOS.writeUTF("END");
                    DOS.flush();
                    // Cierra el socket
                    connection.close();
                    // El cliente procede a borrar al peer de su lista de jugadores (peers) conocidos
                    GameCommunications.del_peer_conocido(IP_peer, puerto_peer, nombre_peer);
                    // Se borran los objetos de ese cliente que se da de baja
                    GameCommunications.del_objetos_peer(nombre_peer);
                    break;

                case 5:
                    // El peer envia los datos de sus objetos al resto de peers
                    DOS.writeUTF("OK");
                    DOS.flush();
                    int n_objetos;
                    float posX, posY, posZ;
                    ModelledObject model_object;

                    // Se mantiene en un bucle para la comunicacion infinita
                    String otro_peer = DIS.readUTF();
                    while (! Main.salir){
                        n_objetos = DIS.readInt();
                        //String otro_peer = DIS.readUTF();
                        //System.out.println("He recibido (5), el otro_peer es: " + otro_peer);
                        // Se borran los objetos previos de ese otro_peer
                        GameCommunications.del_objetos_peer(otro_peer);
                        synchronized(GameEngine.objects){
                            // Se recogen los datos de todos los objetos del otro_peer
                            for(int i=0; i<n_objetos; i++){
                                posX = DIS.readFloat();
                                posY = DIS.readFloat();
                                posZ = DIS.readFloat();
                                // Puede que haya que recibir mas datos de ese objeto

                                // Se registra el objeto
                                model_object = new ModelledObject(posX, posY, posZ, otro_peer, Main.model5);
                                GameEngine.objects.add(model_object);
                            }
                        }
                        // Se recoge el 'END' de fin de transmisión
                        mensaje = DIS.readUTF();
                        // Se envía un 'END' de confirmación
                        if (mensaje.equalsIgnoreCase("END")){
                            DOS.writeUTF("END");
                            DOS.flush();
                        }
                    }

                    // Cierra el socket (solo al final)
                    connection.close();
                    break;

                case 6:
                    // Añade nuevo peer a la lista de peers de los clientes(esta opción sólo le llegará a clientes nuevos)
                    System.out.println("He recibido 6 ADD A KNOWN PEER");
                    DOS.writeUTF("OK");
                    DOS.flush();
                    nombre_peer = DIS.readUTF();
                    IP_peer = DIS.readUTF();
                    puerto_peer = DIS.readInt();
                    System.out.println("Datos recogidos en '6' : " + nombre_peer +" "+ IP_peer +" "+ puerto_peer);
                    DOS.writeUTF("END");
                    DOS.flush();
                    // Cierra el socket
                    connection.close();
                    // El cliente nuevo procede a añadir al peer antiguo a su lista de jugadores (peers) conocidos
                    GameCommunications.add_peer_conocido(IP_peer, puerto_peer, nombre_peer);
                    break;
                    
                default:
                    // Acciones alternativas, en versiones posteriores del juego!!!
                    System.out.println("Protocolo erróneo de mensaje!!!");
                    // Cierra el socket
                    connection.close();
                    break;
            }
        }catch(Exception e){
            System.out.println("Problemas al tratar al cliente");
            try{
                connection.close();
            }catch(Exception exc){
                System.out.println("Excepcion en connection.close() de la clase ThreadForResponse");
            }
        }
    }

}
