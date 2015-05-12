package ptolemy.domains.wireless.lib.bluetooth;

/**
 * This enum defines all allowable Bluetooth Responses.
 * 
 * @author Phillip Azar
 * @see BluetoothResponseToken
 */
public enum BluetoothResponse {
    RESPONSE_OK,
    RESPONSE_DENY,
    RESPONSE_ACCEPTCONNECT,
    RESPONSE_ACCEPTPAIR,
    RESPONSE_FINDME,
    COMMAND_REQUESTPAIR,
    COMMAND_REQUESTCONNECT,
    COMMAND_DISCONNECT,
    COMMAND_SCAN
}
