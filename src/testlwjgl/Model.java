package testlwjgl;

import static org.lwjgl.opengl.GL11.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Model {

    private String fileName = null;
    private int displayList;
    private Texture[] textures;
    private float minRadius = 3.4028235E+38f;
    private float maxRadius = 0.0f;
    private float r;
    private float g;
    private float b;
    private float sx;
    private float sy;
    private float sz;
    private static ArrayList<Model> models = new ArrayList<Model>();
    private static TextureLoader textureLoader;
    private static float[] uSet = new float[6];
    private static int modelCount = 0;

    public Model(String fName) {
        this.fileName = fName;
    }

    public Model(int genList) {
        this.displayList = genList;
    }

    public Model() {
    }

    public void render() {
        glCallList(this.displayList);
    }

    public String get_fileName(){
        return fileName;
    }

    ////////////////////////////////////////////////////////////////
    // PENDING. Implement a Model Manager
    ////////////////////////////////////////////////////////////////
    public static void initModelManager() {
        textureLoader = new TextureLoader();
    }

    public static Model addModel(String fName) {
        Model model;
        models.add(model = new Model(fName));
        model.loadModel();

        return model;
    }

    public static void loadModels() {
        for (Model model : models) {
            model.loadModel();
        }
    }

    public void loadModel() {
        BufferedReader fHandle = null;

        try {
            InputStream stream = GameEngine.class.getResourceAsStream(this.fileName);
            fHandle = new BufferedReader(new InputStreamReader(stream));

            // aquí hay problemas
            //System.out.println("glGenLists = " + glGenLists(1));
            this.displayList = glGenLists(1);
            readModel(this, fHandle);

            modelCount += 1;
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                fHandle.close();
            } catch (IOException e) {
            }
        }
    }

    private void readModel(Model model, BufferedReader mFile) {
        boolean exit = false;
        String data = null;
        String mode = null;

        while (!exit) {
            if ((data = readLine(mFile)) == null) {
                data = "EXIT";
            }
            if (data.startsWith("#")) {
                continue;
            }
            if ((data.charAt(0) >= 'A') && (data.charAt(0) <= 'Z')) {
                mode = data.trim();

                if (data.equals("BEGIN_QUADS")) {
                    glDisable(GL_TEXTURE_2D);
                    glBegin(GL_QUADS);
                    mode = "QUADS";
                } else if (data.equals("BEGIN_EZTEXQUADS")) {
                    glEnable(GL_TEXTURE_2D);
                    glBegin(GL_QUADS);
                    mode = "EZTEXQUADS";
                } else if (data.equals("BEGIN_TRIANGLES")) {
                    glDisable(GL_TEXTURE_2D);
                    glBegin(GL_TRIANGLES);
                    mode = "TRIANGLES";
                } else if (data.equals("BEGIN_EZTEXTRIANGLES")) {
                    glEnable(GL_TEXTURE_2D);
                    glBegin(GL_TRIANGLES);
                    mode = "EZTEXTRIANGLES";
                } else if (data.equals("BEGIN_POINTS")) {
                    glDisable(GL_TEXTURE_2D);
                    glBegin(GL_POINTS);
                    mode = "POINTS";
                } else if (data.equals("BEGIN_LINES")) {
                    glDisable(GL_TEXTURE_2D);
                    glBegin(GL_LINES);
                    mode = "POINTS";
                }

                if (mode.equals("END")) {
                    glEnd();
                } else if (mode.equals("USERCS")) {
                    glScalef(uSet[3], uSet[4], uSet[5]);
                    glColor3f(uSet[0], uSet[1], uSet[2]);
                } else if (mode.equals("SCALE")) {
                    data = readLine(mFile);
                    glScalef(getF(data, 1), getF(data, 2), getF(data, 3));
                } else if (mode.equals("COLOR3F")) {
                    data = readLine(mFile);
                    glColor3f(getF(data, 1), getF(data, 2), getF(data, 3));
                } else if (mode.equals("GENTEXTURES")) {
                    loadTextures(mFile, model);
                } else if (mode.equals("BINDTEXTURE")) {
                    model.textures[((int) getF(readLine(mFile), 1) - 1)].bind();
                    //glBindTexture(GL_TEXTURE_2D, model.texture[((int) getF(readLine(mFile), 1) - 1)]);
                } else if (mode.equals("BLENDON")) {
                    glEnable(GL_BLEND);
                    glDisable(GL_DEPTH_TEST);
                } else if (mode.equals("BLENDOFF")) {
                    glEnable(GL_DEPTH_TEST);
                    glDisable(GL_BLEND);
                } else if (mode.equals("COMPILE")) {
                    glNewList(model.displayList, GL_COMPILE);
                } else if (mode.equals("ENDLIST")) {
                    glEndList();
                    exit = true;
                } else if (mode.equals("EXIT")) {
                    exit = true;
                }
            }

            if (((data.charAt(0) >= '0') && (data.charAt(0) <= '9')) || (data.charAt(0) == '-')) {
                if (mode.equals("TRIANGLES")) {
                    addTriangles(model, data);
                } else if (mode.equals("EZTEXTRIANGLES")) {
                    addEZTexTriangles(model, data);
                } else if (mode.equals("QUADS")) {
                    addQuads(model, data);
                } else if (mode.equals("EZTEXQUADS")) {
                    addEZTexQuads(model, data);
                } else if (mode.equals("POINTS")) {
                    addPoints(model, data);
                }
            }
        }
    }

    private static String readLine(BufferedReader file) {
        String data = null;
        try {
            data = file.readLine();
        } catch (IOException e) {
            System.out.println("ReadLine " + e);
        }
        return data;
    }

    public static void loadTextures(BufferedReader mFile, Model model) {
        int texCnt = 0;

        texCnt = (int) getF(readLine(mFile), 1);
        model.textures = new Texture[texCnt];
        
        for (int t = 0; t < texCnt; t++) {
            try {
                model.textures[t] = textureLoader.getTexture(readLine(mFile));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void addTriangles(Model model, String data) {
        float[] x = new float[4];
        float[] y = new float[4];
        float[] z = new float[4];

        for (int t = 1; t < 4; t++) {
            x[t] = getF(data, 3 * (t - 1) + 1);
            y[t] = getF(data, 3 * (t - 1) + 2);
            z[t] = getF(data, 3 * (t - 1) + 3);
        }

        x[0] = (y[1] * (z[2] - z[3]) + y[2] * (z[3] - z[1]) + y[3] * (z[1] - z[2]));
        y[0] = (z[1] * (x[2] - x[3]) + z[2] * (x[3] - x[1]) + z[3] * (x[1] - x[2]));
        z[0] = (x[1] * (y[2] - y[3]) + x[2] * (y[3] - y[1]) + x[3] * (y[1] - y[2]));

        float n = (float) Math.sqrt(x[0] * x[0] + y[0] * y[0] + z[0] * z[0]);
        x[0] /= n;
        y[0] /= n;
        z[0] /= n;

        glNormal3f(x[0], y[0], z[0]);

        glVertex3f(x[1], y[1], z[1]);
        glVertex3f(x[2], y[2], z[2]);
        glVertex3f(x[3], y[3], z[3]);

        n = (float) Math.sqrt(x[1] * x[1] + y[1] * y[1] + z[1] * z[1]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[2] * x[2] + y[2] * y[2] + z[2] * z[2]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[3] * x[3] + y[3] * y[3] + z[3] * z[3]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
    }

    private static void addPoints(Model model, String data) {
        float[] x = new float[4];
        float[] y = new float[4];
        float[] z = new float[4];

        for (int t = 1; t < 4; t++) {
            x[t] = getF(data, 3 * (t - 1) + 1);
            y[t] = getF(data, 3 * (t - 1) + 2);
            z[t] = getF(data, 3 * (t - 1) + 3);
        }

        x[0] = (y[1] * (z[2] - z[3]) + y[2] * (z[3] - z[1]) + y[3] * (z[1] - z[2]));
        y[0] = (z[1] * (x[2] - x[3]) + z[2] * (x[3] - x[1]) + z[3] * (x[1] - x[2]));
        z[0] = (x[1] * (y[2] - y[3]) + x[2] * (y[3] - y[1]) + x[3] * (y[1] - y[2]));

        float n = (float) Math.sqrt(x[0] * x[0] + y[0] * y[0] + z[0] * z[0]);
        x[0] /= n;
        y[0] /= n;
        z[0] /= n;

        glNormal3f(x[0], y[0], z[0]);

        glVertex3f(x[1], y[1], z[1]);
        glVertex3f(x[2], y[2], z[2]);
        glVertex3f(x[3], y[3], z[3]);

        n = (float) Math.sqrt(x[1] * x[1] + y[1] * y[1] + z[1] * z[1]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[2] * x[2] + y[2] * y[2] + z[2] * z[2]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[3] * x[3] + y[3] * y[3] + z[3] * z[3]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
    }

    private static void addEZTexTriangles(Model model, String data) {
        float[] x = new float[4];
        float[] y = new float[4];
        float[] z = new float[4];

        for (int t = 1; t < 4; t++) {
            x[t] = getF(data, 3 * (t - 1) + 1);
            y[t] = getF(data, 3 * (t - 1) + 2);
            z[t] = getF(data, 3 * (t - 1) + 3);
        }

        x[0] = (y[1] * (z[2] - z[3]) + y[2] * (z[3] - z[1]) + y[3] * (z[1] - z[2]));
        y[0] = (z[1] * (x[2] - x[3]) + z[2] * (x[3] - x[1]) + z[3] * (x[1] - x[2]));
        z[0] = (x[1] * (y[2] - y[3]) + x[2] * (y[3] - y[1]) + x[3] * (y[1] - y[2]));

        float n = (float) Math.sqrt(x[0] * x[0] + y[0] * y[0] + z[0] * z[0]);
        x[0] /= n;
        y[0] /= n;
        z[0] /= n;

        glNormal3f(x[0], y[0], z[0]);
        glTexCoord2f(0.0F, 0.0F);
        glVertex3f(x[1], y[1], z[1]);
        glTexCoord2f(1.0F, 0.0F);
        glVertex3f(x[2], y[2], z[2]);
        glTexCoord2f(0.0F, 1.0F);
        glVertex3f(x[3], y[3], z[3]);

        n = (float) Math.sqrt(x[1] * x[1] + y[1] * y[1] + z[1] * z[1]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[2] * x[2] + y[2] * y[2] + z[2] * z[2]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[3] * x[3] + y[3] * y[3] + z[3] * z[3]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
    }

    private static void addQuads(Model model, String data) {
        float[] x = new float[5];
        float[] y = new float[5];
        float[] z = new float[5];

        for (int t = 1; t < 5; t++) {
            x[t] = getF(data, 3 * (t - 1) + 1);
            y[t] = getF(data, 3 * (t - 1) + 2);
            z[t] = getF(data, 3 * (t - 1) + 3);

            x[0] = (y[1] * (z[2] - z[3]) + y[2] * (z[3] - z[1]) + y[3] * (z[1] - z[2]));
            y[0] = (z[1] * (x[2] - x[3]) + z[2] * (x[3] - x[1]) + z[3] * (x[1] - x[2]));
            z[0] = (x[1] * (y[2] - y[3]) + x[2] * (y[3] - y[1]) + x[3] * (y[1] - y[2]));
        }

        glNormal3f(x[0], y[0], z[0]);

        glVertex3f(x[1], y[1], z[1]);
        glVertex3f(x[2], y[2], z[2]);
        glVertex3f(x[3], y[3], z[3]);
        glVertex3f(x[4], y[4], z[4]);

        float n = (float) Math.sqrt(x[1] * x[1] + y[1] * y[1] + z[1] * z[1]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[2] * x[2] + y[2] * y[2] + z[2] * z[2]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[3] * x[3] + y[3] * y[3] + z[3] * z[3]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
    }

    private static void addEZTexQuads(Model model, String data) {
        float[] x = new float[5];
        float[] y = new float[5];
        float[] z = new float[5];

        for (int t = 1; t < 5; t++) {
            x[t] = getF(data, 3 * (t - 1) + 1);
            y[t] = getF(data, 3 * (t - 1) + 2);
            z[t] = getF(data, 3 * (t - 1) + 3);

            x[0] = (y[1] * (z[2] - z[3]) + y[2] * (z[3] - z[1]) + y[3] * (z[1] - z[2]));
            y[0] = (z[1] * (x[2] - x[3]) + z[2] * (x[3] - x[1]) + z[3] * (x[1] - x[2]));
            z[0] = (x[1] * (y[2] - y[3]) + x[2] * (y[3] - y[1]) + x[3] * (y[1] - y[2]));
        }

        glNormal3f(x[0], y[0], z[0]);

        glTexCoord2f(0.0F, 0.0F);
        glVertex3f(x[1], y[1], z[1]);
        glTexCoord2f(1.0F, 0.0F);
        glVertex3f(x[2], y[2], z[2]);
        glTexCoord2f(1.0F, 1.0F);
        glVertex3f(x[3], y[3], z[3]);
        glTexCoord2f(0.0F, 1.0F);
        glVertex3f(x[4], y[4], z[4]);

        float n = (float) Math.sqrt(x[1] * x[1] + y[1] * y[1] + z[1] * z[1]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[2] * x[2] + y[2] * y[2] + z[2] * z[2]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
        n = (float) Math.sqrt(x[3] * x[3] + y[3] * y[3] + z[3] * z[3]);
        if (n < model.minRadius) {
            model.minRadius = n;
        }
        if (n > model.maxRadius) {
            model.maxRadius = n;
        }
    }

    private static float getF(String text, int numb) {
        String[] strs = text.split("\\s+");
        return Float.parseFloat(strs[numb - 1]);
    }
}
