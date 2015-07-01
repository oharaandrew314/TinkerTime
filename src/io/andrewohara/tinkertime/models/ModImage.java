package io.andrewohara.tinkertime.models;

import io.andrewohara.tinkertime.models.mod.Mod;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;

public class ModImage {

	public static final Dimension MAX_SIZE = new Dimension(250, 250);

	@DatabaseField(generatedId=true)
	private int id;

	@DatabaseField(foreign = true, canBeNull = false)
	private Mod mod;

	@DatabaseField(dataType = DataType.BYTE_ARRAY)
	private byte[] bytes;

	ModImage() { /* Required by ormlite */ }

	private ModImage(Mod mod, byte[] bytes){
		this.bytes = bytes;
		this.mod = mod;
	}

	public static ModImage createModImage(Mod mod, BufferedImage image) throws IOException{
		ModImage modImage = mod.getImage();
		if (modImage == null){
			modImage = new ModImage(mod, getBytes(image));
		} else {
			modImage.bytes = getBytes(image);
		}
		return modImage;
	}

	private static byte[] getBytes(BufferedImage image) throws IOException{
		ByteArrayOutputStream baos=new ByteArrayOutputStream();
		ImageIO.write(image, "jpg", baos);
		return baos.toByteArray();
	}

	public BufferedImage getImage(){
		try {
			return ImageIO.read(new ByteArrayInputStream(bytes));
		} catch (IOException e) {
			return null;
		}
	}

}
