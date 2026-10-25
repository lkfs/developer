package com.venusoft.developer.common.log;

import ch.qos.logback.core.rolling.RollingFileAppender;

import java.io.File;
import java.io.IOException;
import java.nio.file.attribute.PosixFilePermission;
import java.util.EnumSet;

/**
 * 设置日志文件的读写权限
 */
public class WorldWritableFileAppender extends RollingFileAppender {
    @Override
    public synchronized void setFile(String fileName) {
        super.setFile(fileName);
        File f = new File(fileName);
        if (f.exists()) {
            try {
                java.nio.file.Files.setPosixFilePermissions(f.toPath(), EnumSet.of(
                        PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE,
                        PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_WRITE,
                        PosixFilePermission.OTHERS_READ, PosixFilePermission.OTHERS_WRITE)
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
