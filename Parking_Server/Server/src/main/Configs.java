package main;

import java.io.File;

/**
 * Holds public configurations and static variables of the project.
 * */
public class Configs {
    public static final File ROOT_PATH = new File(System.getProperty("user.dir")); // Points to the Server root
    public static final File DATA_PATH = new File(ROOT_PATH, "data");
    public static final File PARK_IMAGES_PATH = new File(DATA_PATH, "parks");

    public static final boolean DEBUG = true;

}
