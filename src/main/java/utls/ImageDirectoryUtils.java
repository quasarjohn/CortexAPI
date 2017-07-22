package utls;

import java.io.File;
import java.util.ArrayList;

public class ImageDirectoryUtils {

    //counts images for directory
    public static int countImage(String path) {
        int imagesCount = 0;
        File parentFile = new File(path);
        for (File f : parentFile.listFiles()) {
            for (File image_folder : f.listFiles()) {
                for (String image : image_folder.list()) {
                    imagesCount++;
                }
            }
        }
        return imagesCount;
    }

    public static ArrayList<String> getLabelsList(String path) {
        ArrayList<String> labels = new ArrayList<>();
        File parent = new File(path);

        for (File child : parent.listFiles()) {
            labels.add(child.getName());
        }

        return labels;
    }
}
