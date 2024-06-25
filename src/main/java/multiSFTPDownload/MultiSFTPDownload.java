package multiSFTPDownload;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import config_SFTP_Server.ServerConfig;
import lombok.RequiredArgsConstructor;
import util.SFTPDownload;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

@RequiredArgsConstructor
public class MultiSFTPDownload {


    public static void main(String[] args) {
        String configFilePath = "src/main/java/config_SFTP_Server/servers_config.json";

        try (FileReader reader = new FileReader(configFilePath)) {
            // Đọc file cấu hình JSON
            Gson gson = new Gson();
            Type listType = new TypeToken<List<ServerConfig>>() {
            }.getType();
            List<ServerConfig> serverConfigs = gson.fromJson(reader, listType);

            for (ServerConfig config : serverConfigs) {
                if(config.getName().equals("STB")){
                    SFTPDownload.downloadNewestFileFromSFTP(config);

                }
//                if(config.getName().equals("IRIS")){
//                    SFTPDownload.downloadNewestFileFromSFTP(config);
//
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}