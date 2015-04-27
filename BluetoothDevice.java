/**
 * 
 */
package ptolemy.domains.wireless.lib;

///////////////////////////////////////////////////////////////////
////Bluetooth Device Actor 

import ptolemy.actor.TypedAtomicActor;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

/**
 *@author Phillip Azar
 *@version 
 *@since
 *@Pt.ProposedRating
 *@Pt.AcceptedRating 
 *
 */

public class BluetoothDevice extends TypedAtomicActor {

    /** Construct an actor with the given container and name.
     *  @param container The container.
     *  @param name The name of this actor.
     *  @exception IllegalActionException If the actor cannot be contained
     *   by the proposed container.
     *  @exception NameDuplicationException If the container already has an
     *   actor with this name.
     */
    public BluetoothDevice(CompositeEntity container, String name)
            throws NameDuplicationException, IllegalActionException {
        super(container, name);
        //TODO: Constructor
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                     ports and parameters                  ////
    
    //TODO: Input Ports, Parameters, Lists, etc.
    
    //TODO: fire() documentation
    /**
     * MISSING DOCUMENTATION
     *  @exception IllegalActionException If there is no director,
     *   or if the parameters cannot be evaluated.
     */
    public void fire() throws IllegalActionException {
        super.fire();
        //TODO: fire()
    }

    //TODO: Actor skeleton.
}
