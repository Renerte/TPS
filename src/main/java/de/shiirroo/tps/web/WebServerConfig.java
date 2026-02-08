package de.shiirroo.tps.web;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;
import lombok.Setter;

public class WebServerConfig {


    public static final BuilderCodec<WebServerConfig> CODEC = BuilderCodec.builder(WebServerConfig.class, WebServerConfig::new)
            .append(new KeyedCodec<Boolean>("EnableWebServer", Codec.BOOLEAN),
                    (WebServerConfig, newbool, extraInfo) -> WebServerConfig.EnableWebServer = newbool,
                    (WebServerConfig, BindIP) -> WebServerConfig.EnableWebServer).add()
            .append(new KeyedCodec<String>("BindIP", Codec.STRING),
                    (WebServerConfig, newUrl, extraInfo) -> WebServerConfig.BindIP = newUrl,
                    (WebServerConfig, extraInfo) -> WebServerConfig.BindIP).add()
            .append(new KeyedCodec<Integer>("Port", Codec.INTEGER),
                    (WebServerConfig, newKey, extraInfo) -> WebServerConfig.Port = newKey,
                    (WebServerConfig, extraInfo) -> WebServerConfig.Port).add()
            .append(new KeyedCodec<Boolean>("HttpOnly", Codec.BOOLEAN),
                    (WebServerConfig, newbool, extraInfo) -> WebServerConfig.HttpOnly = newbool,
                    (WebServerConfig, BindIP) -> WebServerConfig.HttpOnly).add()


            .build();



    @Getter @Setter
    private boolean EnableWebServer = false;
    @Getter @Setter
    private String BindIP = "0.0.0.0";
    @Getter @Setter
    private Integer Port = 3001;
    @Getter
    private boolean HttpOnly = true;

}
