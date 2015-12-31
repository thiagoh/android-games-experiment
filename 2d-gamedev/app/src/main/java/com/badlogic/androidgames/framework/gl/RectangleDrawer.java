package com.badlogic.androidgames.framework.gl;

import com.badlogic.androidgames.framework.ColoredRectangle;
import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.Rectangle;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by thiago on 30/12/15.
 */
public class RectangleDrawer {

	final GLGraphics glGraphics;

	final float[] verticesBuffer;
	int bufferIndex;
	final Vertices vertices;
	int numRectangles;
	final boolean hasColor;

	public RectangleDrawer(GLGraphics glGraphics, int maxRectangles, boolean hasColor) {

		this.glGraphics = glGraphics;
		int vertexSize = (2 + (hasColor ? 4 : 0)) * 4;
		// rectangles count * vertexes count * vertexSize
		this.verticesBuffer = new float[maxRectangles * vertexSize];
		this.vertices = new Vertices(glGraphics, maxRectangles * 4, maxRectangles * 6, hasColor, false);
		this.bufferIndex = 0;
		this.numRectangles = 0;
		this.hasColor = hasColor;

		short[] indices = new short[maxRectangles * 6];
		int len = indices.length;
		short j = 0;
		for (int i = 0; i < len; i += 6, j += 4) {
			indices[i + 0] = (short) (j + 0);
			indices[i + 1] = (short) (j + 1);
			indices[i + 2] = (short) (j + 2);
			indices[i + 3] = (short) (j + 2);
			indices[i + 4] = (short) (j + 3);
			indices[i + 5] = (short) (j + 0);
		}
		vertices.setIndices(indices, 0, indices.length);
	}

	public void beginBatch() {
		numRectangles = 0;
		bufferIndex = 0;
	}

	public void endBatch() {
		vertices.setVertices(verticesBuffer, 0, bufferIndex);

		if (numRectangles > 0) {

//			GL10 gl = glGraphics.getGL();
//
//			// Counter-clockwise winding.
//			gl.glFrontFace(GL10.GL_CCW);
//			// Enable face culling.
//			gl.glEnable(GL10.GL_CULL_FACE);
//			// What faces to remove with the face culling.
//			gl.glCullFace(GL10.GL_BACK);

			vertices.bind();
			vertices.draw(GL10.GL_TRIANGLES, 0, numRectangles * 6);
			vertices.unbind();

//			// Disable the vertices buffer.
//			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//			// Disable face culling.
//			gl.glDisable(GL10.GL_CULL_FACE);
		}
	}

	public void drawRectangle(Rectangle rectangle) {
		drawRectangle(rectangle.lowerLeft.x, rectangle.lowerLeft.y, rectangle.width, rectangle.height, .0f, .0f, .0f, .0f);
	}

	public void drawRectangle(float x, float y, float width, float height) {
		drawRectangle(x, y, width, height, 0.0f, 0.0f, 0.0f, 0.0f);
	}

	public void drawRectangle(ColoredRectangle rectangle) {
		drawRectangle(rectangle, rectangle.red, rectangle.blue, rectangle.green, rectangle.alpha);
	}

	public void drawRectangle(Rectangle rectangle, float red, float blue, float green, float alpha) {
		drawRectangle(rectangle.lowerLeft.x, rectangle.lowerLeft.y, rectangle.width, rectangle.height, red, blue, green, alpha);
	}

	public void drawRectangle(float x, float y, float width, float height, float red, float blue, float green, float alpha) {
		float halfWidth = width / 2;
		float halfHeight = height / 2;
		float x1 = x - halfWidth;
		float y1 = y - halfHeight;
		float x2 = x + halfWidth;
		float y2 = y + halfHeight;

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;

		if (hasColor) {
			verticesBuffer[bufferIndex++] = red;
			verticesBuffer[bufferIndex++] = blue;
			verticesBuffer[bufferIndex++] = green;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y1;

		if (hasColor) {
			verticesBuffer[bufferIndex++] = red;
			verticesBuffer[bufferIndex++] = blue;
			verticesBuffer[bufferIndex++] = green;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;

		if (hasColor) {
			verticesBuffer[bufferIndex++] = red;
			verticesBuffer[bufferIndex++] = blue;
			verticesBuffer[bufferIndex++] = green;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y2;

		if (hasColor) {
			verticesBuffer[bufferIndex++] = red;
			verticesBuffer[bufferIndex++] = blue;
			verticesBuffer[bufferIndex++] = green;
			verticesBuffer[bufferIndex++] = alpha;
		}

		numRectangles++;
	}

//	/**
//	 * This function draws our square on screen.
//	 *
//	 * @param gl
//	 */
//	public void draw(GL10 gl) {
//		gl.glColor4f(0.5f, 0.5f, 1.0f, 1.0f); // 0x8080ffff
//		// Counter-clockwise winding.
//		gl.glFrontFace(GL10.GL_CCW);
//		// Enable face culling.
//		gl.glEnable(GL10.GL_CULL_FACE);
//		// What faces to remove with the face culling.
//		gl.glCullFace(GL10.GL_BACK);
//
//		// Enabled the vertices buffer for writing and to be used during
//		// rendering.
//		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
//		// Specifies the location and data format of an array of vertex
//		// coordinates to use when rendering.
//		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
//
//		gl.glDrawElements(GL10.GL_TRIANGLES, indices.length,
//				GL10.GL_UNSIGNED_SHORT, indexBuffer);
//
//		// Disable the vertices buffer.
//		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
//		// Disable face culling.
//		gl.glDisable(GL10.GL_CULL_FACE);
//	}

}

