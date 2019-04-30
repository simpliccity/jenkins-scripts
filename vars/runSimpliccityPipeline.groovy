/*
 *    Copyright 2016 Information Control Company
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.simpliccity.build.EnvironmentConfiguration
import cleanWorkspace
import jciteCommand
import runMvn

def call(String simpliccityProject, boolean cleanup=true)
{
	node
	{
		echo "Running Jenkins build pipeline for ${simpliccityProject}."

		def config = EnvironmentConfiguration.getConfiguration("${BUILD_ENVIRONMENT}")
		
		// Maven command line properties
		def jacocoProperty = "jacocoDirectory=\"${WORKSPACE}/jacoco\""
		def pomStagingProperty = "pomStagingDirectory=\"${WORKSPACE}/pomStaging\""
		def binaryStagingProperty = "binaryStagingDirectory=\"${WORKSPACE}/binaryStaging\""
		def docStagingProperty = "docStagingDirectory=\"${WORKSPACE}/staging\""
		def jciteCommandPath = jciteCommand config
		def jciteCommandProperty = "jciteCommandPath=\"${jciteCommandPath}\""
		def styleguideStagingProperty = "styleguideStagingDirectory=\"${WORKSPACE}/styleguideStaging\""
		def moduleDocProperty = "moduleDocDirectory=\"${WORKSPACE}/module-docs\""
		
		// Set properties to prune old build jobs
//		properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '20']]]);
		setBuildProperties config
	
		stage('Checkout')
		{
			checkout scm
		}
		
		stage('Build')
		{
			def buildCommandLine = "clean deploy -U -Pdist,int-test,testPlugin,analysis -Dmaven.test.failure.ignore -DdeployAtEnd=true -D${jacocoProperty} -D${pomStagingProperty} -D${binaryStagingProperty}"
			runMvn buildCommandLine, config
		}
	
		stage('Analyze')
		{
			def analyzeCommandLine = "sonar:sonar -U -Psonar -D${jacocoProperty}"
			runMvn analyzeCommandLine, config, true
		}
		
		stage('Package')
		{
			def packageCommandLine = "site-deploy -U -Pdist -D${jciteCommandProperty} -D${docStagingProperty} -D${pomStagingProperty} -D${binaryStagingProperty} -D${styleguideStagingProperty} -D${moduleDocProperty}"
			runMvn packageCommandLine, config, true
		}
		
		// Delete job workspace upon completion (based on setting of cleanup flag)
		// cleanWorkspace cleanup
	}
}
