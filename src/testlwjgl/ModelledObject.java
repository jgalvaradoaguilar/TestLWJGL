/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testlwjgl;

import static org.lwjgl.opengl.GL11.*;

/**
 *
 * @author Agustin
 */
public class ModelledObject extends GameObject {

    protected Model model;

    /**
     * Construct a Modelled Object at the given location. It will be unmoving,
     * with a radius of 0.
     * The up vector will be (0.0f, 1.0f, 0.0f)
     * @param x x coordinate of center of object
     * @param y y coordinate of center of object
     * @param z z coordinate of center of object
     * @param model the model of object
     */
    public ModelledObject(float x, float y, float z, String usuario, Model model) {
        super(x, y, z, usuario);
        this.model = model;
    }

    /**
     * Return the Model of this object.
     * @return the modelof this object
     */
    public Model getModel() {
        return model;
    }
    

    /**
     * Set the model of this object
     * @param model New model
     */
    public void setModel(Model model) {
        this.model = model;
    }

    public void render() {
        glPushMatrix();
        //glLoadIdentity();
        glTranslatef(this.x, this.y, this.z);

        //glRotatef(this.xr, 1.0F, 0.0F, 0.0F);
        //glRotatef(this.yr, 0.0F, 1.0F, 0.0F);
        //glRotatef(this.zr, 0.0F, 0.0F, 1.0F);

        this.model.render();
        glPopMatrix();
    }
}
