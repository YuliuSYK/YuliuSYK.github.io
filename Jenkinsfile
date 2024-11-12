pipeline {
    agent any

    environment {
        TOMCAT_URL = 'http://localhost:8878/'
        TOMCAT_USERNAME = ''
        TOMCAT_PASSWORD = ''
        APP_NAME = ''  // 这个名字将作为Tomcat中的上下文路径
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
                deploy adapter: tomcat9(
                    credentialsId: 'tomcat-credentials-id',  // Jenkins中Tomcat凭证的ID
                    path: '',
                    url: "${env.TOMCAT_URL}",
                    username: "${env.TOMCAT_USERNAME}",
                    password: "${env.TOMCAT_PASSWORD}"
                ) {
                    war file: 'target/your-app-name.war'  // 替换为你的war文件名
                    contextPath: "${env.APP_NAME}"
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