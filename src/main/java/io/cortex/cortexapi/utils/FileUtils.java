package io.cortex.cortexapi.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileUtils {

    //counts images for directory
    public static int countImage(String path) {
        int imagesCount = 0;
        File parent = new File(path);

        for (File child : parent.listFiles()) {
            for (File ignored : child.listFiles()) {
                imagesCount++;
            }
        }

        System.out.println("IMAGES COUNT" + imagesCount);
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

    public static double readDouble(String path) {
        double value = 0;

        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                value = scanner.nextDouble() + 0.00;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }

    public static String readString(String path) {
        String value = "";

        Scanner scanner;
        try {
            scanner = new Scanner(new File(path));
            while (scanner.hasNextLine()) {
                value = scanner.nextLine();
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return value;
    }
}
