package builders.deb;

import Util.Util;
import builders.PkgBuilder;
import jaxb.XFilePlatformSpecific;
import jaxb.XPkgParams;
import org.apache.commons.compress.archivers.ar.ArArchiveEntry;
import org.apache.commons.compress.archivers.ar.ArArchiveOutputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class DebBuilder extends PkgBuilder {
    Logger logger = LoggerFactory.getLogger(DebBuilder.class.getName());
    StringBuilder controlFile = new StringBuilder();
    StringBuilder md5SumsFile = new StringBuilder();
    StringBuilder confFile = new StringBuilder();
    Set<FileObject> files = new HashSet<>();


    //if two builders running at one time
    String builderId = String.valueOf(new Random().nextInt());
    String debFileName;

    private void controlHeader(String header, String value){
        if (StringUtils.isBlank(value)) return;
        String trimmed  = value.trim();
        String replacedVal = trimmed
                .replaceAll("\\r\\n?", "\n")
                .replaceAll("\\n", "\n ");
        controlFile.append(header).append(": ").append(replacedVal).append("\n");
    }
    private void md5sumHeader(String hash, String pkgPath){
        md5SumsFile.append(hash).append(" ").append(pkgPath).append("\n");
    }

    private void confFileEntry(String pkgPath){
        confFile.append(pkgPath).append("\n");
    }
    @Override
    protected void init(XPkgParams params) {

        if (StringUtils.isEmpty(params.packageName)||
            StringUtils.isEmpty(params.packageVersion)||
            StringUtils.isEmpty(params.packageDescription)||
            params.debPackageArch == null){
            throw new RuntimeException("packageName, packageVersion, packageDescription, debPackageArch is mandatory for DEB packages !");
        }

        controlHeader("Package", params.packageName);
        controlHeader("Version", params.packageVersion);
        controlHeader("Architecture", params.debPackageArch.name());
        controlHeader("Maintainer", params.debMaintainer);
        controlHeader("Description", params.packageDescription);
        debFileName = String.format(
                "%s_%s_%s.deb",
                params.packageName,
                params.packageVersion,
                params.debPackageArch);
    }

    @Override
    public void addBuildin(String pkgPath) {
        logger.warn("Buildins not supported on DEB");
    }

    @Override
    public boolean addDirectory(String pkgPath, File file, int dirmode, XFilePlatformSpecific specific, String owner, String group) {
        files.add(new FileObject(pkgPath, dirmode, file, owner, group, true));
        return true;
    }


    @Override
    public boolean addFile(String pkgPath, File file, int filemode, XFilePlatformSpecific specific, String owner, String group) {
        String nonSlashPath =  Util.normalizePkgPath(pkgPath);
        try (InputStream is = Files.newInputStream(file.toPath())) {
            String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
            logger.debug(String.format("File %s, md5 %s", pkgPath, file));
            md5sumHeader(md5, nonSlashPath);
        }catch (Exception e){
            logger.error(String.format("Cant calc MD5 for file %s", file), e);
            return false;
        }

        if (specific.debIsConfig){
            confFileEntry(nonSlashPath);
        }

        files.add(new FileObject(pkgPath, filemode, file, owner, group, false));
        return true;
    }

    private File createControl(String outputFolder){
        try {
            Path controlTarGz = Paths.get(outputFolder, builderId+"control.tar.gz");
            logger.info(String.format("Creating control file - %s", controlTarGz));
            try (OutputStream fOut = Files.newOutputStream(controlTarGz);
                 GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(fOut);
                 TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

                TarArchiveEntry dotEntry = new TarArchiveEntry("./");
                dotEntry.setSize(0);
                tOut.putArchiveEntry(dotEntry);
                tOut.closeArchiveEntry();

                byte[] controlData = controlFile.toString().getBytes(StandardCharsets.UTF_8);
                TarArchiveEntry controlEntry = new TarArchiveEntry("./control");
                controlEntry.setSize(controlData.length);
                tOut.putArchiveEntry(controlEntry);
                tOut.write(controlData);
                tOut.closeArchiveEntry();

                byte[] md5sumsData = md5SumsFile.toString().getBytes(StandardCharsets.UTF_8);
                TarArchiveEntry md5sumsEntry = new TarArchiveEntry("./md5sums");
                md5sumsEntry.setSize(md5sumsData.length);
                tOut.putArchiveEntry(md5sumsEntry);
                tOut.write(md5sumsData);
                tOut.closeArchiveEntry();

                tOut.finish();

            }
            File controlFile = controlTarGz.toFile();
            controlFile.deleteOnExit();
            return controlFile;
        }catch (Exception e){
            logger.error("Cant create Control file - "+ e, e);
        }
        return null;
    }

    private File createData(String outputFolder){
        try {
            Path dataTarGz = Paths.get(outputFolder, builderId+"data.tar.gz");
            logger.info(String.format("Creating data file - %s", dataTarGz));
            try (OutputStream fOut = Files.newOutputStream(dataTarGz);
                 GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(fOut);
                 TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut)) {

                TarArchiveEntry dotEntry = new TarArchiveEntry("./");
                dotEntry.setSize(0);
                tOut.putArchiveEntry(dotEntry);
                tOut.closeArchiveEntry();

                // Ensure path sorted by length
                // eg /opt/, /opt/a, /opt/b, /opt/a/b, /opt/b/a, /opt/a/b/c
                List<FileObject> sortedList =
                        files.stream()
                        .sorted(Comparator.comparingInt(e -> Paths.get(e.pkgPath).getNameCount()))
                        .collect(Collectors.toList());

                for(FileObject  f : sortedList) {
                    TarArchiveEntry entry = new TarArchiveEntry(f.physical, Util.normalizePkgPath(Paths.get("./", f.pkgPath).toString()));
                    entry.setUserName(f.owner);
                    entry.setGroupName(f.group);
                    entry.setMode(f.rights);

                    tOut.putArchiveEntry(entry);
                    if (!f.directory) Files.copy(f.physical.toPath(), tOut);
                    tOut.closeArchiveEntry();
                }

                tOut.finish();
            }
            File dataFile = dataTarGz.toFile();
            dataFile.deleteOnExit();
            return dataFile;
        }catch (Exception e){
            logger.error("Cant create Data file - "+ e, e);
        }
        return null;
    }
    @Override
    public String build(String outputFolder) {
        Path outputFile = Paths.get(outputFolder, debFileName);
        File controlTarGx = null;
        File dataTarGx = null;

        if (outputFile.toFile().exists()) {
            outputFile.toFile().delete();
        }

        controlTarGx = createControl(outputFolder);
        if (controlTarGx == null) return null;
        dataTarGx = createData(outputFolder);
        if (dataTarGx == null) return null;

        String controlFileName = controlTarGx.getName().substring(builderId.length());
        String dataFileName = dataTarGx.getName().substring(builderId.length());
        try (OutputStream fout = Files.newOutputStream(outputFile);
             ArArchiveOutputStream arOutputStream = new ArArchiveOutputStream(fout)) {

            // write debian-binary
            // debian-binary MUST be first file in archive
            byte[] version = "2.0\n".getBytes(StandardCharsets.UTF_8);
            ArArchiveEntry debianBinary = new ArArchiveEntry("debian-binary", version.length);
            arOutputStream.putArchiveEntry(debianBinary);
            arOutputStream.write(version);
            arOutputStream.closeArchiveEntry();

            // write CONTROL
            ArArchiveEntry entryConrol = new ArArchiveEntry(controlTarGx, controlFileName);
            arOutputStream.putArchiveEntry(entryConrol);
            Files.copy(controlTarGx.toPath(), arOutputStream);
            arOutputStream.closeArchiveEntry();

            // write DATA
            ArArchiveEntry entryData = new ArArchiveEntry(dataTarGx, dataFileName);
            arOutputStream.putArchiveEntry(entryData);
            Files.copy(dataTarGx.toPath(), arOutputStream);
            arOutputStream.closeArchiveEntry();
        } catch (IOException e) {
            logger.error("Cant create deb file - " + e, e);
            return null;
        }

        return outputFile.toString();
    }

    static class FileObject{
        final String pkgPath;
        final int rights;
        final File physical;
        final String owner;
        final String group;
        final boolean directory;

        public FileObject(String pkgPath, int rights, File physical, String owner, String group, boolean directory) {
            this.pkgPath = pkgPath;
            this.rights = rights;
            this.physical = physical;
            this.owner = owner;
            this.group = group;
            this.directory = directory;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FileObject that = (FileObject) o;
            return pkgPath.equals(that.pkgPath);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pkgPath);
        }
    }
}
