package {
	import com.electrotank.electroserver5.api.ConnectionResponse;
	import com.electrotank.electroserver5.api.EsObject;
	import com.electrotank.electroserver5.api.LoginRequest;
	import com.electrotank.electroserver5.api.LoginResponse;
	import com.electrotank.electroserver5.api.MessageType;
	import com.electrotank.electroserver5.api.PluginMessageEvent;
	import com.electrotank.electroserver5.api.PluginRequest;
	import com.electrotank.electroserver5.connection.AvailableConnection;
	import com.electrotank.electroserver5.connection.TransportType;
	import com.electrotank.electroserver5.ElectroServer;
	import com.electrotank.electroserver5.server.Server;
	import com.electrotank.electroserver5.util.ES5TraceAdapter;
	import com.electrotank.logging.adapter.Log;
	import flash.display.Sprite;
	
	public class Main extends Sprite {
		Log.setLogAdapter(new ES5TraceAdapter());
		private var _es:ElectroServer = new ElectroServer();
		private var _userName:String = "1@163.com";
		private var _password:String = "123456";
		
		public function Main():void {
			_es.engine.addEventListener(MessageType.ConnectionResponse.name, onConnectionResponse);
			_es.engine.addEventListener(MessageType.LoginResponse.name, onLoginResponse);
			_es.engine.addEventListener(MessageType.PluginMessageEvent.name, onPluginMessage);
			
			var server:Server = new Server("server1");
			var availConn:AvailableConnection = new AvailableConnection("127.0.0.1", 9899, TransportType.BinaryTCP);
			server.addAvailableConnection(availConn);
			
			_es.engine.addServer(server);
			
			_es.engine.connect();
		}
		
		private function onPluginMessage(e:PluginMessageEvent):void {
			if (e.parameters.doesPropertyExist("ACTION")) {
				var actionType:String = e.parameters.getString("ACTION");
				switch (actionType) {
					case "LOGIN_CHECKED":
						trace("LOGIN STATUS : " + e.parameters.getBoolean("LOGIN_PASSED"));
						break;
					default:
						trace("Action not handled: " + actionType);
				}
			}
		}
		
		private function onConnectionResponse(e:ConnectionResponse):void {
			trace("Connection success: " + e.successful.toString());
			if (e.successful) {
				var lr:LoginRequest = new LoginRequest();
				lr.userName = _userName;
				lr.password = _password;
				_es.engine.send(lr);
			}
		}
		
		private function onLoginResponse(e:LoginResponse):void {
			if (e.successful) {
				trace("First step login success...");
			}
		}
		
	}
}