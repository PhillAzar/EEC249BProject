package ptolemy.domains.wireless.lib.bluetooth;

import ptolemy.data.BooleanToken;
import ptolemy.data.Token;
import ptolemy.kernel.util.IllegalActionException;

public abstract class BluetoothToken extends Token {

    @Override
    public BooleanToken isCloseTo(Token token, double epsilon)
            throws IllegalActionException {
        throw new IllegalActionException("Action unsupported");
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

}
