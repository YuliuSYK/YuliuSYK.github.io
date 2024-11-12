pipeline {
    agent any

    environment {
        TOMCAT_URL = 'http://localhost:8878/'
        APP_NAME = 'demo'
        EXPLoded_DIR = 'target/artifacts/demo_Web_exploded'
        MAVEN_TARGET_DIR = 'target'
        WAR_FILE = "${MAVEN_TARGET_DIR}/demo.war"
    }

    stages {
        stage('Clone') {
            steps {
                script {
                    try {
                        git url: 'https://github.com/YuliuSYK/YuliuSYK.github.io.git', branch: 'main'
                    } catch (err) {
                        echo "Error cloning repository: ${err.message}"
                        currentBuild.result = 'FAILURE'
                        error("Failed to clone repository")
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    try {
                        sh "mvn clean package -Dmaven.test.skip=true -DoutputDirectory=${env.MAVEN_TARGET_DIR}"  // 使用Maven打包项目
                    } catch (err) {
                        echo "Error building project: ${err.message}"
                        currentBuild.result = 'FAILURE'
                        error("Failed to build project")
                    }
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    try {
                        withCredentials([usernamePassword(credentialsId: 'tomcat-credentials-id', usernameVariable: 'TOMCAT_USERNAME', passwordVariable: 'TOMCAT_PASSWORD')]) {
                            deploy adapters: [tomcat9(
                                    credentialsId: 'tomcat-credentials-id',
                                    path: '',
                                    url: "${env.TOMCAT_URL}",
                                    username: "${env.TOMCAT_USERNAME}",
                                    password: "${env.TOMCAT_PASSWORD}",
                                    war: "${env.WAR_FILE}"  // 添加 war 参数
                            )] {
                                explodedWar file: "${env.EXPLoded_DIR}"
                                contextPath: "${env.APP_NAME}"
                            }
                        }
                    } catch (err) {
                        echo "Error deploying application: ${err.message}"
                        currentBuild.result = 'FAILURE'
                        error("Failed to deploy application")
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
