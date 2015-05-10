package ptolemy.domains.wireless.lib.bluetooth;

import ptolemy.data.BooleanToken;
import ptolemy.data.Token;
import ptolemy.kernel.util.IllegalActionException;


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
    
    @Override
    public String toString() {
        switch(this._response){
        case COMMAND_DISCONNECT:
            return "DISCONNECT";
        case COMMAND_REQUESTCONNECT:
            return "REQUESTCONNECT";
        case COMMAND_REQUESTPAIR:
            return "REQUESTPAIR";
        case RESPONSE_ACCEPT:
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
}
