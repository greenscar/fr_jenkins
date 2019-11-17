//import jenkins.model.Jenkins;
//
// // start in the state that doesn't do any build.
// Jenkins.instance.doQuietDown();
/* Adds a multibranch pipeline job to Jenkins */
import hudson.model.*
import hudson.plugins.git.extensions.impl.UserIdentity
import hudson.util.PersistedList
import jenkins.*
import jenkins.branch.*
import jenkins.model.*
import jenkins.model.Jenkins
import jenkins.plugins.git.*
import jenkins.plugins.git.traits.*
import jenkins.scm.impl.trait.RegexSCMHeadFilterTrait
import com.cloudbees.hudson.plugins.folder.computed.PeriodicFolderTrigger
import org.jenkinsci.plugins.workflow.multibranch.*
// Create job
def env = System.getenv()
Jenkins jenkins = Jenkins.instance
String jobName = "forgerock"
String jobScript = "Jenkinsfile"
def job = jenkins.getItem(jobName)
// Create the folder if it doesn't exist
if (job == null) {
  job = jenkins.createProject(WorkflowMultiBranchProject.class, jobName)
}
job.getProjectFactory().setScriptPath(jobScript)
// Add git repository
String remote = "git@github.com:greenscar/fr.git"
GitSCMSource gitSCMSource = new GitSCMSource(remote)
BranchSource branchSource = new BranchSource(gitSCMSource)
// Remove and replace?
PersistedList sources = job.getSourcesList()
sources.clear()
sources.add(branchSource)
// Add traits
String username = "Jenkins"
String email = "jenkins@email.com"
String regexExclude = "^(?!no-cicd).*"
def traits = []
traits.add(new BranchDiscoveryTrait())
traits.add(new LocalBranchTrait())
traits.add(new TagDiscoveryTrait())
traits.add(new UserIdentityTrait(new UserIdentity(username, email)))
traits.add(new RegexSCMHeadFilterTrait(regexExclude))
gitSCMSource.setTraits(traits)
// Periodic trigger
job.addTrigger(new PeriodicFolderTrigger("1m"))