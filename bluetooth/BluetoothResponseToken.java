package ptolemy.domains.wireless.lib.bluetooth;

import ptolemy.data.BooleanToken;
import ptolemy.data.Token;
import ptolemy.kernel.util.IllegalActionException;


public class BluetoothResponseToken<T> extends BluetoothToken {
    
    public BluetoothResponseToken(BluetoothResponse response, String deviceIdentifier, String sourceIdentifier, T data){
        this._response = response;
        this._deviceIdentifier = deviceIdentifier;
        this._sourceIdentifier = sourceIdentifier;
        this._data = data;
    }
    
    public BluetoothResponse getResponse(){
        return this._response;
    }
    
    public String getDeviceIdentifier(){
        return this._deviceIdentifier;
    }
    
    public String getSourceIdentifier(){
        return this._sourceIdentifier;
    }
    
    public T getData(){
        return this._data;
    }
    
    @Override
    public String toString() {
        switch(this._response){
        case COMMAND_DISCONNECT:
            return "DISCONNECT";
        case COMMAND_REQUESTCONNECT:
            return "REQUESTCONNECT";
        case COMMAND_REQUESTPAIR:
            return "REQUESTPAIR";
        case RESPONSE_ACCEPTCONNECT:
            return "ACCEPT";
        case RESPONSE_DENY:
            return "DENY";
        case RESPONSE_FINDME:
            return "FINDME";
        case RESPONSE_OK:
            return "OK";
        default:
            return "nil";
        }
    }
    

    @Override
    public BooleanToken isEqualTo(Token rightArgument)
            throws IllegalActionException {
        if (rightArgument instanceof BluetoothResponseToken){
            BluetoothResponseToken right = (BluetoothResponseToken) rightArgument;
            if (this._response == right.getResponse()){
                return new BooleanToken(true);
            }
            else {
                return new BooleanToken(false);
            }
        }
        else {
            throw new IllegalActionException("The argument must be of type BluetoothResponseToken");
        }
    }
    private final BluetoothResponse _response;
    private final String _deviceIdentifier;
    private final String _sourceIdentifier;
    private final T _data;
}
