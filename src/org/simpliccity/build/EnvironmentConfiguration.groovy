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

package org.simpliccity.build;

import java.io.Serializable

class EnvironmentConfiguration implements Serializable
{
	private static final Map configurations = ['ICC': new EnvironmentConfiguration('Maven 3.3.9', 'JDK8', 'org.jenkinsci.plugins.configfiles.maven.MavenSettingsConfig1439846892901', 'JCite', '15')]
	
	String mvnVersion
	String jdkVersion
	String mvnSettings
	String jcite
	String jobPruneCount
	
	EnvironmentConfiguration(mvnVersion, jdkVersion, mvnSettings, jcite, jobPruneCount)
	{
		this.mvnVersion = mvnVersion
		this.jdkVersion = jdkVersion
		this.mvnSettings = mvnSettings
		this.jcite = jcite
		this.jobPruneCount = jobPruneCount
	}	

	static EnvironmentConfiguration getConfiguration(configName)
	{
		configurations[configName]
	}
}