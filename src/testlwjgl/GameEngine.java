package testlwjgl;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import java.util.ArrayList;


public class GameEngine {

    public static int DISPLAY_HEIGHT = 480;
    public static int DISPLAY_WIDTH = 640;
    public static final Logger LOGGER = Logger.getLogger(GameEngine.class.getName());

    
    static {
        try {
            LOGGER.addHandler(new FileHandler("errors.log", true));
        } catch (IOException ex) {
            LOGGER.log(Level.WARNING, ex.toString(), ex);
        }
    }

    // variables importantes para la ingenieria del juego
    protected static final ArrayList<GameObject> objects = new ArrayList<GameObject>();
    private Camera camera;
    boolean ready = false;
    public boolean[] keys = new boolean[256];
    float[] LightAmbient = {1.0F, 1.0F, 1.0F, 1.0F};
    float[] LightDiffuse = {0.75F, 0.75F, 0.75F, 1.0F};
    float[] LightSpecular = {1.0F, 1.0F, 1.0F, 1.0F};
    float[] LightPosition = {-800.0F, 80.0F, 500.0F, 1.0F};


    public GameEngine() {
    }

    public void create() throws LWJGLException {
        Model.initModelManager();

        //Display
        Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
        Display.setFullscreen(false);
        Display.setTitle("A.M.I.D.E!");
        Display.create();

        //Keyboard
        Keyboard.create();

        //Mouse
        Mouse.setGrabbed(false);
        Mouse.create();

        //OpenGL
        initGL();
        resizeGL();
        createCamera();

    }

    public Camera createCamera() {
        if (camera == null) {
            camera = new Camera(0, 0, 1.0f, Main.mi_nombre_usuario);
            this.addObject(camera);
        }
        return camera;
    }

    public void destroy() {
        //Methods already check if created before destroying.
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }

