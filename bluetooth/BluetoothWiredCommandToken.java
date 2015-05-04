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
public class BluetoothWiredCommandToken extends Token {

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
    public BooleanToken isCloseTo(Token token, double epsilon)
            throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
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
    
    private final BluetoothWiredCommand _command;
    
}
