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
public class BluetoothWiredCommandToken extends BluetoothToken {

    public BluetoothWiredCommandToken(BluetoothWiredCommand command){
        this._command = command;
    }
    
    public BluetoothWiredCommand getCommandValue(){
        return this._command;
    }

    @Override
    public String toString() {
        switch(this._command){
            case COMMAND_CONNECT:
                return "CONNECT";
            case COMMAND_DISCONNECT:
                return "DISCONNECT";
            case COMMAND_SCAN:
                return "SCAN";
            case COMMAND_SWITCHOFF:
                return "SWITCHOFF";
            case COMMAND_SWITCHON:
                return "SWITCHON";
            case COMMAND_TRANSMIT:
                return "TRANSMIT";
            default:
                return "nil";        
        }
    }

    @Override
    public BooleanToken isEqualTo(Token rightArgument)
            throws IllegalActionException {
        if (rightArgument instanceof BluetoothWiredCommandToken){
            BluetoothWiredCommandToken right = (BluetoothWiredCommandToken) rightArgument;
            if (this._command == right.getCommandValue()){
                return new BooleanToken(true);
            }
            else {
                return new BooleanToken(false);
            }
        }
        else {
            throw new IllegalActionException("The argument must be of type BluetoothWiredCommandToken");
        }
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////
    
    private final BluetoothWiredCommand _command;
    
}
