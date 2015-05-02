/**
 * 
 */
package ptolemy.domains.wireless.lib.bluetooth;

///////////////////////////////////////////////////////////////////
////BluetoothDevice

import ptolemy.actor.TypeAttribute;
import ptolemy.actor.TypedAtomicActor;
import ptolemy.actor.TypedIOPort;
import ptolemy.data.expr.StringParameter;
import ptolemy.domains.wireless.kernel.WirelessIOPort;
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
    
    private enum States {
        STATE_IDLE,
        STATE_CONNECTED,
        STATE_SCANNING,
        STATE_OFF,
        STATE_ON
    }

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
        
        currentState = States.STATE_OFF;
        
        wirelessInputChannelName = new StringParameter(this, "wirelessInputChannelName");
        wirelessInputChannelName.setExpression("WirelessInputChannel");
        
        wirelessOutputChannelName = new StringParameter(this, "wirelessOutputChannelName");
        wirelessOutputChannelName.setExpression("WirelessOutputChannel");
        
        wiredInput = new TypedIOPort(this, "Wired Input", true, false);
        wiredOutput = new TypedIOPort(this, "Wired Output", false, true);
        
        wirelessInput = new WirelessIOPort(this, "Wireless Input", true, false);
        wirelessInput.outsideChannel.setExpression("$wirelessInputChannelName");
        
        wirelessOutput = new WirelessIOPort(this, "Wireless Output", false, true);
        wirelessOutput.outsideChannel.setExpression("$wirelessOutputChannelName");
        
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                     ports and parameters                  ////
    
    //TODO: Input Ports, Parameters, Lists, etc.
    
    private States currentState;
    
    /** The input port for wired communication, which could potentially facilitate communication with other
     * devices/components/actors which are not wireless that interact with this actor.
     * TODO: This use case - should be explored at some point.
     * TODO: Type Description
     */
    public TypedIOPort wiredInput;
    
    /** Name of the wired input channel. This is a string that defaults to
     *  "WiredInputChannel".
     */
    public StringParameter wiredInputChannelName;
    /** The output port for wired communication, which could potentially facilitate communication with other
     * devices/components/actors which are not wireless that interact with this actor.
     * TODO: This use case - should be explored at some point.
     * TODO: Type Description
     */
    public TypedIOPort wiredOutput;
  
    /** Name of the wired output channel. This is a string that defaults to
     *  "WiredOutputChannel".
     */
    public StringParameter wiredOutputChannelName;  
    /** The input port for wireless communication, which accepts a BluetoothRecordToken - this is to ensure
     * that any RecordToken at this port only comes from another BluetoothDevice.
     * TODO: Type Description.
     */
    public WirelessIOPort wirelessInput;
    
    /** Name of the wireless input channel. This is a string that defaults to
     *  "WirelessInputChannel".
     */
    public StringParameter wirelessInputChannelName;
    /** The output port for wireless communication, which will output a BluetoothRecordToken.
     * TODO: Type Description.
     */
    public WirelessIOPort wirelessOutput;
    
    /** Name of the wireless output channel. This is a string that defaults to
     *  "WirelessOutputChannel".
     */
    public StringParameter wirelessOutputChannelName;
    //TODO: fire() documentation
    /**
     * MISSING DOCUMENTATION
     *  @exception IllegalActionException If there is no director,
     *   or if the parameters cannot be evaluated.
     */
    public void fire() throws IllegalActionException {
        super.fire();
        
        
    }

    //TODO: Actor skeleton.
}
