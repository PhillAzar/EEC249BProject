/**
 * 
 */
package ptolemy.domains.wireless.lib.bluetooth;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ptolemy.actor.Manager;
import ptolemy.actor.TypedAtomicActor;
import ptolemy.actor.TypedIOPort;
import ptolemy.actor.lib.RecordDisassembler;
import ptolemy.actor.util.ConstructAssociativeType;
import ptolemy.actor.util.ExtractFieldType;
import ptolemy.data.RecordToken;
import ptolemy.data.type.BaseType;
import ptolemy.data.type.RecordType;
import ptolemy.graph.Inequality;
import ptolemy.kernel.CompositeEntity;
import ptolemy.kernel.util.IllegalActionException;
import ptolemy.kernel.util.NameDuplicationException;
import ptolemy.kernel.util.NamedObj;
import ptolemy.kernel.util.Workspace;

///////////////////////////////////////////////////////////////////
////RecordAssembler

/**
TODO: Documentation


@author Phillip Azar
@version 
@since 
@Pt.ProposedRating
@Pt.AcceptedRating
@see RecordAssembler
*/
public class BluetoothRecordAssembler extends TypedAtomicActor {

    
    public BluetoothRecordAssembler(CompositeEntity container, String name)
            throws NameDuplicationException, IllegalActionException {
        super(container, name);
        commandOutput = new TypedIOPort(this, "output", false, true);
        commandInput = new TypedIOPort(this, "command", true, false);
        
        commandInput.setTypeEquals(BaseType.STRING);
        commandOutput.setTypeEquals(BaseType.RECORD);

        _attachText("_iconDescription", "<svg>\n"
                + "<rect x=\"0\" y=\"0\" width=\"6\" "
                + "height=\"40\" style=\"fill:red\"/>\n" + "</svg>\n");
    }
    
    ///////////////////////////////////////////////////////////////////
    ////                     ports and parameters                  ////

    public final TypedIOPort commandInput;
    public final TypedIOPort commandOutput;
    
    
    ///////////////////////////////////////////////////////////////////
    ////                         public methods                    ////

    /** Clone the actor into the specified workspace.
     *  @param workspace The workspace for the new object.
     *  @return A new actor.
     *  @exception CloneNotSupportedException If a derived class contains
     *   an attribute that cannot be cloned.
     */
    @Override
    public Object clone(Workspace workspace) throws CloneNotSupportedException {
        BluetoothRecordAssembler newObject = (BluetoothRecordAssembler) super.clone(workspace);
        newObject._portMap = new HashMap<String, TypedIOPort>();
        return newObject;
    }
    
    /** Read a string from the command input port, construct a BluetoothCommandToken based on the string
     * content, package the token into a RecordToken, and send it to the output.
     *  @exception IllegalActionException If there is no director, or if a malformed input was received.
     */
    @Override
    public void fire() throws IllegalActionException {
        int i = 0;
        Set<Entry<String, TypedIOPort>> entries = _portMap.entrySet();
        String[] labels = new String[entries.size()];
        BluetoothWiredCommandToken[] values = new BluetoothWiredCommandToken[entries.size()];

        for (Entry<String, TypedIOPort> entry : entries) {
            labels[i] = entry.getKey();
            String in = entry.getValue().get(0).toString();
            if (in.toLowerCase().equals("\"switchon\"")){
                values[i] = new BluetoothWiredCommandToken(BluetoothWiredCommand.COMMAND_SWITCHON);
            }
            else if (in.toLowerCase().equals("\"switchoff\"")) {
                values[i] = new BluetoothWiredCommandToken(BluetoothWiredCommand.COMMAND_SWITCHOFF);
            }
            else if (in.toLowerCase().equals("\"scan\"")){
                values[i] = new BluetoothWiredCommandToken(BluetoothWiredCommand.COMMAND_SCAN);
            }
            else if (in.toLowerCase().equals("\"stopscan\"")){
                values[i] = new BluetoothWiredCommandToken(BluetoothWiredCommand.COMMAND_STOPSCAN);
            }
            else if (in.toLowerCase().equals("\"connect\"")){
                values[i] = new BluetoothWiredCommandToken(BluetoothWiredCommand.COMMAND_CONNECT);
            }
            else if (in.toLowerCase().equals("\"disconnect\"")){
                values[i] = new BluetoothWiredCommandToken(BluetoothWiredCommand.COMMAND_DISCONNECT);
            }
            else if (in.toLowerCase().equals("\"transmit\"")){
                values[i] = new BluetoothWiredCommandToken(BluetoothWiredCommand.COMMAND_TRANSMIT);
            }
            else {
                throw new IllegalActionException("No command input string was received.");
            }
            i++;
        }

        RecordToken result = new RecordToken(labels, values);

        commandOutput.send(0, result);
    }

