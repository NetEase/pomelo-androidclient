package com.netease.pomelo;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

public class PomeloClient {

	private static final Logger logger = Logger.getLogger("org.netease.pomelo");
	private final static String URLHEADER = "http://";
	private final static String JSONARRAY_FLAG = "[";
	private int reqId;
	private PomeloClient client;
	private SocketIO socket;
	private Map<Integer, DataCallBack> cbs;
	private Map<String, List<DataListener>> listeners;

	public PomeloClient(String url, int port) {
		initSocket(url, port);
		cbs = new HashMap<Integer, DataCallBack>();
		listeners = new HashMap<String, List<DataListener>>();
	}

	/**
	 * Init the socket of pomelo client.
	 * 
	 * @param url
	 * @param port
	 */
	private void initSocket(String url, int port) {
		StringBuffer buff = new StringBuffer();
		if (!url.contains(URLHEADER))
			buff.append(URLHEADER);
		buff.append(url);
		buff.append(":");
		buff.append(port);
		try {
			socket = new SocketIO(buff.toString());
		} catch (MalformedURLException e) {
			throw new RuntimeException("please check your url format.");
		}
	}

	/**
	 * Connect to the server side and return a pomelo client.
	 * 
	 * @return client
	 */
	public PomeloClient init() {
		socket.connect(new IOCallback() {
			public void onConnect() {
				logger.info("pomeloclient is connected.");
			}

			public void onMessage(JSONObject json, IOAcknowledge ack) {
				logger.warning("pomelo send message of string.");
			}

			// get messages from the server side
			public void onMessage(String data, IOAcknowledge ack) {
				if (data.indexOf(JSONARRAY_FLAG) == 0) {
					processMessageBatch(data);
				} else {
					processMessage(data);
				}
			}

			public void onError(SocketIOException socketIOException) {
				socketIOException.printStackTrace();
			}

			public void onDisconnect() {
				logger.info("connection is terminated.");
				socket = null;
			}

			public void on(String event, IOAcknowledge ack, Object... args) {
				logger.info("socket.io emit events.");
			}

		});
		return client;
	}

	/**
	 * Send message to the server side.
	 * 
	 * @param reqId
	 *            request id
	 * @param route
	 *            request route
	 * @param msg
	 *            reqest message
	 */
	private void sendMessage(int reqId, String route, JSONObject msg) {
		socket.send(Protocol.encode(reqId, route, msg));
	}

	/**
	 * Client send request to the server and get response data.
	 * 
	 * @param args
	 */
	public void request(Object... args) {
		if (args.length < 2 || args.length > 3) {
			throw new RuntimeException("the request arguments is error.");
		}
		// first argument must be string
		if (!(args[0] instanceof String)) {
			throw new RuntimeException("the route of request is error.");
		}

		String route = args[0].toString();
		JSONObject msg = null;
		DataCallBack cb = null;

		if (args.length == 2) {
			if (args[1] instanceof JSONObject)
				msg = (JSONObject) args[1];
			else if (args[1] instanceof DataCallBack)
				cb = (DataCallBack) args[1];
		} else {
			msg = (JSONObject) args[1];
			cb = (DataCallBack) args[2];
		}
		msg = filter(msg);
		reqId++;
		cbs.put(reqId, cb);
		sendMessage(reqId, route, msg);
	}

	/**
	 * Notify the server without response
	 * 
	 * @param route
	 * @param msg
	 */
	public void inform(String route, JSONObject msg) {
		request(route, msg);
	}

	/**
	 * Add timestamp to message.
	 * 
	 * @param msg
	 * @return msg
	 */
	private JSONObject filter(JSONObject msg) {
		if (msg == null) {
			msg = new JSONObject();
		}
		long date = System.currentTimeMillis();
		try {
			msg.put("timestamp", date);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return msg;
	}

	/**
	 * Disconnect the connection with the server.
	 */
	public void disconnect() {
		socket.disconnect();
	}

	/**
	 * Process the message from the server.
	 * 
	 * @param msg
	 */
	private void processMessage(String msg) {
		int id;
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(msg);
			// request message
			if (jsonObject.has("id")) {
				id = jsonObject.getInt("id");
				DataCallBack cb = cbs.get(id);
				cb.responseData(jsonObject.getJSONObject("body"));
			}
			// broadcast message
			else {
				emit(jsonObject.getString("route"), jsonObject);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process message in batch.
	 * 
	 * @param msgs
	 */
	private void processMessageBatch(String msgs) {
		JSONArray jsonArray = null;
		try {
			jsonArray = new JSONArray(msgs);
			for (int i = 0; i < jsonArray.length(); i++) {
				processMessage(jsonArray.getJSONObject(i).toString());
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Add event listener and wait for broadcast message.
	 * 
	 * @param route
	 * @param listener
	 */
	public void on(String route, DataListener listener) {
		List<DataListener> list = listeners.get(route);
		if (list == null)
			list = new ArrayList<DataListener>();
		list.add(listener);
		listeners.put(route, list);
	}

	/**
	 * Touch off the event and call listeners corresponding route.
	 * 
	 * @param route
	 * @param message
	 * @return true if call success, false if there is no listeners for this
	 *         route.
	 */
	private void emit(String route, JSONObject message) {
		List<DataListener> list = listeners.get(route);
		if (list == null) {
			throw new RuntimeException("there is no listeners.");
		}
		for (DataListener listener : list) {
			DataEvent event = new DataEvent(this, message);
			listener.receiveData(event);
		}
	}

}
