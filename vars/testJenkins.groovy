
def call(Closure body ){
    Map config = parseConfig(body)

    String ssh_credentials =  "github-ssh"
    String branch = env.BRANCH_NAME
    def environments = /^(dev|prod|qa|stage|perf).*/

 node(config.agent ?: 'centos-python') {
     try{
         stage("Repo Checkout"){
      repoCheckout()
    }
   

    if(branch =~ environments){
        stage("TAC Sync"){
      tacSync()
        }
      stage("Commit Changes"){
          commitChanges(ssh_credentials,branch)
      }
    }
  
    stage("TAC Validate"){
      tacValidate()
    }

     }catch (Exception error){
         throw(error)
     }finally{
         cleanWs()
     }
     


 }
    
}


def repoCheckout(){
    checkout([
        $class: 'GitSCM',
        branches: scm.branches,
        extensions: scm.extensions + builtExtensions,
        userRemoteConfigs: userRemoteConfigs
    ])

 sh "git config --local user.name  Teddy-Parker1388"
 sh "git config --local user.email pteddy17@gmail.com"

}

def tacSync() {
        echo "Running `tsunami tac sync`..."
//        sh "tsunami tac sync"
    
}

def commitChanges(ssh_credentials,branch){
    def changes = sh(script: 'git status --short', returnStdout: true).trim()

    if (changes) {
        // Commit any changes
        sshagent([ssh_credentials]){
            sh """
            git add .
            git commit -m 'tsunami tac sync updates'
            git push origin ${branch}
            """
        }
         }else{
            echo "There are no changes to commit."
        }

}

def tacValidate() {
    echo "Running `tsunami tac validate`..."
//    sh "tsunami tac validate"
}
