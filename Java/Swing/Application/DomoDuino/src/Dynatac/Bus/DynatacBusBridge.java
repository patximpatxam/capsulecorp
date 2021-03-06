package Dynatac.Bus;

import java.util.ArrayList;
import java.util.List;

import Dynatac.Bus.IDynatacBus.IDynatacBusListener;


public class DynatacBusBridge extends DynatacBusCommon implements IDynatacBusListener {
	
	/**
	 * 
	 * 
	 *
	 */	
	
	/****************************************
	 *  OBJECT CONSTRUCTION	
	 ****************************************/
	public DynatacBusBridge () {
		connectedBuses_ = true;
	}
	
	public DynatacBusBridge (IDynatacBus [] buses) {
		connectedBuses_ = true;
		
		for (int i = 0; i<buses.length; i++)
		{
			addBus (buses[i]);
		}
	}
	
	public DynatacBusBridge (boolean busesAreConnected) {
		connectedBuses_ = busesAreConnected;
	}
	
	public DynatacBusBridge (IDynatacBus [] buses, boolean busesAreConnected) {
		connectedBuses_ = busesAreConnected;
		
		for (int i = 0; i<buses.length; i++)
		{
			addBus (buses[i]);
		}
	}
	
	/****************************************
	 *  PUBLIC METHODS 						
	 ****************************************/
	public void write(String data) {
		for (int z = 0; z<buses_.size(); z++)
		{
			IDynatacBus b = buses_.get(z);
					
			b.write(data);
		}
	}
	
	public void dataAvailable(String data, IDynatacBus bus) {
		notifyListeners (data);
		
		for (int z = 0; z<buses_.size(); z++)
		{
			IDynatacBus b = buses_.get(z);
			
			if (b != bus && connectedBuses_) // forward information...
			{		
				b.write(data);
			}
		}
	}

	public void onStatusChange(int busStatus) {
		if ((busStatus & DYNATAC_BUS_STATUS_UNAVAILABLE) != 0)
		{
			setStatus (DYNATAC_BUS_STATUS_CONNECTION_PROBLEM);	
		}
	}

	
	public void addBus (IDynatacBus aBus)
	{
		aBus.installListener(this);
		buses_.add(aBus);
	}
	
	public void removeBus (IDynatacBus aBus)
	{
		aBus.removeListener (this);
		buses_.remove(aBus);
	}
		
	/****************************************
	 *  INTERNAL METHODS 						
	 ****************************************/
	
	/****************************************
	 *  INTERNAL VARS 						
	 ****************************************/	
	private List<IDynatacBus> buses_ = new ArrayList<IDynatacBus>();
	boolean connectedBuses_;
}
	
