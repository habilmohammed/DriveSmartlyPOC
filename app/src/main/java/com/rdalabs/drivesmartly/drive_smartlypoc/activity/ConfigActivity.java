package com.rdalabs.drivesmartly.drive_smartlypoc.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.github.pires.obd.commands.ObdCommand;
import com.rdalabs.drivesmartly.drive_smartlypoc.R;
import com.rdalabs.drivesmartly.drive_smartlypoc.config.ObdConfig;

import java.util.ArrayList;
import java.util.Set;

/**
 * Configuration com.github.pires.obd.reader.activity.
 */
public class ConfigActivity extends PreferenceActivity {

    public static final String BLUETOOTH_LIST_KEY = "bluetooth_list_preference";
    public static final String ENABLE_BT_KEY = "enable_bluetooth_preference";

    public static int getObdUpdatePeriod() {
        int period = 5000;
        return period;
    }

    public static ArrayList<ObdCommand> getObdCommands() {
        ArrayList<ObdCommand> cmds = ObdConfig.getCommands();
        return cmds;
    }

    public static String[] getReaderConfigCommands() {
        String cmdsStr = "atsp0\natz";
        String[] cmds = cmdsStr.split("\n");
        return cmds;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    /*
     * Read preferences resources available at res/xml/preferences.xml
     */
        addPreferencesFromResource(R.xml.preferences);

        ArrayList<CharSequence> pairedDeviceStrings = new ArrayList<>();
        ArrayList<CharSequence> vals = new ArrayList<>();
        ListPreference listBtDevices = (ListPreference) getPreferenceScreen()
                .findPreference(BLUETOOTH_LIST_KEY);

    /*
     * Let's use this device Bluetooth adapter to select which paired OBD-II
     * compliant device we'll use.
     */
        final BluetoothAdapter mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            listBtDevices
                    .setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
            listBtDevices.setEntryValues(vals.toArray(new CharSequence[0]));

            // we shouldn't get here, still warn user
            Toast.makeText(this, "This device does not support Bluetooth.",
                    Toast.LENGTH_LONG).show();

            return;
        }

    /*
     * Listen for preferences click.
     *
     * TODO there are so many repeated validations :-/
     */
        final Activity thisActivity = this;
        listBtDevices.setEntries(new CharSequence[1]);
        listBtDevices.setEntryValues(new CharSequence[1]);
        listBtDevices.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                // see what I mean in the previous comment?
                if (mBtAdapter == null || !mBtAdapter.isEnabled()) {
                    Toast.makeText(thisActivity,
                            "This device does not support Bluetooth or it is disabled.",
                            Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        });

    /*
     * Get paired devices and populate preference list.
     */
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDeviceStrings.add(device.getName() + "\n" + device.getAddress());
                vals.add(device.getAddress());
            }
        }
        listBtDevices.setEntries(pairedDeviceStrings.toArray(new CharSequence[0]));
        listBtDevices.setEntryValues(vals.toArray(new CharSequence[0]));
    }
}
