import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.awt.color.*;
import java.awt.Graphics;
import java.awt.image.ComponentColorModel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import java.nio.ByteBuffer;

import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.common.nio.Buffers;

class TextureLoader {

  /* Reads the specifiled file and returns a Buffered Image containing the file data */
  public BufferedImage getBufferedImage (String fileName) {
    BufferedImage img = null;
    try {
      img = javax.imageio.ImageIO.read(new File(fileName));
    }
    catch (Exception e) {
      System.err.println(e);
      System.exit(1);
    } finally {
      return img;
    }
  }

  public int loadTexture(BufferedImage image, GL2 gl) {
    int[] pixels = new int[image.getWidth() * image.getHeight()];
    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

    ByteBuffer buffer = Buffers.newDirectByteBuffer(image.getWidth() * image.getHeight() * 4);
    Color c;

    for (int y = 0; y < image.getHeight(); y++) {
      for(int x = 0; x < image.getWidth(); x++) {
        c = new Color(image.getRGB(x, y));
        buffer.put((byte) c.getRed());     // Red Component
        buffer.put((byte) c.getGreen());   // Green Component
        buffer.put((byte) c.getBlue());    // Blue Component
        buffer.put((byte) c.getAlpha());   // Alpha Compnent. Only for RGBA
      }
    }

    buffer.flip();

    int[] textureIDs = new int[1];        // reserve an integer textureID (name)
    gl.glGenTextures(1, textureIDs, 0);
    int textureID = textureIDs[0];        // Generate texture ID
    gl.glBindTexture(GL2.GL_TEXTURE_2D, textureID); // Bind Texture ID

    // Setup wrap mode
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_S, GL2.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_WRAP_T, GL2.GL_CLAMP_TO_EDGE);

    // Setup texture scaling filtering
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_NEAREST);
    gl.glTexParameteri(GL2.GL_TEXTURE_2D, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_NEAREST);

    // Send texel data to opengl
    gl.glTexImage2D(GL2.GL_TEXTURE_2D, 0, GL2.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL2.GL_RGBA, GL2.GL_UNSIGNED_BYTE, buffer);

    // Return the texture ID so we can bind it later again
    return textureID;
  }
}
