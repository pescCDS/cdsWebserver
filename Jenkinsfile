/**
 * This file facilitates an automated build and deployment pipeline using Jenkins
 * Job: http://jenkins.cccnext.net:8080/blue/organizations/jenkins/Multibranch/ed-exchange-open-pipeline/activity
 * Reference: https://jenkins.io/doc/book/pipeline/jenkinsfile/
 *
 * jenkinsLib is a shared library that contains the target environment information, and common build and deploy tasks
 * It is currently mapped to https://bitbucket.org/cccnext/jenkins-shared-library
 * The mapping is configured here (parent job folder for EdExchange): http://jenkins.cccnext.net:8080/view/EdExchange/job/Multibranch/
 * Shared Library Reference: https://github.com/jenkinsci/workflow-cps-global-lib-plugin
 **/

@Library('jenkinsLib') _

def channel = "#ed-exchange" //slack notification channel
def buildNode = "jenkins-old-build-1" //build on nodes matching label
def buildCommand = "mvn clean install"

Properties props
def environment
def profile
def IMAGE_TAG
// The build resource is automatically allotted by Jenkins.
// Name should be of the form project-env-resource
def lock_resource = ""

/**
 * An automated build + deploy occurs for CI, QA, and PILOT environments, based on the following branch name patterns
 **/

stage "build"
node(buildNode) {
    try {
        deleteDir()
        checkout scm
        currentBuild.displayName = "#${env.BUILD_NUMBER}-${env.BRANCH_NAME}"

        if (env.BRANCH_NAME =~ /feature/) {
                IMAGE_TAG = "${env.BUILD_NUMBER}"
                environment = "ci"
        }

        if(env.BRANCH_NAME == "develop"){
                IMAGE_TAG = "latest"
                environment = "qa"
        }
        if(env.BRANCH_NAME =~ /release/ || env.BRANCH_NAME == "master"){
            IMAGE_TAG = sh(returnStdout: true, script: "git ls-remote -q --refs --tags | awk -F'/' '{ print \$3 }' | grep '^[0-9].[0-9].[0-9]*\$' | sort --version-sort -r| head -n1").trim()
            if(IMAGE_TAG == '') { error "Was not able to parse a valid version tag from the remote repository" }
            environment = "pilot"
        }
        //FUTURE: add prod environment for pushes to master

        profile = environment
        env.profile = profile

        print "DEBUG: IMAGE_TAG: ${IMAGE_TAG}"
        print "DEBUG: environment: ${environment}"
        print "DEBUG: profile: ${profile}"

        props = ceEnv.getEnvProperties("ed-exchange", environment)

        env.DIRECTORY_DB_USERNAME = props."edex.directory.db.username"
        env.DIRECTORY_DB_PASSWORD = props."edex.directory.db.password"
        env.DIRECTORY_DB_HOST = props."edex.directory.db.dns"

        env.DIRECTORY_HOST = props."edex.directory.dns"
        env.DIRECTORY_PORT = props."edex.directory.port"
        env.DIRECTORY_URL = props."edex.directory.url"

        env.NETWORK_DB_USERNAME = props."edex.network.db.username"
        env.NETWORK_DB_PASSWORD = props."edex.network.db.password"
        env.NETWORK_DB_HOST = props."edex.network.db.dns"

        env.MAIL_SMTP_HOST = props."edex.mail.server.host"
        env.MAIL_SMTP_USERNAME = props."edex.mail.server.username"
        env.MAIL_SMTP_PASSWORD = props."edex.mail.server.password"

        env.environment = environment

        lock_resource = "ed-exchange-${environment}-rancher"

        ceBuild.setupEnv() //add mvn and java tool
        ceBuild.mvnBuild(buildCommand)

        step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/*.xml'])
        if (currentBuild.result == 'UNSTABLE') {
            slackSend channel: channel, color: 'warning', message: "#${env.BUILD_NUMBER}-${env.BRANCH_NAME} - Build had test failures \nJob: ${env.BUILD_URL}"
            error "Tests failed. Not updating image"
        }

        gitsha = ceBuild.getGitsha() // may be used to validate /info endpoints
        echo "DEBUG: gitsha: $gitsha"

        stage "publish"
        sh 'docker images'
        sh "docker tag edex/directory-server:${gitsha} ccctechcenter/cccnext-directory-server:${IMAGE_TAG}"
        sh "docker tag edex/network-server:${gitsha} ccctechcenter/cccnext-network-server:${IMAGE_TAG}"

        archive "networkServer/target/network-server.jar"
        archive "directoryServer/target/directory-server.jar"

        withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: 'ccctech-dockerhub-public-id', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
            try {
                retry (5) {
                    sh 'docker login -u "$USERNAME" -p "$PASSWORD"'
                }
                retry (5) {
                    sh "docker push ccctechcenter/cccnext-directory-server:${IMAGE_TAG}"
                    sh "docker push ccctechcenter/cccnext-network-server:${IMAGE_TAG}"
                    sleep 20
                }
            }
            catch (Exception e) {
                echo "ERROR: " + e.toString()
                error "Error pushing to docker hub"
            }
        }
    } catch (Exception e) {
        echo "ERROR: " + e.toString()
        slackSend channel: channel, color: 'danger', message: "#${env.BUILD_NUMBER}-${env.BRANCH_NAME} - Build and Publish Failed \nJob: ${env.BUILD_URL}"
        error "build and publish image failed"
    }
    slackSend channel: channel, color: 'good', message: "#${env.BUILD_NUMBER}-${env.BRANCH_NAME} - Build and Publish Succeeded \nJob: ${env.BUILD_URL}"

    lock(resource: lock_resource) {
        stage "deploy"

        try {
            ceDeploy.runDeploy("ed-exchange", "network-server", environment, IMAGE_TAG, props."edex.network.url", props."edex.network.port", props."edex.network.protocol", props."edex.network.health", props."rancher.key", props."rancher.pass", channel)
            ceDeploy.slackNotify(channel, "good", "Success", "network", environment ?: "(environment not set)",  props ? props."edex.network.url" + props."edex.network.health" : "(endpoint not set)", IMAGE_TAG)
        } catch (Exception | AssertionError e) {
            echo "ERROR: " + e.toString()
            slackSend channel: channel, color: 'danger', message: "#${env.BUILD_NUMBER}-${env.BRANCH_NAME} - Open Network Deploy Failed in " + environment.toUpperCase() + " Env: " + props."edex.network.url" + "\nJob: ${env.BUILD_URL}"
            error "deploy failed"
        }

        try {
            ceDeploy.runDeploy("ed-exchange", "directory-server", environment, IMAGE_TAG, props."edex.directory.url", props."edex.directory.port", props."edex.directory.protocol", props."edex.directory.health", props."rancher.key", props."rancher.pass", channel)
            ceDeploy.slackNotify(channel, "good", "Success", "directory", environment ?: "(environment not set)",  props ? props."edex.directory.url" + props."edex.directory.health" : "(endpoint not set)", IMAGE_TAG)
        } catch (Exception | AssertionError e) {
            echo "ERROR: " + e.toString()
            slackSend channel: channel, color: 'danger', message: "#${env.BUILD_NUMBER}-${env.BRANCH_NAME} - Open Directory Deploy Failed in " + environment.toUpperCase() + " Env: " + props."edex.directory.url" + "\nJob: ${env.BUILD_URL}"
            error "deploy failed"
        }
    }
}
