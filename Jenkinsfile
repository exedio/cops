
timestamps
{
	//noinspection GroovyAssignabilityCheck
	node
	{
		try
		{
			abortable
			{
				echo("Delete working dir before build")
				deleteDir()

				checkout scm
				sh 'git rev-parse HEAD > GIT_COMMIT'
				env.GIT_COMMIT = readFile('GIT_COMMIT').trim()
				sh "git cat-file -p HEAD | grep '^tree ' | sed -e 's/^tree //' > GIT_TREE"
				env.GIT_TREE = readFile('GIT_TREE').trim()

				env.BUILD_TIMESTAMP = new Date().format("yyyy-MM-dd_HH-mm-ss");
				env.JAVA_HOME = "${tool 'jdk 1.8.0_60'}"
				env.PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
				def antHome = tool 'Ant version 1.9.3'

				sh "java -version"
				sh "${antHome}/bin/ant -version"

				def isRelease = env.BRANCH_NAME.toString().equals("master");

				properties([[$class: 'jenkins.model.BuildDiscarderProperty',
						strategy: [
								$class               : 'LogRotator',
								numToKeepStr         : isRelease ? '1000' : '15',
								artifactNumToKeepStr : isRelease ? '1000' :  '2' ]]])

				sh 'echo' +
						' GIT_COMMIT -${GIT_COMMIT}-' +
						' GIT_TREE -${GIT_TREE}-' +
						' BUILD_TIMESTAMP -${BUILD_TIMESTAMP}-' +
						' BRANCH_NAME -${BRANCH_NAME}-' +
						' BUILD_NUMBER -${BUILD_NUMBER}-' +
						' BUILD_ID -${BUILD_ID}-' +
						' isRelease=' + isRelease

				sh "${antHome}/bin/ant clean jenkins" +
						' "-Dbuild.revision=${BUILD_NUMBER}"' +
						' "-Dbuild.tag=git ${BRANCH_NAME} ${GIT_COMMIT} ${GIT_TREE} jenkins ${BUILD_NUMBER} ${BUILD_TIMESTAMP}"' +
						' -Dtomcat.port.shutdown=' + port(0) +
						' -Dtomcat.port.http='     + port(1) +
						' -Dtomcat.port.https='    + port(2) +
						' -Dfindbugs.output=xml'

				step([$class: 'WarningsPublisher',
						canComputeNew: true,
						canResolveRelativePaths: true,
						consoleParsers: [[parserName: 'Java Compiler (javac)']],
						defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '', messagesPattern: '', unHealthy: '',
						unstableTotalAll: '0',
						usePreviousBuildAsReference: false,
						useStableBuildAsReference: false])
				step([$class: 'FindBugsPublisher',
						canComputeNew: true,
						defaultEncoding: '', excludePattern: '', healthy: '', includePattern: '',
						isRankActivated: false,
						pattern: 'build/findbugs.xml',
						unHealthy: '',
						unstableTotalAll: '0',
						usePreviousBuildAsReference: false,
						useStableBuildAsReference: false])
				archive 'build/catalina-start.log,build/success/*'
			}
		}
		catch(Exception e)
		{
			//todo handle script returned exit code 143
			throw e;
		}
		finally
		{
			// because junit failure aborts ant
			step([$class: 'JUnitResultArchiver',
					allowEmptyResults: false,
					testResults: 'build/testresults/*.xml'])

			def to = emailextrecipients([
					[$class: 'CulpritsRecipientProvider'],
					[$class: 'RequesterRecipientProvider']
			])
			//TODO details
			step([$class: 'Mailer',
					recipients: to,
					attachLog: true,
					notifyEveryUnstableBuild: true])

			if('SUCCESS'.equals(currentBuild.result) ||
				'UNSTABLE'.equals(currentBuild.result))
			{
				echo("Delete working dir after " + currentBuild.result)
				deleteDir()
			}
		}
	}
}

def abortable(Closure body)
{
	try
	{
		body.call();
	}
	catch(hudson.AbortException e)
	{
		if(e.getMessage().contains("exit code 143"))
			return
		throw e;
	}
}

def port(int offset)
{
	return 28000 + 10*env.EXECUTOR_NUMBER.toInteger() + offset
}
