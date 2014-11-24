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

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Map;

import org.apache.karaf.shell.commands.Argument;
import org.apache.karaf.shell.commands.Command;
import org.apache.karaf.shell.commands.Option;

import org.apache.karaf.features.BundleInfo;
import org.apache.karaf.features.Conditional;
import org.apache.karaf.features.Dependency;
import org.apache.karaf.features.Feature;
import org.apache.karaf.features.FeaturesService;
import org.apache.karaf.features.Repository;
import org.apache.karaf.features.command.FeaturesCommandSupport;

@Command(scope = "aetos", name = "analyzer", description = "Karaf feature descriptor analyzer Command")
public class Analyzer extends FeaturesCommandSupport {

    @Argument(index = 0, name = "feature", description = "The name of the Karaf feature", required = true, multiValued = false)
    private String name;

    @Argument(index = 1, name = "version", description = "The version of the feature", required = false, multiValued = false)
    private String version;

    @Option(name = "-s", aliases = { "--system" }, description = "Are all this feature's dependencies in the system repo?", required = false, multiValued = false)
    private boolean systemRepo;

    @Option(name = "-v", aliases = { "--verbose" }, description = "Verbose output", required = false, multiValued = false)
    private boolean verbose;

    protected void doExecute(FeaturesService admin) throws Exception {
        try {
            Feature feature = null;
  
            if (version != null && version.length() > 0) {
                feature = admin.getFeature(name, version);
            } else {
                feature = admin.getFeature(name);
            }

            if (feature == null) {
                System.out.println("Feature not found");
                return;
            }

            int unresolved = displayFeatureTree(admin, feature.getName(), feature.getVersion(), "");
            if (unresolved > 0) {
                System.out.println("Tree contains " + unresolved + " unresolved dependencies");
                System.out.println(" * means that node declares dependency but the dependent feature is not available.");
            }

        } catch (Exception e) {
            //Ignore
        }
    }
  
    private int displayFeatureTree(FeaturesService admin, String featureName, String featureVersion, String prefix) throws Exception {

        /**
         * If system == true, force only system repo usage? Every time not resolved, then not in system?
         */

        int unresolved = 0;

        Feature resolved = admin.getFeature(featureName, featureVersion);

        if (resolved != null) {
            System.out.println(prefix + " " + resolved.getName() + " " + resolved.getVersion());
        } else {
            System.out.println(prefix + " " + featureName + " " + featureVersion + " *");
            unresolved++;
        }

        if (resolved != null) {
            List<String> bundleLocation = new LinkedList<String>();
            List<BundleInfo> bundles = resolved.getBundles();

            for (BundleInfo bundleInfo : bundles) {
                bundleLocation.add(bundleInfo.getLocation());
            }

            for (Conditional cond : resolved.getConditional()) {
                List<? extends Dependency> condition = cond.getCondition();
                List<BundleInfo> conditionalBundles = cond.getBundles();
                for (BundleInfo bundleInfo : conditionalBundles) {
                    bundleLocation.add(bundleInfo.getLocation() + "(condition:"+condition+")");
                }
            }

            for (int i = 0, j = bundleLocation.size(); i < j; i++) {
                if (systemRepo) {
                    String local = convertToLocal(bundleLocation.get(i));
                    if (!isInLocal(local)) {
                        System.out.println("Could not find " + bundleLocation.get(i)  + " in system repo.");
                    } else {
                        System.out.println("Found " + bundleLocation.get(i)  + " in system repo.");
                    }
                }
            }

            prefix += "   ";
            List<Dependency> dependencies = resolved.getDependencies();
            for (int i = 0, j = dependencies.size(); i < j; i++) {
                Dependency toDisplay =  dependencies.get(i);
                unresolved += displayFeatureTree(admin, toDisplay.getName(), toDisplay.getVersion(), prefix +1);
            }

        }

        return unresolved;
    }

    public String convertToLocal(String location) {
        String base = System.getProperty("karaf.home") + System.getProperty("file.separator") + System.getProperty("karaf.default.repository");
        String[] url = location.split(":");
        String[] parts = url[1].split("/"); 
        base = base + System.getProperty("file.separator") + parts[0].replaceAll("\\.", System.getProperty("file.separator"));
        String fileName = System.getProperty("file.separator") + parts[1] + "-" + parts[2] + ".jar";
        base = base + System.getProperty("file.separator") + parts[1] + System.getProperty("file.separator") + parts[2] + fileName;
        return base;
    }

    public boolean isInLocal(String location) {
        if (verbose) { System.out.println("Checking: " + location); }
        File f = new File(location);
        return f.exists();
    }

}
