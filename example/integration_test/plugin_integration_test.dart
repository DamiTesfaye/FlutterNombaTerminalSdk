// This is a basic Flutter integration test.
//
// Since integration tests run in a full Flutter application, they can interact
// with the host side of a plugin implementation, unlike Dart unit tests.
//
// For more information about Flutter integration tests, please see
// https://docs.flutter.dev/cookbook/testing/integration/introduction


import 'package:flutter_test/flutter_test.dart';
import 'package:integration_test/integration_test.dart';

import 'package:nomba_terminal_sdk/nomba_terminal_sdk.dart';

void main() {
  IntegrationTestWidgetsFlutterBinding.ensureInitialized();
  testWidgets('handleTerminalRequest test', (WidgetTester tester) async {
    final NombaTerminalSdk plugin = NombaTerminalSdk();
    final String? version = await plugin.handleTerminalRequest(['card_payment_action', '2', 'txRef12345', '']);
  
    expect(version?.isNotEmpty, true);
  });
}
