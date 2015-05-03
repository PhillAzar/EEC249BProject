/**
 * 
 */
package ptolemy.domains.wireless.lib.bluetooth;

///////////////////////////////////////////////////////////////////
////BluetoothDevice

import ptolemy.actor.TypedAtomicActor;
import ptolemy.actor.TypedIOPort;
import ptolemy.data.RecordToken;
import ptolemy.data.Token;
import ptolemy.data.expr.StringParameter;
import ptolemy.domains.wireless.kernel.WirelessDirector;
import ptolemy.domains.wireless.kernel.WirelessIOPort;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;

/**
 * This Actor is simulation of a Bluetooth adapter in a bluetooth enabled device. The simulation is <i>functional<i>,
 * i.e. the hardware is not simulated. Instead, we provide an abstraction based on the Bluetooth dynamics found in
 * popular platforms such as Android and iOS. 
 * <p>
 * The Actor consists of four IO ports: two TypedIOPorts, and two WirelessIOPort. The wired ports simulate the
 * adapters connection to peripheral hardware. The wired input port will accept a RecordToken that consists of a single
 * string that maps to a Token. The String is the hardware "command" that request a specific action from the actor.
 * These commands, which can be found below, must be found in the first entry of RecordToken, or an IllegalActionException
 * will be thrown.
 * <p>
 * The dynamics of the actor are evaluated via a state machine found in the fire() method. The state machine will evaluate
 * the RecordToken at the wired input before evaluating any BluetoothRecordToken at the wireless input ports.
 * <p>
 *@author Phillip Azar
 *@version 
 *@since
 *@Pt.ProposedRating
 *@Pt.AcceptedRating 
 *
 */

enum States {
    STATE_IDLE,
    STATE_CONNECTED,
    STATE_SCANNING,
    STATE_OFF,
    STATE_ON
}

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
        
        state = States.STATE_OFF;
        
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
    
    private States state;
    
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
        
        /**
         * The following switch case structure is the state machine that controls the main dyanmics of the
         * Actor. Here, depending on the current state of the actor, we will evaluate tokens at each input and
         * perform actions. Each fire() will only evaluate one input, with the wired input, in this case, taking
         * priority over the wireless input. The wireless input will evaluate with priority ONLY when the device is
         * in the connected state. The only exception to this case is when a record token with a disconnect command
         * is received to the wired input.
         * 
         * TODO: Work in Progress state machine
         */
        RecordToken _wiredInputToken = new RecordToken();
        RecordToken _wirelessInputToken = new RecordToken();
        
        if (!(getDirector() instanceof WirelessDirector) ){
            throw new IllegalActionException(this.getClassName() + ": Cannot execute without WirelessDirector.");
        }
        
        if (wiredInput.hasToken(0)){
            _wiredInputToken = (RecordToken) wiredInput.get(0);
        }
        if (wirelessInput.hasToken(0)){
            _wirelessInputToken = (RecordToken) wirelessInput.get(0);
        }
        
        
        switch(state){
            case STATE_OFF:
                if (_wiredInputToken.get("command").equals(BluetoothCommands.COMMAND_SWITCHON)){
                    this.state = States.STATE_ON;
                    this.wiredOutput.send(0, new BluetoothStatusToken(BluetoothStatus.STATUS_OK));
                }
                break;
            case STATE_ON:
                
                break;
            case STATE_IDLE:
                
                break;
            case STATE_CONNECTED:
                
                break;
            case STATE_SCANNING:
                
                break;
            default:
                
                break;
        
    }
        
    }
    
    //TODO: Actor skeleton.
}
