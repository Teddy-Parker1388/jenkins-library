def call(config){
 String git_user = config?.git?.user ?: "Teddy-Parker1388"
 String git_email = config?.git?.email ?: "pteddy17@gmail.com"
 String ssh_credentials_id = config?.git?.credentials ?: "github-ssh"
  stage("Checkout"){
      repoCheckout()
  }
  stage("TAC Sync"){
      tacSyncCommit()
  }
  stage("TAC Validate"){
      tacValidate()
  }

}

String branch = env.BRANCH_NAME
def environments = /^(dev|prod|qa|stage|perf).*/

def repoCheckout(String git_user , String git_email){
    checkout([
        $class: 'GitSCM',
        branches: scm.branches,
        extensions: scm.extensions,
        userRemoteConfigs: scm.userRemoteConfigs
    ])
    sh """
        git checkout ${branch}
        git config user.name ${git_user}
        git config user.email ${git_email}
         """

}

def tacSyncCommit(String ssh_credentials_id) {
    
    if (branch =~ environments) {
        echo "Running `tsunami tac sync`..."
        sh "tsunami tac sync -e ${branch}"
        def changes = sh(script: 'git status --short', returnStdout: true).trim()

        if (changes) {
        // Commit any changes
        sshagent([ssh_credentials_id]){
            sh """
            git add .
            git commit -m 'Changes made after running TAC Sync'
            git push origin ${branch}
            """
        }
         }else{
            echo "There are no changes to commit"
        }
    }
}

def tacValidate() {
    echo "Running `tsunami tac validate`..."
    sh "tsunami tac validate"
}
