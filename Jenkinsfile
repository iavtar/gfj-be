pipeline {
    agent any

    environment {
        EC2_INSTANCE_IP = '13.203.132.105'
        EC2_INSTANCE_USER = 'ec2-user'
        JAR_NAME = 'gems-of-jaipur.jar'
        DEPLOY_PATH = '/home/ec2-user'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests=true'
            }
        }
		
        stage('Deploy') {
            steps {
                sshagent(credentials: ['ec2-creds']) {
                    sh """
                        scp target/${JAR_NAME} ${EC2_INSTANCE_USER}@${EC2_INSTANCE_IP}:${DEPLOY_PATH}
						
                        scp start-gfj.sh ${EC2_INSTANCE_USER}@${EC2_INSTANCE_IP}:${DEPLOY_PATH}/                    
                        
                        # Connect to the EC2 instance via SSH to run deployment commands.
                        ssh -o StrictHostKeyChecking=no ${EC2_INSTANCE_USER}@${EC2_INSTANCE_IP} <<EOF
                            echo "Stopping any existing application process..."
                            pkill -f "${JAR_NAME}" || true
                            
                            # Give the script execute permissions if it doesn't have them.
                            chmod +x ${DEPLOY_PATH}/start-gfj.sh
                            
                            echo "Starting the new application using the start script..."
                            # Use nohup to run the script in the background.
                            nohup bash ${DEPLOY_PATH}/start-gfj.sh &
                        EOF
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Deployment successful!'
        }
        failure {
            echo 'Deployment failed. Check the logs for more details.'
        }
    }
}
