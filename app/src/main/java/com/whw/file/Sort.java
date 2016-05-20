package com.whw.file;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.Comparator;

/**
 * Created by wuhaiwen on 2016/4/20.
 */
public class Sort implements Comparator {


    private String action;


    public Sort() {

    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getAction()

    {
        return action;
    }

    @Override
    public int compare(Object lhs, Object rhs) {
        File f1 = (File) lhs;
        File f2 = (File) rhs;
        if (action != null) {
            if (action.equals("name")) {
                return sortByName(f1, f2);
            }
            if (action.equals("folder")) {
                if (f1.isDirectory()) {
                    if (f2.isDirectory()) {
                        return sortByName(f1, f2);
                    }
                    if (f2.isFile()) {
                        return -1;
                    }
                } else if (f1.isFile()) {
                    if (f2.isFile()) {
                        return sortByName(f1, f2);
                    }
                    if (f2.isDirectory()) {
                        return 1;
                    }
                }
            }
            if (action.equals("desc")) {
                double size1 = f1.length();
                double size2 = f2.length();
                if (f1.isFile()) {
                    if (f2.isFile()) {
                        return size1 > size2 ? 1 : -1;
                    } else {
                        return -1;
                    }
                } else {
                    if (f2.isFile()) {
                        return 1;
                    }
                }
            }
            if (action.equals("asc")) {
                double size1 = f1.length();
                double size2 = f2.length();
                if (f1.isFile()) {
                    if (f2.isFile()) {
                        return size1 > size2 ? -1 : 1;
                    } else {
                        return -1;
                    }
                } else {
                    if (f2.isFile()) {
                        return 1;
                    }
                }
            }
            if (action.equals("lastModify")) {
                long time1 = f1.lastModified();
                long time2 = f2.lastModified();
                return time1 > time2 ? -1 : 1;
            }
        }
        return 0;
    }

    private int sortByName(File f1, File f2) {
        char[] c1 = f1.getName().toCharArray();
        char[] c2 = f2.getName().toCharArray();
        for (int i = 0; i < (c1.length > c2.length ? c2.length : c1.length); i++) {
            if (c1[i] > c2[i]) {
                return 1;
            }
            if (c1[i] < c2[i]) {
                return -1;
            }
        }
        return c1.length > c2.length ? 1 : -1;
    }
}
