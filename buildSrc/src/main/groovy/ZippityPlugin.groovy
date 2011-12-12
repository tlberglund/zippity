import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.bundling.Zip

class ZippityPlugin implements Plugin<Project> { 

  void apply(Project project) { 
    project.task('zippity', type: ZippityTask)
  }

}


class ZippityTask extends Zip { 
  def nonce
  def tempDir
  def tempFilename

  ZippityTask() {
    nonce = new Date().time
    tempDir = "${project.buildDir}/tmp/${nonce}"
    tempFilename = "${tempDir}/monkey.xml"

    println "WILL EMIT FILE TO ${tempFilename}"

    archiveName = 'zippity.zip'

    // Before Zip runs, compute the contents of a file from project state.
    doFirst {
      generateFile()
    }
    
    // Try to ensure that the file gets included in the zip
    project.afterEvaluate {
      println "ZIP INCLUDING ${tempFilename}"
      println "ZIP INCLUDING ${project.sourceSets.main.allSource.files}"
      from tempFilename
      from project.sourceSets.main.allSource
    }
  }

  def generateFile() { 
    println "MAKING ${tempDir}"
    println project.file(tempDir).mkdirs()
    println "EMITTING ${tempFilename}"
    project.file(tempFilename).withWriter { writer ->
      writer.println('<monkey>')
      project.monkey.each { key, value -> 
        writer.println("  <${key}>${value}</${key}>")
      }
      writer.println('</monkey>')
    }
  }
}