package gc.client.com;

import gc.client.util.CommandRouter;
import gc.client.util.Util;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.message.BasicNameValuePair;

import connectivity.connectionmanager.HttpSecureSession;
import connectivity.utility.Utility;



/**
 * ServerConnector
 * Do not create a new ServerConnector object for each connection, but re-use this one.
 * The exception is when you need to connect to another server.
 * @author Leif Andreas Rudlang
 */
public class ServerConnector {

	private static final String MULTICAST_GROUP = "228.5.6.7";
	private static final int PORT_BROADCAST = 9090;
	private static final int DEFAULT_SOCKET_TIMEOUT = 5000;

	private static final String POST_FUNCTION = "function";


	private static final String FUNCTION_UPLOAD = "upload";
	private static final String FUNCTION_GET_ALL_IMAGES = "get all";
	private static final String FUNCTION_DELETE_IMAGE = "delete image";
	private static final String FUNCTION_UPDATE_IMAGE = "update image";
	private static final String FUNCTION_SEARCH_IMAGES = "search images";

	private static final String FUNCTION_GET_METADATA = "get metadata";
	private static final String FUNCTION_UPDATE_METADATA = "update metadata";
	private static final String FUNCTION_DELETE_METADATA = "delete metadata";


	private static final String FUNCTION_CREATE_CATEGORY = "create category";
	private static final String FUNCTION_DELETE_CATEGORY = "delete category";
	private static final String FUNCTION_GET_CATEGORIES = "get categories";

	private static final String FUNCTION_GET_COMMENTS = "get comments";
	private static final String FUNCTION_DELETE_COMMENT = "delete comment";
	private static final String FUNCTION_CREATE_COMMENT = "add comment";

	private static final String FUNCTION_GET_TAGS = "get tags";
	private static final String FUNCTION_DELETE_TAG = "delete tag";
	private static final String FUNCTION_CREATE_TAG = "add tag";

	private static final String POST_SUCCESS = "true";
	private static final String POST_KEY = "key";
	private static final String POST_VALUE = "value";
	private static final String POST_ID = "id";
	private static final String POST_NAME = "name";
	private static final String POST_DESCRIPTION = "description";
	private static final String POST_CATEGORY = "category";
	private static final String POST_FILENAME = "filename";
	private static final String POST_USER = "user";
	private static final String POST_DATA = "data";
	private static final String POST_FOLDER = "folder";



	private String server_url;
	private String server_port;
	private String broadcast_address;
	private String server_address;
	private HttpSecureSession session;

	private CommandRouter router;

	/**
	 * @param discover set to true to run a network discovery at creation
	 */
	public ServerConnector(boolean discover){

		setPort("8080");
		setURL("127.0.0.1");

		setup(discover);
	}


	private void setup(boolean discover){


		session = new HttpSecureSession();		

		try {
			broadcast_address = ServerInfo.getBroadcastAddress();
		} catch (SocketException e) {
			broadcast_address = "127.0.0.255";
			e.printStackTrace();
		}


		if(discover){
			ServerInfo info = discover_multicast();

			if(info!=null && info.isComplete()){
				setPort(info.getPort());
				setURL(info.getIP());
			}
		}

		buildRouter();
	}

	private void buildRouter(){


		router = new CommandRouter("server");
		final CommandRouter setUrl = new CommandRouter("seturl");
		setUrl.setFunction(new Runnable(){

			@Override
			public void run() {
				setAddress(setUrl.getParameter());
			}

		});
		router.addRouter(setUrl);


		MainFrame.add(router);
	}





	/////////// SEARCH FUNCTIONS /////////

	/**
	 * Search images with name
	 * @param name
	 * @return
	 */
	public String search(String name){

		try {

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_SEARCH_IMAGES));
			params.add(new BasicNameValuePair(POST_NAME,name));

			String csv = session.post(getAddress(), params, false);

