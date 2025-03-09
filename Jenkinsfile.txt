pipeline {
    agent any

    stages {
        stage('SCM') {
            steps {
                echo 'Getting code from the Git repository'
                git branch: 'main', url: 'https://github.com/sandeep4358/productManagemnt.git'
            }
        }

         stage('Maven Build'){
                        steps{
                            sh 'mvn clean install -DskipTests'
                        }
                    }

        stage('Docker Build') {
            steps {
                echo 'Starting Docker Build'
                script {
                    withDockerRegistry(credentialsId: 'a77c722e-a2ea-45c3-b4e3-6100d91bcb67') {
                        sh 'docker build --no-cache -t sandeep022/practice-product:${BUILD_NUMBER} .'
                        // Uncomment if you want to push the image
                        // retry(3) {
                        //     sh 'docker push sandeep022/practice-product:${BUILD_NUMBER}'
                        // }
                    }
                }
            }
        }
    }

    post {
        failure {
            emailext(
                subject: "FAILED: Pipeline ${BUILD_TAG} - Build ${BUILD_NUMBER}",
                body: """<html>
                            <body>
                                <p><b>Build FAILED!</b></p>
                                <p>Build Number: ${BUILD_NUMBER}</p>
                                <p>Check the <a href="${BUILD_URL}">console output</a>.</p>
                            </body>
                        </html>""",
                to: 'sandy.msit@gmail.com',
                from: 'sandy.msit@gmail.com',
                replyTo: 'freelanceratsany@gmail.com',
                mimeType: 'text/html'
            )
        }

        success {
            emailext(
                subject: "SUCCESS: Pipeline ${BUILD_TAG} - Build ${BUILD_NUMBER}",
                body: """<html>
                            <body>
                                <p><b>Build SUCCESSFUL!</b></p>
                                <p>Build Number: ${BUILD_NUMBER}</p>
                                <p>Check the <a href="${BUILD_URL}">console output</a>.</p>
                            </body>
                        </html>""",
                to: 'sandy.msit@gmail.com',
                from: 'sandy.msit@gmail.com',
                replyTo: 'freelanceratsany@gmail.com',
                mimeType: 'text/html'
            )
        }
    }
}
