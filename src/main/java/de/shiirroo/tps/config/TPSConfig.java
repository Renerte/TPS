package de.shiirroo.tps.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import de.shiirroo.tps.kumo.KumoConfig;
import de.shiirroo.tps.web.WebServerConfig;
import lombok.Getter;
import lombok.Setter;

public class TPSConfig {


    public static final BuilderCodec<TPSConfig> CODEC = BuilderCodec.builder(TPSConfig.class, TPSConfig::new)
            .append(new KeyedCodec<MetricsConfig>("MetricsConfig", de.shiirroo.tps.config.MetricsConfig.CODEC),
                    (TPSConfig, newbool, extraInfo) -> TPSConfig.MetricsConfig = newbool,
                    (TPSConfig, extraInfo) -> TPSConfig.MetricsConfig).add()
            .append(new KeyedCodec<Boolean>("EnableTPSWarning", Codec.BOOLEAN),
                    (TPSConfig, newbool, extraInfo) -> TPSConfig.EnableTPSWarning = newbool,
                    (TPSConfig, extraInfo) -> TPSConfig.EnableTPSWarning).add()
            .append(new KeyedCodec<Double>("WarningThreshold", Codec.DOUBLE),
                    (TPSConfig, newDouble, extraInfo) -> TPSConfig.WarningThreshold = newDouble,
                    (TPSConfig, extraInfo) -> TPSConfig.WarningThreshold).add()
            .append(new KeyedCodec<KumoConfig>("KumoConfig", de.shiirroo.tps.kumo.KumoConfig.CODEC),
                    (TPSConfig, config, extraInfo) -> TPSConfig.KumoConfig = config,
                    (TPSConfig, extraInfo) -> TPSConfig.KumoConfig).add()
            .append(new KeyedCodec<WebServerConfig>("WebServerConfig", de.shiirroo.tps.web.WebServerConfig.CODEC),
                    (TPSConfig, config, extraInfo) -> TPSConfig.WebServerConfig = config,
                    (TPSConfig, extraInfo) -> TPSConfig.WebServerConfig).add()



            .build();

    @Getter @Setter
    private MetricsConfig MetricsConfig = new MetricsConfig();
    @Getter @Setter
    private boolean EnableTPSWarning = true;
    @Getter
    private double WarningThreshold = 20.0;
    @Getter
    private KumoConfig KumoConfig = new KumoConfig();
    @Getter
    private WebServerConfig WebServerConfig = new WebServerConfig();


}