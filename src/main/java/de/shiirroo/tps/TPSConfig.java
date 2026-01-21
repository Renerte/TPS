package de.shiirroo.tps;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import lombok.Getter;
import lombok.Setter;

public class TPSConfig {


    public static final BuilderCodec<TPSConfig> CODEC = BuilderCodec.builder(TPSConfig.class, TPSConfig::new)
            .append(new KeyedCodec<Boolean>("EnableMetrics", Codec.BOOLEAN),
                    (TPSConfig, newbool, extraInfo) -> TPSConfig.EnableMetrics = newbool,
                    (TPSConfig, extraInfo) -> TPSConfig.EnableMetrics).add()
            .append(new KeyedCodec<Boolean>("EnableTPSWarning", Codec.BOOLEAN),
                    (TPSConfig, newbool, extraInfo) -> TPSConfig.EnableTPSWarning = newbool,
                    (TPSConfig, extraInfo) -> TPSConfig.EnableTPSWarning).add()
            .append(new KeyedCodec<Double>("WarningThreshold", Codec.DOUBLE),
                    (TPSConfig, newDouble, extraInfo) -> TPSConfig.WarningThreshold = newDouble,
                    (TPSConfig, extraInfo) -> TPSConfig.WarningThreshold).add()
            .build();

    @Getter @Setter
    private boolean EnableMetrics = false;
    @Getter @Setter
    private boolean EnableTPSWarning = true;
    @Getter
    private double WarningThreshold = 20.0;


}