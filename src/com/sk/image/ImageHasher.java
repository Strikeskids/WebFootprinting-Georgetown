package com.sk.image;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.codec.binary.Base64;

import com.sk.Driver;
import com.sk.threading.TaskGroup;
import com.sk.util.PersonalData;

public class ImageHasher {

	private final int sectionsX, sectionsY;

	public ImageHasher(int secx, int secy) {
		this.sectionsX = secx & ~1;
		this.sectionsY = secy & ~1;
	}

	public ImageHasher(ImageHasher source) {
		this.sectionsX = source.sectionsX;
		this.sectionsY = source.sectionsY;
	}

	public boolean fingerprint(PersonalData... in) {
		TaskGroup tasks = new TaskGroup();
		for (final PersonalData data : in) {
			if (data.containsKey("profilePictureUrl") && !data.containsKey("profilePicturePrint")) {
				for (final String url : data.getAllValues("profilePictureUrl")) {
					tasks.add(new Runnable() {
						@Override
						public void run() {
							try {
								data.put("profilePicturePrint", fingerprint(ImageIO.read(new URL(url))));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
					break;
				}
			}
		}
		tasks.submit(Driver.EXECUTOR);
		return tasks.waitFor();
	}

	public String fingerprint(BufferedImage img) {
		int sectionWidth = img.getWidth() / sectionsX;
		int sectionHeight = img.getHeight() / sectionsY;
		int resizeWidth = sectionWidth * sectionsX;
		int resizeHeight = sectionHeight * sectionsY;
		BufferedImage dest = new BufferedImage(resizeWidth, resizeHeight, BufferedImage.TYPE_BYTE_GRAY);
		Graphics g = dest.getGraphics();
		g.drawImage(img, 0, 0, resizeWidth, resizeHeight, null);
		g.dispose();

		byte[] ret = new byte[sectionsX * sectionsY + 5];

		ret[0] = (byte) getAverageColor(dest);
		for (int i = 0; i < 4; ++i) {
			ret[i + 1] = (byte) getAverageColor(dest.getSubimage(resizeWidth / 2 * (i % 2), resizeHeight / 2
					* (i / 2), resizeWidth / 2, resizeHeight / 2));
		}

		for (int x = 0; x < sectionsX; ++x) {
			for (int y = 0; y < sectionsY; ++y) {
				ret[x * sectionsY + y + 5] = (byte) getAverageColor(dest.getSubimage(x * sectionWidth, y
						* sectionHeight, sectionWidth, sectionHeight));
			}
		}
		return Base64.encodeBase64String(ret);
	}

	private int getAverageColor(BufferedImage img) {
		Raster raster = img.getRaster();
		int total = 0;
		for (int x = 0; x < raster.getWidth(); x++) {
			for (int y = 0; y < raster.getHeight(); y++) {
				total += raster.getSample(raster.getMinX() + x, raster.getMinY() + y, 0);
			}
		}
		return total / raster.getWidth() / raster.getHeight();
	}

	private static ImageHasher defaultHasher;
	private static final Object deflock = new Object();

	public static ImageHasher getDefault() {
		if (defaultHasher == null) {
			synchronized (deflock) {
				if (defaultHasher == null)
					defaultHasher = new ImageHasher(10, 10);
			}
		}
		return defaultHasher;
	}

}
