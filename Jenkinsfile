
timestamps
{
	def jdk = 'openjdk-8-deb9'
	def isRelease = env.BRANCH_NAME.toString().equals("master")

	properties([
			buildDiscarder(logRotator(
					numToKeepStr         : isRelease ? '1000' : '30',
					artifactNumToKeepStr : isRelease ? '1000' :  '2'
			))
	])

	//noinspection GroovyAssignabilityCheck
	node(jdk)
	{
		try
		{
			abortable
			{
				echo("Delete working dir before build")
				deleteDir()

				def buildTag = makeBuildTag(checkout(scm))

				env.JAVA_HOME = tool type: 'jdk', name: jdk
				env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
				def antHome = tool 'Ant version 1.9.3'

				sh "java -version"
				sh "${antHome}/bin/ant -version"

				sh "${antHome}/bin/ant -noinput clean jenkins" +
						' "-Dbuild.revision=${BUILD_NUMBER}"' +
						' "-Dbuild.tag=' + buildTag + '"' +
						' -Dbuild.status=' + (isRelease?'release':'integration') +
						' -Dtomcat.port.shutdown=' + port(0) +
						' -Dtomcat.port.http='     + port(1) +
						' -Dtomcat.port.https='    + port(2) +
						' -Dfindbugs.output=xml'

				recordIssues(
						failOnError: true,
						enabledForFailure: true,
						ignoreFailedBuilds: false,
						qualityGates: [[threshold: 1, type: 'TOTAL', unstable: true]],
						tools: [
							java(),
							spotBugs(pattern: 'build/findbugs.xml', useRankAsPriority: true),
						],
				)
				archiveArtifacts 'build/catalina-start.log,build/success/*'
				plot(
						csvFileName: 'plots.csv',
						exclZero: false,
						keepRecords: false,
						group: 'Sizes',
						title: 'exedio-cops.jar',
						numBuilds: '1000',
						style: 'line',
						useDescr: false,
						propertiesSeries: [
							[ file: 'build/exedio-cops.jar-plot.properties',     label: 'exedio-cops.jar' ],
							[ file: 'build/exedio-cops-src.zip-plot.properties', label: 'exedio-cops-src.zip' ],
						],
				)
			}
		}
		catch(Exception e)
		{
			//todo handle script returned exit code 143
			throw e
		}
		finally
		{
			// because junit failure aborts ant
			junit(
					allowEmptyResults: false,
					testResults: 'build/testresults/*.xml',
			)
			def to = emailextrecipients([culprits(), requestor()])
			//TODO details
			step([$class: 'Mailer',
					recipients: to,
					attachLog: true,
					notifyEveryUnstableBuild: true])

			echo("Delete working dir after build")
			deleteDir()
		}
	}
}

def abortable(Closure body)
{
	try
	{
		body.call()
	}
	catch(hudson.AbortException e)
	{
		if(e.getMessage().contains("exit code 143"))
			return
		throw e
	}
}

def makeBuildTag(scmResult)
{
	return 'build ' +
			env.BRANCH_NAME + ' ' +
			env.BUILD_NUMBER + ' ' +
			new Date().format("yyyy-MM-dd") + ' ' +
			scmResult.GIT_COMMIT + ' ' +
			sh (script: "git cat-file -p " + scmResult.GIT_COMMIT + " | grep '^tree ' | sed -e 's/^tree //'", returnStdout: true).trim()
}

def port(int offset)
{
	return 28000 + 10*env.EXECUTOR_NUMBER.toInteger() + offset
}
