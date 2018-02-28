package com.rdalabs.drivesmartly.drive_smartlypoc.io;

public interface ObdProgressListener {

    void stateUpdate(final ObdCommandJob job);

}