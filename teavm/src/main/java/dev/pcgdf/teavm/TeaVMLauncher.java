package dev.pcgdf.teavm;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import dev.pcgdf.Main;

/**
 * Launches the TeaVM/HTML application.
 */
public class TeaVMLauncher {
    public static void main(String[] args) {
        WebApplicationConfiguration config = new WebApplicationConfiguration("canvas");

        config.width = 1000;
        config.height = 800;
        config.preloadListener = assetLoader -> assetLoader.loadScript("freetype.js");

        new WebApplication(new Main(), config);
    }
}
