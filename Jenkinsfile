pipeline {
    agent any
    environment {
        NAME = 'osgi-demo'
    }
    stages {
        stage('build') {
            environment {
                JFROG_PASSWORD = credentials('artifactory-password')
                JFROG_USERNAME = credentials('artifactory-username')
                JENKINS_GITLAB_TOKEN = credentials('gitlab_jenkins_token')
                JFROG = credentials('osgi-demo-release')
                GRADLE_OPTS = "-Dorg.gradle.daemon=false"
                _CONTAINER_REGISTRY = "hub.klib.io"
            }
            steps {
                sh 'cat /etc/passwd'
                sh 'ls -lar .'
                sh 'pwd'
                sh 'chmod +x 01_buildAndPublish.sh'
                sh './01_buildAndPublish.sh'
            }
        }
    }
    post {
        always {
            cleanWs(cleanWhenNotBuilt: false,
                    deleteDirs: true,
                    disableDeferredWipeout: true,
                    notFailBuild: true,
                    patterns: [[pattern: '.gitignore', type: 'INCLUDE'],
                               [pattern: '.propsfile', type: 'EXCLUDE']])
        }
    }
}
