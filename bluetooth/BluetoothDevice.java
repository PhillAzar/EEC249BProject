/**
 * 
 */
package ptolemy.domains.wireless.lib.bluetooth;

///////////////////////////////////////////////////////////////////
////BluetoothDevice

import java.util.HashSet;

import ptolemy.actor.TypedAtomicActor;
import ptolemy.actor.TypedIOPort;
import ptolemy.data.StringToken;
import ptolemy.data.Token;
import ptolemy.data.expr.StringParameter;
import ptolemy.data.type.BaseType;
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
    STATE_PAIRINITIATED,
    STATE_OFF,
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
        _foundDevices = new HashSet<String>();
        _pairedDevices = new HashSet<String>();
        _connectedDevices = new HashSet<String>();
        _discoverable = false;
        
        wirelessInputChannelName = new StringParameter(this, "wirelessInputChannelName");
        wirelessInputChannelName.setExpression("WirelessInputChannel");
        
        wirelessOutputChannelName = new StringParameter(this, "wirelessOutputChannelName");
        wirelessOutputChannelName.setExpression("WirelessOutputChannel");
        
        wiredInput = new TypedIOPort(this, "Wired Input", true, false);
        wiredInputDetails = new TypedIOPort(this, "Wired Input - Detail", true, false);
        wiredOutput = new TypedIOPort(this, "Wired Output", false, true);
        
        wiredInput.setTypeEquals(BaseType.STRING);
        wiredInputDetails.setTypeEquals(BaseType.STRING);
        wiredOutput.setTypeEquals(BaseType.GENERAL);
        
        wirelessInput = new WirelessIOPort(this, "Wireless Input", true, false);
        wirelessInput.outsideChannel.setExpression("$wirelessInputChannelName");
        
        wirelessOutput = new WirelessIOPort(this, "Wireless Output", false, true);
        wirelessOutput.outsideChannel.setExpression("$wirelessOutputChannelName");
        
        wirelessInput.setTypeEquals(BaseType.GENERAL);
        wirelessOutput.setTypeEquals(BaseType.GENERAL);
        
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                     ports and parameters                  ////
    
    //TODO: Input Ports, Parameters, Lists, etc.
        
    /** The input port for wired communication, which could potentially facilitate communication with other
     * devices/components/actors which are not wireless that interact with this actor.
     * This port is of type String, and will be checked internally against a list of valid commands.
     */
    public TypedIOPort wiredInput;
    
    /**
     * The input port for details about wired communications, which will contain a device identifier or
     * data to send. The type for this port is declared as General, but will internally check for either an ObjectToken
     * or string. 
     */
    public TypedIOPort wiredInputDetails;   
    
    /** The output port for wired communication, which could potentially facilitate communication with other
     * devices/components/actors which are not wireless that interact with this actor.
     * This port is of type General.
     */
    public TypedIOPort wiredOutput;
  
    /** The input port for wireless communication, which accepts a BluetoothRecordToken - this is to ensure
     * that any RecordToken at this port only comes from another BluetoothDevice.
     * This port is of type General.
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
        
        if (!(getDirector() instanceof WirelessDirector) ){
            throw new IllegalActionException(this.getClassName() + ": Cannot execute without WirelessDirector.");
        }
        
        StringToken _wiredInputToken;
        Token _wiredinputExtra;

        
        if (wiredInput.hasToken(0)){
            _wiredInputToken = (StringToken) wiredInput.get(0);
        }
        else {
            _wiredInputToken = new StringToken("empty");
        }
            BluetoothWiredCommand command;
            switch(_wiredInputToken.stringValue()){
                case "switchon":
                    command = BluetoothWiredCommand.COMMAND_SWITCHON;
                    break;
                case "switchoff":
                    command = BluetoothWiredCommand.COMMAND_SWITCHOFF;
                    break;
                case "scan":
                    command = BluetoothWiredCommand.COMMAND_SCAN;
                    break;
                case "stopscan":
                    command = BluetoothWiredCommand.COMMAND_STOPSCAN;
                    break;
                case "connect":
                    command = BluetoothWiredCommand.COMMAND_CONNECT;
                    break;
                case "disconnect":
                    command = BluetoothWiredCommand.COMMAND_DISCONNECT;
                    break;
                case "pair":
                    command = BluetoothWiredCommand.COMMAND_PAIR;
                    break;
                case "discoverable":
                    command = BluetoothWiredCommand.COMMAND_DISCOVERABLE;
                    break;
                case "hide":
                    command = BluetoothWiredCommand.COMMAND_HIDE;
                    break;
                case "empty":
                    command = BluetoothWiredCommand.COMMAND_NOCOMMAND;
                default:
                    command = null;
                }
            
            if (command == null) {
                throw new IllegalActionException("Input string does not equal supported command value");
            }
            /**
             * The following switch case structure is the state machine that controls the main dynamics of the
             * Actor. Here, depending on the current state of the actor, we will evaluate tokens at each input and
             * perform actions. Each fire() will only evaluate one input, with the wired input, in this case, taking
             * priority over the wireless input. The wireless input will evaluate with priority ONLY when the device is
             * in the connected state. The only exception to this case is when a record token with a disconnect command
             * is received to the wired input.
             * 
             * TODO: Work in Progress state machine
             */
            BluetoothStatusToken status;

            switch(state){
                case STATE_OFF:
                    if (command.equals(BluetoothWiredCommand.COMMAND_SWITCHON)){
                        this.state = States.STATE_IDLE;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else {
                        return;
                    }
                case STATE_IDLE:
                    if (command.equals(BluetoothWiredCommand.COMMAND_SWITCHOFF)){
                        this.state = States.STATE_OFF;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_SCAN)){
                        this.state = States.STATE_SCANNING;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_CONNECT)){
                        if (wiredInputDetails.hasToken(0)){
                            String deviceToConnect = ((StringToken) wiredInputDetails.get(0)).stringValue();
                            if (this._pairedDevices.contains(deviceToConnect)) {
                                this.state = States.STATE_CONNECTED;
                                BluetoothResponseToken connectRequest = new BluetoothResponseToken(BluetoothResponse.COMMAND_REQUESTCONNECT, deviceToConnect, this.getName());
                                wirelessOutput.send(0, connectRequest);
                                status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Attempting to connect to:"+deviceToConnect);
                                wiredOutput.send(0, status);  
                            }
                            else {
                                throw new IllegalActionException("Cannot connect to an unpaired device.");
                            }
                        }
                        else {
                            throw new IllegalActionException("WiredInputDetails port must be filled with device identifier to command connection");
                        }
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_DISCOVERABLE)){
                        this._discoverable = true;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_HIDE)){
                        this._discoverable = false;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    
                    while(this.wirelessInput.hasToken(0)){
                        Token _token = wirelessInput.get(0);
                        if (_token instanceof BluetoothResponseToken) {
                            BluetoothResponseToken _newResponse = (BluetoothResponseToken) _token;
                            if (_newResponse.getDeviceIdentifier().equals(this.getName())){
                                if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_REQUESTCONNECT)){
                                    if (this._pairedDevices.contains(_newResponse.getSourceIdentifier())){
                                        this.state = States.STATE_CONNECTED;
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_ACCEPT, _newResponse.getSourceIdentifier(), this.getName()));
                                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Received connection request from:"+_newResponse.getSourceIdentifier());
                                        wiredOutput.send(0, status);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case STATE_CONNECTED:
                    if (command.equals(BluetoothWiredCommand.COMMAND_SWITCHOFF)){
                        this.state = States.STATE_OFF;
                        for (String device : this._connectedDevices){
                            BluetoothResponseToken shuttingOff = new BluetoothResponseToken(BluetoothResponse.COMMAND_DISCONNECT, device, this.getName());
                            wirelessOutput.send(0, shuttingOff);
                        }
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_SCAN)){
                        this.state = States.STATE_SCANNING;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_CONNECT)){
                        if (wiredInputDetails.hasToken(0)){
                            String deviceToConnect = ((StringToken) wiredInputDetails.get(0)).stringValue();
                            if (this._pairedDevices.contains(deviceToConnect)) {
                                BluetoothResponseToken connectRequest = new BluetoothResponseToken(BluetoothResponse.COMMAND_REQUESTCONNECT, deviceToConnect, this.getName());
                                wirelessOutput.send(0, connectRequest);
                                status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Attempting to connect to:"+deviceToConnect);
                                wiredOutput.send(0, status);  
                            }
                            else {
                                throw new IllegalActionException("Cannot connect to an unpaired device.");
                            }
                        }
                        else {
                            throw new IllegalActionException("WiredInputDetails port must be filled with device identifier to command connection");
                        }
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_DISCOVERABLE)){
                        this._discoverable = true;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_HIDE)){
                        this._discoverable = false;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    while(this.wirelessInput.hasToken(0)){
                        Token _token = wirelessInput.get(0);
                        if (_token instanceof BluetoothResponseToken) {
                            BluetoothResponseToken _newResponse = (BluetoothResponseToken) _token;
                            if (_newResponse.getDeviceIdentifier().equals(this.getName())){
                                if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_ACCEPT)) {
                                    this._connectedDevices.add(_newResponse.getDeviceIdentifier());
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_DENY)) {
                                    continue;
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_OK)){
                                    if (this._connectedDevices.contains(_newResponse.getSourceIdentifier())){
                                        //TODO: left off here! 5/10/2015
                                    }
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_REQUESTCONNECT)){
                                    if (this._pairedDevices.contains(_newResponse.getSourceIdentifier())){
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_ACCEPT, _newResponse.getSourceIdentifier(), this.getName()));
                                    }
                                }
                            }
                        }
                    }
                    break;
                case STATE_SCANNING:
                    if (command.equals(BluetoothWiredCommand.COMMAND_SWITCHOFF)){
                        this.state = States.STATE_OFF;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_STOPSCAN)){
                        this.state = States.STATE_IDLE;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    while (wirelessInput.hasToken(0)) {
                        Token _token = wirelessInput.get(0);
                        if (_token instanceof BluetoothResponseToken) {
                            BluetoothResponseToken _newResponse = (BluetoothResponseToken) _token;
                            if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_FINDME)){
                                _foundDevices.add(_newResponse.getDeviceIdentifier());                            
                            }
                        }
                    }                    
                    break;
                case STATE_PAIRINITIATED:
                    
                    if (command.equals(BluetoothWiredCommand.COMMAND_SWITCHOFF)){
                        this.state = States.STATE_OFF;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_STOPSCAN)){
                        this.state = States.STATE_IDLE;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                default:
                    status = new BluetoothStatusToken(BluetoothStatus.STATUS_ERROR, "empty");
                    this.wiredOutput.send(0, status);
                    break;         
            }
        

    }
    
    ///////////////////////////////////////////////////////////////////
    ////                         private variables                 ////
    
    private HashSet<String> _foundDevices;
    private HashSet<String> _pairedDevices;
    private HashSet<String> _connectedDevices; 
    private States state;
    private boolean _discoverable;
   
}
