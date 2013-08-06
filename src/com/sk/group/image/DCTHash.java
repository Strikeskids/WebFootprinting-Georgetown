package com.sk.group.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import com.sk.threading.TaskGroup;
import com.sk.threading.UniversalExecutor;
import com.sk.util.data.PersonalData;
import com.sk.web.Base64Util;

public class DCTHash {

	private static final int size = 32, lowSize = 8;
	private static final double[][] dctt2;

	public static boolean compare(long h1, long h2, int threshold) {
		return getDist(h1, h2) <= threshold;
	}

	public static int getDist(long h1, long h2) {
		int count = 0;
		for (long h3 = h1 ^ h2; h3 != 0; h3 &= ~(h3 & -h3), ++count)
			;
		return count;
	}

	public static String convertHash(long hash) {
		ByteBuffer bytes = ByteBuffer.allocate(8);
		bytes.putLong(hash);
		return Base64Util.encode(bytes.array());
	}

	public static long convertHash(String hash) {
		ByteBuffer bytes = ByteBuffer.wrap(Base64Util.decode(hash));
		return bytes.getLong();
	}

	public static boolean fingerprint(PersonalData... in) {
		TaskGroup tasks = new TaskGroup();
		for (final PersonalData data : in) {
			if (shouldCreateTask(data)) {
				for (final String url : data.getAllValues("profilePictureUrl")) {
					tasks = addTask(tasks, data, url);
				}
			}
		}
		tasks.submit(UniversalExecutor.search);
		return tasks.waitFor();
	}

	private static boolean shouldCreateTask(PersonalData data) {
		return data.containsKey("profilePictureUrl") && !data.containsKey("profilePicturePrint");
	}

	private static TaskGroup addTask(TaskGroup tasks, final PersonalData data, final String url) {
		tasks.add(new Runnable() {
			@Override
			public void run() {
				try {
					BufferedImage image = getImage();
					String hash = stringHash(image);
					data.put("profilePicturePrint", hash);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			private BufferedImage getImage() throws IOException {
				return ImageIO.read(new URL(url));
			}
		});
		return tasks;
	}

	public static String stringHash(BufferedImage image) {
		long hash = hash(image);
		return convertHash(hash);
	}

	public static long hash(BufferedImage image) {
		BufferedImage scaled = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = scaled.getGraphics();
		g.drawImage(image, 0, 0, size, size, null);
		g.dispose();
		Raster raster = scaled.getRaster();
		double[][] pixels = new double[size][size];
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				pixels[i][j] = raster.getSampleDouble(i, j, 0);
			}
		}
		int[][] trans = copy(mult(dctt2, pixels, null), lowSize, lowSize);
		int sum = 0;
		for (int i = 0; i < lowSize; ++i) {
			for (int j = 0; j < lowSize; ++j) {
				if ((i | j) != 0)
					sum += trans[i][j];
			}
		}
		long hash = 0;
		int average = sum / (lowSize * lowSize - 1);
		long bit = 1;
		for (int i = 0; i < lowSize; ++i) {
			for (int j = 0; j < lowSize; ++j, bit <<= 1) {
				if (trans[i][j] >= average)
					hash |= bit;
			}
		}
		return hash;
	}

	private static int[][] copy(double[][] in, int xsize, int ysize) {
		int[][] ret = new int[xsize][ysize];
		for (int i = 0; i < ret.length && i < in.length; ++i) {
			for (int j = 0; j < ret[i].length && j < in[i].length; ++j)
				ret[i][j] = (int) in[i][j];
		}
		return ret;
	}

	private static double[][] mult(double[][] a, double[][] b, double[][] ret) {
		if (ret == null)
			ret = new double[a.length][b[0].length];
		for (int i = 0; i < ret.length; ++i) {
			for (int j = 0; j < ret[i].length; ++j) {
				for (int loc = 0; loc < b.length; ++loc) {
					ret[i][j] += a[i][loc] * b[loc][j];
				}
			}
		}
		return ret;
	}

	static {
		double[][] dct = new double[size][size];
		double first = Math.sqrt(1d / size);
		for (int j = 0; j < size; ++j)
			dct[0][j] = first;
		double coef = Math.sqrt(2d / size);
		for (int i = 1; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				dct[i][j] = coef * Math.cos(Math.PI * (j + j + 1) * i / 2d / size);
			}
		}
		double[][] dctt = new double[size][size];
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				dctt[i][j] = dct[j][i];
			}
		}
		dctt2 = mult(dctt, dctt, null);
	}

}
