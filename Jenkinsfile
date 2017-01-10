
import hudson.plugins.jacoco.model.CoverageGraphLayout
import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageType
import hudson.plugins.jacoco.model.CoverageGraphLayout.CoverageValue
import java.awt.Color

timestamps
{
	//noinspection GroovyAssignabilityCheck
	node
	{
		try
		{
			abortable
			{
				stage 'Checkout'
				checkout scm
				sh 'git rev-parse HEAD > GIT_COMMIT'
				env.GIT_COMMIT = readFile('GIT_COMMIT').trim()
				sh "git cat-file -p HEAD | grep '^tree ' | sed -e 's/^tree //' > GIT_TREE"
				env.GIT_TREE = readFile('GIT_TREE').trim()

				stage 'Config'
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

				stage 'Build'
				sh "${antHome}/bin/ant clean jenkins" +
						' "-Dbuild.revision=${BUILD_NUMBER}"' +
						' "-Dbuild.tag=git ${BRANCH_NAME} ${GIT_COMMIT} ${GIT_TREE} jenkins ${BUILD_NUMBER} ${BUILD_TIMESTAMP}"' +
						' -Dfindbugs.output=xml'

				stage 'Publish'
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
				step([$class: 'JacocoPublisher',
						changeBuildStatus: true,
						minimumBranchCoverage: '30',
						coverageGraphLayout:
								new CoverageGraphLayout()
								.baseStroke(2f)
								.axis().skipZero().crop(50)
								.plot().type(CoverageType.BRANCH).value(CoverageValue.PERCENTAGE).color(Color.BLUE)
								.axis().skipZero().crop()
								.plot().type(CoverageType.BRANCH).value(CoverageValue.MISSED).color(Color.RED)
								.axis().skipZero().crop()
								.plot().type(CoverageType.LINE).value(CoverageValue.MISSED).color(Color.ORANGE),
						execPattern: 'build/jacoco.exec',
						classPattern: 'build/classes/src/**',
						sourcePattern: 'src/**'])
				archive 'build/success/*'
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
