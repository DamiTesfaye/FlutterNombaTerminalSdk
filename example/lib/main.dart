import 'package:flutter/material.dart';
import 'dart:async';

import 'package:nomba_terminal_sdk/nomba_terminal_sdk.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _response = 'No response yet';

  @override
  void initState() {
    super.initState();
  }

  Future<void> _handleTerminalRequest(String action) async {
    try {
      var receiptData = {
        "email": false,
        "sms": true,
        "print": true,
      };

      final args = [action, '2', 'txRef12345', receiptData.toString()];
      final result = await NombaTerminalSdk().handleTerminalRequest(args);
      setState(() {
        _response = result ?? "";
      });
    } catch (e) {
      setState(() {
        _response = 'Error: $e';
      });
    }
  }

  Future<void> _getDeviceInfo() async {
    try {
      final result = await NombaTerminalSdk().getDeviceInfo();
      setState(() {
        _response = result ?? "";
      });
    } catch (e) {
      setState(() {
        _response = 'Error: $e';
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              Text(_response),
              const SizedBox(
                height: 20,
              ),
              OutlinedButton(
                  onPressed: () =>
                      _handleTerminalRequest('card_payment_action'),
                  child: const Text("Run card payment button")),
              const SizedBox(
                height: 10,
              ),
              OutlinedButton(
                  onPressed: () =>
                      _handleTerminalRequest('pay_by_transfer_action'),
                  child: const Text("Run pay by transfer button")),
              const SizedBox(
                height: 10,
              ),
              OutlinedButton(
                  onPressed: () =>
                      _handleTerminalRequest('card_payment_and_pbt_action'),
                  child: const Text("Run Pay by Card + Transfer Action")),
              const SizedBox(
                height: 10,
              ),
              OutlinedButton(
                  onPressed: _getDeviceInfo,
                  child: const Text("Get device info")),
            ],
          ),
        ),
      ),
    );
  }
}
