import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:nomba_terminal_sdk/nomba_terminal_sdk_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const MethodChannel channel = MethodChannel('nomba_terminal_sdk');
  MethodChannelNombaTerminalSdk platform = MethodChannelNombaTerminalSdk();

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(
      channel,
      (MethodCall methodCall) async {
        return '';
      },
    );
  });

  test('handleTerminalRequest', () async {
    expect(
        await platform.handleTerminalRequest(
            ['card_payment_action', '2', 'txRef12345', '']),
        '');
  });

  test('getDeviceInfo', () async {
    expect(await platform.getDeviceInfo(), '');
  });
}
