package us.zonix.core.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public final class FontRenderer {

	private int[] charOffsets = new int[256];
	private int[] charWidths = new int[256];

	private int textureHeight;
	private int textureWidth;

	FontRenderer() {
		BufferedImage fontTexture;
		try {
			fontTexture = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream("default.png"));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		int width = fontTexture.getWidth();
		int height = fontTexture.getHeight();
		textureWidth = width;
		textureHeight = height;

		calculateCharWidths(fontTexture, width, height);
	}

	private static boolean isColEmpty(int[] imgData, int offset, int imageWidth, int maxCharHeight) {
		// Checks if a column of pixels contains any non-transparent pixels
		for (int row = 0; row < maxCharHeight; row++) {
			int rowOffset = offset + row * imageWidth;
			if (((imgData[rowOffset] >> 24) & 0xFF) > 128) {
				// Non-transparent pixel found in column!
				return false;
			}
		}
		return true;
	}

	// Calculates width and offset of every character, to space characters correctly.
	private void calculateCharWidths(BufferedImage fontTexture, int width, int height) {
		int[] fontData = new int[width * height];
		fontTexture.getRGB(0, 0, width, height, fontData, 0, width);
		int maxCharWidth = width / 16;
		int maxCharHeight = height / 16;

		for (int character = 0; character < 128; ++character) {
			int col = character % 16;
			int row = character / 16;
			int offset = (col * maxCharWidth) + (row * maxCharHeight * width);

			if (character == 32) {
				// Space is always 33% width
				charWidths[32] = maxCharWidth / 3;
			} else {
				// Other chars' width is determined by examining pixels
				// First, find start of character (left-most non-empty column)
				int chStart = 0;
				for (int c = 0; c < maxCharWidth; c++) {
					chStart = c;
					if (!isColEmpty(fontData, offset + c, width, maxCharHeight)) {
						break;
					}
				}
				// Next, find end of character (right-most non-empty column)
				int chEnd = maxCharWidth - 1;
				for (int c = maxCharWidth - 1; c >= chStart; c--) {
					chEnd = c;
					if (!isColEmpty(fontData, offset + c, width, maxCharHeight)) {
						break;
					}
				}

				charOffsets[character] = chStart;
				charWidths[character] = chEnd - chStart + 1;
			}
		}
	}

	public int getWidth(String text) {
		if (text == null) {
			return 0;
		}
		float charWidthScale = 128f / textureWidth;
		float width = 0;
		for (int j = 0; j < text.length(); j++) {
			int k = text.charAt(j);
			if (k == '&') {
				j++;
			} else {
				width += charWidths[k] * charWidthScale + 1;
			}
		}
		return (int) Math.ceil(width);
	}

	public int getHeight() {
		return (int) Math.ceil(textureHeight);
	}

}