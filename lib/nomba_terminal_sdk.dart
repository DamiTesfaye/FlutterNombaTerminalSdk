import 'package:nomba_terminal_sdk/nomba_terminal_sdk_platform_interface.dart';

class NombaTerminalSdk {
  Future<String?> handleTerminalRequest(List<String> args) {
    return NombaTerminalRequestSdk.instance.handleTerminalRequest(args);
  }

  Future<String?> getDeviceInfo() {
    return NombaTerminalRequestSdk.instance.getDeviceInfo();
  }
}
