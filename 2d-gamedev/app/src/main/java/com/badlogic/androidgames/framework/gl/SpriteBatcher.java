package com.badlogic.androidgames.framework.gl;

import javax.microedition.khronos.opengles.GL10;

import android.util.FloatMath;

import com.badlogic.androidgames.framework.impl.GLGraphics;
import com.badlogic.androidgames.framework.math.Vector2;

public class SpriteBatcher {
	final float[] verticesBuffer;
	final boolean opacity;
	int bufferIndex;
	final Vertices vertices;
	int numSprites;

	public SpriteBatcher(GLGraphics glGraphics, int maxSprites, boolean opacity) {

		int vertexSize = (2 + 4 + (opacity ? 4 : 0)) * 4;
		// rectangles count * vertexes count * vertexSize
		this.verticesBuffer = new float[maxSprites * vertexSize];
		this.vertices = new Vertices(glGraphics, maxSprites * 4, maxSprites * 6, opacity, true);
		this.bufferIndex = 0;
		this.numSprites = 0;
		this.opacity = opacity;

		short[] indices = new short[maxSprites * 6];
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

	public void beginBatch(Texture texture) {
		texture.bind();
		numSprites = 0;
		bufferIndex = 0;
	}

	public void endBatch() {

		vertices.setVertices(verticesBuffer, 0, bufferIndex);

		if (numSprites > 0) {
			vertices.bind();
			vertices.draw(GL10.GL_TRIANGLES, 0, numSprites * 6);
			vertices.unbind();
		}
	}

	public void drawLowerLeftSprite(float x, float y, float width, float height, TextureRegion region) {
		drawLowerLeftSprite(x, y, width, height, 1.0f, region);
	}

	public void drawLowerLeftSprite(float x, float y, float width, float height, float alpha, TextureRegion region) {
		float x1 = x;
		float y1 = y;
		float x2 = x + width;
		float y2 = y + height;

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u1;
		verticesBuffer[bufferIndex++] = region.v2;

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y1;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u2;
		verticesBuffer[bufferIndex++] = region.v2;

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u2;
		verticesBuffer[bufferIndex++] = region.v1;

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y2;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u1;
		verticesBuffer[bufferIndex++] = region.v1;

		numSprites++;
	}

	public void drawSprite(float x, float y, float width, float height, TextureRegion region) {
		drawOpacitySprite(x, y, width, height, 1.0f, region);
	}

	public void drawOpacitySprite(float x, float y, float width, float height, float alpha, TextureRegion region) {
		float halfWidth = width / 2;
		float halfHeight = height / 2;
		float x1 = x - halfWidth;
		float y1 = y - halfHeight;
		float x2 = x + halfWidth;
		float y2 = y + halfHeight;

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u1;
		verticesBuffer[bufferIndex++] = region.v2;

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y1;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u2;
		verticesBuffer[bufferIndex++] = region.v2;

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u2;
		verticesBuffer[bufferIndex++] = region.v1;

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y2;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u1;
		verticesBuffer[bufferIndex++] = region.v1;

		numSprites++;
	}

	public void drawAngledSprite(float x, float y, float width, float height, float angle, TextureRegion region) {
		drawAngledOpaqueSprite(x, y, width, height, 0.0f, 1.0f, region);
	}

	public void drawAngledOpaqueSprite(float x, float y, float width, float height, float angle, float alpha, TextureRegion region) {
		float halfWidth = width / 2;
		float halfHeight = height / 2;

		float rad = angle * Vector2.TO_RADIANS;
		float cos = FloatMath.cos(rad);
		float sin = FloatMath.sin(rad);

		float x1 = -halfWidth * cos - (-halfHeight) * sin;
		float y1 = -halfWidth * sin + (-halfHeight) * cos;
		float x2 = halfWidth * cos - (-halfHeight) * sin;
		float y2 = halfWidth * sin + (-halfHeight) * cos;
		float x3 = halfWidth * cos - halfHeight * sin;
		float y3 = halfWidth * sin + halfHeight * cos;
		float x4 = -halfWidth * cos - halfHeight * sin;
		float y4 = -halfWidth * sin + halfHeight * cos;

		x1 += x;
		y1 += y;
		x2 += x;
		y2 += y;
		x3 += x;
		y3 += y;
		x4 += x;
		y4 += y;

		verticesBuffer[bufferIndex++] = x1;
		verticesBuffer[bufferIndex++] = y1;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u1;
		verticesBuffer[bufferIndex++] = region.v2;

		verticesBuffer[bufferIndex++] = x2;
		verticesBuffer[bufferIndex++] = y2;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u2;
		verticesBuffer[bufferIndex++] = region.v2;

		verticesBuffer[bufferIndex++] = x3;
		verticesBuffer[bufferIndex++] = y3;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u2;
		verticesBuffer[bufferIndex++] = region.v1;

		verticesBuffer[bufferIndex++] = x4;
		verticesBuffer[bufferIndex++] = y4;

		if (opacity) {
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = 1.0f;
			verticesBuffer[bufferIndex++] = alpha;
		}

		verticesBuffer[bufferIndex++] = region.u1;
		verticesBuffer[bufferIndex++] = region.v1;

		numSprites++;
	}
}
