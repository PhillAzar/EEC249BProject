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
public class BluetoothStatusToken<T> extends BluetoothToken {

    public BluetoothStatusToken(BluetoothStatus status, T data){
        this._status = status;
        this._data = data;
    }
    
    public BluetoothStatus getStatusValue(){
        return this._status;
    }
    
    public T getData() {
        return this._data;
    }

    @Override
    public BooleanToken isEqualTo(Token rightArgument)
            throws IllegalActionException {
        if (rightArgument instanceof BluetoothStatusToken){
            BluetoothStatusToken right = (BluetoothStatusToken) rightArgument;
            if (this._status == right.getStatusValue()){
                return new BooleanToken(true);
            }
            else {
                return new BooleanToken(false);
            }
        }
        else {
            throw new IllegalActionException("The argument must be of type BluetoothStatusToken");
        }
    }

    @Override
    public String toString() {
        switch (_status){
        case STATUS_ERROR:
            return "ERROR";
        case STATUS_OK:
            return "OK";
        default:
            return "nil"; 
        }
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////
    
    private final BluetoothStatus _status;
    private final T _data;
}
