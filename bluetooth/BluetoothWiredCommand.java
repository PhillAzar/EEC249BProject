/**
 * 
 */
package ptolemy.domains.wireless.lib.bluetooth;

/**
 * This class defines all allowable Bluetooth Wired commands, that wil be parsed within the Bluetooth Device actor.
 * 
 * @author Phillip Azar
 *
 * @see BluetoothDevice
 */
public enum BluetoothWiredCommand {
    COMMAND_SWITCHON,
    COMMAND_SWITCHOFF,
    COMMAND_SCAN,
    COMMAND_STOPSCAN,
    COMMAND_CONNECT,
    COMMAND_DISCONNECT,
    COMMAND_PAIR,
    COMMAND_UNPAIR,
    COMMAND_DISCOVERABLE,
    COMMAND_HIDE,
    COMMAND_SENDDATA,
    COMMAND_NOCOMMAND
}
