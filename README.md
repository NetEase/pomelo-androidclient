pomelo-androidclient
====================
* This is a pomelo client for java and android.

* The project is based on socket.io-java-client(https://github.com/Gottox/socket.io-java-client).

* pomelo-androidclient is an easy to use pomelo client for Android, and it is also compatible with JRE.

##How to use

Using pomelo-androidclient is quite simple.

Checkout and copy the jar file to your project.

>git clone https://github.com/NetEase/pomelo-androidclient.git

##API

create and initialize a new pomelo client.
```java
PomeloClient client = new PomeloClient(host,port);

client.init();
```

send request to server and process data in callback.
```java
client.request(route, msg, new DataCallBack(){
  public void responseData(JSONObject msg){
		//handle data here
	}
});
```

notify the server without response
```java
client.inform(route, msg);
```

receive broadcast message
```java
client.on(route, new DataListener(){
	public void receiveData(DataEvent event){
		JSONObject msg = event.getMessage();
		//handle data from server	
	}
});
```

disconnect with the server
```java
client.disconnect();
```

##License
(The MIT License)

Copyright (c) 2013 Netease, Inc. and other contributors

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the 'Software'), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
