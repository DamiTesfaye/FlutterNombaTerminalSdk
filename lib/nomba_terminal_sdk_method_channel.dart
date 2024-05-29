import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:nomba_terminal_sdk/nomba_terminal_sdk_platform_interface.dart';

class MethodChannelNombaTerminalSdk extends NombaTerminalRequestSdk {
  @visibleForTesting
  final methodChannel = const MethodChannel('nomba_terminal_sdk');

  @override
  Future<String?> handleTerminalRequest(List<String> args) async {
    try {
      return await methodChannel
          .invokeMethod('handleTerminalRequest', {'args': args});
    } on PlatformException catch (e) {
      throw '${e.message}';
    }
  }

  @override
  Future<String?> getDeviceInfo() async {
    try {
      return await methodChannel
          .invokeMethod('getDeviceInfo');
    } on PlatformException catch (e) {
      throw '${e.message}';
    }
  }
}
