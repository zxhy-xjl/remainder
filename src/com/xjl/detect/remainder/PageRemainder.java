package com.xjl.detect.remainder;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
 * 纸张剩余量检测
 * 
 * @author lilisheng
 *
 */
public class PageRemainder {
	/**
	 * 把一个灰度图片转换生成黑白图片
	 * 
	 * @param originalImage
	 * @throws IOException
	 */
	public File binaryImage(File grayImage) throws IOException {
		BufferedImage image = ImageIO.read(grayImage);
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);// 重点，技巧在这个参数BufferedImage.TYPE_BYTE_BINARY
		Graphics2D g = binaryImage.createGraphics();
		int[] rgb = new int[3];
		int whiteLine = 0;
		for (int j = 0; j < height; j++) {
			int whitePoint = 0;
			for (int i = 0; i < width; i++) {
				int pixel = image.getRGB(i, j);
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
				// System.out.print(rgb[0]+"-"+rgb[1]+"-"+rgb[2] + " ");
				// 这个是阀值,如果三个演示有一个小于200则认为是黑色,否则认为是白色
				if (rgb[0] < 200 || rgb[1] < 200 || rgb[2] < 200) {
					g.setColor(Color.BLACK);
					g.drawRect(i, j, 1, 1);
				} else {
					g.setColor(Color.WHITE);
					g.drawRect(i, j, 1, 1);
					// 白色的点数加1,如果一行中白色的图片数量达到一定数量,则认为这一行是白色的,所以这里需要做一个计数
					whitePoint++;
				}
			}
			// 如果一行中白色的点占到了一半,则认为这是一张纸
			if (whitePoint > width / 2) {
				whiteLine++;
			}
		}
		System.out.println(whiteLine);
		File newFile = new File(grayImage.getParentFile(), grayImage.getName() + ".heibai.jpg");
		ImageIO.write(binaryImage, "jpg", newFile);
		return newFile;
	}

	/**
	 * 把一个图片转成灰度图片
	 * 
	 * @param originalFile
	 * @return
	 * @throws IOException
	 */
	public File grayImage(File originalFile) throws IOException {
		BufferedImage image = ImageIO.read(originalFile);
		int width = image.getWidth();
		int height = image.getHeight();
		BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);// 重点，技巧在这个参数BufferedImage.TYPE_BYTE_GRAY
		for (int j = 0; j < height; j++) {
			for (int i = 0; i < width; i++) {
				int rgb = image.getRGB(i, j);
				grayImage.setRGB(i, j, rgb);
			}
		}

		File newFile = new File(originalFile.getParentFile(), originalFile.getName() + ".huidu.jpg");
		ImageIO.write(grayImage, "jpg", newFile);
		return newFile;
	}
	public int detect(File originalFile) throws IOException{
		File grayFile = this.grayImage(originalFile);
		File binaryFile = this.binaryImage(grayFile);
		BufferedImage image = ImageIO.read(binaryFile);
		int width = image.getWidth();
		int height = image.getHeight();
		List<Integer> rgbList = new ArrayList<Integer>();
		int whiteLine = 0;
		int[] rgb = new int[3];
		for (int j = 0; j < height; j++) {
			int whitePoint = 0;
			for (int i = 0; i < width; i++) {
				int pixel = image.getRGB(i, j);
				if (!rgbList.contains(pixel)){
					rgbList.add(pixel);
				}
				rgb[0] = (pixel & 0xff0000) >> 16;
				rgb[1] = (pixel & 0xff00) >> 8;
				rgb[2] = (pixel & 0xff);
				// System.out.print(rgb[0]+"-"+rgb[1]+"-"+rgb[2] + " ");
				// 这个是阀值,如果三个演示有一个小于200则认为是黑色,否则认为是白色
				if (rgb[0] < 200 || rgb[1] < 200 || rgb[2] < 200) {
					
				} else {
					// 白色的点数加1,如果一行中白色的图片数量达到一定数量,则认为这一行是白色的,所以这里需要做一个计数
					whitePoint++;
				}
			}
			if (whitePoint > width / 2) {
				whiteLine++;
			}
		}
		for (Integer integer : rgbList) {
			rgb[0] = (integer & 0xff0000) >> 16;
			rgb[1] = (integer & 0xff00) >> 8;
			rgb[2] = (integer & 0xff);
			System.out.println(rgb[0]+"-"+rgb[1]+"-"+rgb[2] + " ");
		}
		return whiteLine;
	}
	public static void main(String[] args) throws IOException {
		PageRemainder page = new PageRemainder();
		int line = page.detect(new File("/home/lilisheng/下载/1237905321.jpg"));
		System.out.println("line:" + line);
	}
}
