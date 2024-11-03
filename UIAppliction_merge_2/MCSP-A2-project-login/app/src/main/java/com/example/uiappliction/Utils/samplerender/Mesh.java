
package com.example.uiappliction.Utils.samplerender;

import android.opengl.GLES30;
import android.util.Log;
import de.javagl.obj.Obj;
import de.javagl.obj.ObjData;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;


public class Mesh implements Closeable {
  private static final String TAG = Mesh.class.getSimpleName();

  
  public enum PrimitiveMode {
    POINTS(GLES30.GL_POINTS),
    LINE_STRIP(GLES30.GL_LINE_STRIP),
    LINE_LOOP(GLES30.GL_LINE_LOOP),
    LINES(GLES30.GL_LINES),
    TRIANGLE_STRIP(GLES30.GL_TRIANGLE_STRIP),
    TRIANGLE_FAN(GLES30.GL_TRIANGLE_FAN),
    TRIANGLES(GLES30.GL_TRIANGLES);

    
    final int glesEnum;

    private PrimitiveMode(int glesEnum) {
      this.glesEnum = glesEnum;
    }
  }

  private final int[] vertexArrayId = {0};
  private final PrimitiveMode primitiveMode;
  private final IndexBuffer indexBuffer;
  private final VertexBuffer[] vertexBuffers;

  //boundary of model
  private float[] minBounds = {Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE};
  private float[] maxBounds = {Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE};

  
  public Mesh(
      SampleRender render,
      PrimitiveMode primitiveMode,
      IndexBuffer indexBuffer,
      VertexBuffer[] vertexBuffers) {
    if (vertexBuffers == null || vertexBuffers.length == 0) {
      throw new IllegalArgumentException("Must pass at least one vertex buffer");
    }

    this.primitiveMode = primitiveMode;
    this.indexBuffer = indexBuffer;
    this.vertexBuffers = vertexBuffers;

    try {
      // Create vertex array
      GLES30.glGenVertexArrays(1, vertexArrayId, 0);
      GLError.maybeThrowGLException("Failed to generate a vertex array", "glGenVertexArrays");

      // Bind vertex array
      GLES30.glBindVertexArray(vertexArrayId[0]);
      GLError.maybeThrowGLException("Failed to bind vertex array object", "glBindVertexArray");

      if (indexBuffer != null) {
        GLES30.glBindBuffer(GLES30.GL_ELEMENT_ARRAY_BUFFER, indexBuffer.getBufferId());
      }

      for (int i = 0; i < vertexBuffers.length; ++i) {
        // Bind each vertex buffer to vertex array
        GLES30.glBindBuffer(GLES30.GL_ARRAY_BUFFER, vertexBuffers[i].getBufferId());
        GLError.maybeThrowGLException("Failed to bind vertex buffer", "glBindBuffer");
        GLES30.glVertexAttribPointer(
            i, vertexBuffers[i].getNumberOfEntriesPerVertex(), GLES30.GL_FLOAT, false, 0, 0);
        GLError.maybeThrowGLException(
            "Failed to associate vertex buffer with vertex array", "glVertexAttribPointer");
        GLES30.glEnableVertexAttribArray(i);
        GLError.maybeThrowGLException(
            "Failed to enable vertex buffer", "glEnableVertexAttribArray");
      }
    } catch (Throwable t) {
      close();
      throw t;
    }
  }

  
  public static Mesh createFromAsset(SampleRender render, String assetFileName) throws IOException {
    try (InputStream inputStream = render.getAssets().open(assetFileName)) {
      Obj obj = ObjUtils.convertToRenderable(ObjReader.read(inputStream));

      // Obtain the data from the OBJ, as direct buffers:
      IntBuffer vertexIndices = ObjData.getFaceVertexIndices(obj,  3);
      FloatBuffer localCoordinates = ObjData.getVertices(obj);
      FloatBuffer textureCoordinates = ObjData.getTexCoords(obj,  2);
      FloatBuffer normals = ObjData.getNormals(obj);

      VertexBuffer[] vertexBuffers = {
        new VertexBuffer(render, 3, localCoordinates),
        new VertexBuffer(render, 2, textureCoordinates),
        new VertexBuffer(render, 3, normals),
      };

      IndexBuffer indexBuffer = new IndexBuffer(render, vertexIndices);

      return new Mesh(render, PrimitiveMode.TRIANGLES, indexBuffer, vertexBuffers);
    }
  }

  @Override
  public void close() {
    if (vertexArrayId[0] != 0) {
      GLES30.glDeleteVertexArrays(1, vertexArrayId, 0);
      GLError.maybeLogGLError(
          Log.WARN, TAG, "Failed to free vertex array object", "glDeleteVertexArrays");
    }
  }

  
  public void lowLevelDraw() {
    if (vertexArrayId[0] == 0) {
      throw new IllegalStateException("Tried to draw a freed Mesh");
    }

    GLES30.glBindVertexArray(vertexArrayId[0]);
    GLError.maybeThrowGLException("Failed to bind vertex array object", "glBindVertexArray");
    if (indexBuffer == null) {
      // Sanity check for debugging
      int vertexCount = vertexBuffers[0].getNumberOfVertices();
      for (int i = 1; i < vertexBuffers.length; ++i) {
        int iterCount = vertexBuffers[i].getNumberOfVertices();
        if (iterCount != vertexCount) {
          throw new IllegalStateException(
              String.format(
                  "Vertex buffers have mismatching numbers of vertices ([0] has %d but [%d] has"
                      + " %d)",
                  vertexCount, i, iterCount));
        }
      }
      GLES30.glDrawArrays(primitiveMode.glesEnum, 0, vertexCount);
      GLError.maybeThrowGLException("Failed to draw vertex array object", "glDrawArrays");
    } else {
      GLES30.glDrawElements(
          primitiveMode.glesEnum, indexBuffer.getSize(), GLES30.GL_UNSIGNED_INT, 0);
      GLError.maybeThrowGLException(
          "Failed to draw vertex array object with indices", "glDrawElements");
    }
  }


  public float[] calculateBoundingBox() {
    VertexBuffer vertexBuffer = vertexBuffers[0];
    FloatBuffer buffer = vertexBuffer.getBuffer();
    if (buffer == null) {
      return null;
    }

    buffer.rewind();

    float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE, minZ = Float.MAX_VALUE;
    float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE, maxZ = Float.MIN_VALUE;

    while (buffer.hasRemaining()) {
      float x = buffer.get();
      float y = buffer.get();
      float z = buffer.get();


      minX = Math.min(minX, x);
      minY = Math.min(minY, y);
      minZ = Math.min(minZ, z);
      maxX = Math.max(maxX, x);
      maxY = Math.max(maxY, y);
      maxZ = Math.max(maxZ, z);
    }


    return new float[] {maxX - minX, maxY - minY, maxZ - minZ};
  }


}


