import 'package:plugin_platform_interface/plugin_platform_interface.dart';
import 'package:nomba_terminal_sdk/nomba_terminal_sdk_method_channel.dart';

abstract class NombaTerminalRequestSdk  extends PlatformInterface {
  NombaTerminalRequestSdk() : super(token: _token);

  static final Object _token = Object();

  static NombaTerminalRequestSdk _instance = MethodChannelNombaTerminalSdk();

  static NombaTerminalRequestSdk get instance => _instance;

  static set instance(NombaTerminalRequestSdk instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> handleTerminalRequest(List<String> args) {
   throw UnimplementedError('handleTerminalRequest() has not been implemented.'); 
  }

  Future<String?> getDeviceInfo() {
    throw UnimplementedError('getDeviceInfo() has not been implemented.'); 
  }
}