			return csv;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}



	//////// CATEGORY FUNCTIONS ///////////

	/**
	 * Deletes a category
	 * @param id
	 * @return
	 */
	public boolean deleteCategory(String id){

		try {

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_DELETE_CATEGORY));
			params.add(new BasicNameValuePair(POST_ID,id));

			String csv = session.post(getAddress(), params, false);

			return csv.contains(POST_SUCCESS);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}


	/**
	 * Creates a new category
	 * @param id
	 * @param name
	 * @param description
	 * @return
	 */
	public boolean createCategory(String name, String description){

		try {

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_CREATE_CATEGORY));
			params.add(new BasicNameValuePair(POST_NAME,name));
			params.add(new BasicNameValuePair(POST_DESCRIPTION,description));

			String csv = session.post(getAddress(), params, false);

			return csv.contains(POST_SUCCESS);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;

	}


	/**
	 * Returns all categories on the server
	 * @return
	 */
	public String getAllCategories(){

		try {

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_GET_CATEGORIES));

			String csv = session.post(getAddress(), params, false);

			return csv;
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}




	//////////// METADATA FUNCTIONS ////////////

	/**
	 * 
	 * @param imageid
	 * @return
	 */
	public String getMetadata(String imageid){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_GET_METADATA));
			params.add(new BasicNameValuePair(POST_ID,imageid));
			String xml = session.post(getAddress(), params, false);

			return xml;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * 
	 * @param imageid
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean writeMetadata(String imageid, String key, String value){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_UPDATE_METADATA));
			params.add(new BasicNameValuePair(POST_ID,imageid));
			params.add(new BasicNameValuePair(POST_KEY,key));
			params.add(new BasicNameValuePair(POST_VALUE,value));


			String csv = session.post(getAddress(), params, false);

			return csv.contains(POST_SUCCESS);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Deletes a MetaData Tag
	 * @param imageid
	 * @param key
	 * @return
	 */
	public boolean deleteMetadata(String imageid, String key){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_DELETE_METADATA));
			params.add(new BasicNameValuePair(POST_ID,imageid));
			params.add(new BasicNameValuePair(POST_KEY,key));

			String csv = session.post(getAddress(), params, false);

			return csv.contains(POST_SUCCESS);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}


	//////////// IMAGE FUNCTIONS //////////////


	/**
	 * 
	 * @param id
	 * @param name
	 * @param category
	 * @return
	 */
	public boolean updateImage(String id, String name, String category){

		try {

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_UPDATE_IMAGE));
			params.add(new BasicNameValuePair(POST_ID,id));
			params.add(new BasicNameValuePair(POST_NAME,name));
			params.add(new BasicNameValuePair(POST_CATEGORY,category));

			String csv = session.post(getAddress(), params, false);

			return csv.contains(POST_SUCCESS);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Updates a image
	 * @param id
	 * @param filepath
	 * @return
	 */
	public boolean updateImage(String id, String filepath){

		try {

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_UPDATE_IMAGE));
			params.add(new BasicNameValuePair(POST_ID,id));


			String csv = session.postFile(getAddress(), filepath, "", "image/png", params);

			return csv.contains(POST_SUCCESS);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}



	/**
	 * Deletes a image based on the id
	 * @param id
	 * @return
	 */
	public boolean deleteImage(String id){


		try {
			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_DELETE_IMAGE));
			params.add(new BasicNameValuePair(POST_ID,id));


			String csv = session.post(getAddress(), params, false);

			return csv.contains(POST_SUCCESS);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}


	/**
	 * Return all images from the server
	 * @return
	 */
	public String getAllImages(){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_GET_ALL_IMAGES));
			String csv = session.post(getAddress(), params, false);
			return csv;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}




	/**
	 * Get all images by category-id
	 * @param category
	 * @return
	 */
	public String getAllImages(String category){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_GET_ALL_IMAGES));
			params.add(new BasicNameValuePair(POST_CATEGORY,category));
			String csv = session.post(getAddress(), params, false);
			return csv;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}



	/**
	 * Downloads a image from the url
	 * @param url
	 * @return
	 */
	public Image downloadImageEditor(String path){

		try {
			byte[] data = session.get(getAddress()+"/"+path);

			if(data != null){

				Image image = Utility.byteArrayToImage(data);


				return image;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}




	/**
	 * Downloads a image from the url
	 * @param url
	 * @return
	 */
	public Image downloadImage(String path){

		try {
			byte[] data = session.get(getAddress()+"/"+path);

			if(data != null){

				Image image = null;

				if(Util.supportedByToolkit(path)){
					image = Toolkit.getDefaultToolkit().createImage(data);
				}else{
					image = Utility.byteArrayToImage(data);
				}

				return image;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}



	/**
	 * 
	 * @param filepath
	 * @param filename
	 * @param category
	 */
	public boolean uploadImage(String filepath, String filename, String category, String folder){

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair(POST_CATEGORY,category));
		params.add(new BasicNameValuePair(POST_NAME,filename));
		params.add(new BasicNameValuePair(POST_FOLDER,folder));

		return uploadFile(filepath, filename, "image/png", params);
	}



	/**
	 * Upload a file to the server
	 * @param filepath
	 * @param filename
	 * @param mimetype
	 */
	public boolean uploadFile(String filepath, String filename, String mimetype, ArrayList<NameValuePair> params){

		try {		

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_UPLOAD));
			String csv = session.postFile(getAddress(), filepath, filename, mimetype, params);

			return csv.contains(POST_SUCCESS);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}




	//////// COMMENT FUNCTIONS /////////////

	/**
	 * Gets all the comments on the given image.
	 * @param Id of the image to get the comments
	 * @return The comment data in a .CSV format (comment-id, date, user, content)
	 */
	public String getComments(String id){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_GET_COMMENTS));
			params.add(new BasicNameValuePair(POST_ID,id));
			String csv = session.post(getAddress(), params, false);
			return csv;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * Adds a comment to the given image
	 * @param id of the image
	 * @param user who posts the comment
	 * @param content of the comment
	 * @return
	 */
	public boolean createComment(String id, String user, String content){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_CREATE_COMMENT));
			params.add(new BasicNameValuePair(POST_ID,id));
			params.add(new BasicNameValuePair(POST_USER,user));
			params.add(new BasicNameValuePair(POST_DATA,content));
			String csv = session.post(getAddress(), params, false);
			return csv.contains(POST_SUCCESS);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}


	/**
	 * Delete the comment
	 * @param id of the comment (not image-id)
	 * @return
	 */
	public boolean deleteComment(String id){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_DELETE_COMMENT));
			params.add(new BasicNameValuePair(POST_ID,id));
			String csv = session.post(getAddress(), params, false);
			return csv.contains(POST_SUCCESS);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}



	//////// TAG FUNCTIONS /////////////

	/**
	 * Gets all the tags on the given image.
	 * @param Id of the image to get the comments
	 * @return The tag data in a .CSV format (tag-id, data)
	 */
	public String getTags(String id){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_GET_TAGS));
			params.add(new BasicNameValuePair(POST_ID,id));
			String csv = session.post(getAddress(), params, false);
			return csv;

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * Adds a tag to the given image
	 * @param id of the image
	 * @param content of the tag
	 * @return
	 */
	public boolean createTag(String id, String content){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_CREATE_TAG));
			params.add(new BasicNameValuePair(POST_ID,id));
			params.add(new BasicNameValuePair(POST_DATA,content));

			String csv = session.post(getAddress(), params, false);
			return csv.contains(POST_SUCCESS);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}


	/**
	 * Delete the tag
	 * @param id of the tag (not image-id)
	 * @return
	 */
	public boolean deleteTag(String id){

		try {		

			ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair(POST_FUNCTION,FUNCTION_DELETE_TAG));
			params.add(new BasicNameValuePair(POST_ID,id));
			String csv = session.post(getAddress(), params, false);
			return csv.contains(POST_SUCCESS);

		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}



	/////// DISCOVERY FUNCTIONS ////////////

	/**
	 * Runs a network discovery for a GCServer
	 * @return
	 */
	public ServerInfo discover(){

		try {
			return discover(broadcast_address, PORT_BROADCAST);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		return null;
	}


	/**
	 * Runs a network discovery for a GCServer
	 * @param host
	 * @param port
	 * @return
	 * @throws UnknownHostException
	 */
	public ServerInfo discover(String host, int port) throws UnknownHostException{

		DatagramSocket socket = null;


		try {


			socket = new DatagramSocket();
			socket.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);

			// send request
			byte[] buf = new byte[255];
			InetAddress address = InetAddress.getByName(host);
			DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);

			socket.send(packet);

			// get response
			packet = new DatagramPacket(buf, buf.length);
			socket.receive(packet);

			String received = new String(packet.getData(), 0, packet.getLength());

			return new ServerInfo(received,packet.getAddress().toString());

		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			socket.close();
		}

		return null;
	}


	public ServerInfo discover_multicast(){


		MulticastSocket socket = null;
		String received = null;
		DatagramPacket recieved_packet = null;
		InetAddress group = null;

		try {

			socket = new MulticastSocket();
			group = InetAddress.getByName(MULTICAST_GROUP);
			socket.joinGroup(group);
			socket.setSoTimeout(DEFAULT_SOCKET_TIMEOUT);

			// send request
			byte[] buf = new byte[255];
			DatagramPacket packet = new DatagramPacket(buf, buf.length, group, PORT_BROADCAST);

			socket.send(packet);

			// get response
			recieved_packet = new DatagramPacket(buf, buf.length);
			socket.receive(recieved_packet);

			received = new String(packet.getData(), 0, packet.getLength());

		} catch (IOException e) {
			e.printStackTrace();
		}finally{

			try {
				socket.leaveGroup(group);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			socket.close();
		}

		if(received != null && !received.isEmpty() && recieved_packet != null){
			return new ServerInfo(received,recieved_packet.getAddress().toString());
		}

		return null;
	}


	public String getAddress(){
		return server_address;
	}

	public void setAddress(String in){
		this.server_address = in;
	}

	public void setURL(String url){
		this.server_url = url.replaceAll("/", "");
		this.server_address= "http://"+server_url+":"+getPort();		
	}

	public String getURL(){
		return server_url;
	}

	public void setPort(String port){
		this.server_port = port;
	}

	public String getPort(){
		return server_port;
	}


	/**
	 * Destroys the HttpSecureSession object
	 */
	public void destroy(){

		if(session!=null){
			session.destroy();
		}

	}




}
