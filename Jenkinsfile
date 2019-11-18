pipeline{
    agent{
        docker{
            image 'gradle:6.0.0-jdk8'
        }
    }
    stages{
        stage('build'){
            steps {
                sh 'gradle build' 
            }
        }
        
           
    }
}
