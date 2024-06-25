package util;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import config_SFTP_Server.ServerConfig;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

@RequiredArgsConstructor
public class SFTPDownload {
    public static void downloadNewestFileFromSFTP(ServerConfig config) {
        Session session = null;
        ChannelSftp channelSftp = null;

        try {
            JSch jsch = new JSch();
            session = jsch.getSession(config.getUsername(), config.getIp(), Integer.parseInt(config.getPort()));
            session.setPassword(config.getPassword());

            Properties properties = new Properties();
            properties.put("StrictHostKeyChecking", "no");
            session.setConfig(properties);

            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            // Liệt kê các file trong thư mục, chọn file mới nhất
            Vector<ChannelSftp.LsEntry> files = channelSftp.ls(config.getDownloadPath());
            ChannelSftp.LsEntry newestFile = null;

            for (ChannelSftp.LsEntry file : files) {
                if (!file.getAttrs().isDir()) {
                    if (newestFile == null || file.getAttrs().getMTime() > newestFile.getAttrs().getMTime()) {
                        newestFile = file;
                    }
                }
            }

            if (newestFile != null) {
                String remoteFilePath = config.getDownloadPath() + newestFile.getFilename();
                String localFilePath = config.getSavePath() + newestFile.getFilename();

                File localFile = new File(localFilePath);
                if (localFile.exists()) {
                    System.out.println("File already exists. Overwriting: " + localFilePath);
                }

                try (InputStream inputStream = channelSftp.get(remoteFilePath);
                     FileOutputStream fileOutputStream = new FileOutputStream(localFilePath)) {

                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, bytesRead);
                    }

                    System.out.println("File downloaded successfully to " + localFilePath);
                }


            } else {
                System.out.println("No files found in directory " + config.getDownloadPath());
            }
            // demo copy file sang thu muc khac
            String sourceDir = "src/main/java/stb";
            String destDir = "src/main/java/stbcopy";

            File sourceFolder = new File(sourceDir);
            File destFolder = new File(destDir);

            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }

            File newestFilecopy = getNewestFile(sourceFolder);
            if (newestFilecopy != null) {
                copyFile(newestFilecopy, new File(destFolder, newestFilecopy.getName()));
            } else {
                System.out.println("No files found in directory " + sourceDir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }
    //ham lay file moi nhat
    private static File getNewestFile(File folder) {
        File[] files = folder.listFiles((dir, name) -> !new File(dir, name).isDirectory());
        if (files == null || files.length == 0) {
            return null;
        }

        File newestFile = files[0];
        for (File file : files) {
            if (file.lastModified() > newestFile.lastModified()) {
                newestFile = file;
            }
        }
        return newestFile;
    }
    //ham copy file
    @SneakyThrows
    private static void copyFile(File sourceFile, File destFile) {
        try (FileInputStream fis = new FileInputStream(sourceFile);
             FileOutputStream fos = new FileOutputStream(destFile)) {

            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            System.out.println("File copied successfully to " + destFile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}