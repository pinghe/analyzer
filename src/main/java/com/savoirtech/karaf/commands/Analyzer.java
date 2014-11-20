/*
 * Analyzer 
 *
 * Copyright (c) 2014, Savoir Technologies, Inc., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.savoirtech.karaf.commands;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.Repository;
import org.apache.karaf.features.command.FeaturesCommandSupport;

@Command(scope = "aetos", name = "analyzer", description = "Karaf feature descriptor analyzer Command")
public class Analyzer extends FeaturesCommandSupport {

    @Argument(index = 0, name = "feature", description = "The name of the Karaf feature", required = true, multiValued = false)
    private String name;

    @Option(name = "-s", aliases = { "--system" }, description = "Are all this feature's dependencies in the system repo?", required = false, multiValued = false)
    private boolean systemRepo;

    protected void doExecute(FeaturesService admin) throws Exception {
        try {
            // do stuff
            System.out.println("Analyzing feature: " + name);
        } catch (Exception e) {
            //Ignore
        }
    }
}
