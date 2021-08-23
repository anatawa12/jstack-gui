/*
 * SPDX-License-Identifier: BSD-2-Clause
 * Copyright (c) 2021, anatawa12 and other contributors, All rights reserved.
 * This file is a part of jstack-gui which is under BSD 2-Clause "Simplified" License.
 */

package com.anatawa12.jstack.gui;

import cpw.mods.modlauncher.api.IEnvironment;
import cpw.mods.modlauncher.api.ITransformationService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class TransformationService implements ITransformationService {
    static {
        try {
            Util.launchJStackGuiForThisOnOtherProcessViaThisEnv().start();
        } catch (IOException e) {
            throw new IllegalStateException("failed to launch jstack-gui", e);
        }
    }

    @Override
    public String name() {
        return "jstack-gui";
    }

    @Override
    public void initialize(IEnvironment environment) {

    }

    @Override
    public void onLoad(IEnvironment env, Set<String> otherServices) {

    }

    @Override
    public List<?> transformers() {
        return Collections.emptyList();
    }
}
