/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2021, anatawa12 and other contributors, All rights reserved.
 * This file is a part of jstack-gui which is under BSD 2-Clause "Simplified" License.
 */

package com.anatawa12.jstack.gui;

import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

@SuppressWarnings("unused")
public class Tweaker implements ITweaker {
    static {
        try {
            Util.launchJStackGuiForThisOnOtherProcessViaThisEnv().start();
        } catch (IOException e) {
            throw new IllegalStateException("failed to launch jstack-gui", e);
        }
    }

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
