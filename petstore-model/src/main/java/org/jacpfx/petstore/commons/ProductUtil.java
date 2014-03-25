package org.jacpfx.petstore.commons;

/**
 * @author Patrick Symmangk (pete.jacp@gmail.com)
 */
public class ProductUtil {


    private static final String FILE_PATTERN = "%s.png";
    private static final String RELATIVE_FILE_PATH = "%s%s";
    public static final String DEFAULT_IMAGE = "default.png";
    public static final String FILE_PATH = "/images/products/";

    public static String createProductImage(final String productName) {
        return String.format(FILE_PATTERN, productName);
    }



    public static String getProductImageURL(final String fileName) {
        return String.format(RELATIVE_FILE_PATH, FILE_PATH, fileName);
    }

}
