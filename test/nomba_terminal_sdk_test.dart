import 'package:flutter_test/flutter_test.dart';
import 'package:nomba_terminal_sdk/nomba_terminal_sdk.dart';
import 'package:nomba_terminal_sdk/nomba_terminal_sdk_platform_interface.dart';
import 'package:nomba_terminal_sdk/nomba_terminal_sdk_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockNombaTerminalSdkPlatform
    with MockPlatformInterfaceMixin
    implements NombaTerminalRequestSdk {
  @override
  Future<String?> handleTerminalRequest(List<String> args) => Future.value('');

  @override
  Future<String?> getDeviceInfo() => Future.value('Terminal Id: Serial No: ');
}

void main() {
  final NombaTerminalRequestSdk initialPlatform =
      NombaTerminalRequestSdk.instance;

  test('$MethodChannelNombaTerminalSdk is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelNombaTerminalSdk>());
  });

  test('handleTerminalRequest', () async {
    NombaTerminalSdk nombaTerminalSdkPlugin = NombaTerminalSdk();
    MockNombaTerminalSdkPlatform fakePlatform = MockNombaTerminalSdkPlatform();
    NombaTerminalRequestSdk.instance = fakePlatform;

    expect(
        await nombaTerminalSdkPlugin.handleTerminalRequest(
            ['card_payment_action', '2', 'txRef12345', '']),
        '');
  });

  test('getDeviceInfo', () async {
    NombaTerminalSdk nombaTerminalSdkPlugin = NombaTerminalSdk();
    MockNombaTerminalSdkPlatform fakePlatform = MockNombaTerminalSdkPlatform();
    NombaTerminalRequestSdk.instance = fakePlatform;

    expect(await nombaTerminalSdkPlugin.getDeviceInfo(), '');
  });
}
