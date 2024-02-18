
def call(){
    def environments = /^(dev|prod|qa|stage|perf).*/
   
    if (env.BRANCH_NAME ==~ environments){
        stage("TAC SYNC"){
            echo "Running `tsunami tac sync`"
            tacSync()
        }
        stage("COMMIT CHANGES"){
            echo "Commit any changes. Push to remote"
            commitPush()
        }
        stage("TAC VALIDATE"){
            echo "Running `tsunami tac validate`"
            tacValidate()

        }
    }

}


def tacSync() {
    
    sh "python3 -m pip list"
    sh "git branch"
    sh "git checkout ${env.BRANCH_NAME}"
    sh "tsunami tac sync "
}

def commitPush(){
    withCredentials([usernamePassword(credentialsId: 'github-cred', passwordVariable: 'pass', usernameVariable: 'user')]) {
        sh  """
            git config --global user.name ${user}
            git config --global user.password ${pass}
            git diff --quiet && git diff --staged --quiet || git commit -am 'Update various files'
            git config -l
            ls -l
           """
        sh "git push origin ${env.BRANCH_NAME}"
       
    }
    
}

def tacValidate() {
    sh "tsunami tac validate"
}
