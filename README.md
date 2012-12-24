pomelo-androidclient
====================
This is a pomelo client for java and android.

The project is based on socket.io-java-client(https://github.com/Gottox/socket.io-java-client).

pomelo-androidclient is an easy to use pomelo client for Android, the client is also compatible with JRE.

# How to use

Using pomelo-androidclient is quite simple.

Checkout and copy the jar file to your project.

>git clone https://github.com/NetEase/pomelo-androidclient.git

## API

```java
PomeloClient client = new PomeloClient(host,port);

client.init();

// request to server
client.request(route, msg, new DataCallBack(){
  public void responseData(JSONObject msg){
		//handle data here
	}
});

// notify the server without response
client.inform(route, msg);

// receive broadcast message
client.on(route, new DataListener(){
	public void receiveData(DataEvent event){
		JSONObject msg = event.getMessage();
		//handle data from server	
	}
});

// disconnect with the server
client.disconnect();
```
