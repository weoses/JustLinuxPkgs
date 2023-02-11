# JustLinuxPkg
Just a simple java packages builder, rpm part based on a [fork](https://github.com/weoses/redline) from [this library](https://github.com/craigwblake/redline) 

Can build .deb and .rpm packages


### How to

* Create ``config.xml``  
This config create package from ``./sample/sample1/``  
Files in directory ``/opt/hello_application/bin`` marked as executable  
Directories ``/opt`` and ``/opt/hello_application`` skipped

```xml
<config>
  <pkgParams>
      <!-- Basic info -->
      <packageName>Sample-1</packageName>
      <packageVersion>0.1</packageVersion>
      <packageLicense>LICENSE</packageLicense>
      <packageDescription>Sample package</packageDescription>
      <debPackageArch>all</debPackageArch>
      <debMaintainer>I am</debMaintainer>
      <rpmPackageEpoch>1</rpmPackageEpoch>
      <rpmPackageRelease>1</rpmPackageRelease>
      <rpmFileDigestsAlg>MD5</rpmFileDigestsAlg>
      <rpmPackageArch>NOARCH</rpmPackageArch>
  </pkgParams>

  <defaults>
      <!-- Default rights and owner/group for files/directories -->  
      <defaultDirmode>0755</defaultDirmode>
      <defaultMode>0644</defaultMode>
      <defaultOwner>root</defaultOwner>
      <defaultGroup>root</defaultGroup>
  </defaults>
    
  <!-- Directories here don`t appear in package -->
  <buildInDirs>
    <buildInDir>/opt/hello_application</buildInDir>
  </buildInDirs>

  <files>
      <!-- Physical path to '/' folder -->
      <physicalRoot>./sample/sample1/</physicalRoot>

      <!-- File (or directory)
        pkgPath : path in package
        physicalPath : path on disk (by default calculates automatically based on physicalRoot and pkgPath)
        mode: rights for files (octal, starts with 0, eg. 0777)
        dirmode: rights for directories (octal, starts with 0, eg. 0777)
        owner, group: owner and group for files and directories
        recursive: if true, adds subfiles and subdirectories
        specific: -> rpmDirective - Directive for file in RPM (NONE, CONFIG, DOC, ICON, ...)
        specific: -> debIsConfig  - Marks this file in deb packages as config if true
        -->
      <file pkgPath="/opt/hello_application">
          <recursive>true</recursive>
      </file>
    
      <file pkgPath="/opt/hello_application/bin">
          <recursive>true</recursive>
          <mode>755</mode>
      </file>

  </files>

  <scripts>
    <!--
    preinstall, postinstall, prerm, postrm scripts
     -->  
    <preInstallScript fromFile="./sample/scripts/preinst.sh"/>
    <postInstallScript fromFile="./sample/scripts/postinst.sh"/>
  </scripts>
</config>
```
* Then run for .deb or for rpm build  
```shell
java -jar JLinuxPkg.jar --config config.xml -o . -f DEB
java -jar JLinuxPkg.jar --config config.xml -o . -f RPM
```
* Program saved generated packages in your working directory 

#### NSIS scripts 

Nsis script can be used in files clause
Program parses ``SetOutPath`` and ``File`` clauses
```xml
<files>
    <physicalRoot>./root/test</physicalRoot>
    <!-- default file clause -->
    <file pkgPath="/opt/test/test.sh" mode="755"/>
    <!-- nsis clause 
    nsisFilePath: path to nsis file
    nsisWorkingDirectory: working directory for nsis file-->
    <fileNsis nsisFilePath="./root/test/test.nsi" 
              nsisWorkingDirectory="./root/test/nsis_data">
        <!-- replace: use for replace variables in nsis file -->
        <replace regexp="false" first="$INSTDIR" second="/opt/test/"/>
        <!-- exclude: use for unwanted files -->
        <exclude regexp="false" text="splash.bmp"/>
    </fileNsis>
</files>

```