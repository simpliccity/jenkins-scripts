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
				
	pipeline
	{
		agent any
		
		stages
		{
			
			// Set properties to prune old build jobs
	//		properties([[$class: 'BuildDiscarderProperty', strategy: [$class: 'LogRotator', artifactDaysToKeepStr: '', artifactNumToKeepStr: '', daysToKeepStr: '', numToKeepStr: '20']]]);
	//		setBuildProperties config
		
			stage('Setup')
			{
				echo "Running Jenkins build pipeline for ${simpliccityProject}."
				
				checkout scm
			}
			
			stage('Build')
			{
				def config = EnvironmentConfiguration.getConfiguration("${env.BUILD_ENVIRONMENT}")
				def buildCommandLine = "clean deploy -U -Pint-test,testPlugin,analysis -Dmaven.test.failure.ignore -DjacocoDirectory=\"${env.WORKSPACE}/jacoco\""
				runMvn buildCommandLine, config
				junit testResults:'**/target/surefire-reports/TEST-*.xml'
			}
		
			stage('Analyze')
			{
				def config = EnvironmentConfiguration.getConfiguration("${env.BUILD_ENVIRONMENT}")
				def analyzeCommandLine = "sonar:sonar -U -Psonar -DjacocoDirectory=\"${env.WORKSPACE}/jacoco\""
				runMvn analyzeCommandLine, config
			}
			
			stage('Package')
			{
				def config = EnvironmentConfiguration.getConfiguration("${env.BUILD_ENVIRONMENT}")
				def jciteCommandPath = jciteCommand config
				def packageCommandLine = "deploy site-deploy -U -Pdist,distOnly -DskipTests=true -DjciteCommandPath=\"${jciteCommandPath}\""
				runMvn packageCommandLine, config
			}
			
			// Delete job workspace upon completion (based on setting of cleanup flag)
	//		cleanWorkspace cleanup
		}
	}
}
