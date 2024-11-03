
package com.example.uiappliction.Utils.samplerender;

import android.opengl.GLES30;
import java.io.Closeable;
import java.nio.FloatBuffer;


public class VertexBuffer implements Closeable {
  private final GpuBuffer buffer;
  private final int numberOfEntriesPerVertex;
  private FloatBuffer localBuffer;
  
  public VertexBuffer(SampleRender render, int numberOfEntriesPerVertex, FloatBuffer entries) {
    if (entries != null && entries.limit() % numberOfEntriesPerVertex != 0) {
      throw new IllegalArgumentException(
          "If non-null, vertex buffer data must be divisible by the number of data points per"
              + " vertex");
    }

    this.numberOfEntriesPerVertex = numberOfEntriesPerVertex;
    this.localBuffer = entries;
    buffer = new GpuBuffer(GLES30.GL_ARRAY_BUFFER, GpuBuffer.FLOAT_SIZE, entries);
  }

  
  public void set(FloatBuffer entries) {
    if (entries != null && entries.limit() % numberOfEntriesPerVertex != 0) {
      throw new IllegalArgumentException(
          "If non-null, vertex buffer data must be divisible by the number of data points per"
              + " vertex");
    }
    this.localBuffer = entries;
    buffer.set(entries);
  }

  public FloatBuffer getBuffer() {
    return localBuffer;
  }
  @Override
  public void close() {
    buffer.free();
  }

  
  int getBufferId() {
    return buffer.getBufferId();
  }

  
  int getNumberOfEntriesPerVertex() {
    return numberOfEntriesPerVertex;
  }

  
  int getNumberOfVertices() {
    return buffer.getSize() / numberOfEntriesPerVertex;
  }
}