    public void initGL() {
        glEnable(GL_TEXTURE_2D);						// Enable Texture Mapping
        glShadeModel(GL_SMOOTH);						// Enable Smooth Shading
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);					// Black Background
        glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);			// Really Nice Perspective Calculations

        // Create a light (diffuse light, ambient light, position)
        setLight( GL_LIGHT1,
        		LightDiffuse,
        		LightAmbient,
        		LightSpecular,
        		LightPosition );

        glEnable(GL_LIGHTING);

        glColorMaterial(GL_FRONT, GL_AMBIENT_AND_DIFFUSE);
        glEnable(GL_COLOR_MATERIAL);
    }

    public void processKeyboard() {
        final float distance = 0.2f;

        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            camera.moveBackwards(distance);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            camera.moveForwards(distance);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            camera.strafeLeft(distance);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            camera.strafeRight(distance);
        }
        // Para ir al hiperespacio
        if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
            // Se coloca a la nave en una posicion aleatoria en el display
            hiperespacio();
        }
        // Para salir (Esc)
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)){
            Main.salir = true;
        }
    }

    
    public void hiperespacio(){
        // Se calculan las nuevas posiciones para la nave
        ArrayList<Float> x = Random(1, 70);
        ArrayList<Float> y = Random(1, 70);
        ArrayList<Float> z = Random(1, 100);
        // Se pone en z negativo para verla siempre
        if (z.get(0) > 0) {
            float nuevo_valor = z.get(0)* -1;
            z.set(0, nuevo_valor);
        }
        // Se corrigen las posiciones para el objeto nave
        Class clase;
        String modelo_usado;
        ModelledObject model_object;
        synchronized(objects){
            for (GameObject mi_objeto : objects) {
                clase = mi_objeto.getClass();
                if (clase.getName().equalsIgnoreCase("testlwjgl.ModelledObject")){
                    model_object = (ModelledObject) mi_objeto;
                    modelo_usado = model_object.getModel().get_fileName();
                    if (modelo_usado.equalsIgnoreCase("Ship.glo")){
                        mi_objeto.setPosition(x.get(0), y.get(0), -100);
                        break;
                    }
                }
            }
        }
    }
    
    public void processMouse() {
        //Mouse.
    }

    public void renderScene() {
        glClear(GL_COLOR_BUFFER_BIT);
        glLoadIdentity();
        synchronized(objects){
            for (GameObject obj : objects) {
                obj.render();
            }
        }
    }

    public void resizeGL() {
        reshape(DISPLAY_WIDTH, DISPLAY_HEIGHT);
    }


    public void run() {
        last_tm = System.currentTimeMillis();

        // Para salir del juego
        Main.salir = false;

        // Lanzo un objeto de la clase GameMovements (mía) que tratará los movimientos predeterminados
        GameMovements hilo_movimientos = new GameMovements();
        Thread hiloMovimientos = new Thread(hilo_movimientos);
        hiloMovimientos.setName("Hilo para los movimientos, usuario: " + Main.mi_nombre_usuario);
        hiloMovimientos.start();

        //while (!Display.isCloseRequested() && !Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
        while (! Main.salir) {
            if (Display.isVisible()) {
                processKeyboard();
                processMouse();
                update();
                renderScene();
            } else {
                if (Display.isDirty()) {
                    renderScene();
                }
                sleep(100);
            }
            Display.update();
            Display.sync(60);

            if (Display.isCloseRequested()){
                //System.out.println("han pulsado x en display");
                Main.salir = true;
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

    
    protected long last_tm;
    public void update() {
        // Se cambia el display
        /*try{
            Display.destroy();
            Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
            Display.setFullscreen(false);
            Display.setTitle("A.M.I.D.E!");
            Display.create();
        }catch(Exception ex){
            //
        }*/
        
        long tm = last_tm - System.currentTimeMillis();
        updateGameObjects(tm);
        last_tm = tm;
    }

    public static void actualiza_display(){
        // Se cambia el ancho y alto del mundo
        DISPLAY_HEIGHT = DISPLAY_HEIGHT * 2; //+ (int)(GameEngine.DISPLAY_HEIGHT*0.5);
        DISPLAY_WIDTH = DISPLAY_WIDTH * 2; //+ (int)(GameEngine.DISPLAY_WIDTH*0.5);
        // Se cambia el display
            try{
                Display.destroy();
                Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
                Display.setFullscreen(false);
                Display.setTitle("A.M.I.D.E!");
                Display.create();
            }catch(Exception ex){
                //
            }
    }
    public void reshape(int width, int height) {
        if (height == 0) {
            height = 1;
        }
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();

        glViewport(0, 0, width, height);
        gluPerspective(45.0f, (float) width / (float) height, 0.001f, 10000.0f);

        glPushMatrix();
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
    }

    public Model addModel(String fName) {
        return Model.addModel(fName);
    }

    public void addObject(GameObject obj) {
        synchronized(objects){
            objects.add(obj);
        }

    }

    public void updateGameObjects(long tm) {
        synchronized(objects){
            for (GameObject obj : objects) {
                obj.step(tm);
            }
        }

    }

    public void killGameObject(GameObject aDead) {
        // Not Implemented
    }

    public void killGameObjects() {
        synchronized(objects){
            for (GameObject obj : objects) {
                killGameObject(obj);
            }
        }
    }

    public boolean collisionBallBall(GameObject a1, float r1, GameObject a2, float r2) {
        float x = a1.x - a2.x;
        float y = a1.y - a2.y;
        float z = a1.z - a2.z;

        float d = (float) Math.sqrt(x * x + y * y + z * z);

        return d < r1 + r2;
    }

    public void loadModels() {
        Model.loadModels();
    }

    /**
     * Set the color of a 'positional' light (a light that has a specific
     * position within the scene).  <BR>
     *
     * Pass in an OpenGL light number (GL11.GL_LIGHT1),
     * the 'Diffuse' and 'Ambient' colors (direct light and reflected light),
     * and the position.<BR>
     *
     * @param GLLightHandle
     * @param diffuseLightColor
     * @param ambientLightColor
     * @param position
     */
    public static void setLight( int GLLightHandle,
            float[] diffuseLightColor,
            float[] ambientLightColor,
            float[] specularLightColor,
            float[] position)  {
        FloatBuffer ltDiffuse = allocFloats(diffuseLightColor);
        FloatBuffer ltAmbient = allocFloats(ambientLightColor);
        FloatBuffer ltSpecular = allocFloats(specularLightColor);
        FloatBuffer ltPosition = allocFloats(position);
        glLight(GLLightHandle, GL_DIFFUSE, ltDiffuse);   // color of the direct illumination
        glLight(GLLightHandle, GL_SPECULAR, ltSpecular); // color of the highlight
        glLight(GLLightHandle, GL_AMBIENT, ltAmbient);   // color of the reflected light
        glLight(GLLightHandle, GL_POSITION, ltPosition);
        glEnable(GLLightHandle);	// Enable the light (GL_LIGHT1 - 7)
        //GL11.glLightf(GLLightHandle, GL11.GL_QUADRATIC_ATTENUATION, .005F);    // how light beam drops off
    }

    static final int SIZE_FLOAT = 4;

    public static FloatBuffer allocFloats(float[] floatarray) {
    	FloatBuffer fb = ByteBuffer.allocateDirect(floatarray.length * SIZE_FLOAT).order(ByteOrder.nativeOrder()).asFloatBuffer();
    	fb.put(floatarray).flip();
    	return fb;
    }

    public ArrayList<Float> Random(int cantidad, int limite){
        ArrayList<Float> posiciones = new ArrayList<Float>();
        Random randomGenerator = new Random();
        for(int i=0; i<cantidad; i++){
            float posicion = ((randomGenerator.nextFloat())*limite) - (limite/2);
            //System.out.println("La posicion es: " + posicion);
            posiciones.add(posicion);
        }
        return posiciones;
    }

}
