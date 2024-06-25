package config_SFTP_Server;

import com.google.gson.annotations.SerializedName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServerConfig {
    @SerializedName("Name")
    private String name;
    @SerializedName("IP")
    private String ip;
    @SerializedName("Port")
    private String port;
    @SerializedName("Username")
    private String username;
    @SerializedName("Password")
    private String password;
    @SerializedName("DownloadPath")
    private String downloadPath;
    @SerializedName("SavePath")
    private String savePath;
}
