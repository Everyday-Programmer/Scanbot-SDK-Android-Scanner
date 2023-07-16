package com.example.scanbotbarcodescanner;

import android.app.Application;

import io.scanbot.sdk.barcode_scanner.ScanbotBarcodeScannerSDKInitializer;

public class App extends Application {
    private final static String LICENSE_KEY =
            "bJ/sVgnM/2df0UdB5LaPHdfHJsPDId" +
                    "xgrRZiBV9YObmD/9mtzqPj70+07UTt" +
                    "/5jOBsigGxBij/DOkI328zEZ9NDALR" +
                    "Zc4KINQOVZvSd81T7kraK1WfQTmZB6" +
                    "h2aaQJFBFboyqoRkyN4hqp51gg5HnO" +
                    "QQVa2ygSbS2Tn94cEimA/4PCA/Bgld" +
                    "E9Hy/Mw/Zk8Zh/Dwtlcoe4xSLCO23/" +
                    "xZHsBHIcp78LGqFg1/na8ljnb66MSy" +
                    "m3FfGj1rv83P0SN6GaUnT/XNMCYhgp" +
                    "vXoG+mzeeZn1BNtaCrNu9o1BcY3ZNE" +
                    "m1jzvFP/wFVBZSPwipaBx1c6hFW/9r" +
                    "Hn/r2NjX0wwQ==\nU2NhbmJvdFNESw" +
                    "pjb20uZXhhbXBsZS5zY2FuYm90YmFy" +
                    "Y29kZXNjYW5uZXIKMTY4OTk4Mzk5OQ" +
                    "o4Mzg4NjA3CjE5\n";

    @Override
    public void onCreate() {
        super.onCreate();
        new ScanbotBarcodeScannerSDKInitializer()
                .license(this, LICENSE_KEY)
                .initialize(this);
    }
}