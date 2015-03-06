package com.wolf.framework.fileupload;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author jianying9
 */
public class UploadFileJUnitTest {

    public UploadFileJUnitTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }
    //

//    @Test
    public void resizeTest() throws IOException {
        File uploadFile = new File("/home/jianying9/Pictures/reception.jpg");
        BufferedImage imageBuff = ImageIO.read(uploadFile);
        BufferedImage thumb = UploadFileManager.MANAGER.resize(imageBuff, 50, 100);
        File outFile = new File("/home/jianying9/Pictures/aaaaaa.jpg");
        if(outFile.exists()) {
            outFile.delete();
        }
        ImageIO.write(thumb, "jpg", outFile);
    }
}