/**
 * 
 */
package ptolemy.domains.wireless.lib.bluetooth;

import ptolemy.data.BooleanToken;
import ptolemy.data.Token;
import ptolemy.kernel.util.IllegalActionException;

/**
 * @author Phill
 *
 */
public class BluetoothWirelessCommandToken extends BluetoothToken {
    
    public BluetoothWirelessCommandToken(BluetoothWirelessCommand command, String deviceIdentifier){
        this._command = command;
        this._deviceIdentifier = deviceIdentifier;
    }
    
    public BluetoothWirelessCommand getCommandValue(){
        return this._command;
    }
    
    public String getIdentifier(){
        return this._deviceIdentifier;
    }

    @Override
    public String toString() {
        switch(this._command){
        case COMMAND_DISCONNECT:
            return "DISCONNECT";
        case COMMAND_REQUESTCONNECT:
            return "CONNECT";
        case COMMAND_REQUESTPAIR:
            return "PAIR";
        default:
            return "nil";
        }
    }

    @Override
    public BooleanToken isEqualTo(Token rightArgument)
            throws IllegalActionException {
        if (rightArgument instanceof BluetoothWirelessCommandToken){
            BluetoothWirelessCommandToken right = (BluetoothWirelessCommandToken) rightArgument;
            if (this._command == right.getCommandValue()){
                return new BooleanToken(true);
            }
            else {
                return new BooleanToken(false);
            }
        }
        else {
            throw new IllegalActionException("The argument must be of type BluetoothWirelessCommandToken");
        }
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////
    
    private final BluetoothWirelessCommand _command;
    private final String _deviceIdentifier;
    
}
