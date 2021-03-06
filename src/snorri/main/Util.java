package snorri.main;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;

public class Util {

	public static final double GOLDEN_RATIO = 1.61803398875; //TODO use this
	
	public static Integer getInteger(String input) {
		try {
			return Integer.parseInt(input);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Double getDouble(String input) {
		try {
			return Double.parseDouble(input);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String removeExtension(String fileName) {
		return fileName.replaceFirst("[.][^.]+$", "");
	}
	
	public static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}
	
	public static String clean(String constant) {
		return constant.toLowerCase().replace('_', ' ');
	}
	
	public static String unclean(String raw) {
		return raw.toUpperCase().replace(' ', '_');
	}
	
	public static Collection<Object> safe(Collection<Object> c) {
		return c == null ? Collections.emptyList() : c;
	}
	
	public static BufferedImage resize(Image image, int newWidth, int newHeight) {
		Image scaled = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
		BufferedImage img = new BufferedImage(scaled.getWidth(null), scaled.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics g = img.getGraphics();
		g.drawImage(scaled, 0, 0, null);
		g.dispose();
		return img;
	}
	
	public static <T> T[] concatenate(T[] a, T[] b) {
		int aLen = a.length;
		int bLen = b.length;

		@SuppressWarnings("unchecked")
		T[] c = (T[]) Array.newInstance(a.getClass().getComponentType(), aLen + bLen);
		System.arraycopy(a, 0, c, 0, aLen);
		System.arraycopy(b, 0, c, aLen, bLen);

		return c;
	}

	public static <T> T random(Collection<T> coll) {
		int num = (int) (Math.random() * coll.size());
		for (T t : coll) {
			if (--num < 0) {
				return t;
			}
		}
		throw new AssertionError();
	}
	
	public static int niceMod(int n, int m) {
		return (((n % m) + m) % m);
	}
	
}
