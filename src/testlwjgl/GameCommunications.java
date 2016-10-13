/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package testlwjgl;
import java.io.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 *
 * @author Acer
 */
public class GameCommunications extends Thread{

    //private ExecutorService executor = Executors.newCachedThreadPool();
    protected static final ArrayList<String> otros_peers = new ArrayList<String>();
    private String IP_Servidor_principal;
    private int puerto_Servidor_principal;
    private static String mi_ip;
    private static int mi_puerto;
    private static String mi_nombre_usuario;


    // Se define el constructor de la clase
    public GameCommunications(){
    }

    public void set_ip_puerto_servidor_principal(String IP, int puerto){
        IP_Servidor_principal = IP;
        puerto_Servidor_principal = puerto;
    }

    public void set_mi_ip_puerto(String IP, int puerto){
        mi_ip = IP;
        mi_puerto = puerto;
    }

    public void set_mi_nombre_usuario(String nombre){
        mi_nombre_usuario = nombre;
    }

    @Override
    public void run(){

        // variable para comprobar si se registra correctamente
        boolean error_registro = false;

        // Se comprueba si soy el servidor principal
        boolean soy_servidor_principal = false;
        // A nivel de IP
        if (IP_Servidor_principal.equalsIgnoreCase(mi_ip)){
            // A nivel de puerto
            if(puerto_Servidor_principal == mi_puerto)
                soy_servidor_principal = true;
        }

        // Si no soy el principal...
        if (! soy_servidor_principal){
            // se añade la IP+puerto del principal a los otros peer
            add_peer_conocido(IP_Servidor_principal, puerto_Servidor_principal, "servidor");
            System.out.println("Despues de add_peer_conocido, si no soy servidor ppal");
            // se le comunica al servidor principal que acabamos de iniciar,
            // este anuncio siempre será por TCP, por eso se separa
            error_registro = say_Hello2Server(IP_Servidor_principal, puerto_Servidor_principal);
            //System.out.println("Despues de say-Hello");
        }

        // Si no hubo errores de registro
        if (! error_registro){
            /* Versión para más de un server socket
            // Se pondrá cada "peer" a escuchar en su puerto respectivo
            ThreadForRX hilo_recepciones = new ThreadForRX(mi_ip, mi_puerto);
            Thread hiloRX = new Thread(hilo_recepciones);
            hiloRX.setName("Hilo para recepciones, usuario: "+ Main.mi_nombre_usuario);
            hiloRX.start();
             */

            try{
                /*mi_socketRx = new ServerSocket(mi_puerto);*/
                ServerSocket mi_socketRx = new ServerSocket(mi_puerto, 0, InetAddress.getByName(mi_ip));
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
    }   // fin del método run


    
    public boolean say_Hello2Server(String IP, int puerto){
        boolean error = false;
        DataOutputStream DOS = null;
        DataInputStream DIS = null;
        Socket socket = null;
        try{
            String mensaje;
            socket = new Socket(IP, puerto);
            System.out.println ("Conexión establecida con el servidor principal");
            // Se obtienen los streams para enviar/recibir datos
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new DataInputStream(socket.getInputStream());
            // 1 es para decirle al servidor que hay un peer nuevo
            DOS.write(1);
            DOS.flush();
            // Recibo la respuesta
            mensaje = DIS.readUTF();
            // Compruebo que el mensaje recibido sea "HI PEER"
            if (mensaje.equalsIgnoreCase("HI PEER")){
                // Le envío al servidor el nombre de usuario, además de
                // la IP y el puerto donde recibiré los datos
                DOS.writeUTF(mi_nombre_usuario);
                DOS.writeUTF(mi_ip);
                DOS.writeInt(mi_puerto);
                DOS.flush();
                /*System.out.println("say_Hello2Server. El nombre es: " + mi_nombre_usuario);
                System.out.println("say-Hello2Server. la ip  es: " + mi_ip);
                System.out.println("say-Hello2Server. El puerto es: " + mi_puerto);*/
            }else{
                error = true;
            }
            // Si no hay error en el protocolo...
            // Compruebo que el servidor me envía "END"
            if(! error){
                mensaje = DIS.readUTF();
                if (mensaje.equalsIgnoreCase("END")){
                    // Imprimo que todo ha ido bien
                    System.out.println("Protocolo de registro concluído con éxito");
                }else{
                    error = true;
                }
            }
        }catch(Exception e){
            error = true;
            System.err.println(e);
        }
        termina_conexion(socket);
        return error;
    }


    public static boolean say_Hello2Peer(String IP_antiguo, int puerto_antiguo,
                    String IP_nuevo, int puerto_nuevo, String nombre_usuario_nuevo){
        boolean error = false;
        DataOutputStream DOS = null;
        DataInputStream DIS = null;
        Socket socket = null;
        try{
            String mensaje;
            socket = new Socket(IP_antiguo, puerto_antiguo);
            System.out.println ("Conexión establecida con el jugador antiguo");
            // Se obtienen los streams para enviar/recibir datos
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new DataInputStream(socket.getInputStream());
            // 3 es para decirle al cliente antiguo que hay un peer nuevo
            DOS.write(3);
            DOS.flush();
            // Recibo la respuesta
            mensaje = DIS.readUTF();
            // Compruebo que el mensaje recibido sea "OK"
            if (mensaje.equalsIgnoreCase("OK")){
                // Le envío al servidor el nombre de usuario, además de
                // la IP y el puerto donde recibiré los datos
                DOS.writeUTF(nombre_usuario_nuevo);
                DOS.writeUTF(IP_nuevo);
                DOS.writeInt(puerto_nuevo);
                DOS.flush();
                //System.out.println("say_Hello2Peer. El nombre es: " + nombre_usuario_nuevo);
                //System.out.println("say-Hello2Peer. la ip  es: " + IP_nuevo);
                //System.out.println("say-Hello2Peer. El puerto es: " + puerto_nuevo);
            }else{
                error = true;
            }
            // Si no hay error en el protocolo...
            // Compruebo que el servidor me envía "END"
            if(! error){
                mensaje = DIS.readUTF();
                if (mensaje.equalsIgnoreCase("END")){
                    // Imprimo que todo ha ido bien
                    System.out.println("Protocolo de registro (antiguo conoce a nuevo) concluído con éxito");
                }else{
                    error = true;
                }
            }
        }catch(Exception e){
            error = true;
            System.err.println(e);
        }
        termina_conexion(socket);
        return error;
    }


        public static boolean say_Hello2PeerInverse(String IP_nuevo, int puerto_nuevo,
                    String IP_antiguo, int puerto_antiguo, String nombre_usuario_antiguo){
        boolean error = false;
        DataOutputStream DOS = null;
        DataInputStream DIS = null;
        Socket socket = null;
        try{
            String mensaje;
            socket = new Socket(IP_nuevo, puerto_nuevo);
            System.out.println ("Conexión establecida con el jugador nuevo");
            // Se obtienen los streams para enviar/recibir datos
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new DataInputStream(socket.getInputStream());
            // 6 es para decirle al cliente nuevo que conozca al antiguo
            DOS.write(6);
            DOS.flush();
            // Recibo la respuesta
            mensaje = DIS.readUTF();
            // Compruebo que el mensaje recibido sea "OK"
            if (mensaje.equalsIgnoreCase("OK")){
                // Le envío al servidor el nombre de usuario, además de
                // la IP y el puerto donde recibiré los datos
                DOS.writeUTF(nombre_usuario_antiguo);
                DOS.writeUTF(IP_antiguo);
                DOS.writeInt(puerto_antiguo);
                DOS.flush();
                System.out.println("say_Hello2PeerInverse. El nombre es: " + nombre_usuario_antiguo);
                System.out.println("say-Hello2PeerInverse. la ip  es: " + IP_antiguo);
                System.out.println("say-Hello2PeerInverse. El puerto es: " + puerto_antiguo);
            }else{
                error = true;
            }
            // Si no hay error en el protocolo...
            // Compruebo que el servidor me envía "END"
            if(! error){
                mensaje = DIS.readUTF();
                if (mensaje.equalsIgnoreCase("END")){
                    // Imprimo que todo ha ido bien
                    System.out.println("Protocolo de registro (nuevo conoce a antiguo) concluído con éxito");
                }else{
                    error = true;
                }
            }
        }catch(Exception e){
            error = true;
            System.err.println(e);
        }
        termina_conexion(socket);
        return error;
    }

    public static void add_peer_conocido(String IP, int puerto, String nombre_usuario){
        String usuario = IP + " " + puerto + " " + nombre_usuario;
        synchronized(otros_peers){
            otros_peers.add(usuario);
        }
        // Se inicia el proceso de transmisión de datos a ese "peer"
        ThreadForTX hilo_transmision = new ThreadForTX(IP, puerto, nombre_usuario);
        Thread hiloTX = new Thread(hilo_transmision);
        hiloTX.setName("Hilo para transmision periodica, usuario: " + Main.mi_nombre_usuario);
        hiloTX.start();
    }

    
    public static void actualiza_peers_add(String IP, int puerto, String nombre_usuario){
        // Se envia el peer nuevo a todos los peers que conoce el servidor principal
        String []datos_peer;
        String IP_peer, nombre_peer;
        int puerto_peer;
        //System.out.println("En actualiza_peers_add");

        synchronized(otros_peers){
            //System.out.println("Tamaño de otros_peers es:  "+ otros_peers.size());
            for (String peer : otros_peers) {
                // Se obtienen los datos de cada jugador previo
                datos_peer = peer.split("\\s+");
                IP_peer = datos_peer[0];
                puerto_peer = Integer.parseInt(datos_peer[1]);
                nombre_peer = datos_peer[2];
                System.out.println("Datos del jugador antiguo, IP: "+IP_peer+" ,puerto: "+puerto_peer);
                // Se envia los datos del nuevo jugador al jugador antiguo
                say_Hello2Peer(IP_peer, puerto_peer, IP, puerto, nombre_usuario);
            }
        }
    }
         


    public static void del_peer_conocido(String IP, int puerto, String nombre_usuario){
        String usuario = IP + " " + puerto + " " + nombre_usuario;
        System.out.println("En del_peer_conocido 4, usuario a borrar es: " + usuario);
        synchronized(otros_peers){
            System.out.println("Hay " + otros_peers.size() + " otros peers");
            for(int i=0; i<otros_peers.size(); i++){
                if(otros_peers.get(i).equalsIgnoreCase(usuario)){
                    otros_peers.remove(i);
                    break;
                }
            }
            System.out.println("Despues de borrar hay " + otros_peers.size() +" otros peers");
        }
    }


    public static void del_objetos_peer(String nombre_peer){
        // Se borran los objetos de ese otro_peer
        Class clase;
        String propietario_objeto;
        ModelledObject model_object;
        synchronized(GameEngine.objects){
            ArrayList<GameObject> aux_objects = new ArrayList<GameObject>();
            for (GameObject mi_objeto : GameEngine.objects) {
                clase = mi_objeto.getClass();
                if (clase.getName().equalsIgnoreCase("testlwjgl.ModelledObject")){
                    model_object = (ModelledObject) mi_objeto;
                    propietario_objeto = model_object.getUsuario();
                    if (! propietario_objeto.equalsIgnoreCase(nombre_peer)){
                        aux_objects.add(mi_objeto);
                    }
                }
                else{
                    aux_objects.add(mi_objeto);
                }
            }
            // Se vuelca el nuevo ArrayList en el antiguo
            GameEngine.objects.clear();
            for (GameObject mi_objeto : aux_objects){
                GameEngine.objects.add(mi_objeto);
            }
        }
    }

    
    public static boolean actualiza_peers_del(String IP_peer, int puerto_peer){
        // Se envia el peer eliminado (YO) a todos los peers que conoce el servidor principal
        DataOutputStream DOS = null;
        DataInputStream DIS = null;
        String mensaje;
        boolean error = false;
        Socket socket = null; 
             
        // Se envia los datos de que me voy al otro peer
        try{
            socket = new Socket(IP_peer, puerto_peer);
            System.out.println ("Conexión establecida con el peer previamente conocido");
            // Se obtienen los streams para enviar/recibir datos
            DOS = new DataOutputStream(socket.getOutputStream());
            DIS = new DataInputStream(socket.getInputStream());
            // 4 es para decirle al cliente conocido que hay un peer eliminado (YO)
            DOS.write(4);
            DOS.flush();
            // Recibo la respuesta
            mensaje = DIS.readUTF();
            // Compruebo que el mensaje recibido sea "OK"
            if (mensaje.equalsIgnoreCase("OK")){
                // Le envío al cliente conocido el nombre de usuario, además de
                // la IP y el puerto del cliente (peer) eliminado (YO)
                DOS.writeUTF(Main.mi_nombre_usuario);
                DOS.writeUTF(Main.mi_ip);
                DOS.writeInt(Main.mi_puerto);
                DOS.flush();
            }else{
                error = true;
            }
            // Si no hay error en el protocolo...
            // Compruebo que el servidor me envía "END"
            if(! error){
                mensaje = DIS.readUTF();
                if (mensaje.equalsIgnoreCase("END")){
                    // Imprimo que todo ha ido bien
                    System.out.println("Protocolo de actualización (DEL) concluído con éxito");
                }else{
                    error = true;
                }
            }
        }catch(Exception e){
            error = true;
        }
        termina_conexion(socket);
            
        return error;
    }


    public static boolean peer_en_lista(String IP, int puerto, String nombre){
        boolean esta_en_lista = false;
        String peer = IP + " " + puerto + " " + nombre;
        synchronized(otros_peers){
            for(int i=0; i<otros_peers.size(); i++){
                if(otros_peers.get(i).equalsIgnoreCase(peer)){
                    esta_en_lista = true;
                    break;
                }
            }
        }
        return esta_en_lista;
    }
    

    
    public static void termina_conexion(Socket socket){
        // Cierro el socket
        try{
            socket.close();
        }catch(Exception e){
            //System.out.println("Error en la función termina_conexion");
        }
    }

}
