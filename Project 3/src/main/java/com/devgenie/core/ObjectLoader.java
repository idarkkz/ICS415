package com.devgenie.core;

import com.devgenie.core.entity.Model;
import com.devgenie.core.entity.Texture;
import com.devgenie.core.utils.Utils;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;

public class ObjectLoader {
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadOBJModel(String filename) {
        List<String> lines = Utils.readAllLines(filename);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> textures = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for (String line : lines) {
            String[] tokens = line.split("\\s+");
            switch (tokens[0]) {
                case "v":
                    //vertices
                    Vector3f verticesVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVec);
                    break;
                case "vt":
                    //vertex textures
                    Vector2f texturesVec = new Vector2f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2])
                    );
                    textures.add(texturesVec);
                    break;
                case "vn":
                    //vertex normals
                    Vector3f normalsVec = new Vector3f(
                            Float.parseFloat(tokens[1]),
                            Float.parseFloat(tokens[2]),
                            Float.parseFloat(tokens[3])
                    );
                    normals.add(normalsVec);
                    break;
                case "f":
                    // Fan triangulate faces with >3 vertices
                    for (int j = 2; j < tokens.length - 1; j++) {
                        processFace(tokens[1], faces);       // v0
                        processFace(tokens[j], faces);       // v(j)
                        processFace(tokens[j + 1], faces);   // v(j+1)
                    }
                    break;
                default:
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArr = new float[vertices.size() * 3];
        int i = 0;
        for (Vector3f pos : vertices) {
            verticesArr[i * 3] = pos.x;
            verticesArr[i * 3 + 1] = pos.y;
            verticesArr[i * 3 + 2] = pos.z;
            i++;
        }
        float[] texCoordArr = new float[vertices.size() * 2];
        float[] normalArr = new float[vertices.size() * 3];

        for (Vector3i face : faces) {
            processVertex(face.x, face.y, face.z, textures, normals, indices, texCoordArr, normalArr);
        }

        int[] indicesArr = indices.stream().mapToInt((Integer V) -> V).toArray();
        return loadModel(verticesArr, texCoordArr, normalArr, indicesArr);
    }

    private static void processVertex(int pos, int texCoord, int normal, List<Vector2f> texCoordsList,
                                      List<Vector3f> normalList, List<Integer> indicesList, float[] texCoordArr, float[] normalArr) {
        indicesList.add(pos);
        if (texCoord >= 0) {
            Vector2f texCoordVec = texCoordsList.get(texCoord);
            texCoordArr[pos * 2] = texCoordVec.x;
            texCoordArr[pos * 2 + 1] = 1 - texCoordVec.y;
        }
        if (normal >= 0) {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;
        }
    }

    private static void processFace(String token, List<Vector3i> faces) {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) - 1;
        if (length == 1) {
            faces.add(new Vector3i(pos, -1, -1)); // ✅ Only position (v) provided
            return;
        }
        String textCoord = lineToken[1];
        coords = textCoord.length() > 0 ? Integer.parseInt(textCoord) - 1 : -1;
        if (length > 2) {
            normal = Integer.parseInt(lineToken[2]) - 1;
        }
        Vector3i facesVec = new Vector3i(pos, coords, normal);
        faces.add(facesVec);
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices, Texture texture) {
        int id = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAtrribList(0, 3, vertices);
        storeDataInAtrribList(1, 2, textureCoords);
        storeDataInAtrribList(2, 3, normals);
        unbind();
        return new Model(id, indices.length, texture);
    }

    public Model loadModel(float[] vertices, float[] textureCoords, float[] normals, int[] indices) {
        return loadModel(vertices, textureCoords, normals, indices, null);
    }

    public int loadTexture(String filename) {
        int width, height;
        ByteBuffer buffer;
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = stbi_load(filename, w, h, c, 4);
            if (buffer == null)
                System.err.println("Image file [" + filename + "] could not be loaded " + stbi_failure_reason());
            width = w.get();
            height = h.get();

        }
        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL30.glTexParameteri(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL11.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
    }
    public Model createCubeWithAtlas(Texture texture) {
        float[] vertices = {
                // Front face
                -0.5f, -0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,

                // Back face
                -0.5f, -0.5f, -0.5f,
                -0.5f,  0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,

                // Left face
                -0.5f, -0.5f, -0.5f,
                -0.5f, -0.5f,  0.5f,
                -0.5f,  0.5f,  0.5f,
                -0.5f,  0.5f, -0.5f,

                // Right face
                0.5f, -0.5f, -0.5f,
                0.5f,  0.5f, -0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f, -0.5f,  0.5f,

                // Top face
                -0.5f,  0.5f, -0.5f,
                -0.5f,  0.5f,  0.5f,
                0.5f,  0.5f,  0.5f,
                0.5f,  0.5f, -0.5f,

                // Bottom face
                -0.5f, -0.5f, -0.5f,
                0.5f, -0.5f, -0.5f,
                0.5f, -0.5f,  0.5f,
                -0.5f, -0.5f,  0.5f
        };

        // Assume your texture atlas layout:
        // Top texture (grass) = (0, 0) to (0.5, 0.5)
        // Side texture (grass+dirt) = (0.5, 0) to (1, 0.5)
        // Bottom texture (dirt) = (0, 0.5) to (0.5, 1)

        float[] texCoords = {
                // Front face (side)
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.0f, 0.0f,
                0.5f, 0.0f,


                // Back face (side)
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,


                // Left face (side)
                0.5f, 0.5f,
                0.0f, 0.5f,
                0.0f, 0.0f,
                0.5f, 0.0f,


                // Right face (side)
                0.5f, 0.5f,
                0.5f, 0.0f,
                0.0f, 0.0f,
                0.0f, 0.5f,

                // Top face (grass top)
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 1.0f,
                0.0f, 1.0f,

                // Bottom face (dirt)
                0.5f, 0.0f,
                1.0f, 0.0f,
                1.0f, 0.5f,
                0.5f, 0.5f
        };


        float[] normals = {
                // Front
                0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1,
                // Back
                0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1,
                // Left
                -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0, 0,
                // Right
                1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0, 0,
                // Top
                0, 1, 0, 0, 1, 0, 0, 1, 0, 0, 1, 0,
                // Bottom
                0, -1, 0, 0, -1, 0, 0, -1, 0, 0, -1, 0
        };

        int[] indices = {
                0, 1, 2, 2, 3, 0,        // Front
                4, 5, 6, 6, 7, 4,        // Back
                8, 9,10,10,11, 8,        // Left
                12,13,14,14,15,12,        // Right
                16,17,18,18,19,16,        // Top
                20,21,22,22,23,20         // Bottom
        };

        Model model = loadModel(vertices, texCoords, normals, indices, texture);
        return model;
    }

    private int createVAO() {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;
    }

    private void bindIndicesBuffer(int[] indices) {
        int vbo = GL30.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAtrribList(int attribNo, int vertexCount, float[] data) {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(attribNo);
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);


    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        for (int vao : vaos)
            GL30.glDeleteVertexArrays(vao);
        for (int vbo : vbos)
            GL30.glDeleteBuffers(vbo);
        for (int texture : textures)
            GL11.glDeleteTextures(texture);
    }

}

