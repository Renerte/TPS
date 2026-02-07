package de.shiirroo.tps.kumo;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;
import lombok.Setter;

public class KumoConfig {

    public static final BuilderCodec<KumoConfig> CODEC = BuilderCodec.builder(KumoConfig.class, KumoConfig::new)
            .append(new KeyedCodec<Boolean>("EnableKumoSupport", Codec.BOOLEAN),
                    (KumoConfig, newbool, extraInfo) -> KumoConfig.EnableKumoSupport = newbool,
                    (KumoConfig, extraInfo) -> KumoConfig.EnableKumoSupport).add()
            .append(new KeyedCodec<String>("KumoURL", Codec.STRING),
                    (KumoConfig, newUrl, extraInfo) -> KumoConfig.KumoURL = newUrl,
                    (KumoConfig, extraInfo) -> KumoConfig.KumoURL).add()
            .append(new KeyedCodec<String>("KumoKey", Codec.STRING),
                    (KumoConfig, newKey, extraInfo) -> KumoConfig.KumoKey = newKey,
                    (KumoConfig, extraInfo) -> KumoConfig.KumoKey).add()
            .build();


    public static String KumoWebsocketPath(KumoConfig kumoConfig) {
        return kumoConfig.getKumoURL() + "ws";
    }

    @Getter @Setter
    private boolean EnableKumoSupport = false;
    @Getter @Setter
    private String KumoURL = "https://localhost:3001/NOT_SUPPORTED_YET/";
    @Getter @Setter
    private String KumoKey = "DEFAULT_KEY_CHANGE_THIS";


}
