package org.landa.rempi.client.modules.webcam;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamDriver;
import com.github.sarxos.webcam.ds.buildin.WebcamDefaultDriver;
import com.github.sarxos.webcam.ds.fswebcam.FsWebcamDriver;
import com.github.sarxos.webcam.ds.gstreamer.GStreamerDriver;

public class WebcamProvider {

    private enum CamTypes {
        FS(FsWebcamDriver.class), /* V4L4J(V4l4jDriver.class); */GSTREAMER(GStreamerDriver.class), DEFAULT(WebcamDefaultDriver.class);

        private final Class<? extends WebcamDriver> drive;

        /**
         * @param drive
         */
        private CamTypes(final Class<? extends WebcamDriver> drive) {
            this.drive = drive;
        }

    }

    public static Webcam getWebcam() {

        final String type = System.getProperty("piwebcam.type", CamTypes.DEFAULT.name());

        Webcam.setDriver(CamTypes.valueOf(type.toUpperCase()).drive);
        return Webcam.getDefault();
    }
}
