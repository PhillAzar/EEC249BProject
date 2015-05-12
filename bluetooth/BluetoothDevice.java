/**
 * 
 */
package ptolemy.domains.wireless.lib.bluetooth;

///////////////////////////////////////////////////////////////////
////BluetoothDevice

import java.util.HashSet;

import ptolemy.actor.TypedAtomicActor;
import ptolemy.actor.TypedIOPort;
import ptolemy.data.ObjectToken;
import ptolemy.data.StringToken;
import ptolemy.data.Token;
import ptolemy.data.expr.Parameter;
import ptolemy.data.expr.StringParameter;
import ptolemy.data.type.BaseType;
import ptolemy.domains.wireless.kernel.WirelessDirector;
import ptolemy.domains.wireless.kernel.WirelessIOPort;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.StringAttribute;

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
        new Parameter(wiredInput, "_showName").setExpression("true");
        
        wiredInputDetails = new TypedIOPort(this, "Wired Input - Detail", true, false);
        new Parameter(wiredInputDetails, "_showName").setExpression("true");
        
        wiredInputData = new TypedIOPort(this, "Wired Input - Data", true, false);
        new Parameter(wiredInputData, "_showName").setExpression("true");
        
        wiredOutput = new TypedIOPort(this, "Wired Output", false, true);
        new Parameter(wiredOutput, "_showName").setExpression("true");
        
        wiredInput.setTypeEquals(BaseType.STRING);
        wiredInputDetails.setTypeEquals(BaseType.STRING);
        wiredOutput.setTypeEquals(BaseType.OBJECT);
        
        wirelessInput = new WirelessIOPort(this, "Wireless Input", true, false);
        new Parameter(wirelessInput, "_showName").setExpression("true");
        new StringAttribute(wirelessInput, "_cardinal").setExpression("SOUTH");
        wirelessInput.outsideChannel.setExpression("$wirelessInputChannelName");
        
        wirelessOutput = new WirelessIOPort(this, "Wireless Output", false, true);
        new Parameter(wirelessOutput, "_showName").setExpression("true");
        new StringAttribute(wirelessOutput, "_cardinal").setExpression("SOUTH");
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
     * The input port for details about wired communications, which will contain a device identifier. The type of this port is String.
     */
    public TypedIOPort wiredInputDetails;
    
    /**
     * The input port for data to be sent. This will only be checked when the command to send data has been issued, and further will only be checked when in the
     * connected state. This port is of type Object.
     */
    public TypedIOPort wiredInputData;
    
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
        Token _wiredInputExtra;
        Token _wiredInputData;

        
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
            case "unpair":
                command = BluetoothWiredCommand.COMMAND_UNPAIR;
                break;
            case "discoverable":
                command = BluetoothWiredCommand.COMMAND_DISCOVERABLE;
                break;
            case "hide":
                command = BluetoothWiredCommand.COMMAND_HIDE;
                break;
            case "senddata":
                command = BluetoothWiredCommand.COMMAND_SENDDATA;
                break;
            default:
                command = BluetoothWiredCommand.COMMAND_NOCOMMAND;
            }
            
            if (command == null) {
                throw new IllegalActionException("Input string does not equal supported command value");
            }
            
            if (wiredInputDetails.hasToken(0)){
                _wiredInputExtra = (StringToken) wiredInputDetails.get(0);
            }
            else {
                _wiredInputExtra = new StringToken("empty");
            }
            
            if (wiredInputData.hasToken(0)){
                _wiredInputData = (ObjectToken) wiredInputData.get(0);
            }
            else {
                _wiredInputData = new ObjectToken("");
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
                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.COMMAND_SCAN, "scan", this.getName(), "" ));
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_CONNECT)){
                        if (_wiredInputExtra instanceof StringToken) {
                            String deviceToConnect = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._pairedDevices.contains(deviceToConnect)) {
                                BluetoothResponseToken connectRequest = new BluetoothResponseToken(BluetoothResponse.COMMAND_REQUESTCONNECT, deviceToConnect, this.getName(), "");
                                wirelessOutput.send(0, connectRequest);
                                status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Attempting to connect to:"+deviceToConnect);
                                wiredOutput.send(0, status);  
                            }
                            else {
                                throw new IllegalActionException("Cannot connect to an unpaired device.");
                            }
                        }
                        else {
                            throw new IllegalActionException("Invalid device identifier");
                        }
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_DISCOVERABLE)){
                        this._discoverable = true;
                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "empty");
                        this.wiredOutput.send(0, status);
                        break;
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_PAIR)){
                        if (_wiredInputExtra instanceof StringToken){
                            String deviceToPair = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._foundDevices.contains(deviceToPair) && (!this._pairedDevices.contains(deviceToPair))){
                                this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.COMMAND_REQUESTPAIR, deviceToPair, this.getName(), ""));
                            }
                        }
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_UNPAIR)){
                        if (_wiredInputExtra instanceof StringToken){
                            String deviceToUnpair = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._foundDevices.contains(deviceToUnpair) && (this._pairedDevices.contains(deviceToUnpair))){
                                // Here is a behavior which is particularly interesting in Bluetooth - when you unpair, you don't tell the paired device anything. You just remove it from your list of paired devices and move on.
                                this._pairedDevices.remove(deviceToUnpair);
                            }
                        }
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
                                    // For now, we accept all connection requests.
                                    if (this._pairedDevices.contains(_newResponse.getSourceIdentifier())){
                                        this.state = States.STATE_CONNECTED;
                                        this._connectedDevices.add(_newResponse.getSourceIdentifier());
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_ACCEPTCONNECT, _newResponse.getSourceIdentifier(), this.getName(), ""));
                                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Received connection request from:"+_newResponse.getSourceIdentifier());
                                        wiredOutput.send(0, status);
                                    }
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_REQUESTPAIR)){
                                    // For now, we accept all pair requests.
                                    if (this._foundDevices.contains(_newResponse.getSourceIdentifier())){
                                        this._pairedDevices.add(_newResponse.getSourceIdentifier());
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_ACCEPTPAIR, _newResponse.getSourceIdentifier(), this.getName(), ""));
                                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Received pair request from:"+_newResponse.getSourceIdentifier());
                                        wiredOutput.send(0, status);
                                    }
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_ACCEPTPAIR)) {
                                    this._pairedDevices.add(_newResponse.getSourceIdentifier());
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_ACCEPTCONNECT)) {
                                    this.state = States.STATE_CONNECTED;
                                    this._connectedDevices.add(_newResponse.getSourceIdentifier());
                                }
                            }
                            else if (_newResponse.getDeviceIdentifier().equals("scan")){
                                if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_SCAN)){
                                    if (this._discoverable && (!this._foundDevices.contains(_newResponse.getSourceIdentifier()))){
                                        this._foundDevices.add(_newResponse.getSourceIdentifier());
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_FINDME, _newResponse.getSourceIdentifier(), this.getName(), ""));
                                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Received scan request from:"+_newResponse.getSourceIdentifier());
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
                            BluetoothResponseToken shuttingOff = new BluetoothResponseToken(BluetoothResponse.COMMAND_DISCONNECT, device, this.getName(), "");
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
                        if (_wiredInputExtra instanceof StringToken) {
                            String deviceToConnect = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._pairedDevices.contains(deviceToConnect)) {
                                BluetoothResponseToken connectRequest = new BluetoothResponseToken(BluetoothResponse.COMMAND_REQUESTCONNECT, deviceToConnect, this.getName(), "");
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
                    else if (command.equals(BluetoothWiredCommand.COMMAND_DISCONNECT)){
                        if (_wiredInputExtra instanceof StringToken) {
                            String deviceToDisconnect = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._connectedDevices.contains(deviceToDisconnect)) {
                                this._connectedDevices.remove(deviceToDisconnect);
                                BluetoothResponseToken disconnectRequest = new BluetoothResponseToken(BluetoothResponse.COMMAND_DISCONNECT, deviceToDisconnect, this.getName(), "");
                                wirelessOutput.send(0, disconnectRequest);
                                status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Disconnecting from: "+deviceToDisconnect);
                                wiredOutput.send(0, status);  
                            }
                            else {
                                throw new IllegalActionException("Cannot disconnect from a device that is not connected.");
                            }
                        }
                        else {
                            throw new IllegalActionException("WiredInputDetails port must be filled with device identifier to command disconnection");
                        }
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_PAIR)){
                        if (_wiredInputExtra instanceof StringToken){
                            String deviceToPair = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._foundDevices.contains(deviceToPair) && (!this._pairedDevices.contains(deviceToPair))){
                                this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.COMMAND_REQUESTPAIR, deviceToPair, this.getName(), ""));
                            }
                        }
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_UNPAIR)){
                        if (_wiredInputExtra instanceof StringToken){
                            String deviceToUnpair = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._foundDevices.contains(deviceToUnpair) && (this._pairedDevices.contains(deviceToUnpair) && (!this._connectedDevices.contains(deviceToUnpair)))){
                                this._pairedDevices.remove(deviceToUnpair);
                            }
                            else if (this._foundDevices.contains(deviceToUnpair) && (this._pairedDevices.contains(deviceToUnpair) && (this._connectedDevices.contains(deviceToUnpair)))){
                                this._connectedDevices.remove(deviceToUnpair);
                                this._pairedDevices.remove(deviceToUnpair);
                            }
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
                    else if (command.equals(BluetoothWiredCommand.COMMAND_SENDDATA)){
                        if (_wiredInputExtra instanceof StringToken){
                            String deviceToSendData = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._connectedDevices.contains(deviceToSendData)){
                                this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_OK, deviceToSendData, this.getName(), _wiredInputData));
                            }
                        }
                    }
                    while(this.wirelessInput.hasToken(0)){
                        Token _token = wirelessInput.get(0);
                        if (_token instanceof BluetoothResponseToken) {
                            BluetoothResponseToken _newResponse = (BluetoothResponseToken) _token;
                            if (_newResponse.getDeviceIdentifier().equals(this.getName())){
                                if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_ACCEPTCONNECT)) {
                                    this._connectedDevices.add(_newResponse.getDeviceIdentifier());
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_OK)){
                                    if (this._connectedDevices.contains(_newResponse.getSourceIdentifier())){
                                        BluetoothStatusToken<?> _newData = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, _newResponse.getData());
                                        wiredOutput.send(0, _newData);
                                    }
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_REQUESTPAIR)){
                                    // For now, we accept all pair requests.
                                    if (this._foundDevices.contains(_newResponse.getSourceIdentifier())){
                                        this._pairedDevices.add(_newResponse.getSourceIdentifier());
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_ACCEPTPAIR, _newResponse.getSourceIdentifier(), this.getName(), ""));
                                    }
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_REQUESTCONNECT) && (this._connectedDevices.size() <= 7)){
                                    // We will only connect to a new device if we don't have more then 7 connections open.
                                    // See: http://en.wikipedia.org/wiki/Bluetooth#Implementation
                                    if (this._pairedDevices.contains(_newResponse.getSourceIdentifier())){
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_ACCEPTCONNECT, _newResponse.getSourceIdentifier(), this.getName(), ""));
                                        this._connectedDevices.add(_newResponse.getSourceIdentifier());
                                    }
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_DISCONNECT)){
                                    if (this._connectedDevices.contains(_newResponse.getSourceIdentifier())){
                                        this._connectedDevices.remove(_newResponse.getSourceIdentifier());
                                    }
                                }
                            }
                            else if (_newResponse.getDeviceIdentifier().equals("scan")){
                                if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_SCAN)){
                                    if (this._discoverable && (!this._foundDevices.contains(_newResponse.getSourceIdentifier()))){
                                        this._foundDevices.add(_newResponse.getSourceIdentifier());
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_FINDME, _newResponse.getSourceIdentifier(), this.getName(), ""));
                                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Received scan request from:"+_newResponse.getSourceIdentifier());
                                        wiredOutput.send(0, status);
                                    }
                                }
                            }
                        }
                    }
                    if (this.state.equals(States.STATE_CONNECTED) && this._connectedDevices.isEmpty()) {
                        this.state = States.STATE_IDLE;
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
                    else if (command.equals(BluetoothWiredCommand.COMMAND_PAIR)){
                        if (_wiredInputExtra instanceof StringToken){
                            String deviceToPair = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._foundDevices.contains(deviceToPair) && (!this._pairedDevices.contains(deviceToPair))){
                                this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.COMMAND_REQUESTPAIR, deviceToPair, this.getName(), ""));
                            }
                        }
                    }
                    else if (command.equals(BluetoothWiredCommand.COMMAND_CONNECT)){
                        if (_wiredInputExtra instanceof StringToken) {
                            String deviceToConnect = ((StringToken) _wiredInputExtra).stringValue();
                            if (this._pairedDevices.contains(deviceToConnect)) {
                                BluetoothResponseToken connectRequest = new BluetoothResponseToken(BluetoothResponse.COMMAND_REQUESTCONNECT, deviceToConnect, this.getName(), "");
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
                    else if (command.equals(BluetoothWiredCommand.COMMAND_NOCOMMAND)){
                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.COMMAND_SCAN, "scan", this.getName(), "" ));
                    }
                    while (wirelessInput.hasToken(0)) {
                        Token _token = wirelessInput.get(0);
                        if (_token instanceof BluetoothResponseToken) {
                            BluetoothResponseToken _newResponse = (BluetoothResponseToken) _token;
                            if (_newResponse.getDeviceIdentifier().equals(this.getName())){
                                if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_FINDME)){
                                    _foundDevices.add(_newResponse.getSourceIdentifier());                            
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_REQUESTCONNECT)){
                                    if (this._pairedDevices.contains(_newResponse.getSourceIdentifier())){
                                        this.state = States.STATE_CONNECTED;
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_ACCEPTCONNECT, _newResponse.getSourceIdentifier(), this.getName(), ""));
                                        status = new BluetoothStatusToken(BluetoothStatus.STATUS_OK, "Received connection request from:"+_newResponse.getSourceIdentifier());
                                        wiredOutput.send(0, status);
                                    }
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.COMMAND_REQUESTPAIR)){
                                    // For now, we accept all pair requests.
                                    if (this._foundDevices.contains(_newResponse.getSourceIdentifier())){
                                        this._pairedDevices.add(_newResponse.getSourceIdentifier());
                                        this.wirelessOutput.send(0, new BluetoothResponseToken(BluetoothResponse.RESPONSE_ACCEPTPAIR, _newResponse.getSourceIdentifier(), this.getName(), ""));
                                    }
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_ACCEPTPAIR)) {
                                    this._pairedDevices.add(_newResponse.getSourceIdentifier());
                                }
                                else if (_newResponse.getResponse().equals(BluetoothResponse.RESPONSE_ACCEPTCONNECT)) {
                                    this.state = States.STATE_CONNECTED;
                                    this._connectedDevices.add(_newResponse.getSourceIdentifier());
                                }
                            }
                        }
                    }                    
                    break;
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
