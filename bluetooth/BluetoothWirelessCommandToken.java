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
public class BluetoothWirelessCommandToken extends Token {
    
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
    public BooleanToken isCloseTo(Token token, double epsilon)
            throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
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

    @Override
    public Token add(Token rightArgument) throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token addReverse(Token leftArgument) throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token divide(Token rightArgument) throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token divideReverse(Token leftArgument)
            throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token modulo(Token rightArgument) throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token moduloReverse(Token leftArgument)
            throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token multiply(Token rightArgument) throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token multiplyReverse(Token leftArgument)
            throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token pow(int times) throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token subtract(Token rightArgument) throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }

    @Override
    public Token subtractReverse(Token leftArgument)
            throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////
    
    private final BluetoothWirelessCommand _command;
    private final String _deviceIdentifier;
    
}
