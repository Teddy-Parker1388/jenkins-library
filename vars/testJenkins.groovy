
def environments = /^(dev|prod|qa|stage|perf).*/

def call(){
    def environments = /^(dev|prod|qa|stage|perf).*/

    if (env.BRANCH_NAME ==~ environments){
        stage("TAC SYNC"){
            echo "Running `tsunami tac sync`"
            tacSync()
        }
        stage("COMMIT CHANGES"){
            echo "Commit any changes. Push to remote"
            gitCommit()
        }
        stage("TAC VALIDATE"){
            echo "Running `tsunami tac validate`"
            tacValidate()

        }
    }

}


def tacSync() {
    sh "tsunami tac sync"
}

def commitPush(){
    sh """  
    git diff --quiet && git diff --staged --quiet || git commit -am 'Update various files'
    git push origin ${env.BRANCHNAME}
    """
}

def tacValidate() {
    sh "tsunami tac validate"
}