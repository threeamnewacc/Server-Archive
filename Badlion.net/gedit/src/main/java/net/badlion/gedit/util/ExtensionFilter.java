package net.badlion.gedit.util;

import java.io.File;
import java.io.FilenameFilter;

public class ExtensionFilter implements FilenameFilter {

    private final String extension;

    public ExtensionFilter(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(extension);
    }

}
