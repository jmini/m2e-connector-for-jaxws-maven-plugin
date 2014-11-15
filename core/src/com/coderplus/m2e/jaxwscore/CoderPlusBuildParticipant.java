package com.coderplus.m2e.jaxwscore;
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
import java.io.File;
import java.util.Set;

import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.Scanner;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.embedder.IMaven;
import org.eclipse.m2e.core.project.configurator.MojoExecutionBuildParticipant;
import org.sonatype.plexus.build.incremental.BuildContext;

public class CoderPlusBuildParticipant extends MojoExecutionBuildParticipant {

	private static final String STALE_FILE = "staleFile";
	private static final String WSDL_DIRECTORY = "wsdlDirectory";
	private static final String BINDING_DIRECTORY = "bindingDirectory";
	private static final String OUTPUT_DIRECTORY = "sourceDestDir";
	private static final String SKIP = "skip";
	private static final String XNOCOMPILE = "xnocompile";
	private static final String WSIMPORT_TEST = "wsimport-test";
	private static final String WSGEN_TEST = "wsgen-test";
	private static final String KEEP = "keep";

	public CoderPlusBuildParticipant(MojoExecution execution) {

		super(execution, true);
	}

	@Override
	public Set<IProject> build(final int kind, final IProgressMonitor monitor) throws Exception {


		final MojoExecution execution = getMojoExecution();

		if (execution == null) {
			return null;
		}
		IMaven maven = MavenPlugin.getMaven();	
		MavenProject mavenProject = getMavenProjectFacade().getMavenProject();
		final BuildContext buildContext = getBuildContext();
		boolean skip = Boolean.TRUE.equals(maven.getMojoParameterValue(mavenProject, execution, SKIP,Boolean.class, new NullProgressMonitor()));
		if(skip){
			return null;
		}
		File staleFile = maven.getMojoParameterValue(mavenProject, execution, STALE_FILE, File.class, new NullProgressMonitor());

		boolean xnocompile = Boolean.TRUE.equals(maven.getMojoParameterValue(mavenProject, execution, XNOCOMPILE,Boolean.class, new NullProgressMonitor()));
		boolean keep = Boolean.TRUE.equals(maven.getMojoParameterValue(mavenProject, execution, KEEP,Boolean.class, new NullProgressMonitor()));
		File outputDirectory = maven.getMojoParameterValue(mavenProject, execution, OUTPUT_DIRECTORY,File.class, new NullProgressMonitor());
		if((keep || xnocompile) && outputDirectory != null){
			if(WSIMPORT_TEST.equals(execution.getGoal()) || WSGEN_TEST.equals(execution.getGoal())){
				mavenProject.addTestCompileSourceRoot(outputDirectory.getAbsolutePath());

			} else {
				mavenProject.addCompileSourceRoot(outputDirectory.getAbsolutePath());
			}
		}

		if(buildContext.isIncremental() && staleFile!= null && staleFile.exists()){
			File wsdlDirectory = maven.getMojoParameterValue(mavenProject, execution, WSDL_DIRECTORY, File.class, new NullProgressMonitor());
			File bindingDirectory = maven.getMojoParameterValue(mavenProject, execution, BINDING_DIRECTORY, File.class, new NullProgressMonitor());
			Scanner wsdlScanner = buildContext.newScanner(wsdlDirectory); 
			Scanner bindingScanner = buildContext.newScanner(bindingDirectory); 
			String[] includedBindingFiles= null;
			String[] includedWsdlFiles  = null;
			if(bindingScanner!= null){
				bindingScanner.scan();
				includedBindingFiles = bindingScanner.getIncludedFiles();
			}
			if (wsdlScanner != null) {
				wsdlScanner.scan();
				includedWsdlFiles = wsdlScanner.getIncludedFiles();
			}

			if((includedWsdlFiles == null || includedWsdlFiles.length == 0 ) && (includedBindingFiles == null|| includedBindingFiles.length ==0) ){
				//ignore if there were no changes to the resources and was an incremental build
				return null;
			}
		}

		setTaskName(monitor);
		//execute the maven mojo
		final Set<IProject> result = executeMojo(kind, monitor);

		//refresh the output directory
		if(outputDirectory != null && outputDirectory.exists()){
			buildContext.refresh(outputDirectory);
		}
		return result;
	}

	private void setTaskName(IProgressMonitor monitor) throws CoreException {
		if (monitor != null) {
			final String taskName = String.format("CoderPlus M2E: Invoking %s on %s", getMojoExecution().getMojoDescriptor()
					.getFullGoalName(), getMavenProjectFacade().getProject().getName());
			monitor.setTaskName(taskName);
		}
	}

	private Set<IProject> executeMojo(final int kind, final IProgressMonitor monitor) throws Exception {

		return super.build(kind, monitor);
	}


}
