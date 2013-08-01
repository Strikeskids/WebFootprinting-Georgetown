package com.sk.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.imageio.ImageIO;

public class DCTHash {

	private static final int size = 32, lowSize = 8;
	private static final double[][] dctt2;

	public static void main(String[] args) throws IOException {
		Map<String, Long> hashes = new HashMap<>();
		File BASE = new File("images");
		DCTHash hasher = new DCTHash();
		List<String> bases = new ArrayList<>();
		Set<String> names = new HashSet<>();
		for (File source : BASE.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		})) {
			String base = source.getAbsolutePath();
			bases.add(base);
			for (File image : source.listFiles()) {
				String name = image.getName();
				name = name.substring(0, name.lastIndexOf('.'));
				names.add(name);
				hashes.put(base + File.separator + name, hasher.hash(ImageIO.read(image)));
			}
		}
		List<Entry<String, Long>> allHashes = new ArrayList<>(hashes.entrySet());
		for (String name : names) {
			List<Long> current = new ArrayList<>();
			for (String base : bases) {
				String abs = base + File.separator + name;
				if (hashes.containsKey(abs))
					current.add(hashes.get(abs));
			}
			List<Integer> fails = new ArrayList<>();
			for (long a : current) {
				for (long b : current) {
					int dist = getDist(a, b);
					if (dist > 16)
						fails.add(dist);
				}
			}
			if (!fails.isEmpty())
				System.out.printf("Failed %s %s%n", name, fails);
		}
		Random rand = new Random();
		for (int i = 0; i < 50; ++i) {
			Entry<String, Long> ae = allHashes.get(rand.nextInt(allHashes.size())), be = allHashes.get(rand
					.nextInt(allHashes.size()));
			if (new File(ae.getKey()).getName().equals(new File(be.getKey()).getName()))
				continue;
			if (compare(ae.getValue(), be.getValue(), 16)) {
				System.out.printf("Failed %s %s %d%n", ae.getKey(), be.getKey(),
						getDist(ae.getValue(), be.getValue()));
			}
		}
	}

	public static boolean compare(long h1, long h2, int threshold) {
		return getDist(h1, h2) <= threshold;
	}

	public static int getDist(long h1, long h2) {
		int count = 0;
		for (long h3 = h1 ^ h2; h3 != 0; h3 &= ~(h3 & -h3), ++count)
			;
		return count;
	}

	public long hash(BufferedImage image) {
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
