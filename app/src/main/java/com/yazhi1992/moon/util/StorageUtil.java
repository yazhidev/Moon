package com.yazhi1992.moon.util;

import android.os.Environment;
import android.support.annotation.IntDef;

import com.yazhi1992.moon.BaseApplication;

import java.io.File;

/**
 * Created by zengyazhi on 2018/2/18.
 */

public class StorageUtil {

    public static String getPath(DirectoryName name) {
        String path = Environment.getExternalStorageDirectory().getPath()
                + "/" + BaseApplication.getInstance().getPackageName()
                + "/" + name.getPath();
        File file = new File(path);
        if(!file.exists()) {
            file.mkdirs();
        }
        return path;
    }

    public enum DirectoryName {
        IMAGE_DIRECTORY_NAME("image/"),
        ;

        private String path;

        public String getPath() {
            return path;
        }

        private DirectoryName(String path) {
            this.path = path;
        }
    }
}
