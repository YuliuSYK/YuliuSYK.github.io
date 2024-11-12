pipeline {
    agent any

    environment {
        TOMCAT_URL = 'http://localhost:8878/'
        TOMCAT_USERNAME = ''
        TOMCAT_PASSWORD = ''
        APP_NAME = 'demo'
        EXPLoded_DIR = 'target/artifacts/demo_Web_exploded'
    }

    stages {
        stage('Clone') {
            steps {
                git url: 'https://github.com/YuliuSYK/YuliuSYK.github.io.git', branch: 'main'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package'  // 使用Maven打包项目
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([usernamePassword(credentialsId: 'tomcat-credentials-id', usernameVariable: 'TOMCAT_USERNAME', passwordVariable: 'TOMCAT_PASSWORD')]) {
                    deploy adapters: [tomcat9(
                            credentialsId: 'tomcat-credentials-id',
                            path: '',
                            url: "${env.TOMCAT_URL}",
                            username: "${env.TOMCAT_USERNAME}",
                            password: "${env.TOMCAT_PASSWORD}"
                    )] {
                        explodedWar file: "${env.EXPLoded_DIR}"
                        contextPath:
                        "${env.APP_NAME}"
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()  // 清理工作区
        }
    }
}