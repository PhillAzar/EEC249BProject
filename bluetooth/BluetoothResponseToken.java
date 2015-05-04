package ptolemy.domains.wireless.lib.bluetooth;


public class BluetoothResponseToken extends BluetoothToken {
    
    public BluetoothResponseToken(BluetoothResponse response, String deviceIdentifier){
        this._response = response;
        this._deviceIdentifier = deviceIdentifier;
    }
    
    public BluetoothResponse getResponse(){
        return this._response;
    }
    
    public String getDeviceIdentifier(){
        return this._deviceIdentifier;
    }
    
    private final BluetoothResponse _response;
    private final String _deviceIdentifier;
}
