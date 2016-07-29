package fr.ign.cogit.extrapalettor.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.semio.color.CIELabColorSpace;

public class WebglGamutGen {

  /**
   * ##############  The sRGB cube ##############
   *             
   *       cyan o--------------o white
   *          . .             .|
   *        .   .           .  | 
   *  blue o--------------o magenta
   *       |    .         |    |
   *       |    .         |    |
   *       |    .         |    | 
   *       |    . green   |    |
   *       |    o.........|....o yellow
   *       |  .           |  . 
   *       |.             |.  
   * black o--------------o red
   */
  static final float[] black = new float[] { 0, 0, 0 };
  static final float[] blue = new float[] { 0, 0, 1 };
  static final float[] green = new float[] { 0, 1, 0 };
  static final float[] red = new float[] { 1, 0, 0 };
  static final float[] cyan = new float[] { 0, 1, 1 };
  static final float[] magenta = new float[] { 1, 0, 1 };
  static final float[] yellow = new float[] { 1, 1, 0 };
  static final float[] white = new float[] { 1, 1, 1};

  
  
  public static void gensRGBGamutJSON(File output,int n_faces_per_side ){
    //Six n-divided faces of 3D float points in L*a*b*. We store all the vertices for each sub-face so every vertex is at most duplicated 4 times.
    //We then have in total so (n+1)^2 vertices in total.
    int nquads = (n_faces_per_side)*(n_faces_per_side)*6;
    int nv = nquads*4;
    //Triangles vertex indices. There are 2 triangles per sub-face. 
    float[][] vertices = new float[nv][3];
    float[][] colors = new float[nv][3];
    //6 subdivided faces of 3D float points in L*a*b*    
    List[] res = ComputesRGBGamutCIELab(n_faces_per_side);
    for(Object pt : res[0]){
     float[] p = (float[]) pt;
      for(float f : p){
        System.out.print(f+",");
      }
    }
    System.out.println("");
    for(Object pt : res[2]){
      float[] p = (float[]) pt;
//      System.out.println(Arrays.toString(p));
       for(float f : p){
         System.out.print(f+",");
       }
     }
    System.out.println("");
//    System.out.println(Arrays.toString(res[1].toArray(new Object[0])));
    StringBuilder jsonQuads = new StringBuilder();
    StringBuilder jsonColors = new StringBuilder();

    jsonQuads.append("[");
    jsonColors.append("[");
    for(int i =0 ; i < nv; i+=4){
      //A quad and its color
      jsonQuads.append("[");
      jsonColors.append("[");
      //p1
      jsonQuads.append(Arrays.toString(vertices[i])+",");
      jsonColors.append(Arrays.toString(colors[i])+",");
      //p2
      jsonQuads.append(Arrays.toString(vertices[i+1])+",");
      jsonColors.append(Arrays.toString(colors[i+1])+",");
      //p3
      jsonQuads.append(Arrays.toString(vertices[i+2])+",");
      jsonColors.append(Arrays.toString(colors[i+2])+",");
      //p4
      jsonQuads.append(Arrays.toString(vertices[i+3]));
      jsonColors.append(Arrays.toString(colors[i+3]));
      //end the quad
      jsonQuads.append("]");
      jsonColors.append("]");
      if(i < nv-1){
        jsonQuads.append(",");
        jsonColors.append(",");
      }
    }
    jsonQuads.append("]");
    jsonColors.append("]");
    
    StringBuilder jsonString = new StringBuilder();
    jsonString.append("{\n");
    jsonString.append("\"quads\" : "+jsonQuads+"\n");
    jsonString.append("\"colors\" : "+jsonColors+"\n");
    jsonString.append("}\n");
    //System.out.println(jsonString);
    
  }
  /**
   * Generate the vertex coordinates of the sRGB Gamut displayed in CIE L*a*b
   * color space. This method returns quads. 
   * To display it in WebGL you will first have to triangulate each quad into triangles
   * @param n_faces_per_side
   * @param nvertices
   * @param faces
   * @param vertices
   * @param colors
   * @return 
   */
  private static List[] ComputesRGBGamutCIELab(int n_faces_per_side) {
    assert(n_faces_per_side>0);
    
    /*
     * Each face of the sRGB cube is subdivided in squares.
     * Coordinates are stored row per row, starting from the upper side of each vertical face and from the back for horizontal faces.
     * The points on the sides are duplicated 2 times and those on the corners 3 times.
     *  While not memory-efficient, this allow easier manipulations when creating the  vertex arrays. 
     */
    float[][] quadscoordinates = new float[(n_faces_per_side+1)*(n_faces_per_side+1)*6][3];
    
    /*###########   Face 1    ###########
     * blue  o--------------o magenta
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     * black o--------------o red
     */
    float[] topleft = blue;
    float[] topright = magenta;
    float[] botleft = black;
    setFaceCoordinates(0, topleft, topright, botleft, n_faces_per_side, quadscoordinates);
    
    /*###########   Face 2    ###########
     *magentao--------------o white
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *   red o--------------o yellow
     */
    topleft = magenta;
    topright = white;
    botleft = red;
    setFaceCoordinates(1, topleft, topright, botleft, n_faces_per_side, quadscoordinates);
    
    /*###########   Face 3    ###########
     *white  o--------------o cyan
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *yellow o--------------o green
     */
    topleft = white;
    topright = cyan;
    botleft = yellow;
    setFaceCoordinates(2, topleft, topright, botleft, n_faces_per_side, quadscoordinates);

    /*###########   Face 4    ###########
     *cyan   o--------------o blue
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *green  o--------------o black 
     */
    topleft = cyan;
    topright = blue;
    botleft = green;
    setFaceCoordinates(3, topleft, topright, botleft, n_faces_per_side, quadscoordinates);

    /*###########   Face 5    ###########
     *cyan   o--------------o white
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *blue   o--------------o magenta 
     */
    topleft = cyan;
    topright = white;
    botleft = blue;
    setFaceCoordinates(4, topleft, topright, botleft, n_faces_per_side, quadscoordinates);

    /*###########   Face 6    ###########
     *green  o--------------o yellow
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *       |..............|
     *       |  :   :   :   |
     *black  o--------------o red 
     */
    topleft = green;
    topright = yellow;
    botleft = black;
    setFaceCoordinates(5, topleft, topright, botleft, n_faces_per_side, quadscoordinates);

    
    /*
     * Build the vertex, indice and color arrays.
     * In order to avoid duplicate points, we also incremently build a vertex index 
     */
    CIELabColorSpace cs = new CIELabColorSpace(false);
    //Hashmap <Arrays.hashCode, vertex index>
    Map<Integer, Integer> mapIndices = new HashMap<>();
    List<float[]> meshV = new ArrayList<>(0);
    List<Integer> meshI = new ArrayList<>(0);
    List<float[]> vC = new ArrayList<>(0);
    int nquadsperface = (n_faces_per_side*n_faces_per_side);
    int tl, tr, bl, br, row,fid;
    for(int quadbegin =0; quadbegin< nquadsperface*6; quadbegin++){
      int quadIdInFace= quadbegin%nquadsperface;
      fid = Math.floorDiv(quadbegin,nquadsperface);
      row = (int)(quadIdInFace/n_faces_per_side);
      tl = quadbegin+row+fid*(2*(n_faces_per_side+1)-1);
      tr = tl+1;
      bl = tl+n_faces_per_side+1;
      br = bl+1;
      System.out.println(tl+" "+tr+" "+bl+" "+br);
//      int tlhc = Arrays.hashCode(quadscoordinates[tl]);
//      int trhc = Arrays.hashCode(quadscoordinates[tr]);
//      int blhc = Arrays.hashCode(quadscoordinates[bl]);
//      int brhc = Arrays.hashCode(quadscoordinates[br]);
//      Integer tl_index = mapIndices.get(tlhc);
//      Integer tr_index = mapIndices.get(trhc);
//      Integer br_index = mapIndices.get(brhc);
//      Integer bl_index = mapIndices.get(blhc);
//      if(tl_index == null){
//        meshV.add(quadscoordinates[tl]);
//        mapIndices.put(tlhc, meshV.size()-1);
//        tl_index = meshV.size()-1;
//      }
//      if(tr_index == null){
//        meshV.add(quadscoordinates[tr]);
//        mapIndices.put(trhc, meshV.size()-1);
//        tr_index = meshV.size()-1;
//      }
//      if(br_index == null){
//        meshV.add(quadscoordinates[br]);
//        mapIndices.put(brhc, meshV.size()-1);
//        br_index = meshV.size()-1;
//      }
//      if(bl_index == null){
//        meshV.add(quadscoordinates[bl]);
//        mapIndices.put(blhc, meshV.size()-1);
//        bl_index = meshV.size()-1;
//      }
      
      //Triangle 1
      meshV.add(cs.fromRGB(quadscoordinates[tl]));
      meshV.add(cs.fromRGB(quadscoordinates[tr]));
      meshV.add(cs.fromRGB(quadscoordinates[bl]));
      
      vC.add(quadscoordinates[tl]);
      vC.add(quadscoordinates[tr]);
      vC.add(quadscoordinates[bl]);
      
      meshI.add(meshV.indexOf(quadscoordinates[tl]));
      meshI.add(meshV.indexOf(quadscoordinates[bl]));
      meshI.add(meshV.indexOf(quadscoordinates[br]));

      //Triangle 2
      meshV.add(cs.fromRGB(quadscoordinates[bl]));
      meshV.add(cs.fromRGB(quadscoordinates[tr]));
      meshV.add(cs.fromRGB(quadscoordinates[br]));
      vC.add(quadscoordinates[bl]);
      vC.add(quadscoordinates[tr]);
      vC.add(quadscoordinates[br]);
      
      meshI.add(meshV.indexOf(quadscoordinates[tl]));
      meshI.add(meshV.indexOf(quadscoordinates[tr]));
      meshI.add(meshV.indexOf(quadscoordinates[br]));
    }
    return new List[]{meshV,meshI,vC};
  }
  
  public static void setFaceCoordinates(int face_index, float[] topleft, float[] topright,float[] botleft, int nfaces, float[][] coordinates){
    //Left to right
    float[] v2 = new float[]{(topright[0]-topleft[0])/nfaces,(topright[1]-topleft[1])/nfaces,(topright[2]-topleft[2])/nfaces};
    //Top to bottom
    float[] v1 = new float[]{(botleft[0]-topleft[0])/nfaces,(botleft[1]-topleft[1])/nfaces,(botleft[2]-topleft[2])/nfaces};
    int nvertices = nfaces+1;
    int offset = face_index*nvertices*nvertices;
  //Iterates over the rows
    for(int j = 0; j < nvertices;j++){
      int k = j*nvertices+offset;
      coordinates[k] = new float[]{topleft[0]+j*v1[0],topleft[1]+j*v1[1],topleft[2]+j*v1[2]};
      //Iterates over the columns
    for(int i = 1; i < nvertices;i++){
      coordinates[k+i] = new float[]{coordinates[k][0]+i*v2[0],coordinates[k][1]+i*v2[1],coordinates[k][2]+i*v2[2] };
      }
    }
  }

  public static void main(String[] argv){
    gensRGBGamutJSON(new File(""),2);
  }
}
