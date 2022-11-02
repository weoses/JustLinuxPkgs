# Simple RPM builder 
Just a simple cross-platform rpm builder, based on a [fork](https://github.com/weoses/redline) from [this library](https://github.com/craigwblake/redline)

For build it using xml config like
```xml
<config>
    <packageName>Hello-World</packageName>
    <packageVersion>10</packageVersion>
    <packageRelease>1</packageRelease>
    <packageEpoch>1</packageEpoch>
    <packageLicense>HIDDEN</packageLicense>
    <packageDescription>it is hello world package!</packageDescription>
    <packageArch>NOARCH</packageArch>
    <fileDigestsAlg>MD5</fileDigestsAlg>
    <buildInDirs>
        <buildInDir>/opt/hello</buildInDir>
    </buildInDirs>
    <filesDescription>
        <packageFileDescription>
            <pathInRpm>/opt/hello/bin/hello.sh</pathInRpm>
            <mode>0755</mode>
        </packageFileDescription>
        <packageFileDescription>
            <pathInRpm>/opt/hello/lib/hello.txt</pathInRpm>
            <mode>0755</mode>
        </packageFileDescription>
        <packageFileDescription>
            <pathInRpm>/opt/hello/lib/hello_libs</pathInRpm>
            <mode>0755</mode>
        </packageFileDescription>
    </filesDescription>
</config>
```
packageName, packageVersion, packageRelease, packageLicense, packageDescription - have **fromFile** attirbute

**packageFileDescription** tags:
 * pathInRpm - path to file or directory
 * mode - rights for added files (default 0644)
 * dirmode - rights for added directories (default 0755)
 * owner, group (default root root)
 * addChildren - for directory - add all including files and directories with rights *mode*. For directories does nothing
 * addParents - automatically add parents for directory / file with rights  *dirmode*
