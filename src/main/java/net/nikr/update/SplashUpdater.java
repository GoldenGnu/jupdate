/*
 * Copyright 2014 Niklas Kyster Rasmussen
 *
 * This file is part of jUpdate.
 *
 * Original code from jEveAssets (https://code.google.com/p/jeveassets/)
 *
 * jUpdate is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * jUpdate is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with jUpdate; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */

package net.nikr.update;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.SplashScreen;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;


public class SplashUpdater {

	private static int progress = 0;
	private static int subProgress = 0;
	private static String text = "";
	private static int currentLoadingImage = 0;
	private static BufferedImage[] loadingImages;
	private static SplashScreen splash;
	private static final int UPDATE_DELAY = 200;
	private static final Object PAINT_LOCK = new Object();
	private final Animator animator;
	private final Paineter paineter;

	private SplashUpdater() {
		splash = SplashScreen.getSplashScreen();
		loadingImages = new BufferedImage[8];
		for (int i = 0; i < 8; i++) {
			try {
				loadingImages[i] = ImageIO.read(getClass().getResource("loading0" + (i + 1) + ".png"));
			} catch (IOException ex) {
				//LOG.warn("SplashScreen: loading0{}.png (NOT FOUND)", (i + 1));
			}
		}
		animator = new Animator();
		paineter = new Paineter();
	}

	private void run() {
		animator.start();
		paineter.start();
	}

	public static void hide() {
		if (isVisible()) {
			try {
				splash.close();
			} catch (IllegalStateException ex) {
				//LOG.info("SplashScreen: Close failed (NO PROBLEM)");
			}
		}
	}

	public static void start() {
		SplashUpdater splashUpdater = new SplashUpdater();
		splashUpdater.run();
	}

	/**
	 * Set splash screen text.
	 * @param s	 String to show on splash screen
	 */
	public synchronized static void setText(final String s) {
		text = s;
	}

	/**
	 * Set subprogress of splash screen progressbar in the range 0-100.
	 * @param n	 Set progress in the range 0-100
	 */
	public synchronized static void setSubProgress(final int n) {
		int number = n;
		if (number >= 100) {
			number = 0;
		}
		if (number < 0) {
			number = 0;
		}
		if (subProgress != number) {
			subProgress = number;
			update();
		}
	}

	public synchronized static void addSubProgress(final int n) {
		setSubProgress(subProgress + n);
	}

	/**
	 * Set progress of splash screen progressbar in the range 0-100.
	 * @param n	 Set progress in the range 0-100
	 */
	public synchronized static void setProgress(final int n) {
		int number = n;
		if (number > 100) {
			number = 100;
		}
		if (number < 0) {
			number = 0;
		}
		if (progress != number) {
			progress = number;
			update();
		}
	}

	public synchronized static void addProgress(final int n) {
		setProgress(progress + n);
	}

	private synchronized static void nextLoadingImage() {
		currentLoadingImage++;
		if (currentLoadingImage >= 8) {
			currentLoadingImage = 0;
		}
	}

	private synchronized static void update() {
		synchronized (PAINT_LOCK) {
			PAINT_LOCK.notify();
		}
	}

	private synchronized static void repaint() {
		if (isVisible()) {
			try {
				Graphics2D g = splash.createGraphics();
				//Clear Screen
				g.setComposite(AlphaComposite.Clear);
				Dimension size = splash.getSize();
				g.fillRect(0, 0, size.width, size.height);
				g.setPaintMode();
				if (!text.isEmpty()) {
					g.setColor(Color.BLACK);
					g.fillRect(0, 235, 90, 24);
					g.setColor(Color.WHITE);
					g.drawString(text, 5, 252);
				}
				g.setColor(Color.WHITE);
				g.fillRect(106, 242, (int) (progress * 2.6), 12);
				if (subProgress > 0) {
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(106, 248, (int) (subProgress * 2.6), 6);
				}
				if (loadingImages[currentLoadingImage] != null) {
					g.drawImage(loadingImages[currentLoadingImage], 368, 238, null);
				}
				splash.update();
			} catch (IllegalStateException ex) {
				//LOG.info("SplashScreen: Closed before painting ended (NO PROBLEM)");
			}
		}
	}

	private static boolean isVisible() {
		return (splash != null && splash.isVisible());
	}

	private class Paineter extends Thread {

		@Override
		public void run() {
			while (isVisible()) {
				synchronized (PAINT_LOCK) {
					try {
						PAINT_LOCK.wait();
					} catch (InterruptedException ex) {
						
					}
				}
				repaint();
			}
		}
	}

	private class Animator extends Thread {
		@Override
		public void run() {
			while (isVisible()) {
				nextLoadingImage();
				update();
				try {
					Thread.sleep(UPDATE_DELAY);
				} catch (InterruptedException e) {
					break;
				}
			}
		}
	}
}
