/*******************************************************************************
 * Copyright (c) 2014 Aneesh Joseph
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Aneesh Joseph(coderplus.com)
 *******************************************************************************/
package com.coderplus.m2e.jaxwscore;

import org.apache.maven.plugin.MojoExecution;
import org.eclipse.m2e.core.lifecyclemapping.model.IPluginExecutionMetadata;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.configurator.AbstractBuildParticipant;
import org.eclipse.m2e.jdt.AbstractJavaProjectConfigurator;

@SuppressWarnings("deprecation")
public class CoderPlusProjectConfigurator extends AbstractJavaProjectConfigurator {

    private static final String SOURCE_DEST_DIR = "sourceDestDir";

    @Override
    public AbstractBuildParticipant getBuildParticipant(IMavenProjectFacade projectFacade, MojoExecution execution,IPluginExecutionMetadata executionMetadata) {
	return new CoderPlusBuildParticipant(execution);

    }

    @Override
    protected String getOutputFolderParameterName() {
        return SOURCE_DEST_DIR;
    }

}