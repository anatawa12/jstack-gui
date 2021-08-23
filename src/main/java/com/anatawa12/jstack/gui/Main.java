/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2021, anatawa12 and other contributors, All rights reserved.
 * This file is a part of jstack-gui which is under BSD 2-Clause "Simplified" License.
 */

package com.anatawa12.jstack.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import static java.awt.event.KeyEvent.CHAR_UNDEFINED;

public class Main extends Frame {
    // util to be launched by other program.
    File cachedExecutableToBeRemoved;

    // gui
    TextField pid;
    Button run;
    TextArea output;

    public Main() {
        setTitle("jstack gui");
        setResizable(false);
        setLocationRelativeTo(null);
        setSize(600, 300);
        setLayout(new FlowLayout());
        add(new Label("Pid: "));
        add(pid = new TextField());
        pid.setColumns(10);
        pid.addKeyListener(new NumberKeyFilter());
        pid.setText(String.valueOf(Util.getPid()));
        add(run = new Button("run jstack"));
        run.addActionListener(new RunButtonHandler());
        add(output = new TextArea());
        addWindowListener(new ExitWindowAdapter());
    }

    class ExitWindowAdapter extends WindowAdapter {
        public void windowClosing(WindowEvent e) {
            if (cachedExecutableToBeRemoved != null)
                //noinspection ResultOfMethodCallIgnored
                cachedExecutableToBeRemoved.delete();
            System.exit(0);
        }
    }

    class RunButtonHandler implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);

            try {
                Process process = new ProcessBuilder()
                        .command("jstack", pid.getText())
                        .redirectErrorStream(true)
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectInput(ProcessBuilder.Redirect.PIPE)
                        .start();
                process.getOutputStream().close();

                Reader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.defaultCharset()));
                char[] chars = new char[1024];
                CharBuffer buf = CharBuffer.wrap(chars);
                int read;
                while ((read = reader.read(chars)) != -1) {
                    printWriter.append(buf, 0, read);
                }

                process.waitFor();

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                printWriter.append(System.lineSeparator());
                printWriter.append(System.lineSeparator());
                printWriter.append("Exception running jstack.").append(System.lineSeparator());
                ex.printStackTrace(printWriter);
            }

            output.setText(stringWriter.toString());
        }
    }

    private static class NumberKeyFilter extends KeyAdapter {
        @Override
        public void keyTyped(KeyEvent e) {
            if (e.getKeyChar() == CHAR_UNDEFINED) return;
            if ('0' <= e.getKeyChar() && e.getKeyChar() <= '9') return;
            e.consume();
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        if (args.length >= 1)
            main.pid.setText(args[0]);
        String cachedExecutable = System.getProperty(Util.CACHED_EXECUTABLE_PROP);
        if (cachedExecutable != null)
            main.cachedExecutableToBeRemoved = new File(cachedExecutable);
        main.setVisible(true);
    }
}
