package com.nicowoch.recipe_exporter.registers;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public class IconsRegistry extends RegisterBase<String> {

    public static final String NO_ITEM_IMAGE = "iVBORw0KGgoAAAANSUhEUgAAABAAAAAQBAMAAADt3eJSAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAAGUExURcsA/wAAAABNUUkAAAAJcEhZcwAADsMAAA7DAcdvqGQAAAAVSURBVBjTY2AAAkEgYKCQQQ0zgAAAMp8EQTkXHrEAAAAASUVORK5CYII=";

    @Override
    protected String getItemHash(String item) {
        return item;
    }

    @Override
    protected boolean compareItems(String a, String b) {
        return a.equals(b);
    }

    public int registerBase64Image(String base64) {
        return findOrAddItemToRegistry(base64);
    }

    public int registerBufferedImage(BufferedImage bufferedImage) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);

        baos.flush();
        byte[] imageBytes = baos.toByteArray();
        baos.close();

        String base64 = Base64.getEncoder().encodeToString(imageBytes);

        return registerBase64Image(base64);
    }

    public int registerNoImage() {
        return registerBase64Image(NO_ITEM_IMAGE);
    }
}