    /** React to a name change of contained ports. Update the internal
     *  mapping from names and aliases to port objects, and invalidate
     *  the resolved types.
     *  @param object The object that changed.
     */
    @Override
    public void notifyOfNameChange(NamedObj object) {
        if (object instanceof TypedIOPort) {
            _mapPorts();
        }
    }

    /** Return true if all connected input ports have tokens, false if some
     *  connected input ports do not have a token.
     *  @return True if all connected input ports have tokens and the
     *  parent method returns true.
     *  @exception IllegalActionException If the hasToken() call to the
     *   input port throws it.
     *  @see ptolemy.actor.IOPort#hasToken(int)
     */
    @Override
    public boolean prefire() throws IllegalActionException {
        boolean superReturnValue = super.prefire();
        for (TypedIOPort port : _portMap.values()) {
            if (!port.hasToken(0)) {
                if (_debugging) {
                    _debug("Port " + port.getName()
                            + " does not have a token, prefire()"
                            + " will return false.");
                }
                return false;
            }
        }

        return true && superReturnValue;
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected methods                 ////

    /** Set up and return two type constraints.
     *  <ul>
     *  <li><tt>output >= {x = typeOf(inputPortX), y = typeOf(inputPortY), ..}
     *  </tt>, which requires the types of the input ports to be compatible
     *  with the corresponding types in the output record.
     *  </li>
     *  <li><tt>each input >= the type of the corresponding field inside the
     *  output record</tt>, which together with the first constraint forces
     *  the input types to be exactly equal to the types of the corresponding
     *  fields in the output record. This constraint is intended to back-
     *  propagate type information upstream, not to assure type compatibility.
     *  Therefore, this constraint is only set up for input ports that do not
     *  already have a type declared.</li>
     *  </ul>
     *  Note that the output record is not required to contain a corresponding
     *  field for every input, as downstream actors might require fewer fields
     *  in the record they accept for input.
     *  @return A set of type constraints
     *  @see ConstructAssociativeType
     *  @see ExtractFieldType
     */
    @Override
    protected Set<Inequality> _customTypeConstraints() {
        Set<Inequality> result = new HashSet<Inequality>();

        // make sure the ports are mapped
        _mapPorts();

        // constrain the type of every input to be greater than or equal to
        // the resolved type of the corresponding field in the output record
        for (Entry<String, TypedIOPort> entry : _portMap.entrySet()) {
            String inputName = entry.getKey();
            TypedIOPort input = entry.getValue();
            // only include ports that have no type declared
            if (input.getTypeTerm().isSettable()) {
                result.add(new Inequality(new ExtractFieldType(commandOutput,
                        inputName), input.getTypeTerm()));
            }
        }

        // constrain the fields in the output record to be greater than or
        // equal to the declared or resolved types of the input ports:
        // output >= {x = typeOf(outputPortX), y = typeOf(outputPortY), ..}
        result.add(new Inequality(new ConstructAssociativeType(_portMap
                .values(), RecordType.class), commandOutput.getTypeTerm()));

        return result;
    }

    /** Do not establish the usual default type constraints.
     *  @return null
     */
    @Override
    protected Set<Inequality> _defaultTypeConstraints() {
        return null;
    }

    /** Map port names or aliases to port objects. If the mapping
     *  has changed, then invalidate the resolved types, which
     *  forces new type constraints with appropriate field names
     *  to be generated.
     */
    protected void _mapPorts() {
        // Retrieve the manager.
        Manager manager = this.getManager();

        // Generate a new mapping from names/aliases to ports.
        Map<String, TypedIOPort> oldMap = _portMap;
        _portMap = new HashMap<String, TypedIOPort>();
        for (TypedIOPort p : this.inputPortList()) {
            String name = p.getName();
            String alias = p.getDisplayName();
            // ignore unconnected ports
            if (p.numberOfSources() < 1) {
                continue;
            }
            if (alias == null || alias.equals("")) {
                _portMap.put(name, p);
            } else {
                _portMap.put(alias, p);
            }
        }

        // Only invalidate resolved types if there actually was a name change.
        // As a result, new type constraints will be generated.
        if (manager != null && (oldMap == null || !_portMap.equals(oldMap))) {
            manager.invalidateResolvedTypes();
        }
    }

    ///////////////////////////////////////////////////////////////////
    ////                         protected variables               ////

    /** Keeps track of which name or alias is associated with which port. */
    protected Map<String, TypedIOPort> _portMap;

}
