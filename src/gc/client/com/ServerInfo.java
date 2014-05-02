package gc.client.com;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerInfo {

	private static final String PORT_PREFIX = "port";
	
	private String ip;
	private String port;

	/**
	 * 
	 * @param data
	 */
	public ServerInfo(String data, String ip){

		this.ip = ip;
		port = seperate(PORT_PREFIX, data);			
	}


	/**
	 * Returns the ip address
	 * @return
	 */
	public String getIP(){
		return ip;
	}

	/**
	 * Returns the port number
	 * @return
	 */
	public String getPort(){
		return port;
	}

	/**
	 * Returns true if this object has a ip and a port stored
	 * @return
	 */
	public boolean isComplete(){
		return port!=null && ip!=null;
	}


	private String seperate(String name, String data){

		if(!data.contains(name) || !data.contains("=") || !data.contains(";")){
			return null;
		}

		int idx0 = data.indexOf(name);
		int idx1 = data.indexOf('=', idx0);
		int idx2 = data.indexOf(';',idx1);

		String value = data.substring(Math.min(data.length(), idx1+1),Math.min(data.length(), idx2));

		return value;
	}




	/**
	 * Discover the local broadcast address
	 * (part of this code borrowed from stackoverflow)
	 * @return
	 * @throws SocketException
	 */
	public static String getBroadcastAddress() throws SocketException {

		for (Enumeration<NetworkInterface> niEnum = NetworkInterface.getNetworkInterfaces(); niEnum.hasMoreElements();) {
			NetworkInterface ni = niEnum.nextElement();
			if (!ni.isLoopback()) {
				for (InterfaceAddress interfaceAddress : ni.getInterfaceAddresses()) {

					InetAddress inet = interfaceAddress.getBroadcast();

					if(inet!=null){          		
						return inet.toString().substring(1);
					}
				}
			}
		}

		return null;
	}
}
