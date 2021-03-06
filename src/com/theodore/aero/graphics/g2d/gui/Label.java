package com.theodore.aero.graphics.g2d.gui;

import com.theodore.aero.core.Aero;
import com.theodore.aero.core.Input;
import com.theodore.aero.core.Transform;
import com.theodore.aero.graphics.Graphics;
import com.theodore.aero.graphics.Texture;
import com.theodore.aero.graphics.mesh.Mesh;
import com.theodore.aero.graphics.mesh.meshLoading.IndexedModel;
import com.theodore.aero.math.MathUtils;
import com.theodore.aero.math.Vector2;
import com.theodore.aero.math.Vector3;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class Label extends Widget {

    private Transform[] transform;
    private Mesh[] meshes;

    private String text;
    private float xOffset;

    public Label(String text, float x, float y, float width, float height, float xOffset) {
        this(text, new Texture("fontsheet"), x, y, width, height, xOffset);
    }

    public Label(String text, Texture texture, float x, float y, float width, float height, float xOffset) {
        super(x, y, width * 2, height * 2);
        material.setTexture("diffuse", texture);

        this.text = text;

        this.xOffset = xOffset;

        meshes = new Mesh[256];
        transform = new Transform[256];
        for (int i = 0; i < meshes.length; i++) {
            Vector2[] uv = calculateUVMapping(i, 16, 16);

            IndexedModel model = new IndexedModel();

            model.addVertex(new Vector3(-0.5f, -0.5f, 0));
            model.addTexCoord(uv[1]);
            model.addVertex(new Vector3(-0.5f, 0.5f, 0));
            model.addTexCoord(uv[0]);
            model.addVertex(new Vector3(0.5f, 0.5f, 0));
            model.addTexCoord(uv[2]);
            model.addVertex(new Vector3(0.5f, -0.5f, 0));
            model.addTexCoord(uv[3]);
            model.addFace(0, 1, 2);
            model.addFace(0, 2, 3);

            meshes[i] = new Mesh("text" + i, model.finish());
//            meshes[i] = Mesh.customMesh("text", vertices, indices, false, false);

            transform[i] = new Transform();
            transform[i].setPosition(new Vector3(x, y + height / 2, 0));
            transform[i].setScale(new Vector3(width, height, height));
        }
    }

    @Override
    public void draw(Graphics graphics) {
        if (visible) {
            if (material != null) {
                orthoShader.bind();

                char[] characters = text.toCharArray();
                for (int i = 0; i < characters.length; i++) {
                    int ascii = text.charAt(i);

                    transform[ascii].setScale(new Vector3(width / 2, height / 2, height / 2));
                    Vector3 pos = new Vector3(parent.getX() + (((x + (i * xOffset) + width) - (width / 2)) - (xOffset * 2)), parent.getY() + (((this.y + height) - (height / 2)) - (height / 4)), 0);
                    transform[ascii].setPosition(pos);

                    orthoShader.updateUniforms(transform[ascii], material, Aero.graphics);
                    meshes[ascii].draw(GL11.GL_TRIANGLES);
                }
            }

            for (Widget child : children) child.draw(graphics);

        }
    }


    public Vector2[] calculateUVMapping(int index, int atlasWidth, int atlasHeight) {
        int u = index % atlasWidth;
        int v = index / atlasHeight;

        float xOffset = 1f / atlasWidth;
        float yOffset = 1f / atlasHeight;

        float uOffset = (u * xOffset);
        float vOffset = (v * yOffset);

        Vector2[] UVList = new Vector2[4];

        UVList[0] = new Vector2(uOffset, vOffset); // 0,0
        UVList[1] = new Vector2(uOffset, vOffset + yOffset); // 0,1
        UVList[2] = new Vector2(uOffset + xOffset, vOffset); // 1,0
        UVList[3] = new Vector2(uOffset + xOffset, vOffset + yOffset); // 1,1

        return UVList;
    }

    @Override
    public boolean isInside(float x, float y) {
        float dx = parent.getX() + (this.x - (xOffset * 2));
        float dy = parent.getY() + this.y;

        float totalWidth = getWidth(text, 0, text.toCharArray().length);

        return x > dx && x < dx + totalWidth &&
                y > dy && y < dy + (this.height / 2);
    }

    public float getWidth(CharSequence str, int start, int end) {
        int width = 0;
        while (start < end) {
            char ch = str.charAt(start++);
            if (ch == '[') {
                if (!(start < end && str.charAt(start) == '[')) { // non escaped '['
                    while (start < end && str.charAt(start) != ']')
                        start++;
                    start++;
                    continue;
                }
                start++;
            }
            width += xOffset;
        }
        while (start < end) {
            char ch = str.charAt(start++);
            if (ch == '[') {
                if (!(start < end && str.charAt(start) == '[')) { // non escaped '['
                    while (start < end && str.charAt(start) != ']')
                        start++;
                    start++;
                    continue;
                }
                start++;
            }
            width += xOffset;
        }

        return width;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public float getxOffset() {
        return xOffset;
    }

    public void setxOffset(float xOffset) {
        this.xOffset = xOffset;
    }
}
