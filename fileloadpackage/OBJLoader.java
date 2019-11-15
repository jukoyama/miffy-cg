import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.lang.Math;

// public class MainObj {
//   public static void main(String[] args) {
//     OBJLoader calc = new OBJLoader();
//     OBJLoader value = calc.ObjFileLoader("pudding", (float)0.01);
//     System.out.println(Arrays.deepToString(value.vertices_array));
//   }
// }

class OBJLoader {
  float[][] vertices_array = null;
  float[][] textures_array = null;
  float[][] normals_array = null;
  float[][] faces_array = null;
  int[][] indices_array = null;

  public static OBJLoader ObjFileLoader(String fileName, float size) {
    OBJLoader calc = new OBJLoader();
    FileReader fr = ReadObjFile(fileName);
    BufferedReader br = new BufferedReader(fr);
    String line;
    List<ArrayList<Float>> vertices = new ArrayList<>();
    List<ArrayList<Float>> textures = new ArrayList<>();
    List<ArrayList<Float>> normals = new ArrayList<>();
    List<ArrayList<Integer>> indices = new ArrayList<>();
    List<ArrayList<ArrayList<Float>>> faces = new ArrayList<>();
    float[][] verticesArray = null;
    float[][] texturesArray = null;
    float[][] normalsArray = null;
    float[][] facesArray = null;
    int[][] indicesArray = null;

    try{
      while(true) {
        line = br.readLine();
        String[] currentLine = line.split(" ", 0);
        if(line.startsWith("v ")) { // 頂点の座標をArrayListに代入
          ArrayList<Float> vertex = new ArrayList<Float>();
          vertex.add((float)(Float.parseFloat(currentLine[1])*size));
          vertex.add((float)(Float.parseFloat(currentLine[2])*size));
          vertex.add((float)(Float.parseFloat(currentLine[3])*size));
          vertices.add(vertex);
        }
        else if(line.startsWith("vt ")) { // テクスチャの頂点の座標をArrayListに代入
          ArrayList<Float> texture = new ArrayList<Float>();
          texture.add((float)(Float.parseFloat(currentLine[1])*size));
          texture.add((float)(Float.parseFloat(currentLine[2])*size));
          textures.add(texture);
        }
        else if(line.startsWith("vn ")) { // 頂点法線ベクトルをArrayListに代入
          ArrayList<Float> normal = new ArrayList<Float>();
          normal.add((float)(Float.parseFloat(currentLine[1])*size));
          normal.add((float)(Float.parseFloat(currentLine[2])*size));
          normal.add((float)(Float.parseFloat(currentLine[3])*size));
          normals.add(normal);
        }
        else if(line.startsWith("f ")) { // 面のそれぞれの位置を取得してループを抜ける.
          texturesArray = new float[vertices.size()][2];
          normalsArray = new float[vertices.size()][3];
          break;
        }
      }

      while(line != null) {
        if(!line.startsWith("f ")) {
          line = br.readLine();
          continue;
        }
        ArrayList<Integer> indice = new ArrayList<Integer>();
        ArrayList<ArrayList<Float>> face = new ArrayList<ArrayList<Float>>();
        String[] currentLine = line.split(" ", 0);
        for(int i = 1; i < currentLine.length; i++) {
          String[] vertex = currentLine[i].split("/", 0);
          processVertex(vertex, vertex.length, indice, face, vertices, textures, normals, texturesArray, normalsArray);
        }
        indices.add(indice);
        faces.add(face);

        line = br.readLine();
      }

      br.close();

    } catch (Exception e) {
      e.printStackTrace();
    }

    verticesArray = new float[vertices.size()][3];
    indicesArray = new int[indices.size()][];
    facesArray = new float[faces.size()][3];

    int VerticesPointer = 0;
    for(int i = 0; i < vertices.size(); i++) {
      for (int j = 0; j < vertices.get(i).size(); j++) {
        verticesArray[i][j] = vertices.get(i).get(j);
      }
    }

    for(int i = 0; i < faces.size(); i++) {
      processNormVertex(faces.get(i), facesArray[i]);
    }

    for(int i = 0; i < indices.size(); i++) {
      indicesArray[i] = new int[indices.get(i).size()];
      for (int j = 0; j < indices.get(i).size(); j++) {
        indicesArray[i][j] = indices.get(i).get(j);
      }
    }

    calc.vertices_array = verticesArray;
    calc.textures_array = texturesArray;
    calc.normals_array = normalsArray;
    calc.faces_array = facesArray;
    calc.indices_array = indicesArray;

    return calc;
  }

  private static FileReader ReadObjFile (String fileName) {
    FileReader fr = null;
    try {
      fr = new FileReader(new File("res/" + fileName + ".obj"));
    }
    catch (Exception e) {
      System.err.println(e);
      System.exit(1);
    } finally {
      return fr;
    }
  }

  private static void processVertex(String[] vertexData, int size,
    ArrayList<Integer> indice, ArrayList<ArrayList<Float>> face, List<ArrayList<Float>> vertices, List<ArrayList<Float>> textures, List<ArrayList<Float>> normals,
    float[][] textureArray, float[][] normalsArray) {
      int currentVertexPointer = Integer.parseInt(vertexData[0]) - 1;
      indice.add(currentVertexPointer);
      ArrayList<Float> currentVertex = vertices.get(currentVertexPointer);
      face.add(currentVertex);
      if (size == 2) {
        processTex(vertexData, currentVertexPointer, textures, textureArray);
      }
      else if (size == 3) {
        processTex(vertexData, currentVertexPointer, textures, textureArray);
        processNorm(vertexData, currentVertexPointer, normals, normalsArray);
      }
    }

  private static void processTex(String[] vertexData, int currentVertexPointer, List<ArrayList<Float>> textures, float[][] textureArray) {
    ArrayList<Float> currentTex = textures.get(Integer.parseInt(vertexData[1]) - 1);
    textureArray[currentVertexPointer][0] = currentTex.get(0);
    textureArray[currentVertexPointer][1] = currentTex.get(1);
  }

  private static void processNorm(String[] vertexData, int currentVertexPointer, List<ArrayList<Float>> normals, float[][] normalsArray) {
    ArrayList<Float> currentNorm = normals.get(Integer.parseInt(vertexData[2]) - 1);
    normalsArray[currentVertexPointer][0] = currentNorm.get(0);
    normalsArray[currentVertexPointer][1] = currentNorm.get(1);
    normalsArray[currentVertexPointer][2] = currentNorm.get(2);
  }

  private static void processNormVertex(ArrayList<ArrayList<Float>> face, float[] faceArray) {
    ArrayList<Float> P0 = face.get(0);
    ArrayList<Float> P1 = face.get(1);
    ArrayList<Float> P2 = face.get(2);
    float Nx = (P0.get(1)-P1.get(1))*(P2.get(2)-P1.get(2)) - (P0.get(2)-P1.get(2))*(P2.get(1)-P1.get(1));
    float Ny = (P0.get(2)-P1.get(2))*(P2.get(0)-P1.get(0)) - (P0.get(0)-P1.get(0))*(P2.get(2)-P1.get(2));
    float Nz = (P0.get(0)-P1.get(0))*(P2.get(1)-P1.get(1)) - (P0.get(1)-P1.get(1))*(P2.get(0)-P1.get(0));
    float length = (float)Math.sqrt(Nx*Nx + Ny*Ny + Nz*Nz);
    faceArray[0] = Nx/length;
    faceArray[1] = Ny/length;
    faceArray[2] = Nz/length;
  }


}
