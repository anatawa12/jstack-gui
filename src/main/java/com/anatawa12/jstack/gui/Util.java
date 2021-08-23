/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2021, anatawa12 and other contributors, All rights reserved.
 * This file is a part of jstack-gui which is under BSD 2-Clause "Simplified" License.
 */

package com.anatawa12.jstack.gui;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Util {
    static final String CACHED_EXECUTABLE_PROP = "com.anatawa12.jstack.gui.cached-executable-jar";

    static class JavaRuntimeComputer {
        static final String value;
        static {
            String java;
            try {
                String os = System.getProperty("os.name");
                java = System.getProperty("java.home") + File.separator + "bin" + File.separator +
                        (os != null && os.toLowerCase(Locale.ROOT).startsWith("windows") ? "java.exe" : "java");
                if (!new File(java).isFile()) {
                    java = null;
                }
            } catch (Throwable e) {
                e.printStackTrace();
                java = null;
            }
            value = java;
        }
    }

    static String getJavaRuntime() {
        if (JavaRuntimeComputer.value == null)
            throw new IllegalStateException("Unable to find suitable java runtime.");
        else
            return JavaRuntimeComputer.value;
    }

    static class PidComputer {
        static final long value;
        static {
            long pid;
            try {
                String os = ManagementFactory.getRuntimeMXBean().getName();
                String data = os.substring(0, os.indexOf('@'));
                pid = Long.parseLong(data);
            } catch (Throwable e) {
                e.printStackTrace();
                pid = -1;
            }
            value = pid;
        }
    }

    static long getPid() {
        if (PidComputer.value == -1)
            throw new IllegalStateException("Unable to find process id.");
        else
            return PidComputer.value;
    }

    static class ThisJarLocationComputer {
        static final URL value = compute();

        static URL compute() {
            String classFilePath = Util.class.getName().replace('.', '/') + ".class";
            URL classLocation = Util.class.getClassLoader().getResource(classFilePath);
            // unexpected error: Util.class not found
            if (classLocation == null) return null;

            String locationUrl = classLocation.toString();

            if (locationUrl.startsWith("jar:") && locationUrl.endsWith("!/" + classFilePath)) {
                // if "jar:<other-url>!/<classFilePath>", remove "jar:" and "!/<classFilePath>"
                locationUrl = locationUrl.substring("jar:".length(), 
                        locationUrl.length() - ("!/" + classFilePath).length());
            } else {
                // otherwise, remove classFilePath
                locationUrl = locationUrl.substring(0, locationUrl.length() - classFilePath.length());
            }

            try {
                return new URL(locationUrl);
            } catch (MalformedURLException e) {
                //parsing url failed
                return null;
            }
        }
    }

    static URL getThisJarLocation() {
        if (ThisJarLocationComputer.value == null)
            throw new IllegalStateException("Unable to find where this app is in.");
        else
            return ThisJarLocationComputer.value;
    }

    static File getThisJarLocationAsFile(Runnable calledOnCopy) throws IOException {
        URL url = getThisJarLocation();
        if (url.toString().endsWith("/")) {
            // it's directory classpath. if it's not file url, failure.
            try {
                return new File(url.toURI());
            } catch (URISyntaxException e) {
                throw new IllegalStateException("directory classpath on non-file protocol not supported.", e);
            }
        } else {
            // it's jar file classpath. get as file.
            return getAsFile(url, calledOnCopy);
        }
    }

    static File getAsFile(URL url, Runnable calledOnCopy) throws IOException {
        // if it's possible to convert, use it.
        try {
            return new File(url.toURI());
        } catch (Throwable ignored) {
        }

        if (calledOnCopy != null) calledOnCopy.run();
        File copiedFile = File.createTempFile("tools-jar-for-jstack-gui", ".jar");
        // if not, download and copy.
        try (
                InputStream inputStream = url.openStream();
                OutputStream outputStream = new FileOutputStream(copiedFile);
        ) {
            byte[] buf = new byte[1024 * 2];
            int read;
            while ((read = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, read);
            }
            return copiedFile;
        }
    }

    static ProcessBuilder launchJStackGuiForThisOnOtherProcessViaThisEnv() throws IOException {
        CopiedCallBack back = new CopiedCallBack();
        File location = getThisJarLocationAsFile(back);
        long pid = getPid();

        List<String> c = new ArrayList<>();
        c.add(Util.getJavaRuntime());
        c.add("-cp");
        c.add(location.toString());

        if (back.copied) {
            c.add("-D" + CACHED_EXECUTABLE_PROP + "=" + location);
        }

        c.add(Main.class.getName());
        c.add(String.valueOf(pid));

        return new ProcessBuilder().command(c).inheritIO();
    }

    static class CopiedCallBack implements Runnable {
        boolean copied = false;

        @Override
        public void run() {
            copied = true;
        }
    }
}
